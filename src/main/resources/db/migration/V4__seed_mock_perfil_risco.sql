-- Mock data for local testing of GET /perfil-risco/{clienteId}
-- clienteId: 999
INSERT INTO investimento_historico (cliente_id, produto_id, tipo, valor, rentabilidade, data)
VALUES
    (999, 101, 'CDB', 30000.00, 0.110000, '2026-01-10'),
    (999, 104, 'Fundo Multimercado', 85000.00, 0.170000, '2026-02-20'),
    (999, 103, 'Tesouro IPCA+', 45000.00, 0.100000, '2026-03-05');
