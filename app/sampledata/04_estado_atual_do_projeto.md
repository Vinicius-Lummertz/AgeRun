# Estado atual do projeto

## Leitura tecnica

O projeto esta estruturado como um app Android nativo em Kotlin com Jetpack Compose, acompanhado por uma API Node/Express, painel web em EJS e banco Supabase.

Principais partes encontradas:

- `app/src/main/java/com/example/myapplication`: telas, navegacao, ViewModel, repositorios e servicos Android.
- `remote-server.js`: API online usada pelo app mobile.
- `web-dashboard-server.js`: painel web para professor.
- `supabase/migrations`: schema, politicas, rotinas, configuracoes, pareamento web e pagamentos Asaas.
- `app/src/main/res`: icones, tema, fontes, imagens e manifesto do app.

## Stack

| Camada | Tecnologia |
| --- | --- |
| Mobile | Android nativo, Kotlin, Jetpack Compose |
| Navegacao | Navigation Compose |
| Estado | ViewModel e StateFlow |
| Persistencia remota | Supabase |
| API | Node.js, Express, Zod |
| Painel web | Express, EJS, CSS |
| Cache local | Room |
| Mapas | MapLibre |
| Pagamentos | Asaas Pix e assinatura por cartao |
| Midia | Upload em buckets Supabase |

## Funcionalidades identificadas no codigo

### Mobile

- Login/cadastro com fluxo de professor e aluno.
- Primeiro acesso de aluno e convite por codigo/link.
- Dashboard do professor.
- Cadastro, edicao, listagem e exclusao de alunos.
- Cadastro, edicao, listagem e exclusao de treinos.
- Rotinas/turmas e associacao com alunos.
- Eventos com localizacao, check-in, corrida em grupo e resultados.
- Portal do aluno com treinos, financeiro, perfil, historico e eventos.
- Registro de treino em servico de primeiro plano, com tempo, distancia, pace, rota e splits.
- Mapas para rota de treino e resultados.
- Comunicados.
- Desafios.
- Configuracoes do professor.
- Financeiro do aluno com Pix/Asaas, cartao recorrente e envio de comprovante.
- Revisao de pagamento pelo professor.
- Cache offline de dashboard.

### API

- Autenticacao por token.
- Registro e verificacao de professor.
- Primeiro acesso de aluno.
- Cadastro via convite.
- Dashboard por perfil.
- CRUD de alunos.
- CRUD de treinos.
- CRUD de rotinas.
- CRUD de eventos.
- CRUD de desafios.
- Comunicados.
- Upload de midia.
- Pagamentos manuais e comprovantes.
- Integracao Asaas para Pix, assinatura com cartao, cancelamento e webhook.
- Pareamento com painel web por QR/token.

### Painel web

- Login/pareamento.
- Overview com indicadores.
- Listagem e detalhe de alunos.
- Criacao, edicao e exclusao de alunos.
- Treinos e rotinas.
- Eventos.
- Desafios.
- Financeiro.
- Configuracoes.
- Comunicados.

## Pontos pendentes ou parciais

- Consolidar upload e exibicao de planilhas como arquivos PDF/XLSX/imagem, caso isso precise aparecer explicitamente na avaliacao.
- Implementar geracao de PDF, se o objetivo for obter a pontuacao extra correspondente.
- Implementar uso de acelerometro ou giroscopio, se esse opcional for escolhido. Hoje o acompanhamento de distancia e rota esta baseado em GPS/localizacao.
- Confirmar push remoto. O projeto tem permissao de notificacao e notificacoes locais, mas nao foi identificado um provedor de push remoto.

## Aderencia ao escopo

O projeto atual cobre a maior parte do MVP definido para o Age Go. Alem do minimo esperado, ja existem recursos avancados como eventos com rota, painel web, integracao Asaas, cache offline, convites de aluno e desafios.
