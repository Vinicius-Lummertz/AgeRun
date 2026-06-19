ALTER TABLE public.users
  ADD COLUMN IF NOT EXISTS avatar_url TEXT;

NOTIFY pgrst, 'reload schema';
