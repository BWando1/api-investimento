# API de Investimentos — Desafio CAIXA

API REST para simulação de investimentos com perfil de risco dinâmico, recomendação de produtos e telemetria.

## Stack

| Camada | Tecnologia |
|---|---|
| Runtime | Java 21 + Quarkus 3.18.4 |
| Persistência | SQLite + Hibernate ORM/Panache + Flyway |
| Segurança | Keycloak 26 (OIDC/JWT) |
| Testes | JUnit 5 + RestAssured + Mockito |
| Infraestrutura | Docker + Docker Compose |
| Documentação | SmallRye OpenAPI + Swagger UI |

---

## Executando com Docker (recomendado)

```bash
# 1. Gerar o JAR
./mvnw package -DskipTests

# 2. Subir API + Keycloak
docker compose up --build
```

| Serviço | URL |
|---|---|
| API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/q/swagger-ui |
| Keycloak Admin | http://localhost:8180 (admin / admin) |

> Para autenticar e obter o token JWT, consulte [SECURITY.md](SECURITY.md).

---

## Executando em modo desenvolvimento

```bash
./mvnw quarkus:dev
```

Em `dev`, a autenticação OIDC é desabilitada automaticamente pelo profile de teste.
A API sobe em http://localhost:8080.

---

## Endpoints

### Simulação

| Método | Endpoint | Papel | Descrição |
|---|---|---|---|
| `POST` | `/simular-investimento` | usuario, analista | Simula investimento e persiste resultado |
| `GET` | `/simulacoes` | analista | Histórico paginado de simulações |
| `GET` | `/simulacoes/por-produto-dia` | analista | Quantidade e média por produto/dia |

### Perfil de Risco e Produtos

| Método | Endpoint | Papel | Descrição |
|---|---|---|---|
| `GET` | `/perfil-risco/{clienteId}` | usuario, analista | Calcula perfil dinâmico por histórico |
| `GET` | `/produtos-recomendados/{perfil}` | usuario, analista | Lista produtos adequados ao perfil |
| `GET` | `/investimentos/{clienteId}` | usuario, analista | Histórico de investimentos do cliente |

### Observabilidade

| Método | Endpoint | Papel | Descrição |
|---|---|---|---|
| `GET` | `/telemetria` | analista | Volume de chamadas e tempo médio por serviço |

---

## Exemplo de Simulação

```bash
TOKEN=$(curl -sf -X POST "http://localhost:8180/realms/investimentos/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password&client_id=api-investimento&client_secret=api-secret&username=analista1&password=123456" \
  | python -c "import sys,json; print(json.load(sys.stdin)['access_token'])")

curl -X POST http://localhost:8080/simular-investimento \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"clienteId": 100, "valor": 10000.00, "prazoMeses": 12, "tipoProduto": "CDB"}'
```

Resposta:

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
    },
    "dataSimulacao": "2026-04-14T17:49:59Z"
  }
}
```

---

## Fórmula de Cálculo

Juros compostos com rentabilidade anual convertida para mensal:

```
taxaMensal    = rentabilidadeAnual / 12
valorFinal    = valorInicial × (1 + taxaMensal)^prazoMeses
rentEfetiva   = (valorFinal / valorInicial) - 1
```

---

## Motor de Recomendação (Perfil de Risco)

O perfil é calculado com base no histórico de investimentos do cliente, somando três componentes (máx. 100 pontos):

| Componente | Peso | Critério |
|---|---|---|
| Volume investido | 0–40 pts | < R$10k → 10 / até R$50k → 20 / até R$150k → 30 / acima → 40 |
| Frequência | 0–30 pts | ≤ 2 registros → 8 / 3–6 → 15 / 7–12 → 24 / > 12 → 30 |
| Preferência | 0–30 pts | Comparação entre `liquidezScore` e `rentabilidadeScore` dos produtos |

| Pontuação | Perfil |
|---|---|
| < 40 | CONSERVADOR |
| 40–69 | MODERADO |
| ≥ 70 | AGRESSIVO |

---

## Testes

```bash
# Todos os testes (unit + integração)
./mvnw test

# Cobertura JaCoCo (gerada em target/site/jacoco/index.html)
./mvnw test jacoco:report
```

**75 testes** — unitários com Mockito e integração com `@QuarkusTest` + SQLite in-memory.

---

## Estrutura do Projeto

```
src/
├── main/java/com/investimento/
│   ├── api/
│   │   ├── dto/          # Records de request/response
│   │   ├── exception/    # Mappers de exceção (404, 400, 500)
│   │   ├── mapper/       # Conversores de entidade → DTO
│   │   ├── resource/     # Controllers JAX-RS
│   │   └── telemetria/   # Filtro de coleta de métricas
│   ├── domain/
│   │   ├── converter/    # LocalDate e OffsetDateTime ↔ String (SQLite)
│   │   └── entity/       # Entidades JPA
│   ├── repository/       # Repositórios Panache
│   └── service/          # Interfaces e implementações de serviço
└── main/resources/
    ├── application.properties
    └── db/migration/     # Flyway V1–V7
```

---

## Banco de Dados

SQLite com migrações Flyway. Em Docker, o banco fica em `/tmp/investimento.db` dentro do container.

| Migration | Descrição |
|---|---|
| V1 | Schema inicial (produto, simulacao, historico, telemetria) |
| V2 | FK produto em investimento_historico |
| V3–V6 | Ajustes de sequência e índices |
| V4 | Seed: produtos e histórico do cliente 999 (para testes) |
| V7 | Coluna `data_simulacao_date` para agrupamento por dia |
