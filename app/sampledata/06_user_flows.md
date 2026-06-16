# 06 — User Flows

## 6.1 Flow: Onboarding do Instrutor

```
[Splash Screen]
    ↓
[Login / Cadastro]
    ↓ (novo usuário)
[Cadastro: nome, e-mail, senha]
    ↓
[Verificação de e-mail]
    ↓
[Configurar perfil: bio, especialidade, Instagram]
    ↓
[Tela Home — Instrutor]
```

---

## 6.2 Flow: Cadastro de Aluno (pelo Instrutor)

```
[Home] → [Alunos] → [Novo Aluno]
    ↓
[Etapa 1: Nome completo]
    ↓ avançar
[Etapa 2: E-mail + DDD/Telefone]
    ↓ avançar
[Etapa 3: Selecionar Plano]
    ↓ (seleciona plano)
[Configurar Plano: data início, frequência, desconto]
    ↓ avançar
[Confirmação + Salvar]
    ↓
[Aluno criado → Convite enviado por e-mail/link]
    ↓
[Volta para Lista de Alunos]
```

---

## 6.3 Flow: Criação de Treino

```
[Home] → [Treinos] → [Criar Treino]
    ↓
[Etapa 1: Nome do Treino + Descrição + Ícone]
    ↓ avançar
[Etapa 2: Adicionar Atividades]
    → [Atividade: ação, descrição, tempo]
    → [+ Adicionar outra atividade]
    ↓ (quando satisfeito)
[Criar/Salvar Treino]
    ↓
[Treino salvo como DRAFT]
    ↓ (opcional)
[Vincular a Plano ou Turma]
    ↓
[Treino ATIVO]
```

---

## 6.4 Flow: Gerenciar Aluno Existente

```
[Alunos] → [Pesquisar / Listar] → [Selecionar Aluno]
    ↓
[Perfil do Aluno]
    ├── [Editar Dados Pessoais]
    ├── [Ver Progresso] → [Gráficos de evolução]
    ├── [Ver Treinos] → [Lista de treinos atribuídos]
    ├── [Mudar Plano] → [Selecionar novo plano]
    └── [Inativar Aluno]
```

---

## 6.5 Flow: Criação de Comunicado

```
[Avisos] → [Criar Comunicado]
    ↓
[Escrever texto do comunicado]
    ↓
[Selecionar destinatários: Todos / Turma / Plano]
    ↓ (se turma ou plano)
[Selecionar turma ou plano específico]
    ↓
[Publicar]
    ↓
[Notificação push enviada para destinatários]
    ↓
[Feed de Avisos atualizado]
```

---

## 6.6 Flow: Criação de Turma

```
[Home ou Alunos] → [Turmas] → [Criar Turma]
    ↓
[Nome da Turma]
    ↓ avançar
[Adicionar Alunos à Turma]
    → [Pesquisar por nome]
    → [Selecionar alunos]
    ↓
[Associar Treinos à Turma] (opcional)
    ↓
[Salvar Turma]
    ↓
[Turma criada com feed próprio]
```

---

## 6.7 Flow: Aluno — Primeiro Acesso

```
[Recebe convite por e-mail/link]
    ↓
[Abre app / link de convite]
    ↓
[Tela de boas-vindas com nome do instrutor]
    ↓
[Criar senha]
    ↓
[Completar perfil: data de nascimento, nível, foto]
    ↓
[Home do Aluno]
```

---

## 6.8 Flow: Aluno — Realizar Treino

```
[Home] → [Ver Treinos da Semana]
    ↓
[Selecionar Treino Pendente]
    ↓
[Visualizar detalhes: atividades, tempo, descrição]
    ↓
[Iniciar Treino → Modo Corrida (fase 2)]
    ↓
[Cronômetro / GPS ativo]
    ↓
[Finalizar Treino]
    ↓
[Registrar resultado: observação livre]
    ↓
[Treino marcado como COMPLETO]
    ↓
[Progresso atualizado no perfil]
```

---

## 6.9 Flow: Aluno — Visualizar Progresso

```
[Home] → [Seus Resultados / Rendimento]
    ↓
[Dashboard de evolução]
    ├── [Gráfico de distância por semana]
    ├── [Gráfico de pace médio]
    ├── [Frequência de treinos]
    └── [Streak atual]
```

---

## 6.10 Flow: Controle de Presença em Evento

```
[Instrutor → Eventos] → [Selecionar Evento]
    ↓
[Lista de alunos esperados]
    ↓
[Marcar presença manualmente para cada aluno]
    ↓
[Salvar presença]
    ↓
[Histórico de presença vinculado ao perfil de cada aluno]
```

---

## 6.11 Fluxo de Autenticação

```
[Splash (2s)]
    ↓
[Verificar token local]
    ├── [Token válido] → [Redirecionar para Home do perfil]
    └── [Token inválido/ausente] → [Tela de Login]
                                        ↓
                                   [Login c/ email+senha]
                                        ├── [Sucesso] → [Home]
                                        └── [Erro] → [Mensagem + retry]
                                                ↓ (esqueci a senha)
                                           [Recuperação por e-mail]
```

---

## 6.12 Jornada Completa do Usuário (User Journey Map)

| Etapa | Atividade | Objetivo | Obstáculos Atuais | Solução no App |
|---|---|---|---|---|
| Descoberta | Encontra assessoria via Instagram/indicação | Entender como funciona | Informações dispersas | Landing page + perfil no app |
| Intenção | Entra em contato, agenda experimental | Agendar facilmente | Atendimento manual | Formulário de contato + pré-cadastro |
| Decisão | Participa da aula experimental | Avaliar se vale a pena | Sem registro estruturado | Pré-cadastro automatizado |
| Ação | É adicionado ao app, recebe treinos | Acessar treinos e grupos | Comunicação desorganizada | App centralizado |
| Pós-ação | Realiza treinos e envia feedback | Acompanhar evolução | Feedbacks perdidos | Logs + histórico integrado |
