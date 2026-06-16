-- ============================================================
-- AGE GO - Dados de demonstracao para as telas principais
--
-- Requisito: execute primeiro:
--   1. sampledata/banco.txt
--   2. sampledata/seed_admin_aluno.sql
--
-- Este seed usa:
--   admin@agego.local como instrutor
--   aluno@agego.local como aluno
-- ============================================================

DO $$
DECLARE
  v_admin_id UUID := '11111111-1111-1111-1111-111111111111';
  v_aluno_id UUID := '22222222-2222-2222-2222-222222222222';
  v_student_profile_id UUID;
  v_plan_start UUID := '33333333-3333-3333-3333-333333333331';
  v_plan_base UUID := '33333333-3333-3333-3333-333333333332';
  v_plan_perf UUID := '33333333-3333-3333-3333-333333333333';
  v_workout_easy UUID := '44444444-4444-4444-4444-444444444441';
  v_workout_interval UUID := '44444444-4444-4444-4444-444444444442';
  v_workout_long UUID := '44444444-4444-4444-4444-444444444443';
  v_group_morning UUID := '55555555-5555-5555-5555-555555555551';
  v_announcement_id UUID := '66666666-6666-6666-6666-666666666661';
  v_event_id UUID := '77777777-7777-7777-7777-777777777771';
BEGIN
  SELECT id INTO v_student_profile_id
  FROM public.student_profiles
  WHERE user_id = v_aluno_id;

  IF v_student_profile_id IS NULL THEN
    RAISE EXCEPTION 'Aluno seed nao encontrado. Execute seed_admin_aluno.sql antes.';
  END IF;

  INSERT INTO public.plans (id, instructor_id, name, description, price_monthly, price_yearly, is_active)
  VALUES
    (v_plan_start, v_admin_id, 'Start', 'Plano inicial para alunos novos.', 99.90, 999.00, TRUE),
    (v_plan_base, v_admin_id, 'Base', 'Rotina semanal com treinos guiados.', 139.90, 1399.00, TRUE),
    (v_plan_perf, v_admin_id, 'Performance', 'Acompanhamento completo com treinos avancados.', 179.90, 1798.80, TRUE)
  ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    price_monthly = EXCLUDED.price_monthly,
    price_yearly = EXCLUDED.price_yearly,
    is_active = EXCLUDED.is_active,
    updated_at = NOW();

  INSERT INTO public.workouts (id, instructor_id, name, description, icon_name, status)
  VALUES
    (v_workout_easy, v_admin_id, 'Rodagem leve', 'Treino base para construir volume semanal com controle de ritmo.', 'directions_run', 'active'),
    (v_workout_interval, v_admin_id, 'Intervalado curto', 'Estimulo de velocidade com pausas curtas.', 'timer', 'active'),
    (v_workout_long, v_admin_id, 'Longao progressivo', 'Treino longo com final em ritmo moderado.', 'route', 'active')
  ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    icon_name = EXCLUDED.icon_name,
    status = EXCLUDED.status,
    updated_at = NOW();

  DELETE FROM public.workout_activities
  WHERE workout_id IN (v_workout_easy, v_workout_interval, v_workout_long);

  INSERT INTO public.workout_activities (workout_id, order_index, action_name, description, duration_seconds)
  VALUES
    (v_workout_easy, 1, 'Aquecimento', 'Caminhada e trote leve.', 600),
    (v_workout_easy, 2, 'Corrida leve', 'Manter ritmo confortavel.', 2100),
    (v_workout_easy, 3, 'Desacelerar', 'Trote final e mobilidade.', 300),
    (v_workout_interval, 1, 'Aquecimento', 'Corrida leve antes dos tiros.', 720),
    (v_workout_interval, 2, '8 x 400m', 'Tiros fortes com pausa curta.', 1800),
    (v_workout_interval, 3, 'Trote leve', 'Volta a calma.', 600),
    (v_workout_long, 1, 'Corrida confortavel', 'Volume principal em baixa intensidade.', 3000),
    (v_workout_long, 2, 'Ritmo moderado', 'Final progressivo.', 1200),
    (v_workout_long, 3, 'Mobilidade', 'Alongamento e soltura.', 480);

  INSERT INTO public.student_plans (student_id, plan_id, start_date, billing_day, frequency_days, is_active)
  VALUES (v_student_profile_id, v_plan_perf, CURRENT_DATE, 10, ARRAY[1, 3, 5], TRUE)
  ON CONFLICT DO NOTHING;

  INSERT INTO public.plan_workouts (plan_id, workout_id, day_of_week, order_index)
  VALUES
    (v_plan_perf, v_workout_easy, 1, 1),
    (v_plan_perf, v_workout_interval, 3, 2),
    (v_plan_perf, v_workout_long, 6, 3)
  ON CONFLICT (plan_id, workout_id) DO UPDATE SET
    day_of_week = EXCLUDED.day_of_week,
    order_index = EXCLUDED.order_index;

  INSERT INTO public.groups (id, instructor_id, name, description, is_active)
  VALUES (v_group_morning, v_admin_id, 'Turma Manha', 'Grupo de treinos matinais.', TRUE)
  ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    is_active = EXCLUDED.is_active,
    updated_at = NOW();

  INSERT INTO public.group_members (group_id, student_id)
  VALUES (v_group_morning, v_student_profile_id)
  ON CONFLICT (group_id, student_id) DO NOTHING;

  INSERT INTO public.group_workouts (group_id, workout_id, due_date)
  VALUES
    (v_group_morning, v_workout_easy, CURRENT_DATE + INTERVAL '1 day'),
    (v_group_morning, v_workout_interval, CURRENT_DATE + INTERVAL '3 days')
  ON CONFLICT (group_id, workout_id) DO UPDATE SET
    due_date = EXCLUDED.due_date;

  INSERT INTO public.announcements (id, instructor_id, content, target_type, target_id, is_active)
  VALUES (
    v_announcement_id,
    v_admin_id,
    'Treino coletivo confirmado no parque neste sabado as 7h.',
    'all',
    NULL,
    TRUE
  )
  ON CONFLICT (id) DO UPDATE SET
    content = EXCLUDED.content,
    target_type = EXCLUDED.target_type,
    target_id = EXCLUDED.target_id,
    is_active = EXCLUDED.is_active;

  INSERT INTO public.events (id, instructor_id, name, description, event_date, location, target_groups)
  VALUES (
    v_event_id,
    v_admin_id,
    'Treino coletivo no parque',
    'Encontro para rodagem leve e tecnica de corrida.',
    NOW() + INTERVAL '3 days',
    'Parque principal',
    ARRAY[v_group_morning]
  )
  ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    event_date = EXCLUDED.event_date,
    location = EXCLUDED.location,
    target_groups = EXCLUDED.target_groups;

  INSERT INTO public.notifications (user_id, type, title, body, data, is_read)
  VALUES
    (
      v_aluno_id,
      'new_workout',
      'Novos treinos disponiveis',
      'Seu plano Performance recebeu treinos para esta semana.',
      jsonb_build_object('plan_id', v_plan_perf),
      FALSE
    ),
    (
      v_aluno_id,
      'new_event',
      'Evento confirmado',
      'Treino coletivo no parque em 3 dias.',
      jsonb_build_object('event_id', v_event_id),
      FALSE
    );

  RAISE NOTICE 'Dados de demonstracao criados para Admin e Aluno.';
END $$;
