-- ============================================================
-- AGE GO - Seed de usuarios de teste para Supabase
-- Cria:
--   Admin/Instrutor: admin@agego.local / Admin@123456
--   Aluno:           aluno@agego.local / Aluno@123456
--
-- Observacao: no schema atual nao existe role "admin".
-- O usuario admin abaixo usa a role "instructor".
--
-- Execute no SQL Editor do Supabase depois do script banco.txt.
-- ============================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

DO $$
DECLARE
  v_admin_id UUID := '11111111-1111-1111-1111-111111111111';
  v_aluno_id UUID := '22222222-2222-2222-2222-222222222222';
  v_student_profile_id UUID;
  v_identity_id_type TEXT;
  v_has_provider_id BOOLEAN;
BEGIN
  -- Remove sessoes antigas para permitir reexecutar o seed.
  DELETE FROM auth.refresh_tokens WHERE user_id::uuid IN (v_admin_id, v_aluno_id);
  DELETE FROM auth.sessions WHERE user_id IN (v_admin_id, v_aluno_id);
  DELETE FROM auth.identities WHERE user_id IN (v_admin_id, v_aluno_id);

  -- Remove perfis publicos antes dos usuarios auth.
  DELETE FROM public.student_profiles WHERE user_id IN (v_admin_id, v_aluno_id);
  DELETE FROM public.instructor_profiles WHERE user_id IN (v_admin_id, v_aluno_id);
  DELETE FROM public.users WHERE id IN (v_admin_id, v_aluno_id);
  DELETE FROM auth.users WHERE id IN (v_admin_id, v_aluno_id);

  INSERT INTO auth.users (
    id,
    instance_id,
    aud,
    role,
    email,
    encrypted_password,
    email_confirmed_at,
    raw_app_meta_data,
    raw_user_meta_data,
    created_at,
    updated_at,
    confirmation_token,
    email_change,
    email_change_token_new,
    recovery_token
  )
  VALUES
    (
      v_admin_id,
      '00000000-0000-0000-0000-000000000000',
      'authenticated',
      'authenticated',
      'admin@agego.local',
      crypt('Admin@123456', gen_salt('bf')),
      NOW(),
      '{"provider":"email","providers":["email"]}'::jsonb,
      '{"name":"Admin AgeGo","role":"instructor","avatar_url":"https://i.pravatar.cc/120?img=12"}'::jsonb,
      NOW(),
      NOW(),
      '',
      '',
      '',
      ''
    ),
    (
      v_aluno_id,
      '00000000-0000-0000-0000-000000000000',
      'authenticated',
      'authenticated',
      'aluno@agego.local',
      crypt('Aluno@123456', gen_salt('bf')),
      NOW(),
      '{"provider":"email","providers":["email"]}'::jsonb,
      '{"name":"Aluno Teste","role":"student","avatar_url":"https://i.pravatar.cc/120?img=13"}'::jsonb,
      NOW(),
      NOW(),
      '',
      '',
      '',
      ''
    );

  -- Compatibilidade com versoes diferentes do schema auth.identities do Supabase.
  SELECT data_type
    INTO v_identity_id_type
  FROM information_schema.columns
  WHERE table_schema = 'auth'
    AND table_name = 'identities'
    AND column_name = 'id';

  SELECT EXISTS (
    SELECT 1
    FROM information_schema.columns
    WHERE table_schema = 'auth'
      AND table_name = 'identities'
      AND column_name = 'provider_id'
  )
    INTO v_has_provider_id;

  IF v_has_provider_id THEN
    IF v_identity_id_type = 'uuid' THEN
      INSERT INTO auth.identities (
        id,
        user_id,
        provider_id,
        identity_data,
        provider,
        last_sign_in_at,
        created_at,
        updated_at
      )
      VALUES
        (
          gen_random_uuid(),
          v_admin_id,
          'admin@agego.local',
          jsonb_build_object(
            'sub', v_admin_id::text,
            'email', 'admin@agego.local',
            'email_verified', true,
            'phone_verified', false
          ),
          'email',
          NOW(),
          NOW(),
          NOW()
        ),
        (
          gen_random_uuid(),
          v_aluno_id,
          'aluno@agego.local',
          jsonb_build_object(
            'sub', v_aluno_id::text,
            'email', 'aluno@agego.local',
            'email_verified', true,
            'phone_verified', false
          ),
          'email',
          NOW(),
          NOW(),
          NOW()
        );
    ELSE
      INSERT INTO auth.identities (
        id,
        user_id,
        provider_id,
        identity_data,
        provider,
        last_sign_in_at,
        created_at,
        updated_at
      )
      VALUES
        (
          v_admin_id::text,
          v_admin_id,
          'admin@agego.local',
          jsonb_build_object(
            'sub', v_admin_id::text,
            'email', 'admin@agego.local',
            'email_verified', true,
            'phone_verified', false
          ),
          'email',
          NOW(),
          NOW(),
          NOW()
        ),
        (
          v_aluno_id::text,
          v_aluno_id,
          'aluno@agego.local',
          jsonb_build_object(
            'sub', v_aluno_id::text,
            'email', 'aluno@agego.local',
            'email_verified', true,
            'phone_verified', false
          ),
          'email',
          NOW(),
          NOW(),
          NOW()
        );
    END IF;
  ELSE
    IF v_identity_id_type = 'uuid' THEN
      INSERT INTO auth.identities (
        id,
        user_id,
        identity_data,
        provider,
        last_sign_in_at,
        created_at,
        updated_at
      )
      VALUES
        (
          gen_random_uuid(),
          v_admin_id,
          jsonb_build_object(
            'sub', v_admin_id::text,
            'email', 'admin@agego.local',
            'email_verified', true,
            'phone_verified', false
          ),
          'email',
          NOW(),
          NOW(),
          NOW()
        ),
        (
          gen_random_uuid(),
          v_aluno_id,
          jsonb_build_object(
            'sub', v_aluno_id::text,
            'email', 'aluno@agego.local',
            'email_verified', true,
            'phone_verified', false
          ),
          'email',
          NOW(),
          NOW(),
          NOW()
        );
    ELSE
      INSERT INTO auth.identities (
        id,
        user_id,
        identity_data,
        provider,
        last_sign_in_at,
        created_at,
        updated_at
      )
      VALUES
        (
          v_admin_id::text,
          v_admin_id,
          jsonb_build_object(
            'sub', v_admin_id::text,
            'email', 'admin@agego.local',
            'email_verified', true,
            'phone_verified', false
          ),
          'email',
          NOW(),
          NOW(),
          NOW()
        ),
        (
          v_aluno_id::text,
          v_aluno_id,
          jsonb_build_object(
            'sub', v_aluno_id::text,
            'email', 'aluno@agego.local',
            'email_verified', true,
            'phone_verified', false
          ),
          'email',
          NOW(),
          NOW(),
          NOW()
        );
    END IF;
  END IF;

  -- Garante os dados na tabela publica caso o trigger nao rode por alguma mudanca.
  INSERT INTO public.users (id, email, name, phone, avatar_url, role, is_active)
  VALUES
    (v_admin_id, 'admin@agego.local', 'Admin AgeGo', '+55 11 90000-0001', 'https://i.pravatar.cc/120?img=12', 'instructor', TRUE),
    (v_aluno_id, 'aluno@agego.local', 'Aluno Teste', '+55 11 90000-0002', 'https://i.pravatar.cc/120?img=13', 'student', TRUE)
  ON CONFLICT (id) DO UPDATE SET
    email = EXCLUDED.email,
    name = EXCLUDED.name,
    phone = EXCLUDED.phone,
    avatar_url = EXCLUDED.avatar_url,
    role = EXCLUDED.role,
    is_active = EXCLUDED.is_active,
    updated_at = NOW();

  INSERT INTO public.instructor_profiles (user_id, bio, specialty, instagram)
  VALUES (
    v_admin_id,
    'Administrador de teste do AgeGo.',
    'Corrida e condicionamento fisico',
    '@agego.admin'
  )
  ON CONFLICT (user_id) DO UPDATE SET
    bio = EXCLUDED.bio,
    specialty = EXCLUDED.specialty,
    instagram = EXCLUDED.instagram;

  INSERT INTO public.student_profiles (
    user_id,
    instructor_id,
    birth_date,
    gender,
    fitness_level,
    notes,
    status
  )
  VALUES (
    v_aluno_id,
    v_admin_id,
    DATE '1998-05-20',
    'female',
    'beginner',
    'Aluno criado para teste de login e vinculacao ao instrutor admin.',
    'active'
  )
  ON CONFLICT (user_id) DO UPDATE SET
    instructor_id = EXCLUDED.instructor_id,
    birth_date = EXCLUDED.birth_date,
    gender = EXCLUDED.gender,
    fitness_level = EXCLUDED.fitness_level,
    notes = EXCLUDED.notes,
    status = EXCLUDED.status,
    updated_at = NOW()
  RETURNING id INTO v_student_profile_id;

  RAISE NOTICE 'Seed concluido. Admin: admin@agego.local / Admin@123456';
  RAISE NOTICE 'Seed concluido. Aluno: aluno@agego.local / Aluno@123456';
END $$;
