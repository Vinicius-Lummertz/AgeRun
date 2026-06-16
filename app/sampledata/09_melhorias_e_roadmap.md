# 09 — Melhorias e Roadmap

## 9.1 Melhorias Identificadas (a partir do briefing)

### Experiência do Aluno

| Melhoria | Impacto | Prioridade |
|---|---|---|
| Treinos disponíveis offline (cache local) | Alto — resolve dor crítica | Alta |
| Notificações de lembrete de treino | Alto — aumenta engajamento | Alta |
| Visualização da semana de treinos em calendário | Médio | Média |
| Streak de dias treinados (gamificação) | Médio | Média |
| Compartilhamento de resultado nas redes sociais | Baixo | Baixa |
| Modo escuro / claro configurável | Baixo | Baixa |

### Experiência do Instrutor

| Melhoria | Impacto | Prioridade |
|---|---|---|
| Templates de treino reutilizáveis | Alto — reduz retrabalho | Alta |
| Cópia de treino de um aluno para outro | Alto | Alta |
| Histórico de comunicados arquivados | Médio | Média |
| Exportação de relatório de alunos em PDF | Médio | Média |
| Cadastro em lote de alunos via CSV | Médio | Média |
| Agendamento de comunicados (envio futuro) | Médio | Média |
| Relatório de inadimplência automático | Alto | Alta |

---

## 9.2 Backlog Priorizado (MoSCoW)

### Must Have (Fase 1 — MVP)
- [ ] Login e autenticação segura
- [ ] CRUD de alunos com status
- [ ] CRUD de treinos com atividades
- [ ] Atribuição de treinos a alunos via planos
- [ ] Turmas para agrupamento de alunos
- [ ] Feed de comunicados
- [ ] Notificações push básicas
- [ ] Perfil do instrutor e do aluno
- [ ] Controle básico de planos (manual)

### Should Have (Fase 2)
- [ ] Modo de corrida com GPS
- [ ] Histórico de desempenho com gráficos
- [ ] Cache offline de treinos
- [ ] Dashboard de evolução do aluno
- [ ] Eventos com controle de presença
- [ ] Gamificação básica (streaks, conquistas)

### Could Have (Fase 3)
- [ ] Cobranças automáticas (gateway integrado)
- [ ] Relatórios exportáveis (PDF/CSV)
- [ ] Templates de treinos prontos
- [ ] Integração com Strava/Garmin
- [ ] Suporte a múltiplos instrutores
- [ ] App para Apple Watch / Wear OS

### Won't Have (por ora)
- [ ] Marketplace de assessorias
- [ ] Streaming de aulas ao vivo
- [ ] Nutrição e dieta
- [ ] Ranking público entre alunos de diferentes assessorias

---

## 9.3 Roadmap de Lançamento

### v1.0 — MVP (Mês 1–3)
**Foco:** Substituir WhatsApp + planilhas

Funcionalidades:
- Gestão completa de alunos e treinos
- Turmas e comunicados
- Notificações push
- Dois perfis (instrutor e aluno)

**KPI alvo:**
- 3 assessorias pilotos usando o app
- 30 alunos ativos na plataforma
- NPS > 7

---

### v1.5 — Monitoramento (Mês 4–5)
**Foco:** Valor percebido pelo aluno

Funcionalidades:
- Modo de corrida com GPS
- Histórico de desempenho
- Gráficos de evolução
- Cache offline

**KPI alvo:**
- 70% dos alunos usando modo corrida ao menos 1x/semana
- Retenção semanal > 60%

---

### v2.0 — Financeiro (Mês 6–8)
**Foco:** Sustentabilidade do instrutor

Funcionalidades:
- Cobranças automáticas
- Dashboard financeiro
- Relatórios e exportações
- Múltiplos instrutores independentes

**KPI alvo:**
- Redução de inadimplência em 40%
- 10+ assessorias ativas
- Receita recorrente para o produto

---

### v2.5 — Crescimento (Mês 9–12)
**Foco:** Escala e ecossistema

Funcionalidades:
- Integração com Strava/Garmin
- Templates de treino da comunidade
- App complementar para Wear OS
- API pública para integrações externas

---

## 9.4 Débitos Técnicos Conhecidos

| Item | Impacto | Quando resolver |
|---|---|---|
| Testes unitários nos ViewModels | Qualidade | Sprint 6 |
| Instrumentação de analytics (Firebase Analytics) | Dados de uso | Sprint 6 |
| Crash reporting (Firebase Crashlytics) | Estabilidade | Sprint 0 |
| Minificação e obfuscação (R8/ProGuard) | Segurança | Antes do lançamento |
| Política de Privacidade + Termos de Uso | Legal | Antes do lançamento |
| Certificação Google Play | Distribuição | Antes do lançamento |

---

## 9.5 Pontos de Atenção para a IA Geradora de Código

Ao gerar código para este projeto, considere:

1. **Sempre usar Kotlin idiomático** — coroutines, extension functions, data classes
2. **Seguir MVVM** — ViewModels nunca importam Android Framework além do necessário
3. **UseCases são puros** — sem dependência de Android, apenas Kotlin
4. **Repository pattern** — toda chamada de rede/banco passa pelo repositório
5. **StateFlow/LiveData para UI** — nunca expor MutableStateFlow/MutableLiveData publicamente
6. **Injeção de dependência via Hilt** — nenhuma instância criada manualmente nas Activities/Fragments
7. **Single Activity** — usar Navigation Component, não múltiplas Activities (exceto ModoCorreda que pode ser Activity separada por contexto de foreground service)
8. **Supabase SDK Kotlin** — preferir SDK oficial em vez de Retrofit manual para Supabase
9. **RLS no Supabase** — nunca expor dados sem política de segurança ativa
10. **Tratamento de erros tipado** — usar sealed class Result<T> para retornos de repositório

---

## 9.6 Sugestões de Diferenciais Competitivos

1. **Modo Assessoria** — Permite que o instrutor tenha uma landing page pública no app para captar novos alunos
2. **Certificado de Conclusão** — Aluno recebe certificado digital ao completar um plano de treino
3. **Treino Adaptativo** — Com base no histórico, a IA sugere ajustes no treino (integração com Claude API)
4. **Comunidade** — Feed público de corridas realizadas (opt-in) para criar senso de comunidade
5. **Integração Wearable** — Sincronização automática com smartwatches para captura passiva de dados
6. **Dashboard de Negócios** — Visão financeira completa para o instrutor: receita mensal, projeção, alunos em risco de churn
