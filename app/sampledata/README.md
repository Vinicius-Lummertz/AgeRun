# Age GO — Base de Conhecimento do Projeto

> Documento central de referência para desenvolvimento do aplicativo **Age GO**, plataforma de gestão de treinos de corrida para treinadores autônomos e suas assessorias.

---

## Índice de Documentos

| Arquivo | Conteúdo |
|---|---|
| `01_visao_geral.md` | Visão do produto, problema, solução e público-alvo |
| `02_personas.md` | Personas detalhadas (Filipe e Larissa) |
| `03_regras_de_negocio.md` | Regras de negócio, planos, permissões e fluxos |
| `04_design_system.md` | Design System completo (cores, tipografia, componentes) |
| `05_arquitetura_dados.md` | Modelagem de dados, entidades e relacionamentos |
| `06_user_flows.md` | User flows e jornadas por perfil |
| `07_telas_e_navegacao.md` | Estrutura de telas, navegação e wireframes descritivos |
| `08_etapas_desenvolvimento.md` | Marcos do projeto, sprints e critérios de aceite |
| `09_melhorias_e_roadmap.md` | Backlog de melhorias, funcionalidades futuras e roadmap |
| `10_stack_tecnica.md` | Stack técnica, decisões de arquitetura e integrações |

---

## Resumo Executivo

**Age GO** é um aplicativo Android voltado para treinadores de corrida autônomos que desejam centralizar a gestão de alunos, treinos e comunicação. O app elimina a dependência de planilhas e WhatsApp, proporcionando uma experiência profissional tanto para o treinador quanto para os alunos.

### Problema Central
- Comunicação fragmentada via WhatsApp
- Treinos enviados manualmente por planilhas
- Ausência de histórico de desempenho centralizado
- Retrabalho constante do treinador

### Solução
Plataforma mobile com dois perfis distintos (Instrutor e Aluno), permitindo criação e distribuição de treinos, acompanhamento de evolução, comunicados e gestão financeira básica.

### Stack Principal
- **Frontend:** Kotlin + Android Studio
- **Backend:** Node.js
- **Banco de Dados:** Supabase (PostgreSQL)

## Banco Supabase Atual

O arquivo principal atualizado agora e `app/sampledata/banco.txt`.
Ele tambem existe como migracao em:

`supabase/migrations/202606170001_agego_full_schema.sql`

Esse script cria:

- Tabelas de gestao: usuarios, instrutores, alunos, planos, treinos, atividades, grupos, membros de grupo, eventos, presencas, comunicados e notificacoes.
- Tabelas de comunidade: posts, destinos por grupo/aluno/evento, midias, enquetes, votos, curtidas, comentarios e curtidas de comentarios.
- RPC `get_instructor_students()` para o app carregar alunos do instrutor.
- RPC `set_group_members(group_id, student_ids)` para salvar a selecao de pessoas de um grupo em lote.
- Buckets privados separados:
  - `agego-social-posts` para imagens, GIFs e videos da rede social.
  - `agego-management` para arquivos de gestao, documentos, anexos de alunos, eventos e treinos.

Para aplicar direto no Supabase pelo terminal:

```powershell
npm install
$env:SUPABASE_DB_URL="postgresql://postgres:SENHA_REAL@db.upjhdumsfikxsdswesnd.supabase.co:5432/postgres"
npm run supabase:apply
```

Nao salve a senha real no repositorio.

---

*Versão do documento: 1.0 — Baseado no arquivo Section_1.pdf (briefing inicial do projeto)*
