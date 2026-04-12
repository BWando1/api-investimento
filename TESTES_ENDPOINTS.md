# Testes dos Endpoints - Validação Manual

## Produto Configurado no Banco

De acordo com a migration `V4__seed_mock_perfil_risco.sql`, temos:

```sql
INSERT INTO produto VALUES
  (101, 'CDB Caixa 2026', 'CDB', 0.12, 'Baixo', 1, 60, 500.00, 8, 7, 1);
```

**Dados do Produto:**
- ID: 101
- Nome: CDB Caixa 2026
- Tipo: CDB
- **Rentabilidade anual: 0.12 (12% ao ano)**
- Risco: Baixo
- Prazo: 1 a 60 meses
- Valor mínimo: R$ 500,00

---

## Teste 1: Simulação CDB - 12 meses - R$ 10.000,00

### Payload Enviado:
```json
{
  "clienteId": 100,
  "valor": 10000.00,
  "prazoMeses": 12,
  "tipoProduto": "CDB"
}
```

### Cálculo Manual:
```
Rentabilidade anual:  0.12 (12%)
Rentabilidade mensal: 0.12 / 12 = 0.01 (1% ao mês)

Fórmula de Juros Compostos:
Valor Final = Valor Inicial × (1 + taxa_mensal)^prazo

Valor Final = 10000 × (1 + 0.01)^12
Valor Final = 10000 × (1.01)^12
Valor Final = 10000 × 1.126825030131...
Valor Final = 11.268,25

Rentabilidade Efetiva:
Rent. Efetiva = (Valor Final / Valor Inicial) - 1
Rent. Efetiva = (11268.25 / 10000) - 1
Rent. Efetiva = 1.126825 - 1
Rent. Efetiva = 0.126825 (12,68%)
```

### Resposta Esperada:
```json
{
  "success": true,
  "data": {
    "produtoValidado": {
      "id": 101,
      "nome": "CDB Caixa 2026",
      "tipo": "CDB",
      "rentabilidade": 0.12,
      "risco": "Baixo"
    },
    "resultadoSimulacao": {
      "valorFinal": 11268.25,
      "rentabilidadeEfetiva": 0.126825,
      "prazoMeses": 12
    }
  }
}
```

---

## Teste 2: Simulação CDB - 24 meses - R$ 5.000,00

### Payload Enviado:
```json
{
  "clienteId": 101,
  "valor": 5000.00,
  "prazoMeses": 24,
  "tipoProduto": "CDB"
}
```

### Cálculo Manual:
```
Rentabilidade mensal: 0.01 (1% ao mês)

Valor Final = 5000 × (1.01)^24
Valor Final = 5000 × 1.269734648...
Valor Final = 6.348,67

Rentabilidade Efetiva:
Rent. Efetiva = (6348.67 / 5000) - 1
Rent. Efetiva = 0.269734 (26,97%)
```

### Resposta Esperada:
```json
{
  "resultadoSimulacao": {
    "valorFinal": 6348.67,
    "rentabilidadeEfetiva": 0.269734,
    "prazoMeses": 24
  }
}
```

---

## Teste 3: Simulação CDB - 36 meses - R$ 20.000,00

### Payload Enviado:
```json
{
  "clienteId": 102,
  "valor": 20000.00,
  "prazoMeses": 36,
  "tipoProduto": "CDB"
}
```

### Cálculo Manual:
```
Rentabilidade mensal: 0.01 (1% ao mês)

Valor Final = 20000 × (1.01)^36
Valor Final = 20000 × 1.430768783...
Valor Final = 28.615,38

Rentabilidade Efetiva:
Rent. Efetiva = (28615.38 / 20000) - 1
Rent. Efetiva = 0.430769 (43,08%)
```

### Resposta Esperada:
```json
{
  "resultadoSimulacao": {
    "valorFinal": 28615.38,
    "rentabilidadeEfetiva": 0.430769,
    "prazoMeses": 36
  }
}
```

---

## Teste 4: Simulação CDB - 6 meses - R$ 1.000,00

### Payload Enviado:
```json
{
  "clienteId": 103,
  "valor": 1000.00,
  "prazoMeses": 6,
  "tipoProduto": "CDB"
}
```

### Cálculo Manual:
```
Rentabilidade mensal: 0.01 (1% ao mês)

Valor Final = 1000 × (1.01)^6
Valor Final = 1000 × 1.061520150...
Valor Final = 1.061,52

Rentabilidade Efetiva:
Rent. Efetiva = (1061.52 / 1000) - 1
Rent. Efetiva = 0.06152 (6,15%)
```

### Resposta Esperada:
```json
{
  "resultadoSimulacao": {
    "valorFinal": 1061.52,
    "rentabilidadeEfetiva": 0.06152,
    "prazoMeses": 6
  }
}
```

---

## Teste 5: Produto não elegível (valor abaixo do mínimo)

### Payload Enviado:
```json
{
  "clienteId": 104,
  "valor": 100.00,
  "prazoMeses": 12,
  "tipoProduto": "CDB"
}
```

### Resposta Esperada:
```json
{
  "success": false,
  "message": "Nenhum produto elegivel encontrado para tipo 'CDB', valor 100.00 e prazo 12 meses."
}
```

**Motivo:** O produto CDB Caixa 2026 tem valor mínimo de R$ 500,00.

---

## Teste 6: Tipo de produto inexistente

### Payload Enviado:
```json
{
  "clienteId": 105,
  "valor": 10000.00,
  "prazoMeses": 12,
  "tipoProduto": "BITCOIN"
}
```

### Resposta Esperada:
```json
{
  "success": false,
  "message": "Nenhum produto elegivel encontrado para tipo 'BITCOIN', valor 10000.00 e prazo 12 meses."
}
```

---

## Como Executar os Testes

### 1. Obter Token de Autenticação:
```bash
curl -X POST "http://localhost:8180/realms/investimentos/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=api-investimento" \
  -d "client_secret=api-secret" \
  -d "username=analista1" \
  -d "password=123456"
```

### 2. Executar Simulação (substitua TOKEN):
```bash
curl -X POST "http://localhost:8080/simular-investimento" \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": 100,
    "valor": 10000.00,
    "prazoMeses": 12,
    "tipoProduto": "CDB"
  }'
```

---

## Fórmula Implementada no Código

**Arquivo:** `CalculoInvestimentoServiceImpl.java`

```java
// Converte rentabilidade anual para mensal
BigDecimal rentabilidadeMensal = rentabilidadeAnual.divide(
    BigDecimal.valueOf(12),
    MathContext.DECIMAL128
);

// Calcula (1 + taxa_mensal)
BigDecimal umMaisTaxa = BigDecimal.ONE.add(rentabilidadeMensal);

// Eleva ao prazo: (1 + taxa_mensal)^prazoMeses
BigDecimal fator = umMaisTaxa.pow(prazoMeses, MathContext.DECIMAL128);

// Multiplica pelo valor inicial
BigDecimal valorFinal = valorInicial.multiply(fator, MathContext.DECIMAL128);

// Arredonda para 2 casas decimais
valorFinal = valorFinal.setScale(2, RoundingMode.HALF_UP);
```

**Precisão:**
- Cálculos intermediários: 34 dígitos (DECIMAL128)
- Valor final: 2 casas decimais (HALF_UP)
- Rentabilidade efetiva: 6 casas decimais (HALF_UP)
