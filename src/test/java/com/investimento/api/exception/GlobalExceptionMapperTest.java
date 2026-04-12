package com.investimento.api.exception;

import com.investimento.api.dto.ApiResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionMapperTest {

    private GlobalExceptionMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new GlobalExceptionMapper();
    }

    @Test
    void shouldMapBusinessExceptionToNotFound() {
        // Arrange
        BusinessException exception = new ResourceNotFoundException("Cliente não encontrado");

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertEquals(404, response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());

        ApiResponse<?> body = (ApiResponse<?>) response.getEntity();
        assertFalse(body.success());
        assertEquals("Cliente não encontrado", body.message());
        assertNull(body.data());
    }

    @Test
    void shouldMapBusinessExceptionWithCustomStatusCode() {
        // Arrange
        BusinessException exception = new BusinessException("Operação não permitida", 403);

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertEquals(403, response.getStatus());

        ApiResponse<?> body = (ApiResponse<?>) response.getEntity();
        assertFalse(body.success());
        assertEquals("Operação não permitida", body.message());
    }

    @Test
    void shouldMapGenericExceptionToInternalServerError() {
        // Arrange
        Exception exception = new RuntimeException("Erro inesperado");

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertEquals(500, response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());

        ApiResponse<?> body = (ApiResponse<?>) response.getEntity();
        assertFalse(body.success());
        assertEquals("Erro interno do servidor. Tente novamente mais tarde.", body.message());
        assertNull(body.data());
    }

    @Test
    void shouldMapNullPointerExceptionToInternalServerError() {
        // Arrange
        Exception exception = new NullPointerException("Null value");

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertEquals(500, response.getStatus());

        ApiResponse<?> body = (ApiResponse<?>) response.getEntity();
        assertFalse(body.success());
        assertEquals("Erro interno do servidor. Tente novamente mais tarde.", body.message());
    }

    @Test
    void shouldMapIllegalArgumentExceptionToInternalServerError() {
        // Arrange
        Exception exception = new IllegalArgumentException("Invalid argument");

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertEquals(500, response.getStatus());

        ApiResponse<?> body = (ApiResponse<?>) response.getEntity();
        assertFalse(body.success());
    }

    @Test
    void shouldNotExposeInternalErrorDetailsInGenericException() {
        // Arrange
        Exception exception = new RuntimeException("Database connection failed: user=admin password=secret");

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        ApiResponse<?> body = (ApiResponse<?>) response.getEntity();
        assertFalse(body.message().contains("admin"));
        assertFalse(body.message().contains("secret"));
        assertEquals("Erro interno do servidor. Tente novamente mais tarde.", body.message(),
            "Generic message should not expose internal details");
    }
}
