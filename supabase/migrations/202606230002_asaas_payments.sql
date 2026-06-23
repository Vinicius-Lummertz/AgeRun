ALTER TABLE public.student_profiles
  ADD COLUMN IF NOT EXISTS cpf VARCHAR(14),
  ADD COLUMN IF NOT EXISTS asaas_customer_id VARCHAR(40),
  ADD COLUMN IF NOT EXISTS asaas_payment_id VARCHAR(40);
