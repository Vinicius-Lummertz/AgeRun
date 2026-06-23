-- AGE GO - Supabase full schema
-- Idempotent setup for Auth profiles, management CRUD, groups, community posts,
-- comments, poll/challenge support and separated storage buckets.

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'user_role') THEN
    CREATE TYPE public.user_role AS ENUM ('instructor', 'student');
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'student_status') THEN
    CREATE TYPE public.student_status AS ENUM ('active', 'pending_payment', 'inactive');
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'fitness_level') THEN
    CREATE TYPE public.fitness_level AS ENUM ('beginner', 'intermediate', 'advanced');
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'workout_status') THEN
    CREATE TYPE public.workout_status AS ENUM ('active', 'draft', 'inactive');
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'announcement_target') THEN
    CREATE TYPE public.announcement_target AS ENUM ('all', 'group', 'plan');
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'notification_type') THEN
    CREATE TYPE public.notification_type AS ENUM (
      'new_workout',
      'workout_reminder',
      'new_announcement',
      'payment_pending',
      'student_inactive',
      'new_event',
      'new_post',
      'new_comment'
    );
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'community_post_type') THEN
    CREATE TYPE public.community_post_type AS ENUM ('post', 'poll', 'challenge');
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'community_target_type') THEN
    CREATE TYPE public.community_target_type AS ENUM ('all', 'groups', 'events', 'students');
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'media_kind') THEN
    CREATE TYPE public.media_kind AS ENUM ('image', 'video', 'gif', 'generated_image', 'document', 'other');
  END IF;
END $$;

ALTER TYPE public.notification_type ADD VALUE IF NOT EXISTS 'new_post';
ALTER TYPE public.notification_type ADD VALUE IF NOT EXISTS 'new_comment';

CREATE OR REPLACE FUNCTION public.update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TABLE IF NOT EXISTS public.users (
  id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
  email VARCHAR(255) UNIQUE NOT NULL,
  name VARCHAR(255) NOT NULL,
  phone VARCHAR(20),
  avatar_url VARCHAR(500),
  role public.user_role NOT NULL,
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.instructor_profiles (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID UNIQUE NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
  bio TEXT,
  specialty VARCHAR(255),
  instagram VARCHAR(100),
  document_number VARCHAR(40),
  business_name VARCHAR(255),
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.plans (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  instructor_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
  name VARCHAR(255) NOT NULL,
  description TEXT,
  price_monthly DECIMAL(10,2) NOT NULL CHECK (price_monthly >= 0),
  price_yearly DECIMAL(10,2) CHECK (price_yearly >= 0),
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.student_profiles (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID UNIQUE NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
  instructor_id UUID NOT NULL REFERENCES public.users(id),
  birth_date DATE,
  gender VARCHAR(20),
  fitness_level public.fitness_level DEFAULT 'beginner',
  notes TEXT,
  status public.student_status DEFAULT 'active',
  emergency_contact_name VARCHAR(255),
  emergency_contact_phone VARCHAR(20),
  medical_notes TEXT,
  goal TEXT,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.student_plans (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  student_id UUID NOT NULL REFERENCES public.student_profiles(id) ON DELETE CASCADE,
  plan_id UUID NOT NULL REFERENCES public.plans(id),
  start_date DATE NOT NULL,
  end_date DATE,
  custom_discount DECIMAL(10,2) DEFAULT 0 CHECK (custom_discount >= 0),
  billing_day INTEGER CHECK (billing_day BETWEEN 1 AND 28),
  frequency_days INTEGER[],
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  UNIQUE (student_id, plan_id, start_date)
);

CREATE TABLE IF NOT EXISTS public.workouts (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  instructor_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
  name VARCHAR(255) NOT NULL,
  description TEXT,
  icon_name VARCHAR(100),
  status public.workout_status DEFAULT 'draft',
  modality VARCHAR(100),
  intensity VARCHAR(60),
  estimated_duration_seconds INTEGER CHECK (estimated_duration_seconds IS NULL OR estimated_duration_seconds > 0),
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.workout_activities (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  workout_id UUID NOT NULL REFERENCES public.workouts(id) ON DELETE CASCADE,
  order_index INTEGER NOT NULL,
  action_name VARCHAR(255) NOT NULL,
  description TEXT,
  duration_seconds INTEGER CHECK (duration_seconds IS NULL OR duration_seconds > 0),
  distance_m DECIMAL(10,2),
  target_pace VARCHAR(60),
  created_at TIMESTAMPTZ DEFAULT NOW(),
  UNIQUE (workout_id, order_index)
);

CREATE TABLE IF NOT EXISTS public.plan_workouts (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  plan_id UUID NOT NULL REFERENCES public.plans(id) ON DELETE CASCADE,
  workout_id UUID NOT NULL REFERENCES public.workouts(id) ON DELETE CASCADE,
  day_of_week INTEGER CHECK (day_of_week BETWEEN 0 AND 6),
  order_index INTEGER DEFAULT 0,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  UNIQUE (plan_id, workout_id)
);

CREATE TABLE IF NOT EXISTS public.student_workout_logs (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  student_id UUID NOT NULL REFERENCES public.student_profiles(id) ON DELETE CASCADE,
  workout_id UUID NOT NULL REFERENCES public.workouts(id),
  completed_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  duration_actual INTEGER,
  distance_m DECIMAL(10,2),
  avg_pace DECIMAL(5,2),
  avg_bpm INTEGER,
  notes TEXT,
  gps_data JSONB,
  created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.groups (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  instructor_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
  name VARCHAR(255) NOT NULL,
  description TEXT,
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.group_members (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  group_id UUID NOT NULL REFERENCES public.groups(id) ON DELETE CASCADE,
  student_id UUID NOT NULL REFERENCES public.student_profiles(id) ON DELETE CASCADE,
  joined_at TIMESTAMPTZ DEFAULT NOW(),
  UNIQUE (group_id, student_id)
);

CREATE TABLE IF NOT EXISTS public.group_workouts (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  group_id UUID NOT NULL REFERENCES public.groups(id) ON DELETE CASCADE,
  workout_id UUID NOT NULL REFERENCES public.workouts(id) ON DELETE CASCADE,
  assigned_at TIMESTAMPTZ DEFAULT NOW(),
  due_date DATE,
  UNIQUE (group_id, workout_id)
);

CREATE TABLE IF NOT EXISTS public.announcements (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  instructor_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
  content TEXT NOT NULL,
  target_type public.announcement_target DEFAULT 'all',
  target_id UUID,
  published_at TIMESTAMPTZ DEFAULT NOW(),
  is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS public.announcement_reactions (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  announcement_id UUID NOT NULL REFERENCES public.announcements(id) ON DELETE CASCADE,
  student_id UUID NOT NULL REFERENCES public.student_profiles(id) ON DELETE CASCADE,
  emoji VARCHAR(10) NOT NULL,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  UNIQUE (announcement_id, student_id, emoji)
);

CREATE TABLE IF NOT EXISTS public.events (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  instructor_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
  name VARCHAR(255) NOT NULL,
  description TEXT,
  event_date TIMESTAMPTZ NOT NULL,
  location VARCHAR(500),
  target_groups UUID[],
  capacity INTEGER,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.event_attendance (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  event_id UUID NOT NULL REFERENCES public.events(id) ON DELETE CASCADE,
  student_id UUID NOT NULL REFERENCES public.student_profiles(id) ON DELETE CASCADE,
  attended BOOLEAN DEFAULT FALSE,
  registered_at TIMESTAMPTZ DEFAULT NOW(),
  UNIQUE (event_id, student_id)
);

CREATE TABLE IF NOT EXISTS public.notifications (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
  type public.notification_type NOT NULL,
  title VARCHAR(255) NOT NULL,
  body TEXT,
  data JSONB,
  is_read BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.community_posts (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  author_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
  instructor_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
  type public.community_post_type NOT NULL DEFAULT 'post',
  title VARCHAR(255),
  content TEXT NOT NULL,
  target public.community_target_type NOT NULL DEFAULT 'groups',
  linked_workout_id UUID REFERENCES public.workouts(id) ON DELETE SET NULL,
  media_label VARCHAR(255),
  gif_label VARCHAR(255),
  generated_image_prompt TEXT,
  scheduled_at TIMESTAMPTZ,
  location VARCHAR(500),
  content_warning TEXT,
  published_at TIMESTAMPTZ DEFAULT NOW(),
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.community_post_groups (
  post_id UUID NOT NULL REFERENCES public.community_posts(id) ON DELETE CASCADE,
  group_id UUID NOT NULL REFERENCES public.groups(id) ON DELETE CASCADE,
  PRIMARY KEY (post_id, group_id)
);

CREATE TABLE IF NOT EXISTS public.community_post_students (
  post_id UUID NOT NULL REFERENCES public.community_posts(id) ON DELETE CASCADE,
  student_id UUID NOT NULL REFERENCES public.student_profiles(id) ON DELETE CASCADE,
  PRIMARY KEY (post_id, student_id)
);

CREATE TABLE IF NOT EXISTS public.community_post_events (
  post_id UUID NOT NULL REFERENCES public.community_posts(id) ON DELETE CASCADE,
  event_id UUID NOT NULL REFERENCES public.events(id) ON DELETE CASCADE,
  PRIMARY KEY (post_id, event_id)
);

CREATE TABLE IF NOT EXISTS public.community_post_media (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  post_id UUID NOT NULL REFERENCES public.community_posts(id) ON DELETE CASCADE,
  bucket_id TEXT NOT NULL DEFAULT 'agego-social-posts',
  object_path TEXT NOT NULL,
  kind public.media_kind NOT NULL DEFAULT 'image',
  label VARCHAR(255),
  created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.community_poll_options (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  post_id UUID NOT NULL REFERENCES public.community_posts(id) ON DELETE CASCADE,
  option_text TEXT NOT NULL,
  order_index INTEGER NOT NULL DEFAULT 0,
  created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.community_poll_votes (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  post_id UUID NOT NULL REFERENCES public.community_posts(id) ON DELETE CASCADE,
  option_id UUID NOT NULL REFERENCES public.community_poll_options(id) ON DELETE CASCADE,
  user_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  UNIQUE (post_id, user_id)
);

CREATE TABLE IF NOT EXISTS public.community_post_likes (
  post_id UUID NOT NULL REFERENCES public.community_posts(id) ON DELETE CASCADE,
  user_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  PRIMARY KEY (post_id, user_id)
);

CREATE TABLE IF NOT EXISTS public.community_post_comments (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  post_id UUID NOT NULL REFERENCES public.community_posts(id) ON DELETE CASCADE,
  parent_comment_id UUID REFERENCES public.community_post_comments(id) ON DELETE CASCADE,
  author_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
  content TEXT NOT NULL,
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.community_comment_likes (
  comment_id UUID NOT NULL REFERENCES public.community_post_comments(id) ON DELETE CASCADE,
  user_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  PRIMARY KEY (comment_id, user_id)
);

CREATE TABLE IF NOT EXISTS public.management_files (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  owner_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
  student_id UUID REFERENCES public.student_profiles(id) ON DELETE CASCADE,
  workout_id UUID REFERENCES public.workouts(id) ON DELETE CASCADE,
  event_id UUID REFERENCES public.events(id) ON DELETE CASCADE,
  bucket_id TEXT NOT NULL DEFAULT 'agego-management',
  object_path TEXT NOT NULL,
  kind public.media_kind NOT NULL DEFAULT 'document',
  label VARCHAR(255),
  created_at TIMESTAMPTZ DEFAULT NOW()
);

ALTER TABLE public.instructor_profiles ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ DEFAULT NOW();
ALTER TABLE public.student_profiles ADD COLUMN IF NOT EXISTS emergency_contact_name VARCHAR(255);
ALTER TABLE public.student_profiles ADD COLUMN IF NOT EXISTS emergency_contact_phone VARCHAR(20);
ALTER TABLE public.student_profiles ADD COLUMN IF NOT EXISTS medical_notes TEXT;
ALTER TABLE public.student_profiles ADD COLUMN IF NOT EXISTS goal TEXT;
ALTER TABLE public.workouts ADD COLUMN IF NOT EXISTS modality VARCHAR(100);
ALTER TABLE public.workouts ADD COLUMN IF NOT EXISTS intensity VARCHAR(60);
ALTER TABLE public.workouts ADD COLUMN IF NOT EXISTS estimated_duration_seconds INTEGER;
ALTER TABLE public.workout_activities ADD COLUMN IF NOT EXISTS distance_m DECIMAL(10,2);
ALTER TABLE public.workout_activities ADD COLUMN IF NOT EXISTS target_pace VARCHAR(60);
ALTER TABLE public.events ADD COLUMN IF NOT EXISTS capacity INTEGER;
ALTER TABLE public.events ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ DEFAULT NOW();

CREATE OR REPLACE FUNCTION public.current_user_role()
RETURNS public.user_role
LANGUAGE sql
STABLE
SECURITY DEFINER
SET search_path = public
AS $$
  SELECT role FROM public.users WHERE id = auth.uid()
$$;

CREATE OR REPLACE FUNCTION public.is_instructor()
RETURNS BOOLEAN
LANGUAGE sql
STABLE
SECURITY DEFINER
SET search_path = public
AS $$
  SELECT COALESCE((SELECT role = 'instructor' FROM public.users WHERE id = auth.uid()), FALSE)
$$;

CREATE OR REPLACE FUNCTION public.own_student_profile_id()
RETURNS UUID
LANGUAGE sql
STABLE
SECURITY DEFINER
SET search_path = public
AS $$
  SELECT id FROM public.student_profiles WHERE user_id = auth.uid()
$$;

DO $$
DECLARE
  item RECORD;
BEGIN
  FOR item IN
    SELECT * FROM (VALUES
      ('users', 'trg_users_updated_at'),
      ('instructor_profiles', 'trg_instructor_profiles_updated_at'),
      ('plans', 'trg_plans_updated_at'),
      ('student_profiles', 'trg_student_profiles_updated_at'),
      ('workouts', 'trg_workouts_updated_at'),
      ('groups', 'trg_groups_updated_at'),
      ('events', 'trg_events_updated_at'),
      ('community_posts', 'trg_community_posts_updated_at'),
      ('community_post_comments', 'trg_community_post_comments_updated_at')
    ) AS t(table_name, trigger_name)
  LOOP
    EXECUTE format('DROP TRIGGER IF EXISTS %I ON public.%I', item.trigger_name, item.table_name);
    EXECUTE format(
      'CREATE TRIGGER %I BEFORE UPDATE ON public.%I FOR EACH ROW EXECUTE FUNCTION public.update_updated_at()',
      item.trigger_name,
      item.table_name
    );
  END LOOP;
END $$;

CREATE INDEX IF NOT EXISTS idx_student_profiles_instructor ON public.student_profiles(instructor_id);
CREATE INDEX IF NOT EXISTS idx_student_profiles_status ON public.student_profiles(status);
CREATE INDEX IF NOT EXISTS idx_student_plans_student ON public.student_plans(student_id);
CREATE INDEX IF NOT EXISTS idx_student_plans_plan ON public.student_plans(plan_id);
CREATE INDEX IF NOT EXISTS idx_workouts_instructor ON public.workouts(instructor_id);
CREATE INDEX IF NOT EXISTS idx_workouts_status ON public.workouts(status);
CREATE INDEX IF NOT EXISTS idx_workout_activities_workout ON public.workout_activities(workout_id);
CREATE INDEX IF NOT EXISTS idx_plan_workouts_plan ON public.plan_workouts(plan_id);
CREATE INDEX IF NOT EXISTS idx_group_members_group ON public.group_members(group_id);
CREATE INDEX IF NOT EXISTS idx_group_members_student ON public.group_members(student_id);
CREATE INDEX IF NOT EXISTS idx_events_instructor ON public.events(instructor_id);
CREATE INDEX IF NOT EXISTS idx_events_date ON public.events(event_date);
CREATE INDEX IF NOT EXISTS idx_notifications_user_unread ON public.notifications(user_id, is_read);
CREATE INDEX IF NOT EXISTS idx_community_posts_instructor_published ON public.community_posts(instructor_id, published_at DESC);
CREATE INDEX IF NOT EXISTS idx_community_comments_post ON public.community_post_comments(post_id, created_at);
CREATE INDEX IF NOT EXISTS idx_community_media_post ON public.community_post_media(post_id);
CREATE INDEX IF NOT EXISTS idx_management_files_owner ON public.management_files(owner_id);

ALTER TABLE public.users ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.instructor_profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.student_profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.plans ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.student_plans ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.workouts ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.workout_activities ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.plan_workouts ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.student_workout_logs ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.groups ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.group_members ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.group_workouts ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.announcements ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.announcement_reactions ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.events ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.event_attendance ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.notifications ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.community_posts ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.community_post_groups ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.community_post_students ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.community_post_events ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.community_post_media ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.community_poll_options ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.community_poll_votes ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.community_post_likes ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.community_post_comments ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.community_comment_likes ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.management_files ENABLE ROW LEVEL SECURITY;

CREATE OR REPLACE FUNCTION public.can_access_instructor(p_instructor_id UUID)
RETURNS BOOLEAN
LANGUAGE sql
STABLE
SECURITY DEFINER
SET search_path = public
AS $$
  SELECT auth.uid() = p_instructor_id
    OR EXISTS (
      SELECT 1 FROM public.student_profiles sp
      WHERE sp.user_id = auth.uid()
        AND sp.instructor_id = p_instructor_id
    )
$$;

CREATE OR REPLACE FUNCTION public.can_access_post(p_post_id UUID)
RETURNS BOOLEAN
LANGUAGE sql
STABLE
SECURITY DEFINER
SET search_path = public
AS $$
  SELECT EXISTS (
    SELECT 1
    FROM public.community_posts p
    WHERE p.id = p_post_id
      AND p.is_active = TRUE
      AND (
        p.author_id = auth.uid()
        OR p.instructor_id = auth.uid()
        OR (
          EXISTS (
            SELECT 1 FROM public.student_profiles sp
            WHERE sp.user_id = auth.uid()
              AND sp.instructor_id = p.instructor_id
              AND (
                p.target = 'all'
                OR (p.target = 'groups' AND EXISTS (
                  SELECT 1
                  FROM public.community_post_groups cpg
                  JOIN public.group_members gm ON gm.group_id = cpg.group_id
                  WHERE cpg.post_id = p.id
                    AND gm.student_id = sp.id
                ))
                OR (p.target = 'students' AND EXISTS (
                  SELECT 1 FROM public.community_post_students cps
                  WHERE cps.post_id = p.id AND cps.student_id = sp.id
                ))
                OR (p.target = 'events' AND EXISTS (
                  SELECT 1
                  FROM public.community_post_events cpe
                  JOIN public.event_attendance ea ON ea.event_id = cpe.event_id
                  WHERE cpe.post_id = p.id AND ea.student_id = sp.id
                ))
              )
          )
        )
      )
  )
$$;

DO $$
DECLARE
  pol RECORD;
BEGIN
  FOR pol IN
    SELECT schemaname, tablename, policyname
    FROM pg_policies
    WHERE schemaname = 'public'
      AND (
        policyname LIKE 'agego_%'
        OR policyname IN (
          'users_select_own',
          'users_update_own',
          'instructor_profile_own',
          'student_profiles_access',
          'student_profiles_instructor_write',
          'student_profiles_instructor_update',
          'student_profiles_instructor_delete',
          'plans_instructor_all',
          'plans_student_select',
          'student_plans_instructor',
          'student_plans_own',
          'workouts_instructor_all',
          'workouts_student_select',
          'workout_activities_via_workout',
          'workout_logs_student_own',
          'workout_logs_instructor_select',
          'groups_instructor_all',
          'groups_student_select',
          'group_members_instructor_all',
          'group_members_student_select',
          'group_workouts_instructor_all',
          'group_workouts_student_select',
          'announcements_instructor_all',
          'announcements_student_select',
          'reactions_student_own',
          'reactions_instructor_select',
          'events_instructor_all',
          'events_student_select',
          'attendance_instructor_all',
          'attendance_student_own',
          'notifications_own'
        )
      )
  LOOP
    EXECUTE format('DROP POLICY IF EXISTS %I ON %I.%I', pol.policyname, pol.schemaname, pol.tablename);
  END LOOP;
END $$;

CREATE POLICY agego_users_select_related ON public.users
  FOR SELECT USING (
    id = auth.uid()
    OR EXISTS (SELECT 1 FROM public.student_profiles sp WHERE sp.user_id = public.users.id AND sp.instructor_id = auth.uid())
    OR EXISTS (SELECT 1 FROM public.student_profiles sp WHERE sp.user_id = auth.uid() AND sp.instructor_id = public.users.id)
  );
CREATE POLICY agego_users_update_own ON public.users FOR UPDATE USING (id = auth.uid()) WITH CHECK (id = auth.uid());

CREATE POLICY agego_instructor_profiles_crud ON public.instructor_profiles
  FOR ALL USING (user_id = auth.uid()) WITH CHECK (user_id = auth.uid());

CREATE POLICY agego_student_profiles_select ON public.student_profiles
  FOR SELECT USING (user_id = auth.uid() OR instructor_id = auth.uid());
CREATE POLICY agego_student_profiles_insert ON public.student_profiles
  FOR INSERT WITH CHECK (instructor_id = auth.uid());
CREATE POLICY agego_student_profiles_update ON public.student_profiles
  FOR UPDATE USING (instructor_id = auth.uid() OR user_id = auth.uid())
  WITH CHECK (instructor_id = auth.uid() OR user_id = auth.uid());
CREATE POLICY agego_student_profiles_delete ON public.student_profiles
  FOR DELETE USING (instructor_id = auth.uid());

CREATE POLICY agego_plans_select ON public.plans FOR SELECT USING (public.can_access_instructor(instructor_id));
CREATE POLICY agego_plans_write ON public.plans FOR ALL USING (instructor_id = auth.uid()) WITH CHECK (instructor_id = auth.uid());

CREATE POLICY agego_student_plans_select ON public.student_plans FOR SELECT USING (
  EXISTS (SELECT 1 FROM public.student_profiles sp WHERE sp.id = student_id AND (sp.user_id = auth.uid() OR sp.instructor_id = auth.uid()))
);
CREATE POLICY agego_student_plans_write ON public.student_plans FOR ALL USING (
  EXISTS (SELECT 1 FROM public.student_profiles sp WHERE sp.id = student_id AND sp.instructor_id = auth.uid())
) WITH CHECK (
  EXISTS (SELECT 1 FROM public.student_profiles sp WHERE sp.id = student_id AND sp.instructor_id = auth.uid())
);

CREATE POLICY agego_workouts_select ON public.workouts FOR SELECT USING (public.can_access_instructor(instructor_id));
CREATE POLICY agego_workouts_write ON public.workouts FOR ALL USING (instructor_id = auth.uid()) WITH CHECK (instructor_id = auth.uid());

CREATE POLICY agego_workout_activities_select ON public.workout_activities FOR SELECT USING (
  EXISTS (SELECT 1 FROM public.workouts w WHERE w.id = workout_id AND public.can_access_instructor(w.instructor_id))
);
CREATE POLICY agego_workout_activities_write ON public.workout_activities FOR ALL USING (
  EXISTS (SELECT 1 FROM public.workouts w WHERE w.id = workout_id AND w.instructor_id = auth.uid())
) WITH CHECK (
  EXISTS (SELECT 1 FROM public.workouts w WHERE w.id = workout_id AND w.instructor_id = auth.uid())
);

CREATE POLICY agego_plan_workouts_select ON public.plan_workouts FOR SELECT USING (
  EXISTS (SELECT 1 FROM public.plans p WHERE p.id = plan_id AND public.can_access_instructor(p.instructor_id))
);
CREATE POLICY agego_plan_workouts_write ON public.plan_workouts FOR ALL USING (
  EXISTS (SELECT 1 FROM public.plans p WHERE p.id = plan_id AND p.instructor_id = auth.uid())
) WITH CHECK (
  EXISTS (SELECT 1 FROM public.plans p WHERE p.id = plan_id AND p.instructor_id = auth.uid())
);

CREATE POLICY agego_workout_logs_student_crud ON public.student_workout_logs FOR ALL USING (
  EXISTS (SELECT 1 FROM public.student_profiles sp WHERE sp.id = student_id AND sp.user_id = auth.uid())
) WITH CHECK (
  EXISTS (SELECT 1 FROM public.student_profiles sp WHERE sp.id = student_id AND sp.user_id = auth.uid())
);
CREATE POLICY agego_workout_logs_instructor_select ON public.student_workout_logs FOR SELECT USING (
  EXISTS (SELECT 1 FROM public.student_profiles sp WHERE sp.id = student_id AND sp.instructor_id = auth.uid())
);

CREATE POLICY agego_groups_select ON public.groups FOR SELECT USING (
  instructor_id = auth.uid()
  OR EXISTS (
    SELECT 1 FROM public.group_members gm
    JOIN public.student_profiles sp ON sp.id = gm.student_id
    WHERE gm.group_id = public.groups.id AND sp.user_id = auth.uid()
  )
);
CREATE POLICY agego_groups_write ON public.groups FOR ALL USING (instructor_id = auth.uid()) WITH CHECK (instructor_id = auth.uid());

CREATE POLICY agego_group_members_select ON public.group_members FOR SELECT USING (
  EXISTS (
    SELECT 1 FROM public.groups g
    LEFT JOIN public.student_profiles sp ON sp.user_id = auth.uid()
    WHERE g.id = group_id
      AND (g.instructor_id = auth.uid() OR student_id = sp.id)
  )
);
CREATE POLICY agego_group_members_write ON public.group_members FOR ALL USING (
  EXISTS (SELECT 1 FROM public.groups g WHERE g.id = group_id AND g.instructor_id = auth.uid())
) WITH CHECK (
  EXISTS (SELECT 1 FROM public.groups g WHERE g.id = group_id AND g.instructor_id = auth.uid())
);

CREATE POLICY agego_group_workouts_select ON public.group_workouts FOR SELECT USING (
  EXISTS (SELECT 1 FROM public.groups g WHERE g.id = group_id AND public.can_access_instructor(g.instructor_id))
);
CREATE POLICY agego_group_workouts_write ON public.group_workouts FOR ALL USING (
  EXISTS (SELECT 1 FROM public.groups g WHERE g.id = group_id AND g.instructor_id = auth.uid())
) WITH CHECK (
  EXISTS (SELECT 1 FROM public.groups g WHERE g.id = group_id AND g.instructor_id = auth.uid())
);

CREATE POLICY agego_announcements_select ON public.announcements FOR SELECT USING (public.can_access_instructor(instructor_id));
CREATE POLICY agego_announcements_write ON public.announcements FOR ALL USING (instructor_id = auth.uid()) WITH CHECK (instructor_id = auth.uid());

CREATE POLICY agego_reactions_student ON public.announcement_reactions FOR ALL USING (
  EXISTS (SELECT 1 FROM public.student_profiles sp WHERE sp.id = student_id AND sp.user_id = auth.uid())
) WITH CHECK (
  EXISTS (SELECT 1 FROM public.student_profiles sp WHERE sp.id = student_id AND sp.user_id = auth.uid())
);
CREATE POLICY agego_reactions_instructor_select ON public.announcement_reactions FOR SELECT USING (
  EXISTS (SELECT 1 FROM public.announcements a WHERE a.id = announcement_id AND a.instructor_id = auth.uid())
);

CREATE POLICY agego_events_select ON public.events FOR SELECT USING (public.can_access_instructor(instructor_id));
CREATE POLICY agego_events_write ON public.events FOR ALL USING (instructor_id = auth.uid()) WITH CHECK (instructor_id = auth.uid());

CREATE POLICY agego_event_attendance_select ON public.event_attendance FOR SELECT USING (
  EXISTS (SELECT 1 FROM public.events e WHERE e.id = event_id AND public.can_access_instructor(e.instructor_id))
);
CREATE POLICY agego_event_attendance_write ON public.event_attendance FOR ALL USING (
  EXISTS (SELECT 1 FROM public.events e WHERE e.id = event_id AND e.instructor_id = auth.uid())
  OR EXISTS (SELECT 1 FROM public.student_profiles sp WHERE sp.id = student_id AND sp.user_id = auth.uid())
) WITH CHECK (
  EXISTS (SELECT 1 FROM public.events e WHERE e.id = event_id AND e.instructor_id = auth.uid())
  OR EXISTS (SELECT 1 FROM public.student_profiles sp WHERE sp.id = student_id AND sp.user_id = auth.uid())
);

CREATE POLICY agego_notifications_own ON public.notifications FOR ALL USING (user_id = auth.uid()) WITH CHECK (user_id = auth.uid());

CREATE POLICY agego_posts_select ON public.community_posts FOR SELECT USING (public.can_access_post(id));
CREATE POLICY agego_posts_insert ON public.community_posts FOR INSERT WITH CHECK (
  author_id = auth.uid()
  AND (
    instructor_id = auth.uid()
    OR EXISTS (SELECT 1 FROM public.student_profiles sp WHERE sp.user_id = auth.uid() AND sp.instructor_id = community_posts.instructor_id)
  )
);
CREATE POLICY agego_posts_update ON public.community_posts FOR UPDATE USING (author_id = auth.uid() OR instructor_id = auth.uid())
  WITH CHECK (author_id = auth.uid() OR instructor_id = auth.uid());
CREATE POLICY agego_posts_delete ON public.community_posts FOR DELETE USING (author_id = auth.uid() OR instructor_id = auth.uid());

CREATE POLICY agego_post_groups_access ON public.community_post_groups FOR ALL USING (public.can_access_post(post_id))
  WITH CHECK (EXISTS (SELECT 1 FROM public.community_posts p WHERE p.id = post_id AND (p.author_id = auth.uid() OR p.instructor_id = auth.uid())));
CREATE POLICY agego_post_students_access ON public.community_post_students FOR ALL USING (public.can_access_post(post_id))
  WITH CHECK (EXISTS (SELECT 1 FROM public.community_posts p WHERE p.id = post_id AND (p.author_id = auth.uid() OR p.instructor_id = auth.uid())));
CREATE POLICY agego_post_events_access ON public.community_post_events FOR ALL USING (public.can_access_post(post_id))
  WITH CHECK (EXISTS (SELECT 1 FROM public.community_posts p WHERE p.id = post_id AND (p.author_id = auth.uid() OR p.instructor_id = auth.uid())));

CREATE POLICY agego_post_media_access ON public.community_post_media FOR ALL USING (public.can_access_post(post_id))
  WITH CHECK (EXISTS (SELECT 1 FROM public.community_posts p WHERE p.id = post_id AND (p.author_id = auth.uid() OR p.instructor_id = auth.uid())));

CREATE POLICY agego_poll_options_access ON public.community_poll_options FOR ALL USING (public.can_access_post(post_id))
  WITH CHECK (EXISTS (SELECT 1 FROM public.community_posts p WHERE p.id = post_id AND (p.author_id = auth.uid() OR p.instructor_id = auth.uid())));
CREATE POLICY agego_poll_votes_access ON public.community_poll_votes FOR ALL USING (public.can_access_post(post_id) AND user_id = auth.uid())
  WITH CHECK (public.can_access_post(post_id) AND user_id = auth.uid());
CREATE POLICY agego_post_likes_access ON public.community_post_likes FOR ALL USING (public.can_access_post(post_id) AND user_id = auth.uid())
  WITH CHECK (public.can_access_post(post_id) AND user_id = auth.uid());
CREATE POLICY agego_comments_select ON public.community_post_comments FOR SELECT USING (public.can_access_post(post_id));
CREATE POLICY agego_comments_write ON public.community_post_comments FOR ALL USING (author_id = auth.uid() OR EXISTS (
  SELECT 1 FROM public.community_posts p WHERE p.id = post_id AND p.instructor_id = auth.uid()
)) WITH CHECK (public.can_access_post(post_id) AND author_id = auth.uid());
CREATE POLICY agego_comment_likes_access ON public.community_comment_likes FOR ALL USING (
  user_id = auth.uid()
  AND EXISTS (SELECT 1 FROM public.community_post_comments c WHERE c.id = comment_id AND public.can_access_post(c.post_id))
) WITH CHECK (
  user_id = auth.uid()
  AND EXISTS (SELECT 1 FROM public.community_post_comments c WHERE c.id = comment_id AND public.can_access_post(c.post_id))
);

CREATE POLICY agego_management_files_select ON public.management_files FOR SELECT USING (
  owner_id = auth.uid()
  OR EXISTS (SELECT 1 FROM public.student_profiles sp WHERE sp.id = student_id AND (sp.user_id = auth.uid() OR sp.instructor_id = auth.uid()))
);
CREATE POLICY agego_management_files_write ON public.management_files FOR ALL USING (owner_id = auth.uid()) WITH CHECK (owner_id = auth.uid());

CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS TRIGGER AS $$
BEGIN
  INSERT INTO public.users (id, email, name, avatar_url, role)
  VALUES (
    NEW.id,
    NEW.email,
    COALESCE(NEW.raw_user_meta_data->>'name', NEW.raw_user_meta_data->>'full_name', 'Usuario'),
    NEW.raw_user_meta_data->>'avatar_url',
    COALESCE((NEW.raw_user_meta_data->>'role')::public.user_role, 'student')
  )
  ON CONFLICT (id) DO UPDATE SET
    email = EXCLUDED.email,
    name = EXCLUDED.name,
    avatar_url = COALESCE(EXCLUDED.avatar_url, public.users.avatar_url),
    updated_at = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER SET search_path = public;

DROP TRIGGER IF EXISTS on_auth_user_created ON auth.users;
CREATE TRIGGER on_auth_user_created
  AFTER INSERT ON auth.users
  FOR EACH ROW EXECUTE FUNCTION public.handle_new_user();

DROP FUNCTION IF EXISTS public.get_instructor_students();
CREATE OR REPLACE FUNCTION public.get_instructor_students()
RETURNS TABLE (
  id UUID,
  name TEXT,
  email TEXT,
  phone TEXT,
  routine TEXT,
  plan_name TEXT,
  status TEXT
)
LANGUAGE sql
SECURITY DEFINER
SET search_path = public
AS $$
  SELECT
    sp.id,
    u.name::TEXT,
    u.email::TEXT,
    COALESCE(u.phone, '')::TEXT,
    COALESCE(sp.goal, '')::TEXT AS routine,
    COALESCE(p.name, 'Sem plano')::TEXT AS plan_name,
    sp.status::TEXT
  FROM public.student_profiles sp
  JOIN public.users u ON u.id = sp.user_id
  LEFT JOIN public.student_plans spl ON spl.student_id = sp.id AND spl.is_active = TRUE
  LEFT JOIN public.plans p ON p.id = spl.plan_id
  WHERE sp.instructor_id = auth.uid()
  ORDER BY u.name;
$$;

CREATE OR REPLACE FUNCTION public.set_group_members(p_group_id UUID, p_student_ids UUID[])
RETURNS VOID
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
  v_instructor_id UUID;
BEGIN
  SELECT instructor_id INTO v_instructor_id
  FROM public.groups
  WHERE id = p_group_id;

  IF v_instructor_id IS NULL OR v_instructor_id <> auth.uid() THEN
    RAISE EXCEPTION 'Group not found or not owned by current instructor';
  END IF;

  IF EXISTS (
    SELECT 1
    FROM unnest(COALESCE(p_student_ids, ARRAY[]::UUID[])) sid
    LEFT JOIN public.student_profiles sp ON sp.id = sid AND sp.instructor_id = auth.uid()
    WHERE sp.id IS NULL
  ) THEN
    RAISE EXCEPTION 'Every selected student must belong to current instructor';
  END IF;

  DELETE FROM public.group_members
  WHERE group_id = p_group_id
    AND NOT (student_id = ANY(COALESCE(p_student_ids, ARRAY[]::UUID[])));

  INSERT INTO public.group_members (group_id, student_id)
  SELECT p_group_id, sid
  FROM unnest(COALESCE(p_student_ids, ARRAY[]::UUID[])) sid
  ON CONFLICT (group_id, student_id) DO NOTHING;
END;
$$;

DROP VIEW IF EXISTS public.v_agego_community_posts;
DROP VIEW IF EXISTS public.v_agego_groups;
DROP VIEW IF EXISTS public.v_agego_students;
DROP VIEW IF EXISTS public.v_agego_training_now;
DROP VIEW IF EXISTS public.v_agego_events;
DROP VIEW IF EXISTS public.v_agego_weekly_workouts;
DROP VIEW IF EXISTS public.v_agego_announcements;
DROP VIEW IF EXISTS public.v_agego_workout_activities;
DROP VIEW IF EXISTS public.v_agego_workouts;

CREATE OR REPLACE VIEW public.v_agego_students AS
SELECT
  sp.id,
  sp.user_id,
  sp.instructor_id,
  u.name,
  u.email,
  COALESCE(u.phone, '') AS phone,
  u.avatar_url,
  COALESCE(sp.goal, '') AS routine,
  COALESCE(p.name, 'Sem plano') AS plan_name,
  sp.status::TEXT AS status,
  COUNT(DISTINCT swl.id)::INT AS workouts_done,
  COUNT(DISTINCT pw.workout_id)::INT AS workouts_total
FROM public.student_profiles sp
JOIN public.users u ON u.id = sp.user_id
LEFT JOIN public.student_plans spl ON spl.student_id = sp.id AND spl.is_active = TRUE
LEFT JOIN public.plans p ON p.id = spl.plan_id
LEFT JOIN public.plan_workouts pw ON pw.plan_id = p.id
LEFT JOIN public.student_workout_logs swl ON swl.student_id = sp.id
WHERE sp.instructor_id = auth.uid() OR sp.user_id = auth.uid()
GROUP BY sp.id, sp.user_id, sp.instructor_id, u.name, u.email, u.phone, u.avatar_url, sp.goal, p.name, sp.status;

CREATE OR REPLACE VIEW public.v_agego_groups AS
SELECT
  g.id,
  g.instructor_id,
  g.name,
  COALESCE(g.description, '') AS description,
  CASE WHEN g.is_active THEN 'active' ELSE 'inactive' END AS status,
  COUNT(DISTINCT gm.student_id)::INT AS students_count,
  COALESCE(jsonb_agg(DISTINCT gm.student_id) FILTER (WHERE gm.student_id IS NOT NULL), '[]'::jsonb) AS student_ids
FROM public.groups g
LEFT JOIN public.group_members gm ON gm.group_id = g.id
WHERE
  g.instructor_id = auth.uid()
  OR EXISTS (
    SELECT 1
    FROM public.group_members own_gm
    JOIN public.student_profiles own_sp ON own_sp.id = own_gm.student_id
    WHERE own_gm.group_id = g.id AND own_sp.user_id = auth.uid()
  )
GROUP BY g.id, g.instructor_id, g.name, g.description, g.is_active;

CREATE OR REPLACE VIEW public.v_agego_community_posts AS
SELECT
  p.id,
  p.type::TEXT AS type,
  COALESCE(p.title, '') AS title,
  p.content,
  p.target::TEXT AS target,
  p.author_id,
  p.instructor_id,
  COALESCE(u.name, 'AgeGo') AS author_name,
  p.linked_workout_id,
  p.media_label,
  p.gif_label,
  p.generated_image_prompt,
  p.scheduled_at,
  p.location,
  p.content_warning,
  p.published_at,
  COUNT(DISTINCT l.user_id)::INT AS likes,
  COUNT(DISTINCT c.id)::INT AS comments,
  0::INT AS shares,
  EXISTS (
    SELECT 1 FROM public.community_post_likes own_l
    WHERE own_l.post_id = p.id AND own_l.user_id = auth.uid()
  ) AS liked,
  COALESCE(
    jsonb_agg(DISTINCT jsonb_build_object('id', po.id, 'text', po.option_text, 'order_index', po.order_index))
      FILTER (WHERE po.id IS NOT NULL),
    '[]'::jsonb
  ) AS poll_options
FROM public.community_posts p
JOIN public.users u ON u.id = p.author_id
LEFT JOIN public.community_post_likes l ON l.post_id = p.id
LEFT JOIN public.community_post_comments c ON c.post_id = p.id AND c.is_active = TRUE
LEFT JOIN public.community_poll_options po ON po.post_id = p.id
WHERE public.can_access_post(p.id)
GROUP BY p.id, u.name;

GRANT SELECT ON public.v_agego_students TO authenticated;
GRANT SELECT ON public.v_agego_groups TO authenticated;
GRANT SELECT ON public.v_agego_community_posts TO authenticated;
REVOKE ALL ON FUNCTION public.get_instructor_students() FROM PUBLIC;
REVOKE ALL ON FUNCTION public.set_group_members(UUID, UUID[]) FROM PUBLIC;
GRANT EXECUTE ON FUNCTION public.get_instructor_students() TO authenticated;
GRANT EXECUTE ON FUNCTION public.set_group_members(UUID, UUID[]) TO authenticated;

INSERT INTO storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
VALUES
  ('agego-social-posts', 'agego-social-posts', FALSE, 52428800, ARRAY['image/png','image/jpeg','image/webp','image/gif','application/pdf','video/mp4']),
  ('agego-management', 'agego-management', FALSE, 52428800, ARRAY['image/png','image/jpeg','image/webp','application/pdf','text/plain','video/mp4'])
ON CONFLICT (id) DO UPDATE SET
  public = EXCLUDED.public,
  file_size_limit = EXCLUDED.file_size_limit,
  allowed_mime_types = EXCLUDED.allowed_mime_types;

DROP POLICY IF EXISTS agego_social_posts_read ON storage.objects;
DROP POLICY IF EXISTS agego_social_posts_write ON storage.objects;
DROP POLICY IF EXISTS agego_management_read ON storage.objects;
DROP POLICY IF EXISTS agego_management_write ON storage.objects;

CREATE POLICY agego_social_posts_read ON storage.objects
  FOR SELECT TO authenticated
  USING (bucket_id = 'agego-social-posts');

CREATE POLICY agego_social_posts_write ON storage.objects
  FOR ALL TO authenticated
  USING (bucket_id = 'agego-social-posts' AND (storage.foldername(name))[1] = auth.uid()::TEXT)
  WITH CHECK (bucket_id = 'agego-social-posts' AND (storage.foldername(name))[1] = auth.uid()::TEXT);

CREATE POLICY agego_management_read ON storage.objects
  FOR SELECT TO authenticated
  USING (bucket_id = 'agego-management' AND (storage.foldername(name))[1] = auth.uid()::TEXT);

CREATE POLICY agego_management_write ON storage.objects
  FOR ALL TO authenticated
  USING (bucket_id = 'agego-management' AND (storage.foldername(name))[1] = auth.uid()::TEXT)
  WITH CHECK (bucket_id = 'agego-management' AND (storage.foldername(name))[1] = auth.uid()::TEXT);
