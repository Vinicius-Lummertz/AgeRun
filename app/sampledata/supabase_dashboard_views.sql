-- ============================================================
-- AGE GO - Views para o dashboard do app
--
-- Execute depois de:
--   1. sampledata/banco.txt
--   2. sampledata/seed_admin_aluno.sql
--   3. sampledata/seed_demo_app.sql
--
-- Estas views sao consumidas pelo app Android via Supabase PostgREST.
-- ============================================================

CREATE OR REPLACE VIEW public.v_agego_students AS
SELECT
  sp.id,
  sp.user_id,
  sp.instructor_id,
  u.name,
  u.email,
  COALESCE(u.phone, '') AS phone,
  u.avatar_url,
  COALESCE(p.name, 'Sem plano') AS plan,
  sp.status::TEXT AS status,
  COUNT(DISTINCT swl.id)::INT AS workouts_done,
  COUNT(DISTINCT pw.workout_id)::INT AS workouts_total
FROM public.student_profiles sp
JOIN public.users u ON u.id = sp.user_id
LEFT JOIN public.student_plans spl ON spl.student_id = sp.id AND spl.is_active = TRUE
LEFT JOIN public.plans p ON p.id = spl.plan_id
LEFT JOIN public.plan_workouts pw ON pw.plan_id = p.id
LEFT JOIN public.student_workout_logs swl ON swl.student_id = sp.id
WHERE
  sp.instructor_id = auth.uid()
  OR sp.user_id = auth.uid()
GROUP BY sp.id, sp.user_id, sp.instructor_id, u.name, u.email, u.phone, p.name, sp.status;

CREATE OR REPLACE VIEW public.v_agego_workouts AS
SELECT
  w.id,
  w.instructor_id,
  w.name,
  COALESCE(w.description, '') AS description,
  COALESCE(w.icon_name, '') AS icon_name,
  w.status::TEXT AS status,
  COUNT(DISTINCT sp.id)::INT AS students_count
FROM public.workouts w
LEFT JOIN public.plan_workouts pw ON pw.workout_id = w.id
LEFT JOIN public.student_plans spl ON spl.plan_id = pw.plan_id AND spl.is_active = TRUE
LEFT JOIN public.student_profiles sp ON sp.id = spl.student_id
WHERE
  w.instructor_id = auth.uid()
  OR EXISTS (
    SELECT 1
    FROM public.student_profiles own_sp
    WHERE own_sp.user_id = auth.uid()
      AND own_sp.instructor_id = w.instructor_id
  )
GROUP BY w.id, w.instructor_id, w.name, w.description, w.icon_name, w.status;

CREATE OR REPLACE VIEW public.v_agego_workout_activities AS
SELECT
  wa.id,
  wa.workout_id,
  wa.order_index,
  wa.action_name,
  COALESCE(wa.description, '') AS description,
  COALESCE(wa.duration_seconds, 0)::INT AS duration_seconds
FROM public.workout_activities wa
JOIN public.workouts w ON w.id = wa.workout_id
WHERE
  w.instructor_id = auth.uid()
  OR EXISTS (
    SELECT 1
    FROM public.student_profiles own_sp
    WHERE own_sp.user_id = auth.uid()
      AND own_sp.instructor_id = w.instructor_id
  );

CREATE OR REPLACE VIEW public.v_agego_groups AS
SELECT
  g.id,
  g.instructor_id,
  g.name,
  COALESCE(g.description, '') AS description,
  COUNT(DISTINCT gm.student_id)::INT AS students_count,
  COUNT(DISTINCT gw.workout_id)::INT AS workout_count
FROM public.groups g
LEFT JOIN public.group_members gm ON gm.group_id = g.id
LEFT JOIN public.group_workouts gw ON gw.group_id = g.id
WHERE
  g.instructor_id = auth.uid()
  OR EXISTS (
    SELECT 1
    FROM public.group_members own_gm
    JOIN public.student_profiles own_sp ON own_sp.id = own_gm.student_id
    WHERE own_gm.group_id = g.id
      AND own_sp.user_id = auth.uid()
  )
GROUP BY g.id, g.instructor_id, g.name, g.description;

CREATE OR REPLACE VIEW public.v_agego_announcements AS
SELECT
  a.id,
  a.instructor_id,
  a.content,
  a.target_type::TEXT AS target_type,
  a.target_id,
  a.published_at
FROM public.announcements a
WHERE
  a.instructor_id = auth.uid()
  OR EXISTS (
    SELECT 1
    FROM public.student_profiles sp
    WHERE sp.user_id = auth.uid()
      AND sp.instructor_id = a.instructor_id
      AND (
        a.target_type = 'all'
        OR (
          a.target_type = 'group'
          AND EXISTS (
            SELECT 1
            FROM public.group_members gm
            WHERE gm.group_id = a.target_id
              AND gm.student_id = sp.id
          )
        )
        OR (
          a.target_type = 'plan'
          AND EXISTS (
            SELECT 1
            FROM public.student_plans spl
            WHERE spl.plan_id = a.target_id
              AND spl.student_id = sp.id
              AND spl.is_active = TRUE
          )
        )
      )
  );

CREATE OR REPLACE VIEW public.v_agego_weekly_workouts AS
SELECT
  w.instructor_id,
  pw.day_of_week,
  COUNT(DISTINCT pw.workout_id)::INT AS workout_count
FROM public.plan_workouts pw
JOIN public.workouts w ON w.id = pw.workout_id
WHERE
  pw.day_of_week IS NOT NULL
  AND (
    w.instructor_id = auth.uid()
    OR EXISTS (
      SELECT 1
      FROM public.student_profiles sp
      WHERE sp.user_id = auth.uid()
        AND sp.instructor_id = w.instructor_id
    )
  )
GROUP BY w.instructor_id, pw.day_of_week;

CREATE OR REPLACE VIEW public.v_agego_events AS
SELECT
  e.id,
  e.instructor_id,
  e.name,
  COALESCE(e.description, '') AS description,
  e.event_date,
  COALESCE(e.location, '') AS location,
  COALESCE(i.name, 'Professor') AS leader_name,
  COALESCE(array_length(e.target_groups, 1), 0)::INT AS target_groups_count
FROM public.events e
LEFT JOIN public.users i ON i.id = e.instructor_id
WHERE
  e.instructor_id = auth.uid()
  OR EXISTS (
    SELECT 1
    FROM public.student_profiles sp
    WHERE sp.user_id = auth.uid()
      AND sp.instructor_id = e.instructor_id
  );

CREATE OR REPLACE VIEW public.v_agego_training_now AS
SELECT
  sp.id,
  sp.user_id,
  sp.instructor_id,
  u.name,
  COALESCE(u.avatar_url, '') AS avatar_url,
  w.name AS workout_name,
  COALESCE(w.description, '') AS workout_description,
  COALESCE(w.icon_name, 'directions_run') AS icon_name,
  MAX(swl.completed_at) AS last_activity_at,
  COUNT(*)::INT AS sessions_today
FROM public.student_workout_logs swl
JOIN public.student_profiles sp ON sp.id = swl.student_id
JOIN public.users u ON u.id = sp.user_id
JOIN public.workouts w ON w.id = swl.workout_id
WHERE
  (
    swl.completed_at >= date_trunc('day', now())
    OR swl.completed_at >= now() - INTERVAL '3 hours'
  )
  AND (
    sp.instructor_id = auth.uid()
    OR sp.user_id = auth.uid()
  )
GROUP BY sp.id, sp.user_id, sp.instructor_id, u.name, u.avatar_url, w.name, w.description, w.icon_name;

GRANT SELECT ON public.v_agego_students TO authenticated;
GRANT SELECT ON public.v_agego_workouts TO authenticated;
GRANT SELECT ON public.v_agego_workout_activities TO authenticated;
GRANT SELECT ON public.v_agego_groups TO authenticated;
GRANT SELECT ON public.v_agego_announcements TO authenticated;
GRANT SELECT ON public.v_agego_weekly_workouts TO authenticated;
GRANT SELECT ON public.v_agego_events TO authenticated;
GRANT SELECT ON public.v_agego_training_now TO authenticated;
