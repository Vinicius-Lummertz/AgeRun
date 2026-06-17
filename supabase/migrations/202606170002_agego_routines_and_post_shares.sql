CREATE TABLE IF NOT EXISTS public.routines (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  instructor_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
  name VARCHAR(255) NOT NULL,
  description TEXT,
  status public.workout_status DEFAULT 'active',
  created_at TIMESTAMPTZ DEFAULT NOW(),
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

DROP TRIGGER IF EXISTS trg_routines_updated_at ON public.routines;
CREATE TRIGGER trg_routines_updated_at
  BEFORE UPDATE ON public.routines
  FOR EACH ROW EXECUTE FUNCTION public.update_updated_at();

CREATE TABLE IF NOT EXISTS public.community_post_shares (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  post_id UUID NOT NULL REFERENCES public.community_posts(id) ON DELETE CASCADE,
  user_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
  created_at TIMESTAMPTZ DEFAULT NOW()
);

ALTER TABLE public.routines ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.community_post_shares ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS agego_routines_select ON public.routines;
DROP POLICY IF EXISTS agego_routines_write ON public.routines;
DROP POLICY IF EXISTS agego_post_shares_access ON public.community_post_shares;

CREATE POLICY agego_routines_select ON public.routines
  FOR SELECT USING (public.can_access_instructor(instructor_id));

CREATE POLICY agego_routines_write ON public.routines
  FOR ALL USING (instructor_id = auth.uid())
  WITH CHECK (instructor_id = auth.uid());

CREATE POLICY agego_post_shares_access ON public.community_post_shares
  FOR ALL USING (public.can_access_post(post_id) AND user_id = auth.uid())
  WITH CHECK (public.can_access_post(post_id) AND user_id = auth.uid());

CREATE INDEX IF NOT EXISTS idx_routines_instructor ON public.routines(instructor_id);
CREATE INDEX IF NOT EXISTS idx_post_shares_post ON public.community_post_shares(post_id);
