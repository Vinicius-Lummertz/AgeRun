# 08 — Etapas de Desenvolvimento

## 8.1 Visão Geral das Fases

O projeto é dividido em 3 fases maiores, cada uma com entregas incrementais e critérios claros de aceite.

| Fase | Nome | Objetivo | Duração Estimada |
|---|---|---|---|
| **Fase 1** | MVP — Core | Gestão básica de alunos, treinos e comunicação | 8–10 semanas |
| **Fase 2** | Monitoramento | GPS, acelerômetro, modo de corrida, modo offline | 4–6 semanas |
| **Fase 3** | Financeiro & Escala | Cobranças automáticas, relatórios, múltiplos instrutores | 6–8 semanas |

---

## 8.2 Fase 1 — MVP Core

### Sprint 0 — Setup e Infraestrutura (1 semana)

**Objetivos:**
- Configurar o projeto Android Studio (Kotlin)
- Configurar backend Node.js
- Configurar Supabase (banco, autenticação, RLS básico)
- Definir estrutura de pastas e arquitetura (MVVM + Clean Architecture)
- Configurar CI/CD básico (GitHub Actions)

**Entregas:**
- [ ] Projeto Android criado e versionado no Git
- [ ] Backend Node.js com estrutura base (Express ou Fastify)
- [ ] Supabase configurado com tabelas iniciais (users, instructor_profiles, student_profiles)
- [ ] Autenticação funcionando (login/registro via Supabase Auth)
- [ ] Ambiente de desenvolvimento e staging separados

**Critérios de aceite:**
- Login e registro funcionam end-to-end
- Token JWT é armazenado e renovado corretamente
- Requisições autenticadas funcionam no Postman

---

### Sprint 1 — Autenticação e Perfis (1,5 semanas)

**Objetivos:**
- Telas de Login, Registro, Recuperação de senha
- Splash screen com redirecionamento por perfil
- Tela de perfil do usuário (instrutor e aluno)

**Entregas:**
- [ ] SplashActivity com lógica de redirecionamento
- [ ] LoginFragment completo com validação
- [ ] RegisterFragment (cadastro de instrutor)
- [ ] ForgotPasswordFragment
- [ ] PerfilFragment (visualização e edição básica)
- [ ] Suporte a dois perfis distintos na navegação

**Critérios de aceite:**
- Usuário faz login e é redirecionado para a Home correta (instrutor vs aluno)
- Recuperação de senha envia e-mail
- Edição de perfil salva e reflete imediatamente

---

### Sprint 2 — Home e Navegação Base (1 semana)

**Objetivos:**
- HomeFragment do Instrutor com dados reais
- HomeFragment do Aluno com dados reais
- BottomNavigationBar funcional para ambos os perfis
- Design System implementado (cores, fontes, componentes base)

**Entregas:**
- [ ] HomeFragment Instrutor (greeting, resumo semanal, atalhos)
- [ ] HomeFragment Aluno (greeting, progresso, próximo evento)
- [ ] BottomNavigationBar configurado por perfil
- [ ] Tema do app definido (colors.xml, themes.xml, typography)
- [ ] Componentes base criados: Button, TextField, Card, Avatar

**Critérios de aceite:**
- Home exibe dados reais do banco
- Navegação entre tabs funciona sem perda de estado
- Design System aplicado consistentemente

---

### Sprint 3 — Gestão de Alunos (2 semanas)

**Objetivos:**
- CRUD completo de alunos
- Cadastro multi-step de novo aluno
- Perfil do aluno com tabs

**Entregas:**
- [ ] ListaAlunosFragment com busca e filtros
- [ ] Contadores no topo (total, em dia, a pagar, inativos)
- [ ] NovoAlunoActivity com 4 passos (nome, contatos, plano, configuração)
- [ ] PerfilAlunoFragment com tabs (dados, treinos, rendimento)
- [ ] Edição de dados do aluno
- [ ] Mudança de plano do aluno
- [ ] Inativar aluno

**Critérios de aceite:**
- Cadastro completo de aluno persiste no banco
- Busca por nome retorna resultados em tempo real
- Filtros funcionam corretamente
- Contadores refletem o estado real dos alunos

---

### Sprint 4 — Gestão de Treinos (2 semanas)

**Objetivos:**
- CRUD completo de treinos
- Criação multi-step de treino
- Atribuição de treinos a planos e alunos

**Entregas:**
- [ ] ListaTreinosFragment com busca e filtros
- [ ] NovoTreinoActivity com 2 passos (título + atividades)
- [ ] DetalheTreinoFragment (informações + lista de alunos)
- [ ] EditarTreinoFragment
- [ ] Atribuição de treino a aluno específico
- [ ] Visualização de treinos pelo aluno
- [ ] Aluno marca treino como completo
- [ ] Registro de observação ao completar treino

**Critérios de aceite:**
- Treino criado aparece na lista
- Aluno vinculado vê o treino em sua lista
- Aluno consegue marcar como completo e observação é salva
- Status do treino reflete corretamente

---

### Sprint 5 — Turmas e Comunicados (1,5 semanas)

**Objetivos:**
- CRUD de turmas
- Feed de comunicados
- Reações em comunicados

**Entregas:**
- [ ] ListaTurmasFragment
- [ ] NovaTurmaFragment com seleção de alunos
- [ ] DetalheTurmaFragment (alunos, treinos, feed)
- [ ] FeedComunicadosFragment (instrutor e aluno)
- [ ] NovoComunicadoFragment com seleção de destinatários
- [ ] Sistema de reações (emojis)

**Critérios de aceite:**
- Turma criada com alunos aparece na lista
- Comunicado publicado aparece no feed de todos os destinatários
- Reações são contabilizadas e exibidas corretamente

---

### Sprint 6 — Notificações e Polimento (1 semana)

**Objetivos:**
- Push notifications via Firebase Cloud Messaging (FCM)
- Testes de integração
- Correções de bugs e polimento de UX

**Entregas:**
- [ ] Integração FCM para notificações push
- [ ] Notificações: novo treino, treino pendente, novo comunicado
- [ ] Gerenciamento de notificações nas configurações
- [ ] Testes de fluxo completo (instrutor + aluno)
- [ ] Empty states em todas as listas
- [ ] Tratamento de erros com mensagens amigáveis
- [ ] Loading states em todas as chamadas async

**Critérios de aceite:**
- Notificações chegam corretamente nos dispositivos
- App funciona sem crashes nos fluxos principais
- Empty states exibidos quando listas estão vazias

---

## 8.3 Fase 2 — Monitoramento e Offline

### Sprint 7 — Modo de Corrida (2 semanas)

**Objetivos:**
- Integração com GPS e acelerômetro
- Cronômetro de treino
- Registro automático de métricas

**Entregas:**
- [ ] ModoCorridaActivity com timer
- [ ] Integração com LocationManager (GPS)
- [ ] Cálculo de distância, pace e cadência em tempo real
- [ ] Tela de resumo pós-treino
- [ ] Salvar log de treino com dados de GPS

**Critérios de aceite:**
- GPS captura rota com precisão aceitável
- Pace é calculado e exibido em tempo real
- Dados salvos corretamente no histórico

---

### Sprint 8 — Progresso e Offline (2 semanas)

**Objetivos:**
- Dashboard de evolução para aluno
- Gráficos de desempenho
- Cache offline para treinos

**Entregas:**
- [ ] ProgressoAlunoFragment com gráficos (distância, pace, frequência)
- [ ] Componente de gráfico (MPAndroidChart ou similar)
- [ ] Cache local de treinos (Room Database)
- [ ] Sincronização quando conexão retorna
- [ ] Indicador de status de sincronização

**Critérios de aceite:**
- Gráficos exibem dados reais dos últimos 30 dias
- Aluno acessa treinos sem internet (dados cacheados)
- Sincronização ocorre automaticamente ao reconectar

---

## 8.4 Fase 3 — Financeiro e Escala

### Sprint 9 — Gestão Financeira (2 semanas)

**Entregas:**
- [ ] Dashboard financeiro para instrutor
- [ ] Controle de cobranças (manual na fase 3.1)
- [ ] Alertas de inadimplência
- [ ] Integração com gateway de pagamento (Stripe ou Pagar.me)
- [ ] Cobrança automática recorrente

### Sprint 10 — Eventos e Presença (1 semana)

**Entregas:**
- [ ] CRUD de eventos
- [ ] Tela de controle de presença
- [ ] Histórico de presença por aluno

### Sprint 11 — Relatórios e Admin (1,5 semanas)

**Entregas:**
- [ ] Relatório de desempenho por turma
- [ ] Exportação de dados (PDF/CSV)
- [ ] Suporte a múltiplos instrutores independentes

---

## 8.5 Arquitetura de Código Recomendada

### Padrão: MVVM + Clean Architecture

```
app/
├── data/
│   ├── local/          # Room Database (cache offline)
│   ├── remote/         # Retrofit + Supabase API calls
│   └── repository/     # Implementações dos repositórios
├── domain/
│   ├── model/          # Entidades de domínio
│   ├── repository/     # Interfaces de repositório
│   └── usecase/        # Casos de uso (regras de negócio)
├── presentation/
│   ├── auth/           # Login, Register, ForgotPassword
│   ├── home/           # HomeFragment (instrutor e aluno)
│   ├── students/       # Tudo relacionado a alunos
│   ├── workouts/       # Tudo relacionado a treinos
│   ├── groups/         # Turmas
│   ├── announcements/  # Comunicados
│   ├── profile/        # Perfil do usuário
│   └── running/        # Modo corrida (fase 2)
└── utils/
    ├── extensions/     # Kotlin extensions
    ├── constants/      # Constantes do app
    └── helpers/        # Utilitários gerais
```

---

## 8.6 Critérios de Qualidade (Definição de "Pronto")

Para uma história ser considerada concluída, deve atender:

- [ ] Funcionalidade implementada conforme especificação
- [ ] Testes unitários escritos (mínimo para UseCases e ViewModels)
- [ ] Sem crashes em fluxo principal (smoke test)
- [ ] Loading state implementado
- [ ] Empty state implementado (quando aplicável)
- [ ] Tratamento de erro com mensagem amigável
- [ ] Design System respeitado (cores, fontes, espaçamentos)
- [ ] Pull request revisado e aprovado
- [ ] Funciona em Android 8.0+ (API 26+)

---

## 8.7 Dependências Sugeridas (build.gradle)

```gradle
// UI
implementation 'com.google.android.material:material:1.11.0'
implementation 'androidx.navigation:navigation-fragment-ktx:2.7.7'
implementation 'androidx.navigation:navigation-ui-ktx:2.7.7'

// ViewModel + LiveData
implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0'
implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.7.0'

// Coroutines
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'

// Retrofit (API)
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'

// Supabase (Kotlin SDK)
implementation 'io.github.jan-tennert.supabase:postgrest-kt:2.0.0'
implementation 'io.github.jan-tennert.supabase:auth-kt:2.0.0'
implementation 'io.github.jan-tennert.supabase:realtime-kt:2.0.0'

// Room (offline)
implementation 'androidx.room:room-runtime:2.6.1'
kapt 'androidx.room:room-compiler:2.6.1'

// Glide (imagens)
implementation 'com.github.bumptech.glide:glide:4.16.0'

// Firebase (notificações)
implementation platform('com.google.firebase:firebase-bom:32.7.0')
implementation 'com.google.firebase:firebase-messaging-ktx'

// Gráficos (fase 2)
implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'

// Hilt (DI)
implementation 'com.google.dagger:hilt-android:2.50'
kapt 'com.google.dagger:hilt-compiler:2.50'
```
