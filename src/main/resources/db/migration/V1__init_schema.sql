CREATE TABLE IF NOT EXISTS produto (
    id INTEGER PRIMARY KEY,
    nome TEXT NOT NULL,
    tipo TEXT NOT NULL,
    rentabilidade DECIMAL(10,6) NOT NULL,
    risco TEXT NOT NULL,
    prazo_min_meses INTEGER NOT NULL,
    prazo_max_meses INTEGER NOT NULL,
    valor_minimo DECIMAL(18,2) NOT NULL,
    liquidez_score INTEGER NOT NULL,
    rentabilidade_score INTEGER NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT 1,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TEXT
);

CREATE TABLE IF NOT EXISTS simulacao (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    cliente_id INTEGER NOT NULL,
    produto_id INTEGER NOT NULL,
    produto_nome TEXT NOT NULL,
    tipo_produto TEXT NOT NULL,
    valor_investido DECIMAL(18,2) NOT NULL,
    valor_final DECIMAL(18,2) NOT NULL,
    rentabilidade_efetiva DECIMAL(10,6) NOT NULL,
    prazo_meses INTEGER NOT NULL,
    data_simulacao TEXT NOT NULL,
    FOREIGN KEY (produto_id) REFERENCES produto (id)
);

CREATE TABLE IF NOT EXISTS investimento_historico (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    cliente_id INTEGER NOT NULL,
    tipo TEXT NOT NULL,
    valor DECIMAL(18,2) NOT NULL,
    rentabilidade DECIMAL(10,6) NOT NULL,
    data TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS telemetria_servico (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome_servico TEXT NOT NULL UNIQUE,
    quantidade_chamadas INTEGER NOT NULL DEFAULT 0,
    tempo_total_resposta_ms BIGINT NOT NULL DEFAULT 0,
    ultima_atualizacao TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_simulacao_cliente_id ON simulacao (cliente_id);
CREATE INDEX IF NOT EXISTS idx_simulacao_data ON simulacao (data_simulacao);
CREATE INDEX IF NOT EXISTS idx_historico_cliente_id ON investimento_historico (cliente_id);

INSERT OR IGNORE INTO produto (
    id,
    nome,
    tipo,
    rentabilidade,
    risco,
    prazo_min_meses,
    prazo_max_meses,
    valor_minimo,
    liquidez_score,
    rentabilidade_score,
    ativo
) VALUES
    (101, 'CDB Caixa 2026', 'CDB', 0.120000, 'Baixo', 6, 36, 1000.00, 9, 5, 1),
    (102, 'LCA Safra 24M', 'LCA', 0.105000, 'Baixo', 12, 24, 5000.00, 8, 6, 1),
    (103, 'Tesouro Prefixado 2029', 'TESOURO', 0.115000, 'Medio', 12, 48, 100.00, 7, 7, 1),
    (104, 'Fundo Multimercado Dinamico', 'FUNDO', 0.180000, 'Alto', 6, 60, 1000.00, 4, 9, 1);

INSERT OR IGNORE INTO investimento_historico (cliente_id, tipo, valor, rentabilidade, data) VALUES
    (123, 'CDB', 5000.00, 0.120000, '2025-01-15'),
    (123, 'Fundo Multimercado', 3000.00, 0.080000, '2025-03-10');
