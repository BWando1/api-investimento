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

The API will be available at http://localhost:8080

## REST Endpoints

The challenge endpoints are being implemented in the next step based on `desafio.md`.

## Running Tests

```bash
./mvnw test
```

## Database

SQLite is used as a file-based database. The data file is stored at `/data/investimento.db` inside the container (mounted as a Docker volume for persistence).
