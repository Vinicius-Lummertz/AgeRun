import 'dotenv/config';
import express from 'express';
import cookieParser from 'cookie-parser';
import QRCode from 'qrcode';
import { randomBytes, randomUUID } from 'node:crypto';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { createClient } from '@supabase/supabase-js';
import { z } from 'zod';

const __dirname = path.dirname(fileURLToPath(import.meta.url));

const requiredEnv = z.object({
  SUPABASE_URL: z.string().url(),
  SUPABASE_ANON_KEY: z.string().min(1),
  SUPABASE_SERVICE_ROLE_KEY: z.string().min(1),
  WEB_DASHBOARD_PORT: z.coerce.number().default(4444),
  API_BASE_URL: z.string().url().default('http://localhost:3333'),
  APP_GATE_KEY: z.string().optional().default('')
});

const env = requiredEnv.parse(process.env);

const supabaseAdmin = createClient(env.SUPABASE_URL, env.SUPABASE_SERVICE_ROLE_KEY, {
  auth: { autoRefreshToken: false, persistSession: false }
});

const supabaseAnon = createClient(env.SUPABASE_URL, env.SUPABASE_ANON_KEY, {
  auth: { autoRefreshToken: false, persistSession: false }
});

/**
 * Every write goes through the existing mobile API (port 3333) instead of duplicating its
 * business logic here. We impersonate the instructor via a magic-link token (service role can
 * mint one without their password) so the dashboard reuses the exact same validation, side
 * effects (access codes, payment cycle resets, etc.) as the app.
 */
async function mintInstructorApiSession(instructorId) {
  const profile = await queryOrThrow(supabaseAdmin.from('users').select('email').eq('id', instructorId).single());
  const { data, error } = await supabaseAdmin.auth.admin.generateLink({ type: 'magiclink', email: profile.email });
  if (error) throw error;
  const hashedToken = data?.properties?.hashed_token;
  if (!hashedToken) throw new Error('Nao foi possivel gerar sessao de API para o treinador.');
  const { data: verified, error: verifyError } = await supabaseAnon.auth.verifyOtp({ token_hash: hashedToken, type: 'magiclink' });
  if (verifyError || !verified.session) throw verifyError ?? new Error('Falha ao validar sessao do treinador.');
  return { accessToken: verified.session.access_token, refreshToken: verified.session.refresh_token };
}

async function apiRequest(req, method, apiPath, body) {
  const sessionId = req.cookies?.agego_web_session;
  const session = sessionId ? sessions.get(sessionId) : null;
  if (!session) throw Object.assign(new Error('Sessao expirada. Entre novamente.'), { statusCode: 401 });

  async function call(token) {
    return fetch(`${env.API_BASE_URL}${apiPath}`, {
      method,
      headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` },
      body: body !== undefined ? JSON.stringify(body) : undefined
    });
  }

  let res = await call(session.accessToken);
  if (res.status === 401 && session.refreshToken) {
    const refreshRes = await fetch(`${env.API_BASE_URL}/auth/refresh`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(env.APP_GATE_KEY ? { 'X-AgeGo-App-Key': env.APP_GATE_KEY } : {})
      },
      body: JSON.stringify({ refreshToken: session.refreshToken })
    });
    if (refreshRes.ok) {
      const refreshed = await refreshRes.json();
      session.accessToken = refreshed.accessToken;
      session.refreshToken = refreshed.refreshToken || session.refreshToken;
      res = await call(session.accessToken);
    }
  }

  const text = await res.text();
  const json = text ? JSON.parse(text) : null;
  if (!res.ok) {
    throw Object.assign(new Error(json?.error || json?.message || `Erro ${res.status} ao falar com a API`), { statusCode: res.status });
  }
  return json;
}

const PAIRING_TTL_MS = 5 * 60 * 1000;
const SESSION_TTL_MS = 12 * 60 * 60 * 1000;
/** In-memory web sessions: sessionId -> { instructorId, expiresAt }. Lost on restart, by design. */
const sessions = new Map();

async function queryOrThrow(builder) {
  const { data, error } = await builder;
  if (error) throw error;
  return data;
}

function computePaymentCycle(billingDay, lastPaymentAt, billingCycleStartedAt) {
  const day = Math.min(Math.max(Number(billingDay) || 5, 1), 28);
  const now = new Date();
  const cycleStart = billingCycleStartedAt ? new Date(billingCycleStartedAt) : null;
  let dueDate;
  if (cycleStart && !Number.isNaN(cycleStart.getTime())) {
    const cycleStartDate = new Date(cycleStart.getFullYear(), cycleStart.getMonth(), cycleStart.getDate());
    dueDate = new Date(cycleStart.getFullYear(), cycleStart.getMonth(), day);
    if (dueDate < cycleStartDate) dueDate = new Date(cycleStart.getFullYear(), cycleStart.getMonth() + 1, day);
    while (new Date(dueDate.getFullYear(), dueDate.getMonth() + 1, day) <= now) {
      dueDate = new Date(dueDate.getFullYear(), dueDate.getMonth() + 1, day);
    }
  } else {
    dueDate = new Date(now.getFullYear(), now.getMonth(), day);
    if (dueDate > now) dueDate = new Date(now.getFullYear(), now.getMonth() - 1, day);
  }
  const lastPayment = lastPaymentAt ? new Date(lastPaymentAt) : null;
  const paid = lastPayment
    ? lastPayment >= dueDate || (dueDate > now && cycleStart && lastPayment >= cycleStart)
    : false;
  if (paid) return { paymentStatus: 'paid', daysOverdue: 0 };
  if (dueDate > now) return { paymentStatus: 'pending', daysOverdue: 0 };
  const daysOverdue = Math.max(Math.floor((now.getTime() - dueDate.getTime()) / 86_400_000), 0);
  return { paymentStatus: 'pending', daysOverdue };
}

function purgeExpiredSessions() {
  const now = Date.now();
  for (const [id, session] of sessions) {
    if (session.expiresAt < now) sessions.delete(id);
  }
}

const app = express();
app.set('view engine', 'ejs');
app.set('views', path.join(__dirname, 'web', 'views'));
app.use(express.static(path.join(__dirname, 'web', 'public')));
app.use(express.urlencoded({ extended: true }));
app.use(express.json());
app.use(cookieParser());

function requireSession(req, res, next) {
  purgeExpiredSessions();
  const sessionId = req.cookies?.agego_web_session;
  const session = sessionId ? sessions.get(sessionId) : null;
  if (!session) return res.redirect('/login');
  session.expiresAt = Date.now() + SESSION_TTL_MS;
  req.instructorId = session.instructorId;
  req.webSession = session;
  next();
}

function setFlash(req, ok, message) {
  req.webSession.flash = { ok, message };
}

// --- Login / pairing -------------------------------------------------------

app.get('/login', (req, res) => {
  if (req.cookies?.agego_web_session && sessions.has(req.cookies.agego_web_session)) {
    return res.redirect('/');
  }
  res.render('login');
});

app.post('/api/pairing/start', async (req, res, next) => {
  try {
    const token = randomBytes(24).toString('hex');
    const expiresAt = new Date(Date.now() + PAIRING_TTL_MS).toISOString();
    await queryOrThrow(supabaseAdmin.from('web_pairing_tokens').insert({ token, expires_at: expiresAt }));
    const qrDataUrl = await QRCode.toDataURL(`agego://web-pair/${token}`, { margin: 1, width: 320, color: { dark: '#10071F', light: '#FFFFFF' } });
    res.json({ token, qrDataUrl, expiresInMs: PAIRING_TTL_MS });
  } catch (error) {
    next(error);
  }
});

app.get('/api/pairing/:token/status', async (req, res, next) => {
  try {
    const row = await queryOrThrow(
      supabaseAdmin
        .from('web_pairing_tokens')
        .select('instructor_id, approved_at, expires_at')
        .eq('token', req.params.token)
        .maybeSingle()
    );
    if (!row || new Date(row.expires_at) < new Date()) return res.json({ ready: false, expired: true });
    if (!row.approved_at || !row.instructor_id) return res.json({ ready: false });

    const apiSession = await mintInstructorApiSession(row.instructor_id);
    const sessionId = randomUUID();
    sessions.set(sessionId, {
      instructorId: row.instructor_id,
      accessToken: apiSession.accessToken,
      refreshToken: apiSession.refreshToken,
      expiresAt: Date.now() + SESSION_TTL_MS
    });
    await queryOrThrow(supabaseAdmin.from('web_pairing_tokens').delete().eq('token', req.params.token));
    res.cookie('agego_web_session', sessionId, {
      httpOnly: true,
      sameSite: 'lax',
      maxAge: SESSION_TTL_MS
    });
    res.json({ ready: true });
  } catch (error) {
    next(error);
  }
});

app.post('/logout', (req, res) => {
  const sessionId = req.cookies?.agego_web_session;
  if (sessionId) sessions.delete(sessionId);
  res.clearCookie('agego_web_session');
  res.redirect('/login');
});

app.use(requireSession);
app.use((req, res, next) => {
  res.locals.flash = req.webSession.flash || null;
  req.webSession.flash = null;
  next();
});

// --- Shared sidebar data -----------------------------------------------------

async function instructorProfile(instructorId) {
  return queryOrThrow(supabaseAdmin.from('users').select('id, name, email, avatar_url').eq('id', instructorId).maybeSingle());
}

// --- Overview ----------------------------------------------------------------

app.get('/', async (req, res, next) => {
  try {
    const instructorId = req.instructorId;
    const [profile, students, todayEvents, recentSessions, pendingProofs] = await Promise.all([
      instructorProfile(instructorId),
      queryOrThrow(
        supabaseAdmin
          .from('student_profiles')
          .select('id, status, billing_day, last_payment_at, billing_cycle_started_at, payment_proof_url, users!student_profiles_user_id_fkey(name)')
          .eq('instructor_id', instructorId)
      ),
      queryOrThrow(
        supabaseAdmin
          .from('events')
          .select('id, name, event_date, location')
          .eq('instructor_id', instructorId)
          .gte('event_date', new Date(new Date().setHours(0, 0, 0, 0)).toISOString())
          .lte('event_date', new Date(new Date().setHours(23, 59, 59, 999)).toISOString())
          .order('event_date', { ascending: true })
      ),
      queryOrThrow(
        supabaseAdmin
          .from('workout_sessions')
          .select('id, routine_name, distance_meters, elapsed_ms, completed_at, student_profile_id, student_profiles(users!student_profiles_user_id_fkey(name))')
          .eq('instructor_id', instructorId)
          .eq('status', 'completed')
          .order('completed_at', { ascending: false })
          .limit(8)
      ),
      queryOrThrow(
        supabaseAdmin
          .from('student_profiles')
          .select('id, payment_proof_url, users!student_profiles_user_id_fkey(name)')
          .eq('instructor_id', instructorId)
          .not('payment_proof_url', 'is', null)
      )
    ]);

    const withCycle = students.map((s) => ({
      ...s,
      cycle: computePaymentCycle(s.billing_day, s.last_payment_at, s.billing_cycle_started_at)
    }));
    const pendingCount = withCycle.filter((s) => s.cycle.paymentStatus === 'pending').length;
    const overdueCount = withCycle.filter((s) => s.cycle.daysOverdue > 4).length;

    res.render('overview', {
      profile,
      totalStudents: students.length,
      pendingCount,
      overdueCount,
      todayEvents,
      recentSessions,
      pendingProofs
    });
  } catch (error) {
    next(error);
  }
});

// --- Students ------------------------------------------------------------

app.get('/students', async (req, res, next) => {
  try {
    const rows = await queryOrThrow(
      supabaseAdmin
        .from('student_profiles')
        .select(`
          id, status, goal, billing_day, monthly_fee, last_payment_at, billing_cycle_started_at,
          payment_proof_url, created_at,
          users!student_profiles_user_id_fkey(name, email, phone, avatar_url),
          student_plans(is_active, plans(name))
        `)
        .eq('instructor_id', req.instructorId)
        .order('created_at', { ascending: false })
    );
    const students = rows.map((row) => ({
      ...row,
      planName: row.student_plans?.find((p) => p.is_active)?.plans?.name ?? 'Sem plano',
      cycle: computePaymentCycle(row.billing_day, row.last_payment_at, row.billing_cycle_started_at)
    }));
    res.render('students', { students, profile: await instructorProfile(req.instructorId) });
  } catch (error) {
    next(error);
  }
});

app.get('/students/:id', async (req, res, next) => {
  try {
    const student = await queryOrThrow(
      supabaseAdmin
        .from('student_profiles')
        .select(`
          id, status, goal, billing_day, monthly_fee, last_payment_at, billing_cycle_started_at,
          payment_proof_url, payment_proof_rejection_reason, created_at,
          users!student_profiles_user_id_fkey(name, email, phone, avatar_url),
          student_plans(is_active, frequency_days, plans(name))
        `)
        .eq('id', req.params.id)
        .eq('instructor_id', req.instructorId)
        .maybeSingle()
    );
    if (!student) return res.status(404).render('not_found', { profile: await instructorProfile(req.instructorId) });
    const sessions = await queryOrThrow(
      supabaseAdmin
        .from('workout_sessions')
        .select('id, routine_name, elapsed_ms, distance_meters, pace_seconds_per_km, completed_at, status')
        .eq('student_profile_id', req.params.id)
        .order('completed_at', { ascending: false })
        .limit(30)
    );
    res.render('student_detail', {
      student: { ...student, planName: student.student_plans?.find((p) => p.is_active)?.plans?.name ?? 'Sem plano', cycle: computePaymentCycle(student.billing_day, student.last_payment_at, student.billing_cycle_started_at) },
      sessions,
      profile: await instructorProfile(req.instructorId)
    });
  } catch (error) {
    next(error);
  }
});

app.post('/students', async (req, res) => {
  try {
    await apiRequest(req, 'POST', '/students', {
      name: req.body.name,
      email: req.body.email || '',
      phone: req.body.phone || '',
      routine: req.body.routine || '',
      fitnessLevel: req.body.fitnessLevel || 'beginner',
      status: req.body.status || 'active',
      monthlyFee: req.body.monthlyFee || ''
    });
    setFlash(req, true, 'Aluno criado. Veja o codigo de acesso na lista de alunos.');
  } catch (error) {
    setFlash(req, false, error.message);
  }
  res.redirect('/students');
});

app.post('/students/:id/edit', async (req, res) => {
  try {
    await apiRequest(req, 'PUT', `/students/${req.params.id}`, {
      name: req.body.name,
      phone: req.body.phone || '',
      routine: req.body.routine || '',
      fitnessLevel: req.body.fitnessLevel || 'beginner',
      status: req.body.status || 'active',
      monthlyFee: req.body.monthlyFee || '',
      // Preserva o dia de pagamento atual: o aluno e quem escolhe esse valor no app.
      billingDay: Number(req.body.billingDay) || 5
    });
    setFlash(req, true, 'Aluno atualizado.');
  } catch (error) {
    setFlash(req, false, error.message);
  }
  res.redirect(`/students/${req.params.id}`);
});

app.post('/students/:id/delete', async (req, res) => {
  try {
    await apiRequest(req, 'DELETE', `/students/${req.params.id}`);
    setFlash(req, true, 'Aluno removido.');
    return res.redirect('/students');
  } catch (error) {
    setFlash(req, false, error.message);
    return res.redirect(`/students/${req.params.id}`);
  }
});

app.post('/students/:id/approve-payment', async (req, res) => {
  try {
    await apiRequest(req, 'POST', `/students/${req.params.id}/approve-payment`);
    setFlash(req, true, 'Pagamento aprovado.');
  } catch (error) {
    setFlash(req, false, error.message);
  }
  res.redirect(req.body.redirectTo || `/students/${req.params.id}`);
});

app.post('/students/:id/reject-payment', async (req, res) => {
  try {
    await apiRequest(req, 'POST', `/students/${req.params.id}/reject-payment`, { reason: req.body.reason || 'Comprovante invalido' });
    setFlash(req, true, 'Comprovante rejeitado.');
  } catch (error) {
    setFlash(req, false, error.message);
  }
  res.redirect(req.body.redirectTo || `/students/${req.params.id}`);
});

// --- Workouts & routines ---------------------------------------------------

app.get('/workouts', async (req, res, next) => {
  try {
    const [workouts, routines] = await Promise.all([
      queryOrThrow(supabaseAdmin.from('workouts').select('id, name, description, status').eq('instructor_id', req.instructorId).order('created_at', { ascending: false })),
      queryOrThrow(supabaseAdmin.from('routines').select('id, name, description, status').eq('instructor_id', req.instructorId).order('name'))
    ]);
    res.render('workouts', { workouts, routines, profile: await instructorProfile(req.instructorId) });
  } catch (error) {
    next(error);
  }
});

app.post('/workouts', async (req, res) => {
  try {
    await apiRequest(req, 'POST', '/workouts', {
      name: req.body.name,
      description: req.body.description || '',
      status: req.body.status || 'active'
    });
    setFlash(req, true, 'Treino criado.');
  } catch (error) {
    setFlash(req, false, error.message);
  }
  res.redirect('/workouts');
});

app.post('/workouts/:id/edit', async (req, res) => {
  try {
    await apiRequest(req, 'PUT', `/workouts/${req.params.id}`, {
      name: req.body.name,
      description: req.body.description || '',
      status: req.body.status || 'active'
    });
    setFlash(req, true, 'Treino atualizado.');
  } catch (error) {
    setFlash(req, false, error.message);
  }
  res.redirect('/workouts');
});

app.post('/workouts/:id/delete', async (req, res) => {
  try {
    await apiRequest(req, 'DELETE', `/workouts/${req.params.id}`);
    setFlash(req, true, 'Treino excluido.');
  } catch (error) {
    setFlash(req, false, error.message);
  }
  res.redirect('/workouts');
});

app.post('/routines', async (req, res) => {
  try {
    await apiRequest(req, 'POST', '/routines', {
      name: req.body.name,
      description: req.body.description || '',
      status: req.body.status || 'active'
    });
    setFlash(req, true, 'Rotina criada.');
  } catch (error) {
    setFlash(req, false, error.message);
  }
  res.redirect('/workouts');
});

app.post('/routines/:id/edit', async (req, res) => {
  try {
    await apiRequest(req, 'PUT', `/routines/${req.params.id}`, {
      name: req.body.name,
      description: req.body.description || '',
      status: req.body.status || 'active'
    });
    setFlash(req, true, 'Rotina atualizada.');
  } catch (error) {
    setFlash(req, false, error.message);
  }
  res.redirect('/workouts');
});

app.post('/routines/:id/delete', async (req, res) => {
  try {
    await apiRequest(req, 'DELETE', `/routines/${req.params.id}`);
    setFlash(req, true, 'Rotina excluida.');
  } catch (error) {
    setFlash(req, false, error.message);
  }
  res.redirect('/workouts');
});

// --- Events ----------------------------------------------------------------

app.get('/events', async (req, res, next) => {
  try {
    const events = await queryOrThrow(
      supabaseAdmin
        .from('events')
        .select('id, name, event_date, location, event_attendance(attended)')
        .eq('instructor_id', req.instructorId)
        .order('event_date', { ascending: false })
    );
    const withCounts = events.map((event) => ({
      ...event,
      attendeeCount: (event.event_attendance ?? []).filter((a) => a.attended).length
    }));
    res.render('events', { events: withCounts, profile: await instructorProfile(req.instructorId) });
  } catch (error) {
    next(error);
  }
});

app.get('/events/:id', async (req, res, next) => {
  try {
    const event = await queryOrThrow(
      supabaseAdmin
        .from('events')
        .select(`
          id, name, description, event_date, location,
          event_attendance(attended, student_profiles(id, users!student_profiles_user_id_fkey(name, avatar_url)))
        `)
        .eq('id', req.params.id)
        .eq('instructor_id', req.instructorId)
        .maybeSingle()
    );
    if (!event) return res.status(404).render('not_found', { profile: await instructorProfile(req.instructorId) });
    const attendees = (event.event_attendance ?? [])
      .filter((a) => a.attended)
      .map((a) => ({ name: a.student_profiles?.users?.name ?? 'Aluno', avatarUrl: a.student_profiles?.users?.avatar_url ?? '' }));
    res.render('event_detail', { event, attendees, profile: await instructorProfile(req.instructorId) });
  } catch (error) {
    next(error);
  }
});

app.post('/events', async (req, res) => {
  try {
    await apiRequest(req, 'POST', '/events', {
      name: req.body.name,
      description: req.body.description || '',
      eventDate: req.body.eventDate,
      location: req.body.location || ''
    });
    setFlash(req, true, 'Evento criado.');
  } catch (error) {
    setFlash(req, false, error.message);
  }
  res.redirect('/events');
});

app.post('/events/:id/edit', async (req, res) => {
  try {
    await apiRequest(req, 'PUT', `/events/${req.params.id}`, {
      name: req.body.name,
      description: req.body.description || '',
      eventDate: req.body.eventDate,
      location: req.body.location || ''
    });
    setFlash(req, true, 'Evento atualizado.');
  } catch (error) {
    setFlash(req, false, error.message);
  }
  res.redirect(`/events/${req.params.id}`);
});

app.post('/events/:id/delete', async (req, res) => {
  try {
    await apiRequest(req, 'DELETE', `/events/${req.params.id}`);
    setFlash(req, true, 'Evento excluido.');
    return res.redirect('/events');
  } catch (error) {
    setFlash(req, false, error.message);
    return res.redirect(`/events/${req.params.id}`);
  }
});

// --- Challenges --------------------------------------------------------------

app.get('/challenges', async (req, res, next) => {
  try {
    const challenges = await queryOrThrow(
      supabaseAdmin
        .from('challenges')
        .select('id, name, description, target_type, target_value, created_at')
        .eq('instructor_id', req.instructorId)
        .order('created_at', { ascending: false })
    );
    let sessionsByChallenge = new Map();
    if (challenges.length > 0) {
      const sessions = await queryOrThrow(
        supabaseAdmin
          .from('workout_sessions')
          .select('challenge_id, student_profile_id, distance_meters, elapsed_ms')
          .in('challenge_id', challenges.map((c) => c.id))
          .eq('status', 'completed')
      );
      sessionsByChallenge = sessions.reduce((map, row) => {
        const list = map.get(row.challenge_id) ?? [];
        list.push(row);
        map.set(row.challenge_id, list);
        return map;
      }, new Map());
    }
    const withStats = challenges.map((challenge) => {
      const rows = sessionsByChallenge.get(challenge.id) ?? [];
      return {
        ...challenge,
        completions: new Set(rows.map((r) => r.student_profile_id)).size,
        totalDistanceMeters: rows.reduce((sum, r) => sum + Number(r.distance_meters || 0), 0),
        totalElapsedMs: rows.reduce((sum, r) => sum + Number(r.elapsed_ms || 0), 0)
      };
    });
    res.render('challenges', { challenges: withStats, profile: await instructorProfile(req.instructorId) });
  } catch (error) {
    next(error);
  }
});

app.post('/challenges', async (req, res) => {
  try {
    await apiRequest(req, 'POST', '/challenges', {
      name: req.body.name,
      description: req.body.description || '',
      targetType: req.body.targetType || 'distance',
      targetValue: Number(req.body.targetValue) || 0
    });
    setFlash(req, true, 'Desafio criado.');
  } catch (error) {
    setFlash(req, false, error.message);
  }
  res.redirect('/challenges');
});

app.post('/challenges/:id/edit', async (req, res) => {
  try {
    await apiRequest(req, 'PUT', `/challenges/${req.params.id}`, {
      name: req.body.name,
      description: req.body.description || '',
      targetType: req.body.targetType || 'distance',
      targetValue: Number(req.body.targetValue) || 0
    });
    setFlash(req, true, 'Desafio atualizado.');
  } catch (error) {
    setFlash(req, false, error.message);
  }
  res.redirect('/challenges');
});

app.post('/challenges/:id/delete', async (req, res) => {
  try {
    await apiRequest(req, 'DELETE', `/challenges/${req.params.id}`);
    setFlash(req, true, 'Desafio excluido.');
  } catch (error) {
    setFlash(req, false, error.message);
  }
  res.redirect('/challenges');
});

app.post('/announcements', async (req, res) => {
  try {
    await apiRequest(req, 'POST', '/announcements', { content: req.body.content, targetType: 'all' });
    setFlash(req, true, 'Aviso publicado.');
  } catch (error) {
    setFlash(req, false, error.message);
  }
  res.redirect('/');
});

// --- Finance -----------------------------------------------------------------

app.get('/finance', async (req, res, next) => {
  try {
    const [students, charges, settings] = await Promise.all([
      queryOrThrow(
        supabaseAdmin
          .from('student_profiles')
          .select('id, billing_day, monthly_fee, last_payment_at, billing_cycle_started_at, payment_proof_url, users!student_profiles_user_id_fkey(name)')
          .eq('instructor_id', req.instructorId)
      ),
      queryOrThrow(
        supabaseAdmin
          .from('student_charges')
          .select('id, student_id, description, amount, due_date, paid_at, status')
          .eq('instructor_id', req.instructorId)
          .order('due_date', { ascending: false })
          .limit(50)
      ),
      queryOrThrow(supabaseAdmin.from('instructor_settings').select('pix_key').eq('instructor_id', req.instructorId).maybeSingle())
    ]);
    const withCycle = students
      .map((s) => ({ ...s, cycle: computePaymentCycle(s.billing_day, s.last_payment_at, s.billing_cycle_started_at) }))
      .sort((a, b) => b.cycle.daysOverdue - a.cycle.daysOverdue);
    res.render('finance', { students: withCycle, charges, pixKey: settings?.pix_key ?? '', profile: await instructorProfile(req.instructorId) });
  } catch (error) {
    next(error);
  }
});

app.post('/finance/charges', async (req, res) => {
  try {
    await apiRequest(req, 'POST', '/charges', {
      studentId: req.body.studentId,
      description: req.body.description || 'Mensalidade',
      amount: Number(req.body.amount) || 0,
      dueDate: req.body.dueDate
    });
    setFlash(req, true, 'Cobranca criada.');
  } catch (error) {
    setFlash(req, false, error.message);
  }
  res.redirect('/finance');
});

// --- Settings ------------------------------------------------------------

app.get('/settings', async (req, res, next) => {
  try {
    const [profile, settings] = await Promise.all([
      instructorProfile(req.instructorId),
      queryOrThrow(supabaseAdmin.from('instructor_settings').select('pix_key, notification_email, notification_push').eq('instructor_id', req.instructorId).maybeSingle())
    ]);
    res.render('settings', { profile, settings: settings ?? {} });
  } catch (error) {
    next(error);
  }
});

app.post('/settings', async (req, res) => {
  try {
    await apiRequest(req, 'PUT', '/settings', {
      pixKey: req.body.pixKey || '',
      notificationEmail: req.body.notificationEmail === 'on',
      notificationPush: req.body.notificationPush === 'on'
    });
    setFlash(req, true, 'Configuracoes salvas.');
  } catch (error) {
    setFlash(req, false, error.message);
  }
  res.redirect('/settings');
});

app.use((error, req, res, _next) => {
  console.error(error);
  res.status(500).send('Erro interno. Veja os logs do servidor.');
});

app.listen(env.WEB_DASHBOARD_PORT, () => {
  console.log(`AgeGo web dashboard rodando em http://localhost:${env.WEB_DASHBOARD_PORT}`);
});
