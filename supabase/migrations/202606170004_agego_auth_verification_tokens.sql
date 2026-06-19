CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS public.auth_verification_tokens (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
  contact TEXT NOT NULL,
  purpose TEXT NOT NULL CHECK (purpose IN ('instructor_email', 'student_first_access', 'login')),
  token TEXT NOT NULL,
  expires_at TIMESTAMPTZ NOT NULL,
  consumed_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.auth_pending_registrations (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  name TEXT NOT NULL,
  email TEXT NOT NULL,
  phone TEXT,
  token TEXT NOT NULL,
  expires_at TIMESTAMPTZ NOT NULL,
  consumed_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

ALTER TABLE public.auth_verification_tokens ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.auth_pending_registrations ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS agego_auth_tokens_service_only ON public.auth_verification_tokens;
DROP POLICY IF EXISTS agego_pending_registrations_service_only ON public.auth_pending_registrations;

CREATE POLICY agego_auth_tokens_service_only ON public.auth_verification_tokens
  FOR ALL USING (false)
  WITH CHECK (false);

CREATE POLICY agego_pending_registrations_service_only ON public.auth_pending_registrations
  FOR ALL USING (false)
  WITH CHECK (false);

CREATE INDEX IF NOT EXISTS idx_auth_tokens_contact_purpose ON public.auth_verification_tokens(contact, purpose);
CREATE INDEX IF NOT EXISTS idx_auth_tokens_user_created ON public.auth_verification_tokens(user_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_pending_registrations_email_token ON public.auth_pending_registrations(email, token);

NOTIFY pgrst, 'reload schema';
