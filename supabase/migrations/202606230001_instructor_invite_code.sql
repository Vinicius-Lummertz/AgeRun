ALTER TABLE public.instructor_settings
  ADD COLUMN IF NOT EXISTS invite_code VARCHAR(20);

CREATE UNIQUE INDEX IF NOT EXISTS idx_instructor_settings_invite_code
  ON public.instructor_settings(invite_code)
  WHERE invite_code IS NOT NULL;
