# Autenticação — Guia para o Avaliador

A API usa **Keycloak 26** como servidor de identidade. Todos os endpoints de negócio exigem um JWT válido no header `Authorization: Bearer <token>`.

---

## 1. Pré-requisito: subir os containers

```bash
./mvnw package -DskipTests
docker compose up --build
```

Aguarde até que ambos os serviços estejam prontos:

- **Keycloak** → `http://localhost:8180` (pode demorar ~20s na primeira vez)
- **API** → `http://localhost:8080`

---

## 2. Usuários disponíveis

| Usuário | Senha | Papel | Acesso |
|---|---|---|---|
| `analista1` | `123456` | `analista` | Todos os endpoints |
| `usuario1` | `123456` | `usuario` | Simulação, perfil de risco, produtos, investimentos |

---

## 3. Obter o token JWT

### Analista (acesso total)

```bash
curl -X POST "http://localhost:8180/realms/investimentos/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=api-investimento" \
  -d "client_secret=api-secret" \
  -d "username=analista1" \
  -d "password=123456"
```

### Usuário padrão

```bash
curl -X POST "http://localhost:8180/realms/investimentos/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=api-investimento" \
  -d "client_secret=api-secret" \
  -d "username=usuario1" \
  -d "password=123456"
```

A resposta será um JSON. O campo `access_token` é o JWT a ser usado nas chamadas.

---

## 4. Usando o token nas chamadas

```bash
# Extraindo o token automaticamente (requer Python)
TOKEN=$(curl -sf -X POST "http://localhost:8180/realms/investimentos/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password&client_id=api-investimento&client_secret=api-secret&username=analista1&password=123456" \
  | python -c "import sys,json; print(json.load(sys.stdin)['access_token'])")

# Chamada com autenticação
curl -X GET "http://localhost:8080/telemetria" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 5. Controle de acesso por endpoint

| Endpoint | `usuario` | `analista` |
|---|---|---|
| `POST /simular-investimento` | ✅ | ✅ |
| `GET /perfil-risco/{clienteId}` | ✅ | ✅ |
| `GET /produtos-recomendados/{perfil}` | ✅ | ✅ |
| `GET /investimentos/{clienteId}` | ✅ | ✅ |
| `GET /simulacoes` | ❌ 403 | ✅ |
| `GET /simulacoes/por-produto-dia` | ❌ 403 | ✅ |
| `GET /telemetria` | ❌ 403 | ✅ |

Qualquer chamada sem token retorna **401 Unauthorized**.

---

## 6. Validação rápida de controle de acesso

```bash
# Obter token de usuario (papel restrito)
TOKEN_USER=$(curl -sf -X POST "http://localhost:8180/realms/investimentos/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password&client_id=api-investimento&client_secret=api-secret&username=usuario1&password=123456" \
  | python -c "import sys,json; print(json.load(sys.stdin)['access_token'])")

# Deve retornar 200 — usuario tem acesso
curl -s -o /dev/null -w "%{http_code}" \
  -H "Authorization: Bearer $TOKEN_USER" \
  http://localhost:8080/perfil-risco/999

# Deve retornar 403 — usuario não tem acesso a telemetria
curl -s -o /dev/null -w "%{http_code}" \
  -H "Authorization: Bearer $TOKEN_USER" \
  http://localhost:8080/telemetria

# Deve retornar 401 — sem token
curl -s -o /dev/null -w "%{http_code}" \
  http://localhost:8080/simulacoes
```

Resultado esperado: `200`, `403`, `401`.

---

## 7. Swagger UI (alternativa ao curl)

Acesse http://localhost:8080/q/swagger-ui, clique em **Authorize** e informe:

```
Bearer <access_token>
```

Todos os endpoints ficam disponíveis para teste direto na interface.

---

## Detalhes técnicos

- **Realm:** `investimentos`
- **Client ID:** `api-investimento`
- **Client Secret:** `api-secret`
- **Grant type:** `password` (Resource Owner Password Credentials)
- **Roles no JWT:** lidas do claim `realm_access.roles`
- **Configuração na API:** `quarkus.oidc.roles.role-claim-path=realm_access/roles`
