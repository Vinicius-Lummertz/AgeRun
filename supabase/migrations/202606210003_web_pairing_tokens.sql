-- Pairing tokens used to log the instructor web dashboard in by scanning a QR code with the app.
CREATE TABLE IF NOT EXISTS public.web_pairing_tokens (
  token TEXT PRIMARY KEY,
  instructor_id UUID REFERENCES public.users(id) ON DELETE CASCADE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  expires_at TIMESTAMPTZ NOT NULL,
  approved_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS web_pairing_tokens_expires_at_idx ON public.web_pairing_tokens (expires_at);

ALTER TABLE public.web_pairing_tokens ENABLE ROW LEVEL SECURITY;
