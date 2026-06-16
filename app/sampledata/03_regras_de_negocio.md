# 03 — Regras de Negócio

## 3.1 Perfis e Permissões

### Perfil: Instrutor
O instrutor é o administrador principal da plataforma. Ele cria sua conta de forma independente e gerencia tudo relacionado à assessoria.

**Permissões completas:**
- CRUD completo de alunos
- CRUD completo de treinos
- CRUD completo de turmas
- Criação e publicação de comunicados
- Gestão de planos e cobranças
- Visualização de dados de desempenho de todos os alunos
- Configuração de notificações
- Edição do próprio perfil

### Perfil: Aluno
O aluno é adicionado pelo instrutor. Não pode se cadastrar de forma autônoma sem um convite/código do instrutor.

**Permissões do aluno:**
- Visualizar treinos atribuídos ao próprio perfil
- Registrar resultado de treinos realizados
- Visualizar o próprio histórico de desempenho
- Participar de turmas às quais foi adicionado
- Ler e reagir a comunicados
- Visualizar eventos do grupo
- Editar o próprio perfil
- Usar o cronômetro/modo de corrida

**Restrições do aluno:**
- Não pode criar treinos
- Não pode ver dados financeiros de outros alunos
- Não pode adicionar ou remover outros alunos
- Não pode criar comunicados (somente reagir)
- Não pode ver desempenho de outros alunos

---

## 3.2 Gestão de Alunos

### RN-001 — Cadastro de Aluno
- Todo aluno deve ser cadastrado pelo instrutor
- Campos obrigatórios: nome completo, e-mail, telefone (DDD + número)
- O aluno recebe um convite por e-mail ou link para ativar o acesso
- O aluno é associado a um plano no ato do cadastro

### RN-002 — Status do Aluno
Um aluno pode estar em um dos seguintes estados:

| Status | Descrição |
|---|---|
| `ATIVO` | Com plano vigente e treino em dia |
| `PENDENTE` | Com pagamento em aberto |
| `INATIVO` | Sem plano ativo ou sem acesso recente |
| `EM_EDIÇÃO` | Cadastro iniciado mas não concluído |

### RN-003 — Visualização de Alunos
- O instrutor pode pesquisar alunos por nome ou turma
- A tela de alunos deve exibir contadores de: total, com treino em dia, a pagar, inativos
- O perfil individual do aluno exibe: dados pessoais, plano atual, histórico de treinos, rendimento

### RN-004 — Edição de Aluno
- O instrutor pode editar dados pessoais, plano e turma de qualquer aluno
- O aluno pode editar apenas nome, foto de perfil e preferências

---

## 3.3 Gestão de Treinos

### RN-005 — Criação de Treino
- Somente instrutores podem criar treinos
- Campos obrigatórios: nome do treino, ícone, pelo menos uma atividade
- Cada atividade contém: ação (descrição do exercício), descrição detalhada, tempo estimado
- Um treino pode ter N atividades sequenciais

### RN-006 — Estados de Treino
| Status | Descrição |
|---|---|
| `ATIVO` | Em uso por pelo menos um plano ou aluno |
| `EM_EDIÇÃO` | Criado mas não publicado/vinculado |
| `INATIVO` | Desvinculado de todos os planos |

### RN-007 — Atribuição de Treinos
- Treinos são atribuídos a alunos via planos
- Um plano pode incluir N treinos específicos
- O instrutor pode alterar o treino de um aluno individualmente, fora do plano padrão
- A mesma alteração pode ser feita em lote para uma turma

### RN-008 — Visualização de Treinos pelo Aluno
- O aluno visualiza apenas os treinos atribuídos ao seu plano
- O aluno pode marcar um treino como "completo"
- O aluno pode registrar observações ao completar um treino

### RN-009 — Treinos em Grupo
- O instrutor pode criar treinos associados a uma turma específica
- Todos os membros da turma recebem o treino automaticamente
- O controle de presença em treinos coletivos é feito pelo instrutor

---

## 3.4 Gestão de Planos

### RN-010 — Estrutura do Plano
Cada plano contém:
- Nome do plano
- Valor mensal (R$)
- Valor anual (R$)
- Lista de treinos inclusos
- Frequência semanal (dias da semana)
- Data de início
- Desconto manual (campo livre para ajustes)

### RN-011 — Planos Disponíveis (Exemplo)
| Plano | Valor Mensal | Valor Anual |
|---|---|---|
| Plano 1 | R$ 999,00 | — |
| Plano 2 | R$ 1.499,00 | — |
| Plano 3 | R$ 2.299,00 | — |
| Plano 4 | R$ 3.499,00 | — |

> Os planos são configuráveis pelo instrutor. Os valores acima são exemplos do wireframe.

### RN-012 — Troca de Plano
- O instrutor pode alterar o plano de um aluno a qualquer momento
- A troca entra em vigor na próxima cobrança (ou imediatamente, conforme configuração)
- O histórico de planos anteriores é mantido

### RN-013 — Controle Financeiro (Fase 1 — Manual)
- O instrutor visualiza quais alunos estão com pagamento pendente
- Na Fase 1, o controle é manual (sem gateway de pagamento integrado)
- Na Fase 3, cobranças automáticas serão implementadas

---

## 3.5 Gestão de Turmas

### RN-014 — Criação de Turma
- Somente o instrutor cria turmas
- Campo obrigatório: nome da turma
- Uma turma pode ter N alunos

### RN-015 — Turma e Treinos
- Uma turma pode ter treinos específicos associados
- O aluno pode visualizar treinos da turma e marcar como "completados"
- O instrutor visualiza quais alunos completaram os treinos da turma

### RN-016 — Feed da Turma
- Cada turma tem um feed de posts do instrutor
- Os alunos podem reagir com emojis (ex: 👍 🏃)
- Apenas o instrutor pode criar posts no feed da turma

---

## 3.6 Comunicados (Avisos)

### RN-017 — Criação de Comunicado
- Somente o instrutor cria comunicados
- Um comunicado pode ser enviado para: todos os alunos, uma turma específica, alunos de um plano

### RN-018 — Reações
- Alunos podem reagir a comunicados com emojis pré-definidos
- O instrutor visualiza a contagem de reações por comunicado

### RN-019 — Feed de Comunicados
- O feed exibe comunicados em ordem cronológica inversa (mais recente no topo)
- Comunicados antigos não são deletados (apenas arquivados)

---

## 3.7 Notificações

### RN-020 — Tipos de Notificação
| Tipo | Destinatário | Gatilho |
|---|---|---|
| Novo treino disponível | Aluno | Instrutor atribui treino |
| Treino pendente | Aluno | Dia do treino sem conclusão |
| Novo comunicado | Aluno | Instrutor publica aviso |
| Pagamento pendente | Instrutor | Data de vencimento próxima |
| Aluno sem treino | Instrutor | Aluno X dias sem registrar treino |
| Novo evento | Aluno | Evento criado na turma |

### RN-021 — Controle de Notificações
- O instrutor pode ativar/desativar tipos de notificação para os alunos
- O aluno pode ativar/desativar notificações nas preferências pessoais

---

## 3.8 Monitoramento de Corrida (Fase 2)

### RN-022 — Modo de Corrida
- O aluno ativa o "Modo Stand By" antes de iniciar a corrida
- O app utiliza GPS e acelerômetro para métricas em tempo real
- Dados coletados: distância, pace (min/km), cadência, duração
- Ao finalizar, o resultado é salvo no histórico do aluno

### RN-023 — Histórico de Desempenho
- O instrutor visualiza o histórico de cada aluno individualmente
- O aluno visualiza o próprio histórico
- Gráficos de evolução são exibidos por período (semana, mês, semestre)

---

## 3.9 Eventos

### RN-024 — Criação de Evento
- Somente o instrutor cria eventos
- Campos: nome, data, local, descrição, turma(s) relacionada(s)
- O evento aparece no dashboard do aluno como "próximo evento"

### RN-025 — Controle de Presença em Eventos
- O instrutor pode registrar presença dos alunos em eventos
- O histórico de presença é vinculado ao perfil do aluno

---

## 3.10 Regras de Acesso e Segurança

### RN-026 — Autenticação
- Login por e-mail + senha
- Senha com mínimo de 8 caracteres, ao menos 1 número e 1 letra maiúscula
- Opção de recuperação de senha por e-mail

### RN-027 — Sessão
- Sessão persistente com token JWT armazenado de forma segura
- Refresh token com expiração de 30 dias
- Logout automático após 90 dias de inatividade

### RN-028 — Isolamento de Dados
- Um instrutor só acessa dados dos seus próprios alunos
- Um aluno só acessa dados do(s) seu(s) instrutor(es)
- Dados de alunos de diferentes instrutores são completamente isolados

### RN-029 — Exclusão de Dados
- Ao excluir um aluno, o histórico de treinos é mantido por 12 meses (compliance)
- O instrutor pode "inativar" um aluno sem excluí-lo permanentemente
