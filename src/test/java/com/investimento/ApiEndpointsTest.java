package com.investimento;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
@TestSecurity(user = "avaliador", roles = {"usuario", "analista"})
class ApiEndpointsTest {

    @Test
    void shouldCalculateRiskProfile() {
        given()
                .when().get("/perfil-risco/999")
                .then()
                .statusCode(200)
                .body("success", org.hamcrest.Matchers.is(true))
                .body("data.clienteId", org.hamcrest.Matchers.is(999))
                .body("data.perfil", notNullValue())
                .body("data.pontuacao", greaterThanOrEqualTo(0));
    }

    @Test
    void shouldSimulateInvestment() {
        String payload = """
                {
                  "clienteId": 999,
                  "valor": 10000.00,
                  "prazoMeses": 12,
                  "tipoProduto": "CDB"
                }
                """;

        given()
                .contentType("application/json")
                .body(payload)
                .when().post("/simular-investimento")
                .then()
                .statusCode(200)
                .body("success", org.hamcrest.Matchers.is(true))
                .body("data.produtoValidado.nome", notNullValue())
                .body("data.resultadoSimulacao.valorFinal", notNullValue())
                .body("data.dataSimulacao", notNullValue());
    }

    @Test
    void shouldReturnBadRequestForInvalidSimulationPayload() {
        String payload = """
                {
                  "clienteId": 999,
                  "valor": 0,
                  "prazoMeses": 0,
                  "tipoProduto": ""
                }
                """;

        given()
                .contentType("application/json")
                .body(payload)
                .when().post("/simular-investimento")
                .then()
                .statusCode(400);
    }

    @Test
    void shouldListSimulationsHistory() {
        given()
                .queryParam("page", 0)
                .queryParam("pageSize", 10)
                .when().get("/simulacoes")
                .then()
                .statusCode(200)
                .body("success", org.hamcrest.Matchers.is(true))
                .body("data.content", notNullValue())
                .body("data.page", org.hamcrest.Matchers.is(0));
    }

    @Test
    void shouldListRecommendedProductsForProfile() {
        given()
                .when().get("/produtos-recomendados/moderado")
                .then()
                .statusCode(200)
                .body("success", org.hamcrest.Matchers.is(true))
                .body("data", notNullValue());
    }

    @Test
    void shouldReturnBadRequestForInvalidProfileRecommendation() {
        given()
                .when().get("/produtos-recomendados/invalido")
                .then()
                .statusCode(400)
                .body("success", org.hamcrest.Matchers.is(false));
    }

    @Test
    void shouldReturnTelemetry() {
        given()
                .when().get("/telemetria")
                .then()
                .statusCode(200)
                .body("success", org.hamcrest.Matchers.is(true))
                .body("data.servicos", notNullValue())
                .body("data.periodo.inicio", notNullValue())
                .body("data.periodo.fim", notNullValue());
    }

    @Test
    void shouldReturnInvestimentoHistoricoForExistingCliente() {
        given()
                .when().get("/investimentos/999")
                .then()
                .statusCode(200)
                .body("success", org.hamcrest.Matchers.is(true))
                .body("data", notNullValue());
    }

    @Test
    void shouldReturnEmptyListForClienteWithNoHistorico() {
        given()
                .when().get("/investimentos/0")
                .then()
                .statusCode(200)
                .body("success", org.hamcrest.Matchers.is(true))
                .body("data", notNullValue());
    }
}
