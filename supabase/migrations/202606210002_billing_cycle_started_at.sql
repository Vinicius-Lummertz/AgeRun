ALTER TABLE public.student_profiles
  ADD COLUMN IF NOT EXISTS billing_cycle_started_at TIMESTAMPTZ;

UPDATE public.student_profiles
SET billing_cycle_started_at = COALESCE(created_at, NOW())
WHERE billing_cycle_started_at IS NULL;

ALTER TABLE public.student_profiles
  ALTER COLUMN billing_cycle_started_at SET DEFAULT NOW();

