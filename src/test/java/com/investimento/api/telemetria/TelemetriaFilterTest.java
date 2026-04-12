package com.investimento.api.telemetria;

import com.investimento.entity.RequestMetric;
import com.investimento.service.impl.MetricsService;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TelemetriaFilterTest {

    private TelemetriaFilter filter;
    private MetricsService metricsService;
    private ContainerRequestContext requestContext;
    private ContainerResponseContext responseContext;
    private UriInfo uriInfo;

    @BeforeEach
    void setUp() throws Exception {
        filter = new TelemetriaFilter();
        metricsService = mock(MetricsService.class);
        requestContext = mock(ContainerRequestContext.class);
        responseContext = mock(ContainerResponseContext.class);
        uriInfo = mock(UriInfo.class);

        // Inject mock service
        Field field = TelemetriaFilter.class.getDeclaredField("metricsService");
        field.setAccessible(true);
        field.set(filter, metricsService);

        when(requestContext.getUriInfo()).thenReturn(uriInfo);
    }

    @Test
    void shouldSetStartTimePropertyOnRequest() {
        // Act
        filter.filter(requestContext);

        // Assert
        verify(requestContext).setProperty(eq("telemetria.start.nano"), any(Long.class));
    }

    @Test
    void shouldEnqueueMetricOnResponse() {
        // Arrange
        long startNanos = System.nanoTime();
        when(requestContext.getProperty("telemetria.start.nano")).thenReturn(startNanos);
        when(uriInfo.getPath()).thenReturn("simular-investimento");
        when(requestContext.getMethod()).thenReturn("POST");

        // Act
        filter.filter(requestContext, responseContext);

        // Assert
        ArgumentCaptor<RequestMetric> captor = ArgumentCaptor.forClass(RequestMetric.class);
        verify(metricsService).enqueue(captor.capture());

        RequestMetric metric = captor.getValue();
        assertEquals("POST /simular-investimento", metric.getServico());
        assertTrue(metric.getTempoRespostaMs() >= 0);
        assertNotNull(metric.getTimestamp());
    }

    @Test
    void shouldNormalizePathWithoutLeadingSlash() {
        // Arrange
        long startNanos = System.nanoTime();
        when(requestContext.getProperty("telemetria.start.nano")).thenReturn(startNanos);
        when(uriInfo.getPath()).thenReturn("/telemetria");
        when(requestContext.getMethod()).thenReturn("GET");

        // Act
        filter.filter(requestContext, responseContext);

        // Assert
        ArgumentCaptor<RequestMetric> captor = ArgumentCaptor.forClass(RequestMetric.class);
        verify(metricsService).enqueue(captor.capture());

        assertEquals("GET /telemetria", captor.getValue().getServico());
    }

    @Test
    void shouldIgnorePathsStartingWithQ() {
        // Arrange
        when(uriInfo.getPath()).thenReturn("q/health");

        // Act
        filter.filter(requestContext, responseContext);

        // Assert
        verify(metricsService, never()).enqueue(any());
    }

    @Test
    void shouldIgnoreNullPath() {
        // Arrange
        when(uriInfo.getPath()).thenReturn(null);

        // Act
        filter.filter(requestContext, responseContext);

        // Assert
        verify(metricsService, never()).enqueue(any());
    }

    @Test
    void shouldIgnoreRequestWithoutStartTime() {
        // Arrange
        when(uriInfo.getPath()).thenReturn("simular-investimento");
        when(requestContext.getProperty("telemetria.start.nano")).thenReturn(null);

        // Act
        filter.filter(requestContext, responseContext);

        // Assert
        verify(metricsService, never()).enqueue(any());
    }

    @Test
    void shouldIgnoreRequestWithInvalidStartTimeType() {
        // Arrange
        when(uriInfo.getPath()).thenReturn("simular-investimento");
        when(requestContext.getProperty("telemetria.start.nano")).thenReturn("invalid");

        // Act
        filter.filter(requestContext, responseContext);

        // Assert
        verify(metricsService, never()).enqueue(any());
    }

    @Test
    void shouldHandleExceptionDuringEnqueue() {
        // Arrange
        long startNanos = System.nanoTime();
        when(requestContext.getProperty("telemetria.start.nano")).thenReturn(startNanos);
        when(uriInfo.getPath()).thenReturn("simular-investimento");
        when(requestContext.getMethod()).thenReturn("POST");
        doThrow(new RuntimeException("Queue full")).when(metricsService).enqueue(any());

        // Act - should not throw exception
        assertDoesNotThrow(() -> filter.filter(requestContext, responseContext));

        // Assert
        verify(metricsService).enqueue(any());
    }

    @Test
    void shouldCalculateElapsedTimeCorrectly() throws InterruptedException {
        // Arrange
        long startNanos = System.nanoTime() - 10_000_000L; // 10ms ago
        when(requestContext.getProperty("telemetria.start.nano")).thenReturn(startNanos);
        when(uriInfo.getPath()).thenReturn("test");
        when(requestContext.getMethod()).thenReturn("GET");

        // Act
        filter.filter(requestContext, responseContext);

        // Assert
        ArgumentCaptor<RequestMetric> captor = ArgumentCaptor.forClass(RequestMetric.class);
        verify(metricsService).enqueue(captor.capture());

        assertTrue(captor.getValue().getTempoRespostaMs() >= 10,
            "Elapsed time should be at least 10ms");
    }
}
