ALTER TABLE public.student_profiles
  ADD COLUMN IF NOT EXISTS asaas_subscription_id VARCHAR(40);
