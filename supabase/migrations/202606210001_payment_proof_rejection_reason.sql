ALTER TABLE student_profiles
  ADD COLUMN IF NOT EXISTS payment_proof_rejection_reason text;
