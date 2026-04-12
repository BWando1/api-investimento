package com.investimento.service.impl;

import com.investimento.entity.RequestMetric;
import com.investimento.repository.RequestMetricRepository;
import com.investimento.service.TelemetriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MetricsServiceTest {

    private MetricsService metricsService;
    private RequestMetricRepository requestMetricRepository;
    private TelemetriaService telemetriaService;

    @BeforeEach
    void setUp() throws Exception {
        metricsService = new MetricsService();
        requestMetricRepository = mock(RequestMetricRepository.class);
        telemetriaService = mock(TelemetriaService.class);

        // Inject mocks
        injectField("requestMetricRepository", requestMetricRepository);
        injectField("telemetriaService", telemetriaService);
    }

    private void injectField(String fieldName, Object value) throws Exception {
        Field field = MetricsService.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(metricsService, value);
    }

    @Test
    void shouldEnqueueMetric() {
        // Arrange
        RequestMetric metric = new RequestMetric("POST /test", 100L, OffsetDateTime.now());

        // Act
        metricsService.enqueue(metric);

        // Assert - will be verified in flush
        metricsService.flush();
        verify(requestMetricRepository).salvar(anyList());
    }

    @Test
    void shouldIgnoreNullMetricInEnqueue() {
        // Act
        metricsService.enqueue(null);

        // Assert
        metricsService.flush();
        verify(requestMetricRepository, never()).salvar(anyList());
    }

    @Test
    void shouldFlushMetricsAndPersist() {
        // Arrange
        RequestMetric metric1 = new RequestMetric("POST /test1", 100L, OffsetDateTime.now());
        RequestMetric metric2 = new RequestMetric("GET /test2", 50L, OffsetDateTime.now());

        metricsService.enqueue(metric1);
        metricsService.enqueue(metric2);

        // Act
        metricsService.flush();

        // Assert
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<RequestMetric>> captor = ArgumentCaptor.forClass(List.class);
        verify(requestMetricRepository).salvar(captor.capture());

        List<RequestMetric> savedMetrics = captor.getValue();
        assertEquals(2, savedMetrics.size());
        assertTrue(savedMetrics.contains(metric1));
        assertTrue(savedMetrics.contains(metric2));
    }

    @Test
    void shouldCallTelemetriaServiceForEachMetric() {
        // Arrange
        RequestMetric metric1 = new RequestMetric("POST /test", 100L, OffsetDateTime.now());
        RequestMetric metric2 = new RequestMetric("GET /test", 50L, OffsetDateTime.now());

        metricsService.enqueue(metric1);
        metricsService.enqueue(metric2);

        // Act
        metricsService.flush();

        // Assert
        verify(telemetriaService).registrarChamada("POST /test", 100L);
        verify(telemetriaService).registrarChamada("GET /test", 50L);
    }

    @Test
    void shouldNotPersistWhenQueueIsEmpty() {
        // Act
        metricsService.flush();

        // Assert
        verify(requestMetricRepository, never()).salvar(anyList());
        verify(telemetriaService, never()).registrarChamada(anyString(), anyLong());
    }

    @Test
    void shouldClearQueueAfterFlush() {
        // Arrange
        RequestMetric metric = new RequestMetric("POST /test", 100L, OffsetDateTime.now());
        metricsService.enqueue(metric);

        // Act - first flush
        metricsService.flush();

        // Act - second flush
        metricsService.flush();

        // Assert - should only save once
        verify(requestMetricRepository, times(1)).salvar(anyList());
    }

    @Test
    void shouldHandleMultipleFlushCycles() {
        // Arrange & Act - cycle 1
        RequestMetric metric1 = new RequestMetric("POST /test1", 100L, OffsetDateTime.now());
        metricsService.enqueue(metric1);
        metricsService.flush();

        // Arrange & Act - cycle 2
        RequestMetric metric2 = new RequestMetric("POST /test2", 200L, OffsetDateTime.now());
        metricsService.enqueue(metric2);
        metricsService.flush();

        // Assert
        verify(requestMetricRepository, times(2)).salvar(anyList());
        verify(telemetriaService).registrarChamada("POST /test1", 100L);
        verify(telemetriaService).registrarChamada("POST /test2", 200L);
    }

    @Test
    void shouldEnqueueMetricsFromMultipleThreads() throws InterruptedException {
        // Arrange
        int threadCount = 10;
        int metricsPerThread = 5;
        Thread[] threads = new Thread[threadCount];

        // Act - enqueue from multiple threads
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < metricsPerThread; j++) {
                    RequestMetric metric = new RequestMetric(
                        "Thread-" + threadId + " metric-" + j,
                        100L,
                        OffsetDateTime.now()
                    );
                    metricsService.enqueue(metric);
                }
            });
            threads[i].start();
        }

        // Wait for all threads
        for (Thread thread : threads) {
            thread.join();
        }

        // Flush and verify
        metricsService.flush();

        // Assert
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<RequestMetric>> captor = ArgumentCaptor.forClass(List.class);
        verify(requestMetricRepository).salvar(captor.capture());

        assertEquals(threadCount * metricsPerThread, captor.getValue().size(),
            "All metrics from all threads should be captured");
    }
}
