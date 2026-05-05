# AgeRun Database

Execute este script no SQL Editor do Supabase. O nome do arquivo segue o pedido original (`databse.md`).

Roles oficiais do app:

- `aluno`: role padrao de todo cadastro.
- `professor`: pode gerenciar escalas, participantes e promover alunos.

```sql
create extension if not exists pgcrypto;

create table if not exists public.profiles (
  id uuid primary key references auth.users(id) on delete cascade,
  nome text not null check (char_length(trim(nome)) >= 2),
  email text not null unique,
  role text not null default 'aluno',
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists public.escalas (
  id uuid primary key default gen_random_uuid(),
  titulo text not null,
  descricao text,
  local text,
  inicio_at timestamptz not null,
  fim_at timestamptz,
  created_by uuid not null references public.profiles(id),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  check (fim_at is null or fim_at > inicio_at)
);

create table if not exists public.escala_participantes (
  escala_id uuid not null references public.escalas(id) on delete cascade,
  aluno_id uuid not null references public.profiles(id) on delete cascade,
  status text not null default 'convocado' check (status in ('convocado', 'confirmado', 'faltou', 'dispensado')),
  created_at timestamptz not null default now(),
  primary key (escala_id, aluno_id)
);

-- Compatibilidade caso voce tenha rodado a versao antiga com role = 'deus'.
update public.profiles
   set role = 'professor'
 where role = 'deus';

alter table public.profiles
  drop constraint if exists profiles_role_check;

alter table public.profiles
  add constraint profiles_role_check check (role in ('aluno', 'professor'));

create or replace function public.touch_updated_at()
returns trigger
language plpgsql
as $$
begin
  new.updated_at = now();
  return new;
end;
$$;

drop trigger if exists profiles_touch_updated_at on public.profiles;
create trigger profiles_touch_updated_at
before update on public.profiles
for each row execute function public.touch_updated_at();

drop trigger if exists escalas_touch_updated_at on public.escalas;
create trigger escalas_touch_updated_at
before update on public.escalas
for each row execute function public.touch_updated_at();

create or replace function public.current_user_role()
returns text
language sql
security definer
set search_path = public
stable
as $$
  select role from public.profiles where id = auth.uid()
$$;

create or replace function public.is_professor()
returns boolean
language sql
security definer
set search_path = public
stable
as $$
  select coalesce(public.current_user_role() = 'professor', false)
$$;

drop function if exists public.is_professor_or_deus();
drop function if exists public.is_deus();

create or replace function public.set_profile_role(target_profile_id uuid, new_role text)
returns public.profiles
language plpgsql
security definer
set search_path = public
as $$
declare
  changed_profile public.profiles;
begin
  if not public.is_professor() then
    raise exception 'Apenas professores podem alterar roles.';
  end if;

  if new_role not in ('aluno', 'professor') then
    raise exception 'Role invalida.';
  end if;

  update public.profiles
     set role = new_role
   where id = target_profile_id
   returning * into changed_profile;

  if changed_profile.id is null then
    raise exception 'Perfil nao encontrado.';
  end if;

  return changed_profile;
end;
$$;

drop function if exists public.promote_profile(uuid, text);

alter table public.profiles enable row level security;
alter table public.escalas enable row level security;
alter table public.escala_participantes enable row level security;

drop policy if exists "profiles_select_own_or_staff" on public.profiles;
drop policy if exists "profiles_select_own_or_professor" on public.profiles;
create policy "profiles_select_own_or_professor"
on public.profiles for select
to authenticated
using (id = auth.uid() or public.is_professor());

drop policy if exists "profiles_insert_self_as_aluno" on public.profiles;
create policy "profiles_insert_self_as_aluno"
on public.profiles for insert
to authenticated
with check (id = auth.uid() and role = 'aluno');

drop policy if exists "profiles_update_own_basic_data" on public.profiles;
create policy "profiles_update_own_basic_data"
on public.profiles for update
to authenticated
using (id = auth.uid())
with check (id = auth.uid());

drop policy if exists "escalas_select_all_authenticated" on public.escalas;
create policy "escalas_select_all_authenticated"
on public.escalas for select
to authenticated
using (true);

drop policy if exists "escalas_insert_staff" on public.escalas;
drop policy if exists "escalas_insert_professor" on public.escalas;
create policy "escalas_insert_professor"
on public.escalas for insert
to authenticated
with check (public.is_professor() and created_by = auth.uid());

drop policy if exists "escalas_update_staff" on public.escalas;
drop policy if exists "escalas_update_professor" on public.escalas;
create policy "escalas_update_professor"
on public.escalas for update
to authenticated
using (public.is_professor())
with check (public.is_professor());

drop policy if exists "escalas_delete_deus" on public.escalas;
drop policy if exists "escalas_delete_professor" on public.escalas;
create policy "escalas_delete_professor"
on public.escalas for delete
to authenticated
using (public.is_professor());

drop policy if exists "participantes_select_own_or_staff" on public.escala_participantes;
drop policy if exists "participantes_select_own_or_professor" on public.escala_participantes;
create policy "participantes_select_own_or_professor"
on public.escala_participantes for select
to authenticated
using (aluno_id = auth.uid() or public.is_professor());

drop policy if exists "participantes_insert_staff" on public.escala_participantes;
drop policy if exists "participantes_insert_professor" on public.escala_participantes;
create policy "participantes_insert_professor"
on public.escala_participantes for insert
to authenticated
with check (public.is_professor());

drop policy if exists "participantes_update_staff_or_self_confirm" on public.escala_participantes;
drop policy if exists "participantes_update_professor_or_self_confirm" on public.escala_participantes;
create policy "participantes_update_professor_or_self_confirm"
on public.escala_participantes for update
to authenticated
using (public.is_professor() or aluno_id = auth.uid())
with check (
  public.is_professor()
  or (aluno_id = auth.uid() and status in ('confirmado', 'dispensado'))
);

drop policy if exists "participantes_delete_staff" on public.escala_participantes;
drop policy if exists "participantes_delete_professor" on public.escala_participantes;
create policy "participantes_delete_professor"
on public.escala_participantes for delete
to authenticated
using (public.is_professor());

revoke all on public.profiles from anon, authenticated;
revoke all on public.escalas from anon, authenticated;
revoke all on public.escala_participantes from anon, authenticated;

grant select, insert on public.profiles to authenticated;
grant update (nome) on public.profiles to authenticated;
grant select, insert, update, delete on public.escalas to authenticated;
grant select, insert, update, delete on public.escala_participantes to authenticated;
grant execute on function public.set_profile_role(uuid, text) to authenticated;
grant execute on function public.current_user_role() to authenticated;
grant execute on function public.is_professor() to authenticated;
```

## Criando o primeiro professor

1. Cadastre o usuario normalmente pelo app/backend.
2. No Supabase, rode este update uma unica vez, trocando o email:

```sql
update public.profiles
   set role = 'professor'
 where email = 'email@exemplo.com';
```

Depois disso, esse professor consegue alterar roles via RPC:

```sql
select public.set_profile_role('uuid-do-aluno', 'professor');
select public.set_profile_role('uuid-do-professor', 'aluno');
```
