ALTER TABLE simulacao ADD COLUMN data_simulacao_date TEXT;

UPDATE simulacao SET data_simulacao_date = substr(data_simulacao, 1, 10);

CREATE INDEX IF NOT EXISTS idx_simulacao_data_date ON simulacao (data_simulacao_date);
