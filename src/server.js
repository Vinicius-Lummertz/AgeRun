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
  PORT: z.coerce.number().default(3333)
});

const env = requiredEnv.parse(process.env);
const app = express();

app.use(helmet());
app.use(cors());
app.use(express.json({ limit: '5mb' }));
app.use(morgan('dev'));

const supabaseAdmin = createClient(env.SUPABASE_URL, env.SUPABASE_SERVICE_ROLE_KEY, {
  auth: { autoRefreshToken: false, persistSession: false }
});

const supabaseAnon = createClient(env.SUPABASE_URL, env.SUPABASE_ANON_KEY, {
  auth: { autoRefreshToken: false, persistSession: false }
});

const statusSchema = z.enum(['active', 'draft', 'inactive']).default('active');
const studentStatusSchema = z.enum(['active', 'pending_payment', 'inactive']).default('active');
const postTypeSchema = z.enum(['post', 'poll', 'challenge']).default('post');
const targetSchema = z.enum(['all', 'groups', 'events', 'students']).default('groups');

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
  planId: z.string().uuid().optional()
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
  targetGroups: z.array(z.string().uuid()).optional().default([]),
  capacity: z.number().int().positive().optional()
});

const announcementSchema = z.object({
  content: z.string().trim().min(2),
  targetType: z.enum(['all', 'group', 'plan']).default('all'),
  targetId: z.string().uuid().nullable().optional()
});

const postSchema = z.object({
  type: postTypeSchema,
  title: z.string().trim().optional().default(''),
  content: z.string().trim().min(1),
  target: targetSchema,
  linkedWorkoutId: z.string().uuid().nullable().optional(),
  groupIds: z.array(z.string().uuid()).optional().default([]),
  studentIds: z.array(z.string().uuid()).optional().default([]),
  eventIds: z.array(z.string().uuid()).optional().default([]),
  pollOptions: z.array(z.string().trim().min(1)).optional().default([]),
  mediaLabel: z.string().trim().nullable().optional(),
  gifLabel: z.string().trim().nullable().optional(),
  generatedImagePrompt: z.string().trim().nullable().optional(),
  scheduledAt: z.string().trim().nullable().optional(),
  location: z.string().trim().nullable().optional(),
  contentWarning: z.string().trim().nullable().optional()
});

const commentSchema = z.object({
  content: z.string().trim().min(1),
  parentCommentId: z.string().uuid().nullable().optional()
});

function randomPassword() {
  return `AgeGo${randomUUID().slice(0, 8)}A1`;
}

function toCamelStudent(row) {
  return {
    id: row.id,
    name: row.name,
    email: row.email ?? '',
    phone: row.phone ?? '',
    routine: row.routine ?? '',
    plan_name: row.plan_name ?? row.plan ?? 'Sem plano',
    status: row.status ?? 'active'
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
  return {
    id: row.id,
    name: row.name,
    description: row.description ?? '',
    event_date: row.event_date,
    location: row.location ?? ''
  };
}

function toPost(row, comments = []) {
  return {
    id: row.id,
    type: String(row.type ?? 'post').toUpperCase(),
    title: row.title ?? '',
    content: row.content ?? '',
    target: row.target ?? 'groups',
    authorName: row.author_name ?? 'AgeGo',
    linkedWorkoutId: row.linked_workout_id,
    pollOptions: normalizePollOptions(row.poll_options),
    commentThreads: comments,
    mediaLabel: row.media_label,
    gifLabel: row.gif_label,
    generatedImagePrompt: row.generated_image_prompt,
    scheduledAt: row.scheduled_at,
    location: row.location,
    contentWarning: row.content_warning,
    liked: Boolean(row.liked),
    likes: row.likes ?? 0,
    comments: row.comments ?? 0,
    shares: row.shares ?? 0
  };
}

function normalizePollOptions(value) {
  if (!Array.isArray(value)) return [];
  return value
    .sort((a, b) => (a.order_index ?? 0) - (b.order_index ?? 0))
    .map((option) => option.text ?? option.option_text ?? option)
    .filter(Boolean);
}

function nestComments(rows) {
  const byId = new Map();
  const roots = [];

  rows.forEach((row) => {
    byId.set(row.id, {
      id: row.id,
      authorName: row.author_name ?? 'AgeGo',
      content: row.content ?? '',
      liked: Boolean(row.liked),
      likes: row.likes ?? 0,
      replies: []
    });
  });

  rows.forEach((row) => {
    const comment = byId.get(row.id);
    const parent = row.parent_comment_id ? byId.get(row.parent_comment_id) : null;
    if (parent) parent.replies.push(comment);
    else roots.push(comment);
  });

  return roots;
}

async function authUserFromRequest(req) {
  const authHeader = req.headers.authorization || '';
  const token = authHeader.startsWith('Bearer ') ? authHeader.slice(7) : null;
  if (!token) return null;

  const { data, error } = await supabaseAnon.auth.getUser(token);
  if (error || !data.user) return null;
  return data.user;
}

async function ensureInstructor(req) {
  const authUser = await authUserFromRequest(req);
  if (authUser) {
    const { data } = await supabaseAdmin
      .from('users')
      .select('id, role')
      .eq('id', authUser.id)
      .maybeSingle();
    if (data?.role === 'instructor') return data.id;
  }

  const { data: existingInstructor, error: findError } = await supabaseAdmin
    .from('users')
    .select('id')
    .eq('role', 'instructor')
    .order('created_at', { ascending: true })
    .limit(1)
    .maybeSingle();

  if (findError) throw findError;
  if (existingInstructor?.id) return existingInstructor.id;

  const email = 'admin@agego.local';
  const { data: created, error: createError } = await supabaseAdmin.auth.admin.createUser({
    email,
    password: 'Admin@123456',
    email_confirm: true,
    user_metadata: { name: 'Admin AgeGo', role: 'instructor' }
  });

  if (createError) throw createError;
  const id = created.user.id;
  const { error: upsertError } = await supabaseAdmin.from('users').upsert({
    id,
    email,
    name: 'Admin AgeGo',
    role: 'instructor',
    is_active: true
  });
  if (upsertError) throw upsertError;
  return id;
}

async function currentUserId(req, instructorId) {
  const authUser = await authUserFromRequest(req);
  return authUser?.id ?? instructorId;
}

async function requireInstructor(req, res, next) {
  try {
    req.instructorId = await ensureInstructor(req);
    req.actorId = await currentUserId(req, req.instructorId);
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

async function loadDashboard(instructorId) {
  const [students, workouts, announcements, events, groups, routines, posts] = await Promise.all([
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
    queryOrThrow(
      supabaseAdmin
        .from('events')
        .select('id, name, description, event_date, location')
        .eq('instructor_id', instructorId)
        .order('event_date', { ascending: true })
    ),
    loadGroups(instructorId),
    queryOrThrow(
      supabaseAdmin
        .from('routines')
        .select('id, name, description, status')
        .eq('instructor_id', instructorId)
        .order('name')
    ),
    loadPosts(instructorId, instructorId)
  ]);

  return {
    students: students.map(toCamelStudent),
    workouts: workouts.map(toCamelWorkout),
    announcements,
    events: events.map(toEvent),
    groups: groups.map(toDirectory),
    routines: routines.map(toDirectory),
    communityPosts: posts
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
        users!student_profiles_user_id_fkey(name, email, phone),
        student_plans(is_active, plans(name))
      `)
      .eq('instructor_id', instructorId)
      .order('created_at', { ascending: false })
  );

  return rows.map((row) => {
    const activePlan = row.student_plans?.find((item) => item.is_active);
    return toCamelStudent({
      id: row.id,
      name: row.users?.name ?? 'Aluno',
      email: row.users?.email ?? '',
      phone: row.users?.phone ?? '',
      routine: row.goal ?? '',
      plan_name: activePlan?.plans?.name ?? 'Sem plano',
      status: row.status
    });
  });
}

async function loadGroups(instructorId) {
  const rows = await queryOrThrow(
    supabaseAdmin
      .from('groups')
      .select('id, name, description, is_active, group_members(student_id)')
      .eq('instructor_id', instructorId)
      .order('name')
  );

  return rows.map((row) => toDirectory({
    ...row,
    status: row.is_active === false ? 'inactive' : 'active',
    student_ids: row.group_members?.map((member) => member.student_id) ?? []
  }));
}

async function loadPosts(instructorId, actorId = instructorId) {
  const posts = await queryOrThrow(
    supabaseAdmin
      .from('community_posts')
      .select(`
        id,
        type,
        title,
        content,
        target,
        author_id,
        instructor_id,
        linked_workout_id,
        media_label,
        gif_label,
        generated_image_prompt,
        scheduled_at,
        location,
        content_warning,
        published_at,
        users!community_posts_author_id_fkey(name)
      `)
      .eq('instructor_id', instructorId)
      .eq('is_active', true)
      .order('published_at', { ascending: false })
  );

  if (posts.length === 0) return [];
  const postIds = posts.map((post) => post.id);

  const [likes, shares, options, comments] = await Promise.all([
    queryOrThrow(supabaseAdmin.from('community_post_likes').select('post_id, user_id').in('post_id', postIds)),
    queryOrThrow(supabaseAdmin.from('community_post_shares').select('post_id').in('post_id', postIds)),
    queryOrThrow(supabaseAdmin.from('community_poll_options').select('post_id, option_text, order_index').in('post_id', postIds)),
    queryOrThrow(
      supabaseAdmin
        .from('community_post_comments')
        .select('id, post_id, parent_comment_id, author_id, content, users!community_post_comments_author_id_fkey(name)')
        .in('post_id', postIds)
        .eq('is_active', true)
        .order('created_at')
    ),
  ]);

  const likesByPost = countBy(likes, 'post_id');
  const sharesByPost = countBy(shares, 'post_id');
  const optionsByPost = groupBy(options, 'post_id');
  const commentIds = comments.map((comment) => comment.id);
  const commentLikes = commentIds.length
    ? await queryOrThrow(supabaseAdmin.from('community_comment_likes').select('comment_id, user_id').in('comment_id', commentIds))
    : [];
  const commentLikesByComment = countBy(commentLikes, 'comment_id');
  const likedCommentIds = new Set(commentLikes.filter((like) => like.user_id === actorId).map((like) => like.comment_id));

  const likedPostIds = new Set(likes.filter((like) => like.user_id === actorId).map((like) => like.post_id));
  const commentsByPost = groupBy(comments.map((comment) => ({
    ...comment,
    author_name: comment.users?.name ?? 'AgeGo',
    likes: commentLikesByComment.get(comment.id) ?? 0,
    liked: likedCommentIds.has(comment.id)
  })), 'post_id');

  return posts.map((post) => {
    const postComments = commentsByPost.get(post.id) ?? [];
    return toPost({
      ...post,
      author_name: post.users?.name ?? 'AgeGo',
      likes: likesByPost.get(post.id) ?? 0,
      comments: postComments.length,
      shares: sharesByPost.get(post.id) ?? 0,
      liked: likedPostIds.has(post.id),
      poll_options: (optionsByPost.get(post.id) ?? []).map((option) => ({
        text: option.option_text,
        order_index: option.order_index
      }))
    }, nestComments(postComments));
  });
}

function countBy(rows, key) {
  const counts = new Map();
  rows.forEach((row) => counts.set(row[key], (counts.get(row[key]) ?? 0) + 1));
  return counts;
}

function groupBy(rows, key) {
  const groups = new Map();
  rows.forEach((row) => {
    const group = groups.get(row[key]) ?? [];
    group.push(row);
    groups.set(row[key], group);
  });
  return groups;
}

async function setGroupMembers(groupId, studentIds) {
  await queryOrThrow(supabaseAdmin.rpc('set_group_members', {
    p_group_id: groupId,
    p_student_ids: studentIds
  }));
}

app.get('/health', (req, res) => {
  res.json({ ok: true, service: 'agego-api' });
});

app.get('/dashboard', requireInstructor, async (req, res, next) => {
  try {
    res.json(await loadDashboard(req.instructorId));
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
    const password = input.password || randomPassword();
    const email = input.email || `aluno-${Date.now()}@agego.local`;

    const { data: createdUser, error: createUserError } = await supabaseAdmin.auth.admin.createUser({
      email,
      password,
      email_confirm: true,
      user_metadata: { name: input.name, role: 'student' }
    });
    if (createUserError || !createdUser.user) throw createUserError;

    const userId = createdUser.user.id;
    await queryOrThrow(supabaseAdmin.from('users').upsert({
      id: userId,
      email,
      name: input.name,
      phone: input.phone,
      role: 'student',
      is_active: true
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
          goal: input.routine
        })
        .select()
        .single()
    );

    res.status(201).json({ student, temporaryPassword: input.password ? undefined : password });
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
      phone: input.phone
    }).eq('id', profile.user_id));
    const student = await queryOrThrow(
      supabaseAdmin.from('student_profiles').update({
        status: input.status,
        goal: input.routine,
        fitness_level: input.fitnessLevel
      }).eq('id', req.params.id).select().single()
    );
    res.json({ student });
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

app.get('/groups', requireInstructor, async (req, res, next) => {
  try {
    const rows = await queryOrThrow(supabaseAdmin.from('v_agego_groups').select('*').eq('instructor_id', req.instructorId).order('name'));
    res.json({ groups: rows.map(toDirectory) });
  } catch (error) {
    next(error);
  }
});

app.post('/groups', requireInstructor, async (req, res, next) => {
  try {
    const input = directorySchema.parse(req.body);
    const group = await queryOrThrow(
      supabaseAdmin.from('groups').insert({
        instructor_id: req.instructorId,
        name: input.name,
        description: input.description,
        is_active: input.status !== 'inactive'
      }).select().single()
    );
    await setGroupMembers(group.id, input.studentIds);
    res.status(201).json({ group: toDirectory({ ...group, status: input.status, student_ids: input.studentIds }) });
  } catch (error) {
    next(error);
  }
});

app.put('/groups/:id', requireInstructor, async (req, res, next) => {
  try {
    const input = directorySchema.parse(req.body);
    const group = await queryOrThrow(
      supabaseAdmin.from('groups').update({
        name: input.name,
        description: input.description,
        is_active: input.status !== 'inactive'
      }).eq('id', req.params.id).eq('instructor_id', req.instructorId).select().single()
    );
    await setGroupMembers(group.id, input.studentIds);
    res.json({ group: toDirectory({ ...group, status: input.status, student_ids: input.studentIds }) });
  } catch (error) {
    next(error);
  }
});

app.delete('/groups/:id', requireInstructor, async (req, res, next) => {
  try {
    await queryOrThrow(supabaseAdmin.from('groups').delete().eq('id', req.params.id).eq('instructor_id', req.instructorId));
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

app.get('/events', requireInstructor, async (req, res, next) => {
  try {
    const rows = await queryOrThrow(supabaseAdmin.from('events').select('*').eq('instructor_id', req.instructorId).order('event_date'));
    res.json({ events: rows.map(toEvent) });
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
        description: input.description,
        event_date: input.eventDate,
        location: input.location,
        target_groups: input.targetGroups,
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
    const event = await queryOrThrow(
      supabaseAdmin.from('events').update({
        name: input.name,
        description: input.description,
        event_date: input.eventDate,
        location: input.location,
        target_groups: input.targetGroups,
        capacity: input.capacity || null
      }).eq('id', req.params.id).eq('instructor_id', req.instructorId).select().single()
    );
    res.json({ event: toEvent(event) });
  } catch (error) {
    next(error);
  }
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

app.get('/posts', requireInstructor, async (req, res, next) => {
  try {
    res.json({ posts: await loadPosts(req.instructorId, req.actorId) });
  } catch (error) {
    next(error);
  }
});

app.post('/posts', requireInstructor, async (req, res, next) => {
  try {
    const input = postSchema.parse(req.body);
    const post = await queryOrThrow(
      supabaseAdmin.from('community_posts').insert({
        author_id: req.actorId,
        instructor_id: req.instructorId,
        type: input.type,
        title: input.title,
        content: input.content,
        target: input.target,
        linked_workout_id: input.linkedWorkoutId || null,
        media_label: input.mediaLabel || null,
        gif_label: input.gifLabel || null,
        generated_image_prompt: input.generatedImagePrompt || null,
        scheduled_at: input.scheduledAt || null,
        location: input.location || null,
        content_warning: input.contentWarning || null,
        is_active: true
      }).select().single()
    );

    await Promise.all([
      input.groupIds.length ? queryOrThrow(supabaseAdmin.from('community_post_groups').insert(input.groupIds.map((groupId) => ({ post_id: post.id, group_id: groupId })))) : null,
      input.studentIds.length ? queryOrThrow(supabaseAdmin.from('community_post_students').insert(input.studentIds.map((studentId) => ({ post_id: post.id, student_id: studentId })))) : null,
      input.eventIds.length ? queryOrThrow(supabaseAdmin.from('community_post_events').insert(input.eventIds.map((eventId) => ({ post_id: post.id, event_id: eventId })))) : null,
      input.pollOptions.length ? queryOrThrow(supabaseAdmin.from('community_poll_options').insert(input.pollOptions.map((optionText, index) => ({ post_id: post.id, option_text: optionText, order_index: index })))) : null
    ]);

    res.status(201).json({ post: (await loadPosts(req.instructorId, req.actorId)).find((item) => item.id === post.id) });
  } catch (error) {
    next(error);
  }
});

app.post('/posts/:id/like', requireInstructor, async (req, res, next) => {
  try {
    const existing = await queryOrThrow(
      supabaseAdmin.from('community_post_likes').select('post_id').eq('post_id', req.params.id).eq('user_id', req.actorId).maybeSingle()
    );
    if (existing) {
      await queryOrThrow(supabaseAdmin.from('community_post_likes').delete().eq('post_id', req.params.id).eq('user_id', req.actorId));
    } else {
      await queryOrThrow(supabaseAdmin.from('community_post_likes').insert({ post_id: req.params.id, user_id: req.actorId }));
    }
    res.json({ liked: !existing });
  } catch (error) {
    next(error);
  }
});

app.post('/posts/:id/share', requireInstructor, async (req, res, next) => {
  try {
    await queryOrThrow(supabaseAdmin.from('community_post_shares').insert({ post_id: req.params.id, user_id: req.actorId }));
    res.status(201).json({ ok: true });
  } catch (error) {
    next(error);
  }
});

app.post('/posts/:id/comments', requireInstructor, async (req, res, next) => {
  try {
    const input = commentSchema.parse(req.body);
    const comment = await queryOrThrow(
      supabaseAdmin.from('community_post_comments').insert({
        post_id: req.params.id,
        parent_comment_id: input.parentCommentId || null,
        author_id: req.actorId,
        content: input.content
      }).select().single()
    );
    res.status(201).json({ comment });
  } catch (error) {
    next(error);
  }
});

app.post('/comments/:id/like', requireInstructor, async (req, res, next) => {
  try {
    const existing = await queryOrThrow(
      supabaseAdmin.from('community_comment_likes').select('comment_id').eq('comment_id', req.params.id).eq('user_id', req.actorId).maybeSingle()
    );
    if (existing) {
      await queryOrThrow(supabaseAdmin.from('community_comment_likes').delete().eq('comment_id', req.params.id).eq('user_id', req.actorId));
    } else {
      await queryOrThrow(supabaseAdmin.from('community_comment_likes').insert({ comment_id: req.params.id, user_id: req.actorId }));
    }
    res.json({ liked: !existing });
  } catch (error) {
    next(error);
  }
});

app.post('/poll-options/:id/vote', requireInstructor, async (req, res, next) => {
  try {
    const option = await queryOrThrow(supabaseAdmin.from('community_poll_options').select('id, post_id').eq('id', req.params.id).single());
    await queryOrThrow(supabaseAdmin.from('community_poll_votes').upsert({
      post_id: option.post_id,
      option_id: option.id,
      user_id: req.actorId
    }, { onConflict: 'post_id,user_id' }));
    res.json({ ok: true });
  } catch (error) {
    next(error);
  }
});

app.use((err, req, res, next) => {
  const status = err instanceof z.ZodError ? 400 : 500;
  console.error(err);
  res.status(status).json({
    error: status === 400 ? 'Invalid payload' : 'Internal server error',
    details: err instanceof z.ZodError ? err.flatten() : err.message
  });
});

const server = app.listen(env.PORT, () => {
  console.log(`AgeGo API listening on http://localhost:${env.PORT}`);
});

server.on('error', (error) => {
  if (error.code === 'EADDRINUSE') {
    console.error(`Port ${env.PORT} is already in use. Stop the previous API process or set another PORT in .env.`);
    process.exit(1);
  }
  console.error(error);
  process.exit(1);
});
