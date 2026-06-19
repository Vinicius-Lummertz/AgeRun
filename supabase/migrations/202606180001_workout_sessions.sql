CREATE TABLE IF NOT EXISTS public.workout_sessions (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
  student_profile_id UUID NOT NULL REFERENCES public.student_profiles(id) ON DELETE CASCADE,
  instructor_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
  routine_id UUID NULL REFERENCES public.routines(id) ON DELETE SET NULL,
  routine_name TEXT NOT NULL DEFAULT '',
  day_number INTEGER NOT NULL DEFAULT 1,
  cycle_step INTEGER NOT NULL DEFAULT 1,
  elapsed_ms BIGINT NOT NULL DEFAULT 0,
  distance_meters DOUBLE PRECISION NOT NULL DEFAULT 0,
  pace_seconds_per_km DOUBLE PRECISION NOT NULL DEFAULT 0,
  status TEXT NOT NULL DEFAULT 'completed',
  planned_steps JSONB NOT NULL DEFAULT '[]'::jsonb,
  route_points JSONB NOT NULL DEFAULT '[]'::jsonb,
  splits JSONB NOT NULL DEFAULT '[]'::jsonb,
  started_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  completed_at TIMESTAMPTZ NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_workout_sessions_student_created
  ON public.workout_sessions(student_profile_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_workout_sessions_instructor_created
  ON public.workout_sessions(instructor_id, created_at DESC);

ALTER TABLE public.workout_sessions ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS agego_workout_sessions_service_only ON public.workout_sessions;
CREATE POLICY agego_workout_sessions_service_only ON public.workout_sessions
  FOR ALL
  USING (false)
  WITH CHECK (false);

NOTIFY pgrst, 'reload schema';
