# api-investimento

A Quarkus REST API for managing investments, using SQLite as the database.

## Technologies

- [Quarkus 3.x](https://quarkus.io/)
- SQLite (via `org.xerial:sqlite-jdbc`)
- Hibernate ORM with Panache
- RESTEasy Reactive (JAX-RS)
- SmallRye OpenAPI / Swagger UI
- Docker / Docker Compose

## Running in Development Mode

```bash
./mvnw quarkus:dev
```

The API will be available at http://localhost:8080  
Swagger UI: http://localhost:8080/q/swagger-ui

## Building and Running with Docker

```bash
# 1. Package the application
./mvnw package

# 2. Build and run with Docker Compose
docker compose up --build
```

The API will be available at http://localhost:8080 and Keycloak at http://localhost:8180.

### Authentication Flow For Evaluator

This project runs with real authentication by default:

- `docker-compose.yml` starts API + Keycloak
- Keycloak issues JWT tokens with roles `usuario` and `analista`
- API enforces endpoint access using `@RolesAllowed`

1. Get token (usuario):

```bash
curl -X POST "http://localhost:8180/realms/investimentos/protocol/openid-connect/token" \
	-H "Content-Type: application/x-www-form-urlencoded" \
	-d "client_id=api-investimento" \
	-d "client_secret=api-secret" \
	-d "grant_type=password" \
	-d "username=usuario1" \
	-d "password=123456"
```

2. Get token (analista):

```bash
curl -X POST "http://localhost:8180/realms/investimentos/protocol/openid-connect/token" \
	-H "Content-Type: application/x-www-form-urlencoded" \
	-d "client_id=api-investimento" \
	-d "client_secret=api-secret" \
	-d "grant_type=password" \
	-d "username=analista1" \
	-d "password=123456"
```

3. Call API with bearer token:

```bash
curl -X GET "http://localhost:8080/telemetria" \
	-H "Authorization: Bearer <ACCESS_TOKEN>"
```

Default test users in the imported realm:

- `usuario1` / `123456` with role `usuario`
- `analista1` / `123456` with role `analista`

Quick validation script (expected behavior):

1. `GET /perfil-risco/999` with token `usuario1` -> `200`
2. `GET /telemetria` with token `usuario1` -> `403`
3. `GET /telemetria` with token `analista1` -> `200`

### Where Roles Are Defined

- Token role extraction is configured in `src/main/resources/application.properties`:
	- `quarkus.oidc.roles.role-claim-path=realm_access/roles`
- This reads roles from Keycloak JWT claim `realm_access.roles` (e.g., `usuario`, `analista`).
- Authorization is enforced in resource classes using `@RolesAllowed`.

## REST Endpoints

The challenge endpoints are being implemented in the next step based on `desafio.md`.

## Running Tests

```bash
./mvnw test
```

## Database

SQLite is used as a file-based database. In Docker, this setup uses `jdbc:sqlite:/tmp/investimento.db` for simplicity in challenge execution.
