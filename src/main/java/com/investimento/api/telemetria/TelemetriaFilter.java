package com.investimento.api.telemetria;

import com.investimento.entity.RequestMetric;
import com.investimento.service.impl.MetricsService;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Provider
public class TelemetriaFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOG = Logger.getLogger(TelemetriaFilter.class);
    private static final String START_TIME_PROP = "telemetria.start.nano";

    @Inject
    MetricsService metricsService;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        requestContext.setProperty(START_TIME_PROP, System.nanoTime());
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        String path = requestContext.getUriInfo().getPath();
        if (path == null || path.startsWith("q/")) {
            return;
        }
        String normalizedPath = path.startsWith("/") ? path.substring(1) : path;

        Object startObj = requestContext.getProperty(START_TIME_PROP);
        if (!(startObj instanceof Long startNanos)) {
            return;
        }

        long elapsedMs = (System.nanoTime() - startNanos) / 1_000_000L;
        String nomeServico = requestContext.getMethod() + " /" + normalizedPath;
        
        try {
            RequestMetric metric = new RequestMetric(
                    nomeServico,
                    elapsedMs,
                    OffsetDateTime.now(ZoneOffset.UTC)
            );
            metricsService.enqueue(metric);
        } catch (Exception ex) {
            // Telemetry is non-critical: do not break endpoint response due to metrics persistence issues.
            LOG.warnf("Falha ao enfileirar telemetria para %s: %s", nomeServico, ex.getMessage());
        }
    }
}
