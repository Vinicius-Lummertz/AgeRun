CREATE TABLE IF NOT EXISTS public.instructor_settings (
  instructor_id UUID PRIMARY KEY REFERENCES public.users(id) ON DELETE CASCADE,
  pix_key TEXT,
  notification_email BOOLEAN NOT NULL DEFAULT TRUE,
  notification_push BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.student_charges (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  instructor_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
  student_id UUID NOT NULL REFERENCES public.student_profiles(id) ON DELETE CASCADE,
  description TEXT NOT NULL DEFAULT 'Mensalidade',
  amount DECIMAL(10,2) NOT NULL CHECK (amount >= 0),
  due_date DATE NOT NULL,
  paid_at TIMESTAMPTZ,
  status TEXT NOT NULL DEFAULT 'pending' CHECK (status IN ('pending', 'paid', 'overdue', 'cancelled')),
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

ALTER TABLE public.instructor_settings ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.student_charges ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS agego_instructor_settings_access ON public.instructor_settings;
DROP POLICY IF EXISTS agego_student_charges_access ON public.student_charges;

CREATE POLICY agego_instructor_settings_access ON public.instructor_settings
  FOR ALL USING (instructor_id = auth.uid())
  WITH CHECK (instructor_id = auth.uid());

CREATE POLICY agego_student_charges_access ON public.student_charges
  FOR ALL USING (
    instructor_id = auth.uid()
    OR EXISTS (
      SELECT 1 FROM public.student_profiles sp
      WHERE sp.id = student_charges.student_id AND sp.user_id = auth.uid()
    )
  )
  WITH CHECK (instructor_id = auth.uid());

CREATE INDEX IF NOT EXISTS idx_student_charges_student_due ON public.student_charges(student_id, due_date DESC);
CREATE INDEX IF NOT EXISTS idx_student_charges_instructor_status ON public.student_charges(instructor_id, status);
