CREATE TABLE IF NOT EXISTS investimento_historico_SEQ (
    next_val INTEGER
);

INSERT INTO investimento_historico_SEQ(next_val)
SELECT COALESCE(MAX(id), 0) + 1
FROM investimento_historico
WHERE NOT EXISTS (SELECT 1 FROM investimento_historico_SEQ);

CREATE TABLE IF NOT EXISTS simulacao_SEQ (
    next_val INTEGER
);

INSERT INTO simulacao_SEQ(next_val)
SELECT COALESCE(MAX(id), 0) + 1
FROM simulacao
WHERE NOT EXISTS (SELECT 1 FROM simulacao_SEQ);

CREATE TABLE IF NOT EXISTS telemetria_servico_SEQ (
    next_val INTEGER
);

INSERT INTO telemetria_servico_SEQ(next_val)
SELECT COALESCE(MAX(id), 0) + 1
FROM telemetria_servico
WHERE NOT EXISTS (SELECT 1 FROM telemetria_servico_SEQ);
