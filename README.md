<div align="center">

<img src="app/src/main/res/drawable-nodpi/logo_sem_fundo.png" alt="AgeGo" width="180"/>

# AgeGo

Aplicativo Android para gestao de academias, personal trainers e alunos.

https://canva.link/wcfmyggbkx24whl

</div>

---

## Sobre o projeto

AgeGo conecta professores (instrutores) e alunos em um unico app: o professor cadastra turmas, treinos, eventos e cobranca mensal, enquanto o aluno acompanha seus treinos, resultados, pagamentos e comunidade direto do celular.

O app e composto por tres partes:

- **App Android** (Kotlin) — interface usada por professores e alunos.
- **Servidor remoto** (`remote-server.js`) — API REST que centraliza autenticacao, treinos, pagamentos (Asaas) e dados no Supabase.
- **Painel web do professor** (`web-dashboard-server.js`) — dashboard acessado pelo navegador para o professor gerenciar alunos remotamente.

## Acesso remoto do professor

O painel web do professor pode ser acessado em:

[https://3jj803zs-4444.brs.devtunnels.ms/login](https://3jj803zs-4444.brs.devtunnels.ms/login)

Use o login e senha cadastrados como instrutor para entrar.

## Principais funcionalidades

- Cadastro e gerenciamento de alunos e turmas
- Montagem e atribuicao de treinos e rotinas
- Eventos e mapa de eventos
- Comunidade com upload de fotos
- Cobranca mensal via Pix e assinatura por cartao (integracao Asaas)
- Monitoramento de presenca e "treinando agora"
- Resultados e desempenho dos alunos
- Avisos e notificacoes

## Tecnologias

- **App:** Kotlin, Jetpack Compose
- **Backend:** Node.js, Express, Supabase (PostgreSQL)
- **Pagamentos:** Asaas (Pix e assinatura recorrente)
- **Outros:** Zod, EJS, dotenv

## Estrutura do projeto

```
app/                    Codigo do aplicativo Android
remote-server.js        API usada pelo app (autenticacao, treinos, pagamentos)
web-dashboard-server.js Painel web de acesso remoto do professor
tools/                  Scripts de apoio ao Supabase
```

## Como rodar localmente

### Servidor

```
npm install
npm run server   # API usada pelo app (remote-server.js)
npm run web      # painel web do professor (web-dashboard-server.js)
```

Configure as variaveis de ambiente a partir de `.env.example`.

### App Android

Abra a pasta do projeto no Android Studio e execute no emulador ou em um dispositivo fisico.

```
./gradlew assembleDebug
```

## Licenca

Projeto privado, uso restrito.
