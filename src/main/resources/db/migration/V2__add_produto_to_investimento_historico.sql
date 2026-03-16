-- SQLite doesn't support ADD COLUMN with FK, so recreate the table
CREATE TABLE investimento_historico_new (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    cliente_id  INTEGER NOT NULL,
    produto_id  INTEGER,
    tipo        TEXT    NOT NULL,
    valor       DECIMAL(18,2) NOT NULL,
    rentabilidade DECIMAL(10,6) NOT NULL,
    data        TEXT    NOT NULL,
    FOREIGN KEY (produto_id) REFERENCES produto (id)
);

INSERT INTO investimento_historico_new (id, cliente_id, produto_id, tipo, valor, rentabilidade, data)
SELECT id, cliente_id, NULL, tipo, valor, rentabilidade, data
FROM investimento_historico;

DROP TABLE investimento_historico;

ALTER TABLE investimento_historico_new RENAME TO investimento_historico;

CREATE INDEX IF NOT EXISTS idx_historico_cliente_id ON investimento_historico (cliente_id);
CREATE INDEX IF NOT EXISTS idx_historico_produto_id ON investimento_historico (produto_id);

-- Update seed rows to link their respective products
UPDATE investimento_historico SET produto_id = 101 WHERE tipo = 'CDB'              AND cliente_id = 123;
UPDATE investimento_historico SET produto_id = 104 WHERE tipo = 'Fundo Multimercado' AND cliente_id = 123;
