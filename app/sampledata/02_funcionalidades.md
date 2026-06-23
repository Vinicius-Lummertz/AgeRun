# Funcionalidades do Age Go

## Painel do Professor

### Gestao de alunos

| Funcionalidade | Descricao | Prioridade |
| --- | --- | --- |
| Cadastro de aluno | Criar perfil com nome, e-mail, telefone, plano, mensalidade, dia de vencimento e dados de acesso | Alta |
| Listagem de alunos | Visualizar alunos ativos, inativos e com pendencia financeira | Alta |
| Perfil individual | Ver dados do aluno, plano, status de pagamento, treinos e informacoes de acompanhamento | Alta |
| Edicao e exclusao | Atualizar cadastro ou remover aluno da base | Alta |
| Ativacao / inativacao | Bloquear ou reativar acesso de alunos | Media |
| Grupos / turmas | Organizar alunos em turmas ou rotinas especificas | Media |
| Convite de aluno | Gerar codigo/link de convite para primeiro acesso do aluno | Alta |

### Gestao de treinos

| Funcionalidade | Descricao | Prioridade |
| --- | --- | --- |
| Criar treino | Cadastro com nome, tipo, estrutura, intensidade, duracao, observacoes e status | Alta |
| Atribuir treino | Vincular treino a um aluno ou turma/rotina | Alta |
| Treino personalizado | Criar treino exclusivo durante o cadastro do aluno | Alta |
| Listagem de treinos | Visualizar todos os treinos cadastrados | Alta |
| Historico de treinos | Consultar treinos realizados por aluno por meio do historico de sessoes | Alta |
| Edicao e exclusao | Atualizar ou remover treinos existentes | Alta |

### Planilhas e rotinas

| Funcionalidade | Descricao | Prioridade |
| --- | --- | --- |
| Rotinas de treino | Criar rotinas com nome, descricao, status e alunos vinculados | Alta |
| Atribuicao de rotina | Vincular rotina a um aluno ou grupo especifico | Alta |
| Planilha ativa | Representar a programacao de treino vigente do aluno | Alta |
| Vigencia da planilha | Definir periodo ou ciclo de validade da rotina | Media |
| Upload de arquivos | Anexar PDF, XLSX ou imagem como material de treino | Media |

### Financeiro

| Funcionalidade | Descricao | Prioridade |
| --- | --- | --- |
| Planos de mensalidade | Definir plano, valor mensal e dia de vencimento do aluno | Alta |
| Controle manual de pagamentos | Marcar mensalidade como paga, pendente ou rejeitar comprovante | Alta |
| Comprovante de pagamento | Aluno pode enviar comprovante para revisao do professor | Alta |
| Relatorio financeiro | Visualizar alunos pendentes, pagos, receita prevista e cobrancas | Media |
| Notificacao de vencimento | Avisar aluno sobre vencimento ou atraso | Media |
| Integracao de pagamento | Gerar Pix e assinatura recorrente com cartao via Asaas | Media |
| Configuracoes financeiras | Salvar chave Pix, dados do recebedor e preferencias do professor | Media |

### Comunicados

| Funcionalidade | Descricao | Prioridade |
| --- | --- | --- |
| Criar comunicado | Publicar texto para a turma ou publico definido | Alta |
| Feed de comunicados | Exibir comunicados recentes no app | Alta |
| Notificacao de novo comunicado | Alertar alunos quando houver novo aviso | Alta |
| Fixar comunicado | Destacar comunicado importante no topo do feed | Media |

### Eventos e comunidade

| Funcionalidade | Descricao | Prioridade |
| --- | --- | --- |
| Criar evento | Cadastrar evento com nome, data, local, descricao, foto e capacidade | Alta |
| Localizacao do evento | Informar local por mapa/geolocalizacao | Alta |
| Check-in | Permitir confirmacao de presenca do aluno | Alta |
| Corrida em grupo | Professor pode abrir, iniciar e finalizar evento de corrida | Media |
| Resultado de evento | Registrar tempo, distancia, pace e rota dos participantes | Media |
| Desafios | Criar metas por distancia ou tempo para engajar alunos | Media |

### Dashboard do professor

Indicadores esperados:

- Resumo de alunos ativos e inadimplentes.
- Treinos e eventos programados.
- Alunos treinando agora.
- KM total registrado pela turma.
- Receita do mes, separando pago e pendente.
- Ultimos comunicados publicados.
- Comprovantes pendentes de aprovacao.

## Area do Aluno

### Acompanhamento de treinos

| Funcionalidade | Descricao | Prioridade |
| --- | --- | --- |
| Meus treinos | Ver lista de treinos atribuidos com detalhes | Alta |
| Treino de hoje | Acessar o treino previsto para o dia | Alta |
| Registrar treino | Registrar atividade com tempo, distancia, pace, intensidade, pausas e rota | Alta |
| Modo stand-by | Manter o treino em andamento/pausado com servico em primeiro plano | Alta |
| Confirmar treino | Registrar que o treino foi realizado | Media |
| Historico pessoal | Ver corridas realizadas e evolucao ao longo do tempo | Alta |
| Compartilhar resultado | Gerar arte/resumo do treino realizado | Media |

### Acompanhamento de KM

| Funcionalidade | Descricao | Prioridade |
| --- | --- | --- |
| KM do mes | Exibir quilometros registrados no mes atual | Alta |
| Evolucao mensal | Exibir evolucao por periodo, como ultimos 6 meses | Alta |
| Rota no mapa | Mostrar percurso registrado por GPS | Alta |

Observacao: o projeto atual utiliza localizacao/GPS para registro de rota. O uso de acelerometro ou giroscopio permanece como opcional de avaliacao, caso seja necessario pontuar esse requisito especifico.

### Planilhas de treino

| Funcionalidade | Descricao | Prioridade |
| --- | --- | --- |
| Acessar planilha ativa | Visualizar a rotina/treino vigente atribuida pelo professor | Alta |
| Baixar arquivo | Baixar PDF, XLSX ou imagem de planilha quando houver anexo | Media |

### Mensalidades

| Funcionalidade | Descricao | Prioridade |
| --- | --- | --- |
| Status da mensalidade | Ver se esta em dia, proxima do vencimento ou em atraso | Alta |
| Historico de pagamentos | Listar mensalidades e status | Alta |
| Envio de comprovante | Enviar imagem/comprovante para aprovacao | Alta |
| Pagamento Pix | Gerar Pix via Asaas | Alta |
| Assinatura no cartao | Cadastrar assinatura recorrente via Asaas | Media |
| Notificacao de vencimento | Receber push antes ou apos vencimento | Alta |

### Feed de comunicados

| Funcionalidade | Descricao | Prioridade |
| --- | --- | --- |
| Ver comunicados | Acessar comunicados publicados pela assessoria | Alta |
| Notificacao de novo comunicado | Receber push notification ao publicar novo aviso | Alta |
| Comunicados fixados | Comunicados importantes aparecem em destaque | Media |

### Eventos

| Funcionalidade | Descricao | Prioridade |
| --- | --- | --- |
| Ver eventos | Consultar eventos disponiveis da assessoria | Alta |
| Check-in | Confirmar presenca em evento | Alta |
| Registrar corrida do evento | Enviar resultado com rota, tempo, distancia e pace | Media |
