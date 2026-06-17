# AgeGo API

Backend Node para operacoes que nao devem rodar no app Android com `service_role`.

## Setup

```bash
cd C:\Users\megan\AndroidStudioProjects\Agego\api
npm install
npm run dev
```

O `.env` precisa conter:

- `SUPABASE_URL`
- `SUPABASE_ANON_KEY`
- `SUPABASE_SERVICE_ROLE_KEY`
- `PORT=3333`

Nunca coloque `SUPABASE_SERVICE_ROLE_KEY` no app Android.

No app Android, o endpoint padrao em desenvolvimento e:

```properties
AGEGO_API_URL=http://10.0.2.2:3333
```

Em celular fisico, use o IP da maquina na rede:

```properties
AGEGO_API_URL=http://SEU_IP_LOCAL:3333
```

## Fluxo Atual

Enquanto nao existe tela de login, a API aceita chamadas sem Bearer token e usa o primeiro instrutor encontrado no Supabase. Se nao houver instrutor, cria `admin@agego.local` automaticamente.

Quando houver login, envie:

```http
Authorization: Bearer <access_token>
```

## Rotas

- `GET /health`
- `GET /dashboard`

CRUDs:

- `GET /students`
- `POST /students`
- `PUT /students/:id`
- `DELETE /students/:id`
- `GET /workouts`
- `POST /workouts`
- `PUT /workouts/:id`
- `DELETE /workouts/:id`
- `GET /groups`
- `POST /groups`
- `PUT /groups/:id`
- `DELETE /groups/:id`
- `GET /routines`
- `POST /routines`
- `PUT /routines/:id`
- `DELETE /routines/:id`
- `GET /events`
- `POST /events`
- `PUT /events/:id`
- `DELETE /events/:id`
- `POST /announcements`

Comunidade:

- `GET /posts`
- `POST /posts`
- `POST /posts/:id/like`
- `POST /posts/:id/share`
- `POST /posts/:id/comments`
- `POST /comments/:id/like`
- `POST /poll-options/:id/vote`

Grupos aceitam `studentIds` para selecionar pessoas:

```json
{
  "name": "Turma Manha",
  "description": "Grupo de treinos matinais",
  "status": "active",
  "studentIds": ["uuid-do-student-profile"]
}
```
