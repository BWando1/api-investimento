package com.investimento;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@QuarkusTest
class InvestimentoResourceTest {

    @Test
    void testListAll() {
        given()
            .when().get("/investimentos")
            .then()
            .statusCode(200);
    }

    @Test
    void testCreateAndGet() {
        String body = """
            {
              "nome": "Tesouro Direto",
              "tipo": "Renda Fixa",
              "valor": 1000.00,
              "dataAplicacao": "2024-01-15"
            }
            """;

        Integer id = given()
            .contentType(ContentType.JSON)
            .body(body)
            .when().post("/investimentos")
            .then()
            .statusCode(201)
            .extract().path("id");

        given()
            .when().get("/investimentos/" + id)
            .then()
            .statusCode(200)
            .body("nome", is("Tesouro Direto"));
    }

    @Test
    void testGetNotFound() {
        given()
            .when().get("/investimentos/999999")
            .then()
            .statusCode(404);
    }
}
