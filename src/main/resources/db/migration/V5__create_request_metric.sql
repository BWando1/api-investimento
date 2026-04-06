CREATE TABLE request_metric (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    servico VARCHAR(255) NOT NULL,
    tempo_resposta_ms INTEGER NOT NULL,
    timestamp TEXT NOT NULL
);

CREATE INDEX idx_request_metric_timestamp ON request_metric(timestamp);
CREATE INDEX idx_request_metric_servico ON request_metric(servico);
