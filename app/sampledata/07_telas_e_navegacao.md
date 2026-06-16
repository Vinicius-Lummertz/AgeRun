# 07 — Telas e Navegação

## 7.1 Mapa de Navegação Geral

```
SplashActivity
    └── AuthActivity
            ├── LoginFragment
            ├── RegisterFragment
            └── ForgotPasswordFragment

MainActivity (Instrutor)
    ├── HomeFragment
    ├── AlunosFragment
    │     ├── ListaAlunosFragment
    │     ├── PerfilAlunoFragment
    │     │     ├── EditarAlunoFragment
    │     │     ├── ProgressoAlunoFragment
    │     │     └── TreinosAlunoFragment
    │     ├── NovoAlunoActivity (step-by-step)
    │     └── TurmasFragment
    │           ├── ListaTurmasFragment
    │           ├── DetalheTurmaFragment
    │           └── NovaTurmaFragment
    ├── TreinosFragment
    │     ├── ListaTreinosFragment
    │     ├── DetalheTreinoFragment
    │     │     └── EditarTreinoFragment
    │     └── NovoTreinoActivity (step-by-step)
    ├── AvisosFragment
    │     ├── FeedComunicadosFragment
    │     └── NovoComunicadoFragment
    └── PerfilFragment
          └── EditarPerfilFragment

MainActivity (Aluno)
    ├── HomeAlunoFragment
    ├── TreinosAlunoFragment
    │     ├── ListaTreinosPendentesFragment
    │     └── DetalheTreinoFragment
    │           └── ModoCorridaActivity (fase 2)
    ├── TurmasAlunoFragment
    │     ├── ListaTurmasAlunoFragment
    │     └── DetalheTurmaAlunoFragment
    ├── AvisosAlunoFragment
    └── PerfilAlunoFragment
```

---

## 7.2 Telas — Detalhamento

### TELA: Splash
- Duração: 2 segundos
- Exibe logo Age GO animado (fade in)
- Fundo: `color_primary` (roxo escuro)
- Redireciona para: Login (se não autenticado) ou Home (se autenticado)

---

### TELA: Login
**Elementos:**
- Logo do app (topo, centralizado)
- Campo: E-mail
- Campo: Senha (com toggle de visibilidade)
- Botão: "Entrar" (primary)
- Link: "Esqueci minha senha"
- Link: "Criar conta" (se fluxo de cadastro for aberto)

**Comportamentos:**
- Validação inline de e-mail e senha
- Exibe erro específico: "E-mail não encontrado" ou "Senha incorreta"
- Loading state no botão durante chamada API

---

### TELA: Home — Instrutor
**Elementos:**
- Greeting: "Bem-vindo, {nome}" + dia da semana
- Mini resumo semanal de treinos (grid 3 colunas: dom, seg, ter... com contagem)
- Seção "Acesso Rápido": cards de atalho para Alunos, Treinos, Criar Treino
- Seção "Alunos": total, novos no mês
- Bottom Navigation Bar

**Dados exibidos:**
- Treinos por dia da semana (contagem de alunos com treino em cada dia)
- Número total de alunos ativos
- Atalhos para ações mais frequentes

---

### TELA: Home — Aluno
**Elementos:**
- Greeting: "Bem-vindo, {nome}" + dia da semana
- Barra de progresso semanal (75% = 3 de 4 treinos feitos)
- Card: "Próximo evento: {nome} em X dias"
- Card: "Seus resultados" (link para progresso)
- Card: "Turmas" (link para turmas)
- Bottom Navigation Bar

---

### TELA: Lista de Alunos
**Elementos:**
- SearchBar no topo
- Filtros rápidos em chips: Todos / Com treino / A pagar / Inativos
- Contador de alunos por categoria (29 alunos, 8 a pagar, 21 em dia, 8 inativos)
- Lista vertical de cards de aluno:
  - Avatar, nome, plano, status badge
- FAB: "Novo Aluno"
- Bottom Navigation

**Comportamento:**
- Busca em tempo real conforme digita
- Toque no card → Perfil do aluno

---

### TELA: Perfil do Aluno
**Elementos:**
- Header: avatar grande, nome, e-mail
- Plano atual + valor + botão "Mudar Plano"
- Tabs: Rendimentos | Treinos | Dados
- Botão: "Editar"
- No tab Rendimentos: gráfico de desempenho
- No tab Treinos: lista de treinos atribuídos
- No tab Dados: informações pessoais editáveis

---

### TELA: Novo Aluno (Step-by-step)
**Passo 1 — Nome:**
- Título: "Nome"
- Campo: Nome do novo aluno
- Botão: Avançar

**Passo 2 — Contatos:**
- Título: "Contatos"
- Campo: E-mail
- Campo: DDD + Telefone
- Botão: Avançar

**Passo 3 — Plano:**
- Título: "Plano"
- Lista de planos com nome e valor mensal
- Seleção única

**Passo 4 — Configurar Plano:**
- Nome do plano selecionado
- Campo: data de início (date picker)
- Campo: frequência (dias da semana, multi-select)
- Lista de treinos inclusos
- Campo: desconto manual (R$)
- Resumo: R$ X/mês | R$ X/ano
- Botão: Avançar (Salvar)

---

### TELA: Lista de Treinos
**Elementos:**
- SearchBar
- Filtros: Todos / Recentes / Em edição / Inativos
- Contadores (29 treinos, 2 em edição, 27 ativos, 8 inativos)
- Lista de cards de treino: ícone, nome, contagem de alunos, status
- Opções adicionais: Alterar treino do aluno, Criar treino, Criar plano
- FAB: Criar Treino

---

### TELA: Detalhe do Treino
**Elementos:**
- Nome do treino
- Descrição
- Contadores: N alunos que treinam este treino, 4 em planos ativos
- Lista de atividades: ação, descrição, tempo
- Lista de alunos que fazem este treino (recentes / todos)
- Botão: Editar

---

### TELA: Novo Treino (Step-by-step)
**Passo 1 — Título:**
- Campo: Nome do treino
- Campo: Descrição do treino
- Seleção: Ícone do treino (grid de ícones)
- Botão: Avançar

**Passo 2 — Atividades:**
- Lista de atividades adicionadas
- Para cada atividade: campo ação, campo descrição, campo tempo
- Botão: + Adicionar Atividade
- Botão: Criar (Salvar)

---

### TELA: Lista de Turmas
**Elementos:**
- SearchBar
- Lista de turmas: nome, contagem de alunos, ícone
- FAB: Criar Turma

---

### TELA: Detalhe da Turma
**Tabs:**
- **Alunos:** lista dos membros (com busca), botão "Seguir" para adicionar
- **Treinos:** lista de treinos da turma, botão "Completar" para marcar
- **Feed:** posts do instrutor, reações dos alunos

---

### TELA: Feed de Avisos
**Elementos:**
- Lista de comunicados em ordem cronológica inversa
- Cada item: texto do comunicado, data, reações (contagem por emoji)
- Área de reação: botões de emoji fixos (👍 🏃)
- FAB: Criar Comunicado (visível apenas para instrutor)

---

### TELA: Perfil do Usuário
**Elementos:**
- Avatar editável, nome, papel (Instrutor/Aluno)
- Seção: Preferências do sistema
- Seção: Gerenciamento de notificações (toggles por tipo)
- Botão: Editar Perfil
- Botão: Sair (logout)

---

## 7.3 Bottom Navigation

### Instrutor
| Posição | Ícone | Label | Fragment |
|---|---|---|---|
| 1 | `home` | Home | HomeFragment |
| 2 | `group` | Alunos | AlunosFragment |
| 3 | `fitness_center` | Treinos | TreinosFragment |
| 4 | `notifications` | Avisos | AvisosFragment |

### Aluno
| Posição | Ícone | Label | Fragment |
|---|---|---|---|
| 1 | `home` | Home | HomeAlunoFragment |
| 2 | `directions_run` | Treinos | TreinosAlunoFragment |
| 3 | `groups` | Turmas | TurmasAlunoFragment |
| 4 | `notifications` | Avisos | AvisosAlunoFragment |

---

## 7.4 Padrões de Tela

### Tela com Lista + FAB
- AppBar com título e ícone de busca
- Lista com RecyclerView
- FAB posicionado bottom-end (direita inferior)
- Empty state quando lista vazia: ícone + mensagem + ação

### Tela de Formulário Multi-step
- Barra de progresso no topo (Step 1 de 4)
- Conteúdo da etapa atual
- Botão "Avançar" fixo no bottom
- Seta voltar no AppBar para etapa anterior
- Título da etapa no AppBar

### Tela de Detalhe
- Hero section (avatar/ícone grande + título)
- Informações primárias
- Tabs ou seções expandíveis
- FAB ou botão de ação no canto
