import 'dotenv/config';
import cors from 'cors';
import express from 'express';
import helmet from 'helmet';
import morgan from 'morgan';
import { randomUUID } from 'node:crypto';
import { createClient } from '@supabase/supabase-js';
import { z } from 'zod';

const requiredEnv = z.object({
  SUPABASE_URL: z.string().url(),
  SUPABASE_ANON_KEY: z.string().min(1),
  SUPABASE_SERVICE_ROLE_KEY: z.string().min(1),
  PORT: z.coerce.number().default(3333),
  APP_GATE_KEY: z.string().optional().default(''),
  CORS_ORIGIN: z.string().optional().default('*')
});

const env = requiredEnv.parse(process.env);
const app = express();

const rateBuckets = new Map();
const AUTH_RATE_LIMIT = { windowMs: 60_000, max: 20 };
const API_RATE_LIMIT = { windowMs: 60_000, max: 180 };

app.disable('x-powered-by');
app.set('trust proxy', 1);
app.use(helmet({
  crossOriginResourcePolicy: { policy: 'cross-origin' }
}));
app.use(cors({
  origin: env.CORS_ORIGIN === '*' ? true : env.CORS_ORIGIN.split(',').map((origin) => origin.trim()).filter(Boolean),
  credentials: false
}));
app.use(express.json({ limit: '8mb' }));
app.use(morgan('dev'));
app.use((req, res, next) => {
  req.requestId = randomUUID();
  res.setHeader('X-Request-Id', req.requestId);
  next();
});

const supabaseAdmin = createClient(env.SUPABASE_URL, env.SUPABASE_SERVICE_ROLE_KEY, {
  auth: { autoRefreshToken: false, persistSession: false }
});

const supabaseAnon = createClient(env.SUPABASE_URL, env.SUPABASE_ANON_KEY, {
  auth: { autoRefreshToken: false, persistSession: false }
});

const SOCIAL_POSTS_BUCKET = 'agego-social-posts';
const PROFILE_PHOTOS_BUCKET = 'agego-profile-photos';

const statusSchema = z.enum(['active', 'draft', 'inactive']).default('active');
const studentStatusSchema = z.enum(['active', 'pending_payment', 'inactive']).default('active');

const studentSchema = z.object({
  name: z.string().trim().min(2),
  email: z.string().trim().email().optional().or(z.literal('')),
  phone: z.string().trim().optional().default(''),
  password: z.string().min(8).optional(),
  routine: z.string().trim().optional().default(''),
  birthDate: z.string().optional(),
  gender: z.string().optional(),
  fitnessLevel: z.enum(['beginner', 'intermediate', 'advanced']).default('beginner'),
  status: studentStatusSchema,
  planId: z.string().uuid().optional(),
  billingDay: z.number().int().min(1).max(28).optional().default(5),
  monthlyFee: z.string().trim().optional().default('')
});

const workoutSchema = z.object({
  name: z.string().trim().min(2),
  description: z.string().trim().optional().default(''),
  iconName: z.string().trim().optional().default('directions_run'),
  status: statusSchema,
  modality: z.string().trim().optional().default(''),
  intensity: z.string().trim().optional().default(''),
  estimatedDurationSeconds: z.number().int().positive().optional(),
  activities: z.array(z.object({
    actionName: z.string().trim().min(1),
    description: z.string().trim().optional().default(''),
    durationSeconds: z.number().int().positive().optional().default(900),
    distanceM: z.number().positive().optional(),
    targetPace: z.string().trim().optional().default('')
  })).default([])
});

const directorySchema = z.object({
  name: z.string().trim().min(2),
  description: z.string().trim().optional().default(''),
  status: statusSchema,
  studentIds: z.array(z.string().uuid()).optional().default([])
});

const eventSchema = z.object({
  name: z.string().trim().min(2),
  description: z.string().trim().optional().default(''),
  eventDate: z.string().trim().min(1),
  location: z.string().trim().optional().default(''),
  latitude: z.number().nullable().optional(),
  longitude: z.number().nullable().optional(),
  coverPhotoUrl: z.string().trim().nullable().optional(),
  capacity: z.number().int().positive().optional()
});

const announcementSchema = z.object({
  content: z.string().trim().min(2),
  targetType: z.enum(['all', 'group', 'plan']).default('all'),
  targetId: z.string().uuid().nullable().optional()
});

const settingsSchema = z.object({
  pixKey: z.string().trim().optional().default(''),
  notificationEmail: z.boolean().optional().default(true),
  notificationPush: z.boolean().optional().default(true)
});

const chargeSchema = z.object({
  studentId: z.string().uuid(),
  description: z.string().trim().optional().default('Mensalidade'),
  amount: z.number().nonnegative(),
  dueDate: z.string().trim().min(1)
});

const challengeSchema = z.object({
  name: z.string().trim().min(2),
  description: z.string().trim().optional().default(''),
  targetType: z.enum(['distance', 'time']).default('distance'),
  targetValue: z.number().nonnegative().default(0)
});

const workoutSessionSchema = z.object({
  routineId: z.string().trim().optional().default(''),
  routineName: z.string().trim().optional().default(''),
  challengeId: z.string().uuid().nullable().optional(),
  dayNumber: z.number().int().positive().optional().default(1),
  cycleStep: z.number().int().positive().optional().default(1),
  elapsedMs: z.number().nonnegative(),
  distanceMeters: z.number().nonnegative(),
  paceSecondsPerKm: z.number().nonnegative().optional().default(0),
  status: z.enum(['completed', 'paused']).default('completed'),
  plannedSteps: z.array(z.object({
    name: z.string().trim().default(''),
    targetType: z.string().trim().default('open'),
    targetValue: z.number().nonnegative().default(0),
    unit: z.string().trim().default('')
  })).default([]),
  routePoints: z.array(z.object({
    lat: z.number(),
    lon: z.number(),
    timestamp: z.number()
  })).default([]),
  splits: z.array(z.object({
    km: z.number(),
    elapsedMs: z.number().nonnegative(),
    paceSeconds: z.number().nonnegative()
  })).default([])
});

const loginSchema = z.object({
  identifier: z.string().trim().min(3)
});

const loginVerifySchema = z.object({
  identifier: z.string().trim().min(3),
  token: z.string().trim().min(4)
});

const instructorRegisterSchema = z.object({
  name: z.string().trim().min(2),
  email: z.string().trim().email(),
  phone: z.string().trim().optional().default('')
});

const verifyTokenSchema = z.object({
  email: z.string().trim().email(),
  token: z.string().trim().min(4),
  displayName: z.string().trim().optional().default(''),
  photoUrl: z.string().trim().optional().default(''),
  photoBase64: z.string().trim().optional().default(''),
  photoMimeType: z.string().trim().optional().default('')
});

const studentStartSchema = z.object({
  phone: z.string().trim().min(6)
});

const studentCompleteSchema = z.object({
  phone: z.string().trim().min(6),
  email: z.string().trim().email(),
  nickname: z.string().trim().min(2),
  photoUrl: z.string().trim().optional().default(''),
  photoBase64: z.string().trim().optional().default(''),
  photoMimeType: z.string().trim().optional().default(''),
  token: z.string().trim().min(4)
});

const profileSchema = z.object({
  name: z.string().trim().min(2),
  photoBase64: z.string().trim().optional().default(''),
  photoMimeType: z.string().trim().optional().default('')
});

function randomPassword() {
  return `AgeGo${randomUUID().slice(0, 8)}A1`;
}

function verificationCode() {
  return String(Math.floor(100000 + Math.random() * 900000));
}

function normalizePhone(value = '') {
  return String(value).replace(/\D/g, '');
}

function loginContact(value = '') {
  const raw = String(value).trim();
  return raw.includes('@') ? raw.toLowerCase() : normalizePhone(raw);
}

function clientIp(req) {
  return req.ip || req.headers['x-forwarded-for'] || req.socket?.remoteAddress || 'unknown';
}

function rateLimit({ windowMs, max }, scope) {
  return (req, res, next) => {
    const now = Date.now();
    const key = `${scope}:${clientIp(req)}`;
    const bucket = rateBuckets.get(key);
    if (!bucket || bucket.resetAt <= now) {
      rateBuckets.set(key, { count: 1, resetAt: now + windowMs });
      return next();
    }
    bucket.count += 1;
    if (bucket.count > max) {
      res.setHeader('Retry-After', Math.ceil((bucket.resetAt - now) / 1000));
      return res.status(429).json({ error: 'Muitas tentativas. Tente novamente em instantes.' });
    }
    next();
  };
}

function requireAppGate(req, res, next) {
  if (!env.APP_GATE_KEY) return next();
  const provided = req.headers['x-agego-app-key'];
  if (provided !== env.APP_GATE_KEY) {
    return res.status(401).json({ error: 'App nao autorizado' });
  }
  next();
}

function authResponse(session, profile) {
  return {
    accessToken: session?.access_token ?? '',
    refreshToken: session?.refresh_token ?? '',
    id: profile?.id ?? '',
    role: profile?.role ?? '',
    name: profile?.name ?? '',
    email: profile?.email ?? '',
    phone: profile?.phone ?? '',
    avatarUrl: profile?.avatar_url ?? '',
    needsProfile: false
  };
}

function computePaymentCycle(billingDay, lastPaymentAt) {
  const day = Math.min(Math.max(Number(billingDay) || 5, 1), 28);
  const now = new Date();
  let dueDate = new Date(now.getFullYear(), now.getMonth(), day);
  if (dueDate > now) {
    dueDate = new Date(now.getFullYear(), now.getMonth() - 1, day);
  }
  const paid = lastPaymentAt ? new Date(lastPaymentAt) >= dueDate : false;
  if (paid) return { paymentStatus: 'paid', daysOverdue: 0 };
  const daysOverdue = Math.max(Math.floor((now.getTime() - dueDate.getTime()) / 86_400_000), 0);
  return { paymentStatus: 'pending', daysOverdue };
}

function toCamelStudent(row) {
  const email = row.email ?? '';
  const cycle = computePaymentCycle(row.billing_day, row.last_payment_at);
  return {
    id: row.id,
    name: row.name,
    email: email.endsWith('@internal.agego.local') ? '' : email,
    phone: row.phone ?? '',
    avatarUrl: row.avatar_url ?? '',
    routine: row.routine ?? '',
    plan_name: row.plan_name ?? row.plan ?? 'Sem plano',
    status: row.status ?? 'active',
    workoutsCompleted: row.workouts_completed ?? 0,
    workoutsTotal: row.workouts_total ?? 0,
    performanceDeltaPercent: row.performance_delta_percent ?? null,
    billingDay: Number(row.billing_day) || 5,
    monthlyFee: row.monthly_fee ?? '',
    paymentStatus: cycle.paymentStatus,
    daysOverdue: cycle.daysOverdue,
    paymentProofUrl: row.payment_proof_url ?? null,
    paymentProofRejectionReason: row.payment_proof_rejection_reason ?? null
  };
}

function toCamelWorkout(row) {
  return {
    id: row.id,
    name: row.name,
    description: row.description ?? '',
    icon_name: row.icon_name ?? 'directions_run',
    status: row.status ?? 'draft'
  };
}

function toDirectory(row) {
  return {
    id: row.id,
    name: row.name,
    status: row.status ?? (row.is_active === false ? 'inactive' : 'active'),
    description: row.description ?? '',
    studentIds: row.student_ids ?? []
  };
}

function toEvent(row) {
  const metaLine = String(row.description ?? '').split('\n').find((line) => line.startsWith('@agego-event:'));
  const meta = metaLine ? (() => { try { return JSON.parse(metaLine.slice(13)); } catch { return {}; } })() : {};
  const cleanDescription = String(row.description ?? '').split('\n').filter((line) => !line.startsWith('@agego-event:')).join('\n').trim();
  return {
    id: row.id,
    name: row.name,
    description: cleanDescription,
    event_date: row.event_date,
    location: row.location ?? '',
    latitude: meta.latitude ?? null,
    longitude: meta.longitude ?? null,
    groupStatus: meta.groupStatus ?? 'waiting',
    checkedIn: Boolean(row.checked_in),
    attendees: row.attendees ?? [],
    results: row.results ?? [],
    coverPhotoUrl: row.cover_photo_url ?? null
  };
}

function eventDescriptionWithMeta(description, meta) {
  const clean = String(description ?? '').split('\n').filter((line) => !line.startsWith('@agego-event:')).join('\n').trim();
  return `@agego-event:${JSON.stringify(meta)}${clean ? `\n${clean}` : ''}`;
}

function eventMeta(row) {
  const line = String(row?.description ?? '').split('\n').find((item) => item.startsWith('@agego-event:'));
  if (!line) return {};
  try { return JSON.parse(line.slice(13)); } catch { return {}; }
}

async function authUserFromRequest(req) {
  const authHeader = req.headers.authorization || '';
  const token = authHeader.startsWith('Bearer ') ? authHeader.slice(7) : null;
  if (!token) return null;

  const { data, error } = await supabaseAnon.auth.getUser(token);
  if (error || !data.user) return null;
  return data.user;
}

async function profileFromRequest(req) {
  const authUser = await authUserFromRequest(req);
  if (!authUser) return null;

  return queryOrThrow(
    supabaseAdmin
      .from('users')
      .select('id, name, email, phone, role, avatar_url, is_active')
      .eq('id', authUser.id)
      .maybeSingle()
  );
}

async function requireAuthenticated(req, res, next) {
  try {
    const profile = await profileFromRequest(req);
    if (!profile || profile.is_active === false) return res.status(401).json({ error: 'Sessao invalida' });
    req.userProfile = profile;
    req.actorId = profile.id;
    next();
  } catch (error) {
    next(error);
  }
}

async function requireInstructor(req, res, next) {
  try {
    const profile = await profileFromRequest(req);
    if (!profile || profile.is_active === false) return res.status(401).json({ error: 'Sessao invalida' });
    if (profile.role !== 'instructor') return res.status(403).json({ error: 'Acesso restrito ao professor' });
    req.userProfile = profile;
    req.instructorId = profile.id;
    req.actorId = profile.id;
    next();
  } catch (error) {
    next(error);
  }
}

async function queryOrThrow(builder) {
  const { data, error } = await builder;
  if (error) throw error;
  return data;
}

async function loadEvents(instructorId, actorId) {
  const events = await queryOrThrow(
    supabaseAdmin
      .from('events')
      .select(`
        id, name, description, event_date, location, cover_photo_url,
        event_attendance(attended, student_id, student_profiles(user_id, users!student_profiles_user_id_fkey(name, avatar_url)))
      `)
      .eq('instructor_id', instructorId)
      .order('event_date', { ascending: true })
  );
  const sessions = await queryOrThrow(
    supabaseAdmin
      .from('workout_sessions')
      .select('student_profile_id, elapsed_ms, distance_meters, pace_seconds_per_km, planned_steps, route_points, completed_at')
      .eq('instructor_id', instructorId)
      .eq('status', 'completed')
  );
  return events.map((event) => {
    const attendance = (event.event_attendance ?? []).filter((item) => item.attended);
    const attendees = attendance.map((item) => ({
      studentId: item.student_id,
      name: item.student_profiles?.users?.name ?? 'Aluno',
      avatarUrl: item.student_profiles?.users?.avatar_url ?? ''
    }));
    const attendeeNames = new Map(attendees.map((item) => [item.studentId, item.name]));
    const results = sessions
      .filter((session) => Array.isArray(session.planned_steps) && session.planned_steps.some((step) => step.name === `event:${event.id}`))
      .map((session) => ({
        studentId: session.student_profile_id,
        name: attendeeNames.get(session.student_profile_id) ?? 'Aluno',
        elapsedMs: Number(session.elapsed_ms || 0),
        distanceMeters: Number(session.distance_meters || 0),
        paceSecondsPerKm: Number(session.pace_seconds_per_km || 0),
        routePoints: Array.isArray(session.route_points) ? session.route_points : []
      }));
    return toEvent({
      ...event,
      checked_in: attendance.some((item) => item.student_profiles?.user_id === actorId),
      attendees,
      results
    });
  });
}

async function loadChallenges(instructorId, studentProfileId = null) {
  const challenges = await queryOrThrow(
    supabaseAdmin
      .from('challenges')
      .select('id, name, description, target_type, target_value, created_at')
      .eq('instructor_id', instructorId)
      .order('created_at', { ascending: false })
  );
  if (challenges.length === 0) return [];
  const ids = challenges.map((challenge) => challenge.id);
  const sessions = await queryOrThrow(
    supabaseAdmin
      .from('workout_sessions')
      .select('challenge_id, student_profile_id, distance_meters, elapsed_ms')
      .in('challenge_id', ids)
      .eq('status', 'completed')
  );
  const byChallenge = new Map();
  sessions.forEach((row) => {
    const list = byChallenge.get(row.challenge_id) ?? [];
    list.push(row);
    byChallenge.set(row.challenge_id, list);
  });
  return challenges.map((challenge) => {
    const rows = byChallenge.get(challenge.id) ?? [];
    return {
      id: challenge.id,
      name: challenge.name,
      description: challenge.description ?? '',
      targetType: challenge.target_type,
      targetValue: Number(challenge.target_value || 0),
      completions: new Set(rows.map((row) => row.student_profile_id)).size,
      totalDistanceMeters: rows.reduce((sum, row) => sum + Number(row.distance_meters || 0), 0),
      totalElapsedMs: rows.reduce((sum, row) => sum + Number(row.elapsed_ms || 0), 0),
      myCompleted: studentProfileId ? rows.some((row) => row.student_profile_id === studentProfileId) : false
    };
  });
}

async function loadDashboard(instructorId) {
  const [students, workouts, announcements, events, routines, trainingNow, challenges] = await Promise.all([
    loadStudents(instructorId),
    queryOrThrow(
      supabaseAdmin
        .from('workouts')
        .select('id, name, description, icon_name, status')
        .eq('instructor_id', instructorId)
        .order('created_at', { ascending: false })
    ),
    queryOrThrow(
      supabaseAdmin
        .from('announcements')
        .select('id, content, published_at, target_type')
        .eq('instructor_id', instructorId)
        .order('published_at', { ascending: false })
    ),
    loadEvents(instructorId, instructorId),
    queryOrThrow(
      supabaseAdmin
        .from('routines')
        .select('id, name, description, status')
        .eq('instructor_id', instructorId)
        .order('name')
    ),
    loadTrainingNow(instructorId),
    loadChallenges(instructorId)
  ]);

  return {
    students: students.map(toCamelStudent),
    workouts: workouts.map(toCamelWorkout),
    announcements,
    events,
    routines: routines.map(toDirectory),
    trainingNow,
    challenges
  };
}

function workoutNamesFromRoutineDescription(description = '') {
  return String(description)
    .split('\n')
    .filter((line) => line.trim().startsWith('Dia '))
    .flatMap((line) => line.split(':').slice(1).join(':').split('|')[0].split(','))
    .map((name) => name.trim())
    .filter((name) => name && name !== 'sem treinos');
}

async function loadDashboardForProfile(profile) {
  if (profile.role === 'instructor') return loadDashboard(profile.id);
  if (profile.role !== 'student') throw Object.assign(new Error('Role sem dashboard'), { statusCode: 403 });

  const studentProfile = await queryOrThrow(
    supabaseAdmin
      .from('student_profiles')
      .select(`
        id,
        instructor_id,
        status,
        goal,
        billing_day,
        monthly_fee,
        last_payment_at,
        payment_proof_url,
        payment_proof_rejection_reason,
        users!student_profiles_user_id_fkey(name, email, phone, avatar_url),
        student_plans(is_active, plans(name))
      `)
      .eq('user_id', profile.id)
      .maybeSingle()
  );
  if (!studentProfile) throw Object.assign(new Error('Aluno nao vinculado a professor'), { statusCode: 403 });

  const instructorId = studentProfile.instructor_id;
  const instructorWorkouts = await queryOrThrow(
    supabaseAdmin
      .from('workouts')
      .select('id, name, description, icon_name, status')
      .eq('instructor_id', instructorId)
      .order('name')
  );
  const workoutGoal = String(studentProfile.goal ?? '');
  const assignedWorkouts = instructorWorkouts.filter(
    (workout) => workout.id === workoutGoal || String(workout.name ?? '').toLowerCase() === workoutGoal.toLowerCase()
  );
  const visibleEvents = await loadEvents(instructorId, profile.id);
  const activePlan = studentProfile.student_plans?.find((item) => item.is_active);
  const instructorSettingsRow = await queryOrThrow(
    supabaseAdmin
      .from('instructor_settings')
      .select('pix_key')
      .eq('instructor_id', instructorId)
      .maybeSingle()
  );
  const instructorUserRow = await queryOrThrow(
    supabaseAdmin.from('users').select('name, avatar_url').eq('id', instructorId).maybeSingle()
  );
  const announcementRows = await queryOrThrow(
    supabaseAdmin
      .from('announcements')
      .select('id, content, published_at, target_type')
      .eq('instructor_id', instructorId)
      .order('published_at', { ascending: false })
      .limit(10)
  );
  const sixMonthsAgo = new Date();
  sixMonthsAgo.setMonth(sixMonthsAgo.getMonth() - 6);
  const sessionRows = await queryOrThrow(
    supabaseAdmin
      .from('workout_sessions')
      .select('id, routine_name, elapsed_ms, distance_meters, pace_seconds_per_km, route_points, completed_at')
      .eq('student_profile_id', studentProfile.id)
      .eq('status', 'completed')
      .gte('completed_at', sixMonthsAgo.toISOString())
      .order('completed_at', { ascending: false })
      .limit(200)
  );
  const runHistory = sessionRows.map((row) => ({
    id: row.id,
    routineName: row.routine_name ?? '',
    elapsedMs: Number(row.elapsed_ms || 0),
    distanceMeters: Number(row.distance_meters || 0),
    paceSecondsPerKm: Number(row.pace_seconds_per_km || 0),
    routePoints: Array.isArray(row.route_points) ? row.route_points : [],
    completedAt: row.completed_at
  }));

  return {
    students: [toCamelStudent({
      id: studentProfile.id,
      name: studentProfile.users?.name ?? profile.name ?? 'Aluno',
      email: studentProfile.users?.email ?? profile.email ?? '',
      phone: studentProfile.users?.phone ?? profile.phone ?? '',
      avatar_url: studentProfile.users?.avatar_url ?? profile.avatar_url ?? '',
      routine: studentProfile.goal ?? '',
      plan_name: activePlan?.plans?.name ?? 'Sem plano',
      status: studentProfile.status,
      billing_day: studentProfile.billing_day,
      monthly_fee: studentProfile.monthly_fee,
      last_payment_at: studentProfile.last_payment_at,
      payment_proof_url: studentProfile.payment_proof_url,
      payment_proof_rejection_reason: studentProfile.payment_proof_rejection_reason
    })],
    workouts: assignedWorkouts.map(toCamelWorkout),
    announcements: announcementRows,
    events: visibleEvents,
    routines: [],
    trainingNow: [],
    instructorPixKey: instructorSettingsRow?.pix_key ?? '',
    instructorName: instructorUserRow?.name ?? 'Professor',
    instructorAvatarUrl: instructorUserRow?.avatar_url ?? '',
    runHistory,
    challenges: await loadChallenges(instructorId, studentProfile.id)
  };
}

async function loadStudents(instructorId) {
  const rows = await queryOrThrow(
    supabaseAdmin
      .from('student_profiles')
      .select(`
        id,
        status,
        goal,
        billing_day,
        monthly_fee,
        last_payment_at,
        payment_proof_url,
        payment_proof_rejection_reason,
        users!student_profiles_user_id_fkey(name, email, phone, avatar_url),
        student_plans(is_active, frequency_days, plans(name, plan_workouts(id)))
      `)
      .eq('instructor_id', instructorId)
      .order('created_at', { ascending: false })
  );

  const weekStart = new Date();
  const dayFromMonday = (weekStart.getDay() + 6) % 7;
  weekStart.setDate(weekStart.getDate() - dayFromMonday);
  weekStart.setHours(0, 0, 0, 0);
  const sessions = await queryOrThrow(
    supabaseAdmin
      .from('workout_sessions')
      .select('student_profile_id, day_number, elapsed_ms, distance_meters, planned_steps, completed_at')
      .eq('instructor_id', instructorId)
      .eq('status', 'completed')
      .gte('completed_at', weekStart.toISOString())
      .order('completed_at', { ascending: false })
  );
  const routineWorkouts = await queryOrThrow(
    supabaseAdmin
      .from('workouts')
      .select('id, name, description')
      .eq('instructor_id', instructorId)
  );
  const routineWorkoutsById = new Map(routineWorkouts.map((workout) => [workout.id, workout]));
  const routineWorkoutsByName = new Map(routineWorkouts.map((workout) => [String(workout.name).toLowerCase(), workout]));
  const sessionsByStudent = new Map();
  sessions.forEach((session) => {
    const current = sessionsByStudent.get(session.student_profile_id) ?? [];
    current.push(session);
    sessionsByStudent.set(session.student_profile_id, current);
  });

  return rows.map((row) => {
    const activePlan = row.student_plans?.find((item) => item.is_active);
    const studentSessions = sessionsByStudent.get(row.id) ?? [];
    const frequencyDays = activePlan?.frequency_days ?? [];
    const planWorkouts = activePlan?.plans?.plan_workouts ?? [];
    const assignedWorkout = routineWorkoutsById.get(row.goal)
      ?? routineWorkoutsByName.get(String(row.goal ?? '').toLowerCase());
    const routineDayNumbers = new Set(
      String(assignedWorkout?.description ?? '')
        .split('\n')
        .map((line) => line.trim().match(/^Dia\s+(\d+)/i)?.[1])
        .filter(Boolean)
    );
    const workoutsTotal = frequencyDays.length || routineDayNumbers.size || planWorkouts.length;
    const completedDays = new Set(studentSessions.map((session) => session.day_number).filter((day) => day > 0)).size;
    const latestSession = studentSessions[0];
    const plannedSteps = Array.isArray(latestSession?.planned_steps) ? latestSession.planned_steps : [];
    const targetDistanceMeters = plannedSteps
      .filter((step) => step.targetType === 'distance')
      .reduce((total, step) => total + Number(step.targetValue || 0) * 1000, 0);
    const targetTimeMs = plannedSteps
      .filter((step) => step.targetType === 'time')
      .reduce((total, step) => total + Number(step.targetValue || 0) * 60000, 0);
    const workloadRatios = [];
    if (targetDistanceMeters > 0) workloadRatios.push(Number(latestSession.distance_meters || 0) / targetDistanceMeters);
    if (targetTimeMs > 0) workloadRatios.push(Number(latestSession.elapsed_ms || 0) / targetTimeMs);
    const performanceDeltaPercent = workloadRatios.length
      ? Math.round((workloadRatios.reduce((sum, ratio) => sum + ratio, 0) / workloadRatios.length - 1) * 100)
      : null;
    return toCamelStudent({
      id: row.id,
      name: row.users?.name ?? 'Aluno',
      email: row.users?.email ?? '',
      phone: row.users?.phone ?? '',
      avatar_url: row.users?.avatar_url ?? '',
      routine: row.goal ?? '',
      plan_name: activePlan?.plans?.name ?? 'Sem plano',
      status: row.status,
      workouts_completed: Math.min(completedDays, workoutsTotal),
      workouts_total: workoutsTotal,
      performance_delta_percent: performanceDeltaPercent,
      billing_day: row.billing_day,
      monthly_fee: row.monthly_fee,
      last_payment_at: row.last_payment_at,
      payment_proof_url: row.payment_proof_url,
      payment_proof_rejection_reason: row.payment_proof_rejection_reason
    });
  });
}

async function loadTrainingNow(instructorId) {
  const since = new Date(Date.now() - 5 * 60 * 1000).toISOString();
  const rows = await queryOrThrow(
    supabaseAdmin
      .from('student_presence')
      .select(`
        last_seen_at,
        student_profiles!student_presence_student_profile_id_fkey(
          id,
          instructor_id,
          users!student_profiles_user_id_fkey(name, avatar_url)
        )
      `)
      .gte('last_seen_at', since)
      .order('last_seen_at', { ascending: false })
  );

  return rows
    .filter((row) => row.student_profiles?.instructor_id === instructorId)
    .map((row) => ({
      id: row.student_profiles.id,
      name: row.student_profiles.users?.name ?? 'Aluno',
      avatarUrl: row.student_profiles.users?.avatar_url ?? '',
      lastSeenAt: row.last_seen_at
    }));
}

app.get('/health', (req, res) => {
  res.json({ ok: true, service: 'agego-api' });
});

app.use('/auth', rateLimit(AUTH_RATE_LIMIT, 'auth'), requireAppGate);
app.use(rateLimit(API_RATE_LIMIT, 'api'));

app.post('/auth/login/start', async (req, res, next) => {
  try {
    const input = loginSchema.parse(req.body);
    const identifier = input.identifier.trim();
    let email = identifier.includes('@') ? identifier.toLowerCase() : '';
    const contact = loginContact(identifier);

    if (!email) {
      const digits = normalizePhone(identifier);
      const profile = await queryOrThrow(
        supabaseAdmin
          .from('users')
          .select('id, email, is_active')
          .eq('phone', digits)
          .maybeSingle()
      );
      email = profile?.email ?? '';
    }

    const profile = await queryOrThrow(
      supabaseAdmin
        .from('users')
        .select('id, email, phone, role, is_active')
        .or(`email.eq.${email || contact},phone.eq.${contact}`)
        .maybeSingle()
    );

    if (!profile) return res.status(404).json({ error: 'Contato nao encontrado' });

    if (profile.is_active === false && profile.role === 'student' && !identifier.includes('@')) {
      return res.json({
        ok: true,
        message: 'Primeiro acesso detectado. Use o codigo enviado pelo professor.',
        verificationToken: '',
        nextStep: 'student_first_access'
      });
    }

    if (profile.is_active === false) return res.status(403).json({ error: 'Conta ainda precisa de verificacao' });

    const token = verificationCode();
    await queryOrThrow(supabaseAdmin.from('auth_verification_tokens').insert({
      user_id: profile.id,
      contact,
      purpose: 'login',
      token,
      expires_at: new Date(Date.now() + 20 * 60 * 1000).toISOString()
    }));

    res.json({
      ok: true,
      message: 'Token de acesso gerado',
      verificationToken: token,
      nextStep: 'login'
    });
  } catch (error) {
    next(error);
  }
});

app.post('/auth/login/verify', async (req, res, next) => {
  try {
    const input = loginVerifySchema.parse(req.body);
    const contact = loginContact(input.identifier);
    const tokenRow = await queryOrThrow(
      supabaseAdmin
        .from('auth_verification_tokens')
        .select('id, user_id, expires_at, consumed_at')
        .eq('contact', contact)
        .eq('purpose', 'login')
        .eq('token', input.token)
        .maybeSingle()
    );

    if (!tokenRow || tokenRow.consumed_at || new Date(tokenRow.expires_at) < new Date()) {
      return res.status(400).json({ error: 'Token invalido ou expirado' });
    }

    const profile = await queryOrThrow(
      supabaseAdmin
        .from('users')
        .select('id, name, email, phone, role, avatar_url, is_active')
        .eq('id', tokenRow.user_id)
        .maybeSingle()
    );

    if (!profile || profile.is_active === false) {
      return res.status(403).json({ error: 'Conta ainda nao verificada' });
    }

    const tempPassword = randomPassword();
    const { error: updateAuthError } = await supabaseAdmin.auth.admin.updateUserById(profile.id, {
      password: tempPassword,
      email_confirm: true
    });
    if (updateAuthError) throw updateAuthError;

    const { data, error } = await supabaseAnon.auth.signInWithPassword({
      email: profile.email,
      password: tempPassword
    });
    if (error || !data.session) throw error;

    await queryOrThrow(supabaseAdmin.from('auth_verification_tokens').update({ consumed_at: new Date().toISOString() }).eq('id', tokenRow.id));

    res.json(authResponse(data.session, profile));
  } catch (error) {
    next(error);
  }
});

app.post('/auth/refresh', async (req, res, next) => {
  try {
    const refreshToken = String(req.body?.refreshToken || '');
    if (!refreshToken) return res.status(401).json({ error: 'Refresh token ausente' });

    const { data, error } = await supabaseAnon.auth.refreshSession({ refresh_token: refreshToken });
    if (error || !data.session?.user) return res.status(401).json({ error: 'Sessao expirada. Entre novamente.' });

    const profile = await queryOrThrow(
      supabaseAdmin
        .from('users')
        .select('id, name, email, phone, role, avatar_url, is_active')
        .eq('id', data.session.user.id)
        .maybeSingle()
    );
    if (!profile || profile.is_active === false) return res.status(401).json({ error: 'Sessao invalida' });

    res.json(authResponse(data.session, profile));
  } catch (error) {
    next(error);
  }
});

app.post('/auth/instructor/register', async (req, res, next) => {
  try {
    const input = instructorRegisterSchema.parse(req.body);
    const existing = await queryOrThrow(
      supabaseAdmin
        .from('users')
        .select('id')
        .eq('email', input.email)
        .maybeSingle()
    );
    if (existing) return res.status(409).json({ error: 'Email ja cadastrado' });

    const token = verificationCode();
    await queryOrThrow(supabaseAdmin.from('auth_pending_registrations').insert({
      name: input.name,
      email: input.email,
      phone: normalizePhone(input.phone),
      token,
      expires_at: new Date(Date.now() + 30 * 60 * 1000).toISOString()
    }));

    res.status(201).json({
      ok: true,
      message: 'Token de verificacao gerado',
      verificationToken: token
    });
  } catch (error) {
    next(error);
  }
});

app.post('/auth/instructor/verify', async (req, res, next) => {
  try {
    const input = verifyTokenSchema.parse(req.body);
    const pending = await queryOrThrow(
      supabaseAdmin
        .from('auth_pending_registrations')
        .select('id, name, email, phone, token, expires_at, consumed_at')
        .eq('email', input.email)
        .eq('token', input.token)
        .order('created_at', { ascending: false })
        .maybeSingle()
    );

    if (!pending || pending.consumed_at || new Date(pending.expires_at) < new Date()) {
      return res.status(400).json({ error: 'Token invalido ou expirado' });
    }

    const existing = await queryOrThrow(
      supabaseAdmin
        .from('users')
        .select('id')
        .eq('email', input.email)
        .maybeSingle()
    );
    if (existing) return res.status(409).json({ error: 'Email ja cadastrado' });

    const tempPassword = randomPassword();
    const displayName = input.displayName || pending.name;
    const { data: created, error: createError } = await supabaseAdmin.auth.admin.createUser({
      email: pending.email,
      password: tempPassword,
      email_confirm: true,
      user_metadata: {
        name: displayName,
        role: 'instructor',
        photoUrl: input.photoUrl
      }
    });
    if (createError || !created.user) throw createError;
    const avatarUrl = await uploadProfilePhoto(created.user.id, input.photoBase64, input.photoMimeType) || input.photoUrl;

    const profile = await queryOrThrow(
      supabaseAdmin
        .from('users')
        .upsert({
          id: created.user.id,
          email: pending.email,
          name: displayName,
          phone: pending.phone,
          avatar_url: avatarUrl,
          role: 'instructor',
          is_active: true
        }, { onConflict: 'id' })
        .select('id, name, email, phone, role, avatar_url, is_active')
        .single()
    );

    if (avatarUrl) {
      await supabaseAdmin.auth.admin.updateUserById(profile.id, {
        user_metadata: { name: displayName, role: 'instructor', avatar_url: avatarUrl, photoUrl: avatarUrl }
      });
    }

    const { data, error } = await supabaseAnon.auth.signInWithPassword({
      email: profile.email,
      password: tempPassword
    });
    if (error || !data.session) throw error;

    await queryOrThrow(supabaseAdmin.from('auth_pending_registrations').update({ consumed_at: new Date().toISOString() }).eq('id', pending.id));
    res.json(authResponse(data.session, profile));
  } catch (error) {
    next(error);
  }
});

app.post('/auth/student/start', async (req, res, next) => {
  try {
    const input = studentStartSchema.parse(req.body);
    const digits = normalizePhone(input.phone);
    const profile = await queryOrThrow(
      supabaseAdmin
        .from('users')
        .select('id, email, role')
        .eq('phone', digits)
        .eq('role', 'student')
        .maybeSingle()
    );
    if (!profile) return res.status(404).json({ error: 'Telefone ainda nao foi cadastrado por um professor' });

    const token = verificationCode();
    await queryOrThrow(supabaseAdmin.from('auth_verification_tokens').insert({
      user_id: profile.id,
      contact: digits,
      purpose: 'student_first_access',
      token,
      expires_at: new Date(Date.now() + 30 * 60 * 1000).toISOString()
    }));

    res.json({
      ok: true,
      message: 'Token de primeiro acesso gerado',
      verificationToken: token
    });
  } catch (error) {
    next(error);
  }
});

app.post('/auth/student/complete', async (req, res, next) => {
  try {
    const input = studentCompleteSchema.parse(req.body);
    const digits = normalizePhone(input.phone);
    const tokenRow = await queryOrThrow(
      supabaseAdmin
        .from('auth_verification_tokens')
        .select('id, user_id, expires_at, consumed_at')
        .eq('contact', digits)
        .eq('purpose', 'student_first_access')
        .eq('token', input.token)
        .maybeSingle()
    );

    if (!tokenRow || tokenRow.consumed_at || new Date(tokenRow.expires_at) < new Date()) {
      return res.status(400).json({ error: 'Token invalido ou expirado' });
    }

    const tempPassword = randomPassword();
    const avatarUrl = await uploadProfilePhoto(tokenRow.user_id, input.photoBase64, input.photoMimeType) || input.photoUrl;
    const { error: updateAuthError } = await supabaseAdmin.auth.admin.updateUserById(tokenRow.user_id, {
      email: input.email,
      password: tempPassword,
      email_confirm: true,
      user_metadata: {
        name: input.nickname,
        role: 'student',
        avatar_url: avatarUrl,
        photoUrl: avatarUrl
      }
    });
    if (updateAuthError) throw updateAuthError;

    const profile = await queryOrThrow(
      supabaseAdmin.from('users').update({
        email: input.email,
        name: input.nickname,
        phone: digits,
        avatar_url: avatarUrl,
        role: 'student',
        is_active: true
      }).eq('id', tokenRow.user_id).select('id, name, email, phone, role, avatar_url, is_active').single()
    );
    await queryOrThrow(supabaseAdmin.from('auth_verification_tokens').update({ consumed_at: new Date().toISOString() }).eq('id', tokenRow.id));

    const { data, error } = await supabaseAnon.auth.signInWithPassword({
      email: input.email,
      password: tempPassword
    });
    if (error || !data.session) throw error;

    res.json(authResponse(data.session, profile));
  } catch (error) {
    next(error);
  }
});

app.get('/dashboard', requireAuthenticated, async (req, res, next) => {
  try {
    res.json(await loadDashboardForProfile(req.userProfile));
  } catch (error) {
    next(error);
  }
});

app.post('/presence/training-now', requireAuthenticated, async (req, res, next) => {
  try {
    if (req.userProfile.role !== 'student') return res.status(403).json({ error: 'Apenas alunos podem marcar presenca' });
    const studentProfile = await queryOrThrow(
      supabaseAdmin
        .from('student_profiles')
        .select('id, instructor_id')
        .eq('user_id', req.actorId)
        .maybeSingle()
    );
    if (!studentProfile) return res.status(403).json({ error: 'Aluno nao vinculado a professor' });

    await queryOrThrow(
      supabaseAdmin
        .from('student_presence')
        .upsert({
          user_id: req.actorId,
          student_profile_id: studentProfile.id,
          instructor_id: studentProfile.instructor_id,
          last_seen_at: new Date().toISOString()
        }, { onConflict: 'user_id' })
    );
    res.json({ ok: true });
  } catch (error) {
    next(error);
  }
});

app.post('/workout-sessions', requireAuthenticated, async (req, res, next) => {
  try {
    if (req.userProfile.role !== 'student' && req.userProfile.role !== 'instructor') {
      return res.status(403).json({ error: 'Apenas alunos ou professores podem salvar sessao de treino' });
    }
    const input = workoutSessionSchema.parse(req.body);

    let studentProfileId = null;
    let instructorId = req.actorId;
    if (req.userProfile.role === 'student') {
      const studentProfile = await queryOrThrow(
        supabaseAdmin
          .from('student_profiles')
          .select('id, instructor_id')
          .eq('user_id', req.actorId)
          .maybeSingle()
      );
      if (!studentProfile) return res.status(403).json({ error: 'Aluno nao vinculado a professor' });
      studentProfileId = studentProfile.id;
      instructorId = studentProfile.instructor_id;
    }

    const row = await queryOrThrow(
      supabaseAdmin
        .from('workout_sessions')
        .insert({
          user_id: req.actorId,
          student_profile_id: studentProfileId,
          instructor_id: instructorId,
          routine_id: null,
          routine_name: input.routineName,
          challenge_id: input.challengeId || null,
          day_number: input.dayNumber,
          cycle_step: input.cycleStep,
          elapsed_ms: Math.round(input.elapsedMs),
          distance_meters: input.distanceMeters,
          pace_seconds_per_km: input.paceSecondsPerKm,
          status: input.status,
          planned_steps: input.plannedSteps,
          route_points: input.routePoints,
          splits: input.splits,
          completed_at: input.status === 'completed' ? new Date().toISOString() : null
        })
        .select()
        .single()
    );

    res.status(201).json({ session: row });
  } catch (error) {
    next(error);
  }
});

app.get('/students', requireInstructor, async (req, res, next) => {
  try {
    const dashboard = await loadDashboard(req.instructorId);
    res.json({ students: dashboard.students });
  } catch (error) {
    next(error);
  }
});

app.post('/students', requireInstructor, async (req, res, next) => {
  try {
    const input = studentSchema.parse(req.body);
    const publicEmail = input.email || '';
    const authEmail = publicEmail || `student-${randomUUID()}@internal.agego.local`;
    const phone = normalizePhone(input.phone);
    if (!phone) return res.status(400).json({ error: 'Telefone do aluno e obrigatorio' });

    const { data: createdUser, error: createUserError } = await supabaseAdmin.auth.admin.createUser({
      email: authEmail,
      password: randomPassword(),
      email_confirm: true,
      user_metadata: { name: input.name, role: 'student' }
    });
    if (createUserError || !createdUser.user) throw createUserError;

    const userId = createdUser.user.id;
    await queryOrThrow(supabaseAdmin.from('users').upsert({
      id: userId,
      email: publicEmail || authEmail,
      name: input.name,
      phone,
      role: 'student',
      is_active: false
    }));

    const student = await queryOrThrow(
      supabaseAdmin
        .from('student_profiles')
        .insert({
          user_id: userId,
          instructor_id: req.instructorId,
          birth_date: input.birthDate || null,
          gender: input.gender || null,
          fitness_level: input.fitnessLevel,
          status: input.status,
          goal: input.routine,
          billing_day: input.billingDay,
          monthly_fee: input.monthlyFee
        })
        .select()
        .single()
    );

    const accessCode = verificationCode();
    await queryOrThrow(supabaseAdmin.from('auth_verification_tokens').insert({
      user_id: userId,
      contact: phone,
      purpose: 'student_first_access',
      token: accessCode,
      expires_at: new Date(Date.now() + 24 * 60 * 60 * 1000).toISOString()
    }));

    res.status(201).json({
      student: toCamelStudent({
        id: student.id,
        name: input.name,
        email: publicEmail,
        phone,
        routine: input.routine,
        plan_name: input.routine || 'Sem rotina',
        status: input.status,
        billing_day: student.billing_day,
        monthly_fee: student.monthly_fee,
        last_payment_at: student.last_payment_at,
        payment_proof_url: student.payment_proof_url,
        payment_proof_rejection_reason: student.payment_proof_rejection_reason
      }),
      accessCode,
      accessCodeExpiresAt: new Date(Date.now() + 24 * 60 * 60 * 1000).toISOString()
    });
  } catch (error) {
    next(error);
  }
});

app.put('/students/:id', requireInstructor, async (req, res, next) => {
  try {
    const input = studentSchema.partial({ email: true, password: true }).parse(req.body);
    const profile = await queryOrThrow(
      supabaseAdmin.from('student_profiles').select('id, user_id').eq('id', req.params.id).eq('instructor_id', req.instructorId).single()
    );
    await queryOrThrow(supabaseAdmin.from('users').update({
      name: input.name,
      phone: normalizePhone(input.phone)
    }).eq('id', profile.user_id));
    const student = await queryOrThrow(
      supabaseAdmin.from('student_profiles').update({
        status: input.status,
        goal: input.routine,
        fitness_level: input.fitnessLevel,
        billing_day: input.billingDay,
        monthly_fee: input.monthlyFee
      }).eq('id', req.params.id).select().single()
    );
    res.json({
      student: toCamelStudent({
        id: student.id,
        name: input.name,
        email: input.email ?? '',
        phone: input.phone,
        routine: student.goal,
        plan_name: input.routine || 'Sem rotina',
        status: student.status,
        billing_day: student.billing_day,
        monthly_fee: student.monthly_fee,
        last_payment_at: student.last_payment_at,
        payment_proof_url: student.payment_proof_url,
        payment_proof_rejection_reason: student.payment_proof_rejection_reason
      })
    });
  } catch (error) {
    next(error);
  }
});

app.post('/me/payment-proof', requireAuthenticated, async (req, res, next) => {
  try {
    if (req.userProfile.role !== 'student') return res.status(403).json({ error: 'Apenas alunos enviam comprovante' });
    const url = z.string().trim().min(1).parse(req.body?.url);
    const studentProfile = await queryOrThrow(
      supabaseAdmin.from('student_profiles').select('id').eq('user_id', req.actorId).maybeSingle()
    );
    if (!studentProfile) return res.status(403).json({ error: 'Aluno nao vinculado a professor' });
    await queryOrThrow(
      supabaseAdmin.from('student_profiles').update({ payment_proof_url: url, payment_proof_rejection_reason: null }).eq('id', studentProfile.id)
    );
    res.json({ ok: true });
  } catch (error) {
    next(error);
  }
});

app.post('/students/:id/approve-payment', requireInstructor, async (req, res, next) => {
  try {
    await queryOrThrow(
      supabaseAdmin
        .from('student_profiles')
        .update({ last_payment_at: new Date().toISOString(), payment_proof_url: null, payment_proof_rejection_reason: null })
        .eq('id', req.params.id)
        .eq('instructor_id', req.instructorId)
    );
    res.json({ ok: true });
  } catch (error) {
    next(error);
  }
});

app.post('/students/:id/reject-payment', requireInstructor, async (req, res, next) => {
  try {
    const reason = z.string().trim().min(1).parse(req.body?.reason);
    await queryOrThrow(
      supabaseAdmin
        .from('student_profiles')
        .update({ payment_proof_url: null, payment_proof_rejection_reason: reason })
        .eq('id', req.params.id)
        .eq('instructor_id', req.instructorId)
    );
    res.json({ ok: true });
  } catch (error) {
    next(error);
  }
});

app.delete('/students/:id', requireInstructor, async (req, res, next) => {
  try {
    const profile = await queryOrThrow(
      supabaseAdmin.from('student_profiles').select('user_id').eq('id', req.params.id).eq('instructor_id', req.instructorId).single()
    );
    await supabaseAdmin.auth.admin.deleteUser(profile.user_id);
    res.status(204).end();
  } catch (error) {
    next(error);
  }
});

app.get('/workouts', requireInstructor, async (req, res, next) => {
  try {
    const rows = await queryOrThrow(supabaseAdmin.from('workouts').select('*').eq('instructor_id', req.instructorId).order('created_at', { ascending: false }));
    res.json({ workouts: rows.map(toCamelWorkout) });
  } catch (error) {
    next(error);
  }
});

app.post('/workouts', requireInstructor, async (req, res, next) => {
  try {
    const input = workoutSchema.parse(req.body);
    const workout = await queryOrThrow(
      supabaseAdmin.from('workouts').insert({
        instructor_id: req.instructorId,
        name: input.name,
        description: input.description,
        icon_name: input.iconName,
        status: input.status,
        modality: input.modality || null,
        intensity: input.intensity || null,
        estimated_duration_seconds: input.estimatedDurationSeconds || null
      }).select().single()
    );
    if (input.activities.length > 0) {
      await queryOrThrow(supabaseAdmin.from('workout_activities').insert(input.activities.map((activity, index) => ({
        workout_id: workout.id,
        order_index: index + 1,
        action_name: activity.actionName,
        description: activity.description,
        duration_seconds: activity.durationSeconds,
        distance_m: activity.distanceM || null,
        target_pace: activity.targetPace || null
      }))));
    }
    res.status(201).json({ workout: toCamelWorkout(workout) });
  } catch (error) {
    next(error);
  }
});

app.put('/workouts/:id', requireInstructor, async (req, res, next) => {
  try {
    const input = workoutSchema.partial({ activities: true }).parse(req.body);
    const workout = await queryOrThrow(
      supabaseAdmin.from('workouts').update({
        name: input.name,
        description: input.description,
        icon_name: input.iconName,
        status: input.status
      }).eq('id', req.params.id).eq('instructor_id', req.instructorId).select().single()
    );
    res.json({ workout: toCamelWorkout(workout) });
  } catch (error) {
    next(error);
  }
});

app.delete('/workouts/:id', requireInstructor, async (req, res, next) => {
  try {
    await queryOrThrow(supabaseAdmin.from('workouts').delete().eq('id', req.params.id).eq('instructor_id', req.instructorId));
    res.status(204).end();
  } catch (error) {
    next(error);
  }
});

app.get('/routines', requireInstructor, async (req, res, next) => {
  try {
    const rows = await queryOrThrow(supabaseAdmin.from('routines').select('*').eq('instructor_id', req.instructorId).order('name'));
    res.json({ routines: rows.map(toDirectory) });
  } catch (error) {
    next(error);
  }
});

app.post('/routines', requireInstructor, async (req, res, next) => {
  try {
    const input = directorySchema.parse(req.body);
    const routine = await queryOrThrow(
      supabaseAdmin.from('routines').insert({
        instructor_id: req.instructorId,
        name: input.name,
        description: input.description,
        status: input.status
      }).select().single()
    );
    res.status(201).json({ routine: toDirectory(routine) });
  } catch (error) {
    next(error);
  }
});

app.put('/routines/:id', requireInstructor, async (req, res, next) => {
  try {
    const input = directorySchema.parse(req.body);
    const routine = await queryOrThrow(
      supabaseAdmin.from('routines').update({
        name: input.name,
        description: input.description,
        status: input.status
      }).eq('id', req.params.id).eq('instructor_id', req.instructorId).select().single()
    );
    res.json({ routine: toDirectory(routine) });
  } catch (error) {
    next(error);
  }
});

app.delete('/routines/:id', requireInstructor, async (req, res, next) => {
  try {
    await queryOrThrow(supabaseAdmin.from('routines').delete().eq('id', req.params.id).eq('instructor_id', req.instructorId));
    res.status(204).end();
  } catch (error) {
    next(error);
  }
});

app.post('/challenges', requireInstructor, async (req, res, next) => {
  try {
    const input = challengeSchema.parse(req.body);
    await queryOrThrow(
      supabaseAdmin.from('challenges').insert({
        instructor_id: req.instructorId,
        name: input.name,
        description: input.description,
        target_type: input.targetType,
        target_value: input.targetValue
      }).select().single()
    );
    res.status(201).json({ challenges: await loadChallenges(req.instructorId) });
  } catch (error) {
    next(error);
  }
});

app.put('/challenges/:id', requireInstructor, async (req, res, next) => {
  try {
    const input = challengeSchema.parse(req.body);
    await queryOrThrow(
      supabaseAdmin
        .from('challenges')
        .update({
          name: input.name,
          description: input.description,
          target_type: input.targetType,
          target_value: input.targetValue
        })
        .eq('id', req.params.id)
        .eq('instructor_id', req.instructorId)
    );
    res.json({ challenges: await loadChallenges(req.instructorId) });
  } catch (error) {
    next(error);
  }
});

app.delete('/challenges/:id', requireInstructor, async (req, res, next) => {
  try {
    await queryOrThrow(
      supabaseAdmin.from('challenges').delete().eq('id', req.params.id).eq('instructor_id', req.instructorId)
    );
    res.status(204).end();
  } catch (error) {
    next(error);
  }
});

app.get('/events', requireInstructor, async (req, res, next) => {
  try {
    res.json({ events: await loadEvents(req.instructorId, req.actorId) });
  } catch (error) {
    next(error);
  }
});

app.post('/events', requireInstructor, async (req, res, next) => {
  try {
    const input = eventSchema.parse(req.body);
    const event = await queryOrThrow(
      supabaseAdmin.from('events').insert({
        instructor_id: req.instructorId,
        name: input.name,
        description: eventDescriptionWithMeta(input.description, { latitude: input.latitude ?? null, longitude: input.longitude ?? null, groupStatus: 'waiting' }),
        event_date: input.eventDate,
        location: input.location,
        cover_photo_url: input.coverPhotoUrl ?? null,
        capacity: input.capacity || null
      }).select().single()
    );
    res.status(201).json({ event: toEvent(event) });
  } catch (error) {
    next(error);
  }
});

app.put('/events/:id', requireInstructor, async (req, res, next) => {
  try {
    const input = eventSchema.parse(req.body);
    const existing = await queryOrThrow(
      supabaseAdmin.from('events').select('description').eq('id', req.params.id).eq('instructor_id', req.instructorId).maybeSingle()
    );
    const currentMeta = eventMeta(existing);
    const event = await queryOrThrow(
      supabaseAdmin.from('events').update({
        name: input.name,
        description: eventDescriptionWithMeta(input.description, {
          ...currentMeta,
          latitude: input.latitude ?? null,
          longitude: input.longitude ?? null
        }),
        event_date: input.eventDate,
        location: input.location,
        cover_photo_url: input.coverPhotoUrl ?? null,
        capacity: input.capacity || null
      }).eq('id', req.params.id).eq('instructor_id', req.instructorId).select().single()
    );
    res.json({ event: toEvent(event) });
  } catch (error) {
    next(error);
  }
});

app.post('/events/:id/check-in', requireCommunityActor, async (req, res, next) => {
  try {
    if (req.userProfile.role !== 'student' || !req.studentProfileId) return res.status(403).json({ error: 'Apenas alunos podem confirmar presenca' });
    const event = await queryOrThrow(
      supabaseAdmin.from('events').select('id, instructor_id').eq('id', req.params.id).maybeSingle()
    );
    if (!event || event.instructor_id !== req.instructorId) return res.status(404).json({ error: 'Evento nao encontrado' });
    const fullEvent = await queryOrThrow(supabaseAdmin.from('events').select('description').eq('id', event.id).single());
    if (!['checkin', 'running'].includes(eventMeta(fullEvent).groupStatus)) return res.status(409).json({ error: 'O professor ainda nao iniciou o evento' });
    await queryOrThrow(
      supabaseAdmin.from('event_attendance').upsert({
        event_id: event.id,
        student_id: req.studentProfileId,
        attended: true,
        registered_at: new Date().toISOString()
      }, { onConflict: 'event_id,student_id' })
    );
    res.json({ ok: true });
  } catch (error) { next(error); }
});

app.post('/events/:id/open', requireInstructor, async (req, res, next) => {
  try {
    const event = await queryOrThrow(
      supabaseAdmin.from('events').select('id, description').eq('id', req.params.id).eq('instructor_id', req.instructorId).maybeSingle()
    );
    if (!event) return res.status(404).json({ error: 'Evento nao encontrado' });
    const meta = { ...eventMeta(event), groupStatus: 'checkin', openedAt: new Date().toISOString() };
    await queryOrThrow(supabaseAdmin.from('events').update({ description: eventDescriptionWithMeta(toEvent(event).description, meta) }).eq('id', event.id));
    res.json({ ok: true, groupStatus: 'checkin' });
  } catch (error) { next(error); }
});

app.post('/events/:id/start', requireInstructor, async (req, res, next) => {
  try {
    const event = await queryOrThrow(
      supabaseAdmin.from('events').select('id, description').eq('id', req.params.id).eq('instructor_id', req.instructorId).maybeSingle()
    );
    if (!event) return res.status(404).json({ error: 'Evento nao encontrado' });
    const meta = { ...eventMeta(event), groupStatus: 'running', startedAt: new Date().toISOString() };
    await queryOrThrow(supabaseAdmin.from('events').update({ description: eventDescriptionWithMeta(toEvent(event).description, meta) }).eq('id', event.id));
    res.json({ ok: true, groupStatus: 'running' });
  } catch (error) { next(error); }
});

app.post('/events/:id/finish', requireInstructor, async (req, res, next) => {
  try {
    const event = await queryOrThrow(
      supabaseAdmin.from('events').select('id, description').eq('id', req.params.id).eq('instructor_id', req.instructorId).maybeSingle()
    );
    if (!event) return res.status(404).json({ error: 'Evento nao encontrado' });
    const meta = { ...eventMeta(event), groupStatus: 'finished', finishedAt: new Date().toISOString() };
    await queryOrThrow(supabaseAdmin.from('events').update({ description: eventDescriptionWithMeta(toEvent(event).description, meta) }).eq('id', event.id));
    res.json({ ok: true, groupStatus: 'finished' });
  } catch (error) { next(error); }
});

app.post('/events/:id/results', requireCommunityActor, async (req, res, next) => {
  try {
    if (req.userProfile.role !== 'student' || !req.studentProfileId) return res.status(403).json({ error: 'Apenas alunos enviam resultados' });
    const input = workoutSessionSchema.parse(req.body);
    const event = await queryOrThrow(
      supabaseAdmin.from('events').select('id, name, instructor_id').eq('id', req.params.id).maybeSingle()
    );
    if (!event || event.instructor_id !== req.instructorId) return res.status(404).json({ error: 'Evento nao encontrado' });
    await queryOrThrow(
      supabaseAdmin.from('workout_sessions').insert({
        user_id: req.actorId,
        student_profile_id: req.studentProfileId,
        instructor_id: req.instructorId,
        routine_id: null,
        routine_name: `Evento: ${event.name}`,
        day_number: 1,
        cycle_step: 1,
        elapsed_ms: Math.round(input.elapsedMs),
        distance_meters: input.distanceMeters,
        pace_seconds_per_km: input.paceSecondsPerKm,
        status: 'completed',
        planned_steps: [...input.plannedSteps, { name: `event:${event.id}`, targetType: 'event', targetValue: 0, unit: '' }],
        route_points: input.routePoints,
        splits: input.splits,
        completed_at: new Date().toISOString()
      })
    );
    res.status(201).json({ ok: true });
  } catch (error) { next(error); }
});

app.delete('/events/:id', requireInstructor, async (req, res, next) => {
  try {
    await queryOrThrow(supabaseAdmin.from('events').delete().eq('id', req.params.id).eq('instructor_id', req.instructorId));
    res.status(204).end();
  } catch (error) {
    next(error);
  }
});

app.post('/announcements', requireInstructor, async (req, res, next) => {
  try {
    const input = announcementSchema.parse(req.body);
    const announcement = await queryOrThrow(
      supabaseAdmin.from('announcements').insert({
        instructor_id: req.instructorId,
        content: input.content,
        target_type: input.targetType,
        target_id: input.targetType === 'all' ? null : input.targetId
      }).select().single()
    );
    res.status(201).json({ announcement });
  } catch (error) {
    next(error);
  }
});

app.get('/me', requireAuthenticated, async (req, res) => {
  res.json({ user: authResponse(null, req.userProfile) });
});

app.put('/me', requireAuthenticated, async (req, res, next) => {
  try {
    const input = profileSchema.parse(req.body);
    const avatarUrl = await uploadProfilePhoto(req.actorId, input.photoBase64, input.photoMimeType);
    const update = {
      name: input.name
    };
    if (avatarUrl) update.avatar_url = avatarUrl;

    const profile = await queryOrThrow(
      supabaseAdmin
        .from('users')
        .update(update)
        .eq('id', req.actorId)
        .select('id, name, email, phone, role, avatar_url, is_active')
        .single()
    );

    await supabaseAdmin.auth.admin.updateUserById(req.actorId, {
      user_metadata: {
        name: profile.name,
        role: profile.role,
        avatar_url: profile.avatar_url ?? '',
        photoUrl: profile.avatar_url ?? ''
      }
    });

    res.json({ user: authResponse(null, profile) });
  } catch (error) {
    next(error);
  }
});

app.get('/settings', requireInstructor, async (req, res, next) => {
  try {
    const row = await queryOrThrow(
      supabaseAdmin
        .from('instructor_settings')
        .select('pix_key, notification_email, notification_push')
        .eq('instructor_id', req.instructorId)
        .maybeSingle()
    );
    res.json({
      settings: {
        pixKey: row?.pix_key ?? '',
        notificationEmail: row?.notification_email ?? true,
        notificationPush: row?.notification_push ?? true
      }
    });
  } catch (error) {
    next(error);
  }
});

app.put('/settings', requireInstructor, async (req, res, next) => {
  try {
    const input = settingsSchema.parse(req.body);
    const row = await queryOrThrow(
      supabaseAdmin
        .from('instructor_settings')
        .upsert({
          instructor_id: req.instructorId,
          pix_key: input.pixKey,
          notification_email: input.notificationEmail,
          notification_push: input.notificationPush,
          updated_at: new Date().toISOString()
        })
        .select()
        .single()
    );
    res.json({
      settings: {
        pixKey: row.pix_key ?? '',
        notificationEmail: row.notification_email ?? true,
        notificationPush: row.notification_push ?? true
      }
    });
  } catch (error) {
    next(error);
  }
});

app.get('/charges', requireInstructor, async (req, res, next) => {
  try {
    const rows = await queryOrThrow(
      supabaseAdmin
        .from('student_charges')
        .select('id, student_id, description, amount, due_date, paid_at, status')
        .eq('instructor_id', req.instructorId)
        .order('due_date', { ascending: false })
    );
    res.json({ charges: rows });
  } catch (error) {
    next(error);
  }
});

app.post('/charges', requireInstructor, async (req, res, next) => {
  try {
    const input = chargeSchema.parse(req.body);
    const row = await queryOrThrow(
      supabaseAdmin
        .from('student_charges')
        .insert({
          instructor_id: req.instructorId,
          student_id: input.studentId,
          description: input.description,
          amount: input.amount,
          due_date: input.dueDate,
          status: 'pending'
        })
        .select()
        .single()
    );
    res.status(201).json({ charge: row });
  } catch (error) {
    next(error);
  }
});

app.post('/upload-media', requireAuthenticated, async (req, res, next) => {
  try {
    const { base64, mimeType } = req.body;
    if (!base64 || !mimeType) return res.status(400).json({ error: 'base64 and mimeType required' });
    if (!String(mimeType).startsWith('image/')) return res.status(400).json({ error: 'Only images are supported' });

    const buffer = Buffer.from(base64, 'base64');
    const ext = (mimeType.split('/')[1] || 'jpg').replace('jpeg', 'jpg').replace('svg+xml', 'svg');
    const fileName = `${req.actorId}/${randomUUID()}.${ext}`;

    const { error: uploadError } = await supabaseAdmin.storage
      .from(SOCIAL_POSTS_BUCKET)
      .upload(fileName, buffer, { contentType: mimeType, upsert: false });

    if (uploadError) throw uploadError;

    const { data: { publicUrl } } = supabaseAdmin.storage
      .from(SOCIAL_POSTS_BUCKET)
      .getPublicUrl(fileName);

    res.json({ url: publicUrl });
  } catch (error) {
    next(error);
  }
});

app.use((err, req, res, next) => {
  const status = err instanceof z.ZodError
    ? 400
    : (err.statusCode || err.status || (err.code === '23505' ? 409 : 500));
  const code = err.code || (err instanceof z.ZodError ? 'invalid_payload' : 'server_error');
  const message = err instanceof z.ZodError
    ? 'Payload invalido'
    : (err.message || 'Erro inesperado');
  console.error({
    requestId: req.requestId,
    method: req.method,
    path: req.originalUrl,
    status,
    code,
    message,
    details: err.details,
    hint: err.hint
  });
  res.status(status).json({
    error: message,
    requestId: req.requestId,
    code,
    method: req.method,
    path: req.originalUrl,
    details: err instanceof z.ZodError ? err.flatten() : (err.details || err.hint || null)
  });
});

async function uploadImageToBucket(bucketName, ownerId, base64, mimeType) {
  if (!base64 || !mimeType) return '';
  if (!String(mimeType).startsWith('image/')) {
    throw Object.assign(new Error('Only images are supported'), { statusCode: 400 });
  }

  const buffer = Buffer.from(base64, 'base64');
  const ext = (mimeType.split('/')[1] || 'jpg').replace('jpeg', 'jpg').replace('svg+xml', 'svg');
  const fileName = `${ownerId}/${randomUUID()}.${ext}`;

  const { error: uploadError } = await supabaseAdmin.storage
    .from(bucketName)
    .upload(fileName, buffer, { contentType: mimeType, upsert: false });

  if (uploadError) throw uploadError;

  const { data: { publicUrl } } = supabaseAdmin.storage
    .from(bucketName)
    .getPublicUrl(fileName);

  return publicUrl;
}

async function uploadProfilePhoto(ownerId, base64, mimeType) {
  return uploadImageToBucket(PROFILE_PHOTOS_BUCKET, ownerId, base64, mimeType);
}

async function ensureStorageBucket() {
  const { data: buckets } = await supabaseAdmin.storage.listBuckets();
  const bucketOptions = {
    public: true,
    fileSizeLimit: 52428800,
    allowedMimeTypes: ['image/png', 'image/jpeg', 'image/webp', 'image/gif']
  };

  for (const bucketName of [SOCIAL_POSTS_BUCKET, PROFILE_PHOTOS_BUCKET]) {
    if (!buckets?.find((b) => b.name === bucketName)) {
      const { error } = await supabaseAdmin.storage.createBucket(bucketName, bucketOptions);
      if (error) console.error(`Could not create ${bucketName} bucket:`, error.message);
      else console.log(`Created storage bucket: ${bucketName}`);
      continue;
    }

    const { error } = await supabaseAdmin.storage.updateBucket(bucketName, bucketOptions);
    if (error) console.error(`Could not update ${bucketName} bucket:`, error.message);
  }
}

async function communityContextForProfile(profile) {
  if (profile.role === 'instructor') {
    return { instructorId: profile.id, actorId: profile.id, studentProfileId: null };
  }
  if (profile.role === 'student') {
    const studentProfile = await queryOrThrow(
      supabaseAdmin
        .from('student_profiles')
        .select('id, instructor_id')
        .eq('user_id', profile.id)
        .maybeSingle()
    );
    if (!studentProfile) throw Object.assign(new Error('Aluno nao vinculado a professor'), { statusCode: 403 });
    return { instructorId: studentProfile.instructor_id, actorId: profile.id, studentProfileId: studentProfile.id };
  }
  throw Object.assign(new Error('Perfil sem acesso a comunidade'), { statusCode: 403 });
}

async function requireCommunityActor(req, res, next) {
  try {
    const profile = await profileFromRequest(req);
    if (!profile || profile.is_active === false) return res.status(401).json({ error: 'Sessao invalida' });
    const context = await communityContextForProfile(profile);
    req.userProfile = profile;
    req.instructorId = context.instructorId;
    req.actorId = context.actorId;
    req.studentProfileId = context.studentProfileId;
    next();
  } catch (error) {
    next(error);
  }
}

const server = app.listen(env.PORT, async () => {
  console.log(`AgeGo API listening on http://localhost:${env.PORT}`);
  await ensureStorageBucket().catch((e) => console.error('Storage init error:', e));
});

server.on('error', (error) => {
  if (error.code === 'EADDRINUSE') {
    console.error(`Port ${env.PORT} is already in use. Stop the previous API process or set another PORT in .env.`);
    process.exit(1);
  }
  console.error(error);
  process.exit(1);
});
