CREATE TABLE IF NOT EXISTS public.student_presence (
  user_id UUID PRIMARY KEY REFERENCES public.users(id) ON DELETE CASCADE,
  student_profile_id UUID NOT NULL REFERENCES public.student_profiles(id) ON DELETE CASCADE,
  instructor_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
  last_seen_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_student_presence_instructor_seen
  ON public.student_presence(instructor_id, last_seen_at DESC);

ALTER TABLE public.student_presence ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS agego_student_presence_service_only ON public.student_presence;
CREATE POLICY agego_student_presence_service_only ON public.student_presence
  FOR ALL
  USING (false)
  WITH CHECK (false);

NOTIFY pgrst, 'reload schema';
