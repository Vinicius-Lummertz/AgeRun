# 05 — Arquitetura de Dados

## 5.1 Visão Geral

O banco de dados é gerenciado pelo **Supabase** (PostgreSQL como base). As tabelas abaixo representam a modelagem relacional do sistema.

---

## 5.2 Entidades Principais

### users (Usuários)
```sql
CREATE TABLE users (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email         VARCHAR(255) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  role          ENUM('instructor', 'student') NOT NULL,
  name          VARCHAR(255) NOT NULL,
  phone         VARCHAR(20),
  avatar_url    VARCHAR(500),
  created_at    TIMESTAMPTZ DEFAULT NOW(),
  updated_at    TIMESTAMPTZ DEFAULT NOW(),
  is_active     BOOLEAN DEFAULT TRUE
);
```

---

### instructor_profiles (Perfil do Instrutor)
```sql
CREATE TABLE instructor_profiles (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id       UUID REFERENCES users(id) ON DELETE CASCADE,
  bio           TEXT,
  specialty     VARCHAR(255),
  instagram     VARCHAR(100),
  created_at    TIMESTAMPTZ DEFAULT NOW()
);
```

---

### student_profiles (Perfil do Aluno)
```sql
CREATE TABLE student_profiles (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id         UUID REFERENCES users(id) ON DELETE CASCADE,
  instructor_id   UUID REFERENCES users(id),
  birth_date      DATE,
  gender          VARCHAR(20),
  fitness_level   ENUM('beginner', 'intermediate', 'advanced'),
  notes           TEXT,
  status          ENUM('active', 'pending_payment', 'inactive') DEFAULT 'active',
  created_at      TIMESTAMPTZ DEFAULT NOW(),
  updated_at      TIMESTAMPTZ DEFAULT NOW()
);
```

---

### plans (Planos)
```sql
CREATE TABLE plans (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  instructor_id   UUID REFERENCES users(id) ON DELETE CASCADE,
  name            VARCHAR(255) NOT NULL,
  description     TEXT,
  price_monthly   DECIMAL(10,2) NOT NULL,
  price_yearly    DECIMAL(10,2),
  is_active       BOOLEAN DEFAULT TRUE,
  created_at      TIMESTAMPTZ DEFAULT NOW(),
  updated_at      TIMESTAMPTZ DEFAULT NOW()
);
```

---

### student_plans (Vínculo Aluno-Plano)
```sql
CREATE TABLE student_plans (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  student_id      UUID REFERENCES student_profiles(id),
  plan_id         UUID REFERENCES plans(id),
  start_date      DATE NOT NULL,
  end_date        DATE,
  custom_discount DECIMAL(10,2) DEFAULT 0,
  billing_day     INTEGER CHECK (billing_day BETWEEN 1 AND 28),
  frequency_days  INTEGER[], -- array de dias da semana [0=dom, 1=seg...]
  is_active       BOOLEAN DEFAULT TRUE,
  created_at      TIMESTAMPTZ DEFAULT NOW()
);
```

---

### workouts (Treinos)
```sql
CREATE TABLE workouts (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  instructor_id   UUID REFERENCES users(id) ON DELETE CASCADE,
  name            VARCHAR(255) NOT NULL,
  description     TEXT,
  icon_name       VARCHAR(100),
  status          ENUM('active', 'draft', 'inactive') DEFAULT 'draft',
  created_at      TIMESTAMPTZ DEFAULT NOW(),
  updated_at      TIMESTAMPTZ DEFAULT NOW()
);
```

---

### workout_activities (Atividades de Treino)
```sql
CREATE TABLE workout_activities (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  workout_id      UUID REFERENCES workouts(id) ON DELETE CASCADE,
  order_index     INTEGER NOT NULL,
  action_name     VARCHAR(255) NOT NULL,
  description     TEXT,
  duration_seconds INTEGER,
  created_at      TIMESTAMPTZ DEFAULT NOW()
);
```

---

### plan_workouts (Treinos por Plano)
```sql
CREATE TABLE plan_workouts (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  plan_id     UUID REFERENCES plans(id) ON DELETE CASCADE,
  workout_id  UUID REFERENCES workouts(id) ON DELETE CASCADE,
  day_of_week INTEGER, -- 0=dom, 1=seg... null=livre
  order_index INTEGER DEFAULT 0,
  UNIQUE(plan_id, workout_id)
);
```

---

### student_workout_logs (Registro de Treinos Realizados)
```sql
CREATE TABLE student_workout_logs (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  student_id      UUID REFERENCES student_profiles(id),
  workout_id      UUID REFERENCES workouts(id),
  completed_at    TIMESTAMPTZ NOT NULL,
  duration_actual INTEGER, -- em segundos
  distance_m      DECIMAL(10,2), -- metros
  avg_pace        DECIMAL(5,2),  -- min/km
  avg_bpm         INTEGER,
  notes           TEXT,
  gps_data        JSONB,         -- rota GPS completa (fase 2)
  created_at      TIMESTAMPTZ DEFAULT NOW()
);
```

---

### groups (Turmas)
```sql
CREATE TABLE groups (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  instructor_id   UUID REFERENCES users(id) ON DELETE CASCADE,
  name            VARCHAR(255) NOT NULL,
  description     TEXT,
  is_active       BOOLEAN DEFAULT TRUE,
  created_at      TIMESTAMPTZ DEFAULT NOW(),
  updated_at      TIMESTAMPTZ DEFAULT NOW()
);
```

---

### group_members (Membros de Turma)
```sql
CREATE TABLE group_members (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  group_id    UUID REFERENCES groups(id) ON DELETE CASCADE,
  student_id  UUID REFERENCES student_profiles(id) ON DELETE CASCADE,
  joined_at   TIMESTAMPTZ DEFAULT NOW(),
  UNIQUE(group_id, student_id)
);
```

---

### group_workouts (Treinos da Turma)
```sql
CREATE TABLE group_workouts (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  group_id    UUID REFERENCES groups(id) ON DELETE CASCADE,
  workout_id  UUID REFERENCES workouts(id),
  assigned_at TIMESTAMPTZ DEFAULT NOW(),
  due_date    DATE
);
```

---

### announcements (Comunicados)
```sql
CREATE TABLE announcements (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  instructor_id   UUID REFERENCES users(id) ON DELETE CASCADE,
  content         TEXT NOT NULL,
  target_type     ENUM('all', 'group', 'plan') DEFAULT 'all',
  target_id       UUID, -- group_id ou plan_id (null se 'all')
  published_at    TIMESTAMPTZ DEFAULT NOW(),
  is_active       BOOLEAN DEFAULT TRUE
);
```

---

### announcement_reactions (Reações a Comunicados)
```sql
CREATE TABLE announcement_reactions (
  id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  announcement_id   UUID REFERENCES announcements(id) ON DELETE CASCADE,
  student_id        UUID REFERENCES student_profiles(id),
  emoji             VARCHAR(10) NOT NULL,
  created_at        TIMESTAMPTZ DEFAULT NOW(),
  UNIQUE(announcement_id, student_id, emoji)
);
```

---

### events (Eventos)
```sql
CREATE TABLE events (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  instructor_id   UUID REFERENCES users(id) ON DELETE CASCADE,
  name            VARCHAR(255) NOT NULL,
  description     TEXT,
  event_date      TIMESTAMPTZ NOT NULL,
  location        VARCHAR(500),
  target_groups   UUID[], -- array de group_ids
  created_at      TIMESTAMPTZ DEFAULT NOW()
);
```

---

### event_attendance (Presença em Eventos)
```sql
CREATE TABLE event_attendance (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  event_id    UUID REFERENCES events(id) ON DELETE CASCADE,
  student_id  UUID REFERENCES student_profiles(id),
  attended    BOOLEAN DEFAULT FALSE,
  registered_at TIMESTAMPTZ DEFAULT NOW(),
  UNIQUE(event_id, student_id)
);
```

---

### notifications (Notificações)
```sql
CREATE TABLE notifications (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id     UUID REFERENCES users(id) ON DELETE CASCADE,
  type        VARCHAR(100) NOT NULL,
  title       VARCHAR(255) NOT NULL,
  body        TEXT,
  data        JSONB,
  is_read     BOOLEAN DEFAULT FALSE,
  created_at  TIMESTAMPTZ DEFAULT NOW()
);
```

---

## 5.3 Relacionamentos (Diagrama Simplificado)

```
users
  ├── instructor_profiles (1:1)
  ├── student_profiles (1:1)
  │     ├── student_plans (1:N)
  │     │     └── plans (N:1)
  │     │           └── plan_workouts (1:N)
  │     │                 └── workouts (N:1)
  │     │                       └── workout_activities (1:N)
  │     ├── student_workout_logs (1:N)
  │     ├── group_members (1:N)
  │     │     └── groups (N:1)
  │     │           └── group_workouts (1:N)
  │     └── announcement_reactions (1:N)
  └── announcements (1:N)
        └── announcement_reactions (1:N)

events
  └── event_attendance (1:N)
        └── student_profiles (N:1)
```

---

## 5.4 Índices Recomendados

```sql
-- Performance em buscas frequentes
CREATE INDEX idx_student_profiles_instructor ON student_profiles(instructor_id);
CREATE INDEX idx_student_plans_student ON student_plans(student_id);
CREATE INDEX idx_workout_logs_student ON student_workout_logs(student_id);
CREATE INDEX idx_workout_logs_completed ON student_workout_logs(completed_at);
CREATE INDEX idx_group_members_group ON group_members(group_id);
CREATE INDEX idx_announcements_instructor ON announcements(instructor_id);
CREATE INDEX idx_notifications_user_unread ON notifications(user_id, is_read);
```

---

## 5.5 Políticas de Row Level Security (Supabase RLS)

```sql
-- Alunos só veem seus próprios dados
ALTER TABLE student_workout_logs ENABLE ROW LEVEL SECURITY;
CREATE POLICY "student_own_logs" ON student_workout_logs
  USING (student_id = auth.uid()::uuid OR
         EXISTS (SELECT 1 FROM student_profiles sp
                 WHERE sp.id = student_id
                 AND sp.instructor_id = auth.uid()::uuid));

-- Instrutores só veem alunos que gerenciam
ALTER TABLE student_profiles ENABLE ROW LEVEL SECURITY;
CREATE POLICY "instructor_own_students" ON student_profiles
  USING (user_id = auth.uid()::uuid OR instructor_id = auth.uid()::uuid);
```

---

## 5.6 Considerações de Escalabilidade

- **Particionamento:** A tabela `student_workout_logs` deve ser particionada por mês após atingir 1M de registros
- **Archiving:** Logs de treino com mais de 2 anos podem ser movidos para tabela de arquivo
- **Caching:** Dados de dashboard (contadores de alunos, treinos em dia) devem ser cacheados por 5 minutos no backend
- **Realtime:** Supabase Realtime pode ser utilizado para o feed de comunicados e reações sem polling
