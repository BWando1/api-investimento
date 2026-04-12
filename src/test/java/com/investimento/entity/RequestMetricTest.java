package com.investimento.entity;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RequestMetricTest {

    @Test
    void shouldCreateRequestMetricWithDefaultConstructor() {
        // Act
        RequestMetric metric = new RequestMetric();

        // Assert
        assertNotNull(metric);
        assertNull(metric.getId());
        assertNull(metric.getServico());
        assertNull(metric.getTempoRespostaMs());
        assertNull(metric.getTimestamp());
    }

    @Test
    void shouldCreateRequestMetricWithParameterizedConstructor() {
        // Arrange
        String servico = "POST /api/test";
        Long tempo = 150L;
        OffsetDateTime timestamp = OffsetDateTime.now();

        // Act
        RequestMetric metric = new RequestMetric(servico, tempo, timestamp);

        // Assert
        assertNotNull(metric);
        assertNull(metric.getId()); // ID is not set via constructor
        assertEquals(servico, metric.getServico());
        assertEquals(tempo, metric.getTempoRespostaMs());
        assertEquals(timestamp, metric.getTimestamp());
    }

    @Test
    void shouldSetAndGetId() {
        // Arrange
        RequestMetric metric = new RequestMetric();
        Long id = 123L;

        // Act
        metric.setId(id);

        // Assert
        assertEquals(id, metric.getId());
    }

    @Test
    void shouldSetAndGetServico() {
        // Arrange
        RequestMetric metric = new RequestMetric();
        String servico = "GET /telemetria";

        // Act
        metric.setServico(servico);

        // Assert
        assertEquals(servico, metric.getServico());
    }

    @Test
    void shouldSetAndGetTempoRespostaMs() {
        // Arrange
        RequestMetric metric = new RequestMetric();
        Long tempo = 500L;

        // Act
        metric.setTempoRespostaMs(tempo);

        // Assert
        assertEquals(tempo, metric.getTempoRespostaMs());
    }

    @Test
    void shouldSetAndGetTimestamp() {
        // Arrange
        RequestMetric metric = new RequestMetric();
        OffsetDateTime timestamp = OffsetDateTime.now();

        // Act
        metric.setTimestamp(timestamp);

        // Assert
        assertEquals(timestamp, metric.getTimestamp());
    }

    @Test
    void shouldAllowNullValues() {
        // Arrange
        RequestMetric metric = new RequestMetric("test", 100L, OffsetDateTime.now());

        // Act
        metric.setServico(null);
        metric.setTempoRespostaMs(null);
        metric.setTimestamp(null);

        // Assert
        assertNull(metric.getServico());
        assertNull(metric.getTempoRespostaMs());
        assertNull(metric.getTimestamp());
    }

    @Test
    void shouldAcceptZeroTempoResposta() {
        // Arrange & Act
        RequestMetric metric = new RequestMetric("GET /fast", 0L, OffsetDateTime.now());

        // Assert
        assertEquals(0L, metric.getTempoRespostaMs());
    }

    @Test
    void shouldAcceptVeryLargeTempoResposta() {
        // Arrange & Act
        Long largeValue = Long.MAX_VALUE;
        RequestMetric metric = new RequestMetric("GET /slow", largeValue, OffsetDateTime.now());

        // Assert
        assertEquals(largeValue, metric.getTempoRespostaMs());
    }

    @Test
    void shouldHandleLongServiceNames() {
        // Arrange
        String longServiceName = "POST /".concat("a".repeat(1000));

        // Act
        RequestMetric metric = new RequestMetric(longServiceName, 100L, OffsetDateTime.now());

        // Assert
        assertEquals(longServiceName, metric.getServico());
        assertEquals(1006, metric.getServico().length()); // "POST /" (6 chars) + 1000 'a's
    }
}
