-- Execute no SQL Editor do Supabase.
-- A policy atual de public.users só permite ler o próprio usuário. Esta RPC
-- entrega ao instrutor apenas os alunos vinculados a ele, sem abrir a tabela.

CREATE OR REPLACE FUNCTION public.get_instructor_students()
RETURNS TABLE (
  id UUID,
  name TEXT,
  email TEXT,
  plan_name TEXT,
  status TEXT
)
LANGUAGE sql
SECURITY DEFINER
SET search_path = public
AS $$
  SELECT
    sp.id,
    u.name::TEXT,
    u.email::TEXT,
    COALESCE(p.name, 'Sem plano')::TEXT AS plan_name,
    sp.status::TEXT
  FROM public.student_profiles sp
  JOIN public.users u ON u.id = sp.user_id
  LEFT JOIN public.student_plans spl
    ON spl.student_id = sp.id AND spl.is_active = TRUE
  LEFT JOIN public.plans p ON p.id = spl.plan_id
  WHERE sp.instructor_id = auth.uid()
  ORDER BY u.name;
$$;

REVOKE ALL ON FUNCTION public.get_instructor_students() FROM PUBLIC;
GRANT EXECUTE ON FUNCTION public.get_instructor_students() TO authenticated;
