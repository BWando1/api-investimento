  Desafio – Painel de Investimentos com Perfil de Risco Dinâmico 
 
 Objetivo 
Criar uma aplicação web que analisa o comportamento financeiro do cliente e ajusta automaticamente seu perfil de risco, sugerindo produtos de investimento como CDBs, LCIs, LCAs, Tesouro Direto,  Fundos, etc. 
 
  Desafio Back-end – Java ou .NET C# 
A CAIXA está evoluindo sua plataforma de investimentos digital. Você precisa disponibilizar para todos os brasileiros a possibilidade de simulação de Investimentos. Esperamos uma API que retornem perfil de risco com base em dados financeiros, produtos de investimento mais adequados ao perfil e histórico de investimentos e simular investimentos com entrada de valor, prazo e tipo.  
Desenvolva uma API em linguagem Java 21 ou C# (.Net) 8+ que terão como requisitos: 
•	Receber um envelope JSON, via chamada à API, contendo uma solicitação de simulação de investimentos.  
•	Consultar um conjunto de informações parametrizadas em uma tabela de banco de dados SQL Server ou SQLite.  
•	Validar os dados de entrada da API com base nos parâmetros de produtos retornados no banco de dados.  
•	Filtrar qual produto se adequa aos parâmetros de entrada. 
•	Realizar cálculos para as simulações de cada tipo de investimento 
•	Retornar um envelope JSON contendo o nome do produto validado, e o resultado da simulação.  
•	Persistir em banco local a simulação realizada.  
•	Criar um endpoint para retornar todas as simulações realizadas.  a
•	Criar um endpoint para retornar os valores simulados para cada produto em cada dia.  
•	Criar um endpoint para retornar dados de telemetria com volumes e tempos de resposta para cada serviço.  
•	Disponibilizar o código fonte, com todas as evidências no formato zip ou arquivo texto contendo link para o Git público.  
•	Incluir no projeto todos os arquivos para execução via container (dockerfile / Docker compose)  
•	Autenticação em JWT, OAuth2 ou Keycloak 
 
•	Motor de Recomendação: 
	o 	Algoritmo simples baseado em: 
▪	Volume de investimentos 
▪	Frequência de movimentações 
▪	Preferência por liquidez ou rentabilidade o Pode usar pontuação para definir perfil: 
▪	Conservador: baixa movimentação, foco em liquidez 
▪	Moderado: equilíbrio entre liquidez e rentabilidade 
▪	Agressivo: busca por alta rentabilidade, maior risco 
 
Modelos de Envelopes JSON para a API 
1. Solicitação de Simulação de Investimento 
Endpoint: POST /simular-investimento 
Request 
{ 
  "clienteId": 123, 
  "valor": 10000.00, 
  "prazoMeses": 12, 
  "tipoProduto": "CDB" 
} 
Response 
{ 
  "produtoValidado": { 
    "id": 101, 
    "nome": "CDB Caixa 2026", 
    "tipo": "CDB", 
    "rentabilidade": 0.12, 
    "risco": "Baixo" 
  }, 
  "resultadoSimulacao": { 
    "valorFinal": 11200.00, 
    "rentabilidadeEfetiva": 0.12, 
    "prazoMeses": 12 
  }, 
  "dataSimulacao": "2025-10-31T14:00:00Z" 
} 
2. Histórico de Simulações Realizadas 
Endpoint: GET /simulacoes 
Response 
[ 
  { 
    "id": 1, 
    "clienteId": 123, 
    "produto": "CDB Caixa 2026", 
    "valorInvestido": 10000.00, 
    "valorFinal": 11200.00, 
    "prazoMeses": 12, 
    "dataSimulacao": "2025-10-31T14:00:00Z" 
  }, 
  { 
    "id": 2, 
    "clienteId": 123, 
    "produto": "Fundo XPTO", 
    "valorInvestido": 5000.00, 
    "valorFinal": 5800.00, 
    "prazoMeses": 6, 
    "dataSimulacao": "2025-09-15T10:30:00Z" 
  } 
] 
 
3.  Valores Simulados por Produto e Dia 
Endpoint: GET /simulacoes/por-produto-dia 
Response 
[ 
  { 
    "produto": "CDB Caixa 2026",     "data": "2025-10-30", 
    "quantidadeSimulacoes": 15, 
    "mediaValorFinal": 11050.00 
  }, 
  { 
    "produto": "Fundo XPTO", 
    "data": "2025-10-30", 
    "quantidadeSimulacoes": 8, 
    "mediaValorFinal": 5700.00 
  } 
] 
4. Dados de Telemetria Endpoint: GET /telemetria Response 
{ 
  "servicos": [ 
    { 
      "nome": "simular-investimento", 
      "quantidadeChamadas": 120, 
      "mediaTempoRespostaMs": 250 
    }, 
    { 
      "nome": "perfil-risco", 
      "quantidadeChamadas": 80, 
      "mediaTempoRespostaMs": 180 
    } 
  ], 
  "periodo": { 
    "inicio": "2025-10-01", 
    "fim": "2025-10-31" 
  } 
} 
5.	Perfil de Risco 
Endpoint: GET /perfil-risco/{clienteId} 
Response 
{ 
  "clienteId": 123, 
  "perfil": "Moderado", 
  "pontuacao": 65, 
  "descricao": "Perfil equilibrado entre segurança e rentabilidade." 
} 
6.	Produtos Recomendados 
Endpoint: GET /produtos-recomendados/{perfil} 
Response 
[ 
  { 
    "id": 101, 
    "nome": "CDB Caixa 2026", 
    "tipo": "CDB", 
    "rentabilidade": 0.12, 
    "risco": "Baixo" 
  }, 
  { 
    "id": 102, 
    "nome": "Fundo XPTO", 
    "tipo": "Fundo", 
    "rentabilidade": 0.18, 
    "risco": "Alto" 
  } 
] 
7. Histórico de Investimentos 
Endpoint: GET /investimentos/{clienteId} 
Response 
[ 
  { 
    "id": 1, 
    "tipo": "CDB", 
    "valor": 5000.00, 
    "rentabilidade": 0.12, 
    "data": "2025-01-15" 
  }, 
  { 
    "id": 2, 
    "tipo": "Fundo Multimercado", 
    "valor": 3000.00, 
    "rentabilidade": 0.08, 
    "data": "2025-03-10" 
  } 
] 
 
  Critérios de Avaliação 
•	Estrutura da API e documentação 
•	Qualidade do motor de recomendação 
•	Segurança e tratamento de erros 
•	Testes unitários e integração 
 
