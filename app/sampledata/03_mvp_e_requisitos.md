# MVP e Requisitos

## Escopo do MVP

| # | Funcionalidade | Perfil | Status no projeto |
| --- | --- | --- | --- |
| 1 | Cadastro e login de professor e aluno | Ambos | Implementado com fluxo de token, primeiro acesso e convite |
| 2 | Cadastro e gestao de alunos | Professor | Implementado |
| 3 | Criacao e atribuicao de treinos | Professor | Implementado |
| 4 | Visualizacao de treinos pelo aluno | Aluno | Implementado |
| 5 | Acompanhamento de KM mensal | Aluno | Implementado por sessoes de treino e historico |
| 6 | Upload e acesso a planilhas | Ambos | Parcial: ha upload de midia e rotinas; anexos de planilha devem ser consolidados |
| 7 | Controle manual de mensalidades | Professor | Implementado com aprovacao/rejeicao de comprovante |
| 8 | Visualizacao de status de mensalidade | Aluno | Implementado |
| 9 | Feed de comunicados | Ambos | Implementado |
| 10 | Push notifications basicas | Ambos | Parcial: app tem permissao e notificacao local de treino; push remoto pode ser evolucao |
| 11 | Integracao de pagamento | Ambos | Implementado com Asaas para Pix e assinatura no cartao |
| 12 | Dashboard com indicadores | Professor | Implementado no app e no painel web |
| 13 | Supabase para persistencia | Ambos | Implementado |
| 14 | Gerar PDF | Ambos | Pendente/opcional |

## Requisitos obrigatorios do app mobile

| Requisito | Atendimento no projeto |
| --- | --- |
| No minimo 4 telas | Atendido. Ha telas de autenticacao, hub, alunos, treinos, financeiro, eventos, comunicados, desafios, perfil e portal do aluno |
| Nome e icone do app personalizado | Atendido. Nome `AgeGo` e icones em `mipmap`/`drawable-nodpi` |
| Persistencia dos dados | Atendido. Supabase/API online e cache local com Room |
| No minimo 2 operacoes CRUD | Atendido. CRUD de alunos, treinos, rotinas, eventos e desafios |
| 100% Android nativo com Kotlin e Jetpack Compose ou React Native/Expo | Atendido. Projeto Android nativo com Kotlin e Jetpack Compose |

## Requisitos opcionais para pontuacao extra

| Opcional | Atendimento no projeto |
| --- | --- |
| Mapas e geolocalizacao | Atendido. Uso de permissao de localizacao, MapLibre e registro de rotas |
| API online para persistencia ou Firebase | Atendido. API Node/Express com Supabase |
| Integracao com app web existente | Atendido. Ha painel web em Express/EJS |
| Integracao com API externa | Atendido. Asaas para pagamentos |
| Publicar o app na loja | Nao identificado |
| Salvar informacoes em arquivo, gerar PDF ou texto | Pendente/opcional |
| Utilizar acelerometro ou giroscopio | Nao identificado; o app usa GPS para distancia e rota |

## Telas para demonstracao

As telas abaixo sao exemplos de fluxo e nao precisam seguir um padrao visual especifico:

- Login/cadastro de professor e aluno.
- Hub Fit / dashboard do professor.
- Gestao de alunos.
- Gestao de treinos.
- Portal do aluno com treino de hoje.
- Registro de treino com mapa.
- Financeiro do aluno e do professor.
- Eventos e check-in.
- Comunicados.
- Configuracoes do professor.
