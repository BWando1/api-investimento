package com.investimento.api.telemetria;

import com.investimento.service.TelemetriaService;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class TelemetriaFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOG = Logger.getLogger(TelemetriaFilter.class);
    private static final String START_TIME_PROP = "telemetria.start.nano";

    @Inject
    TelemetriaService telemetriaService;

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

        Object startObj = requestContext.getProperty(START_TIME_PROP);
        if (!(startObj instanceof Long startNanos)) {
            return;
        }

        long elapsedMs = (System.nanoTime() - startNanos) / 1_000_000L;
        String nomeServico = requestContext.getMethod() + " /" + path;
        try {
            telemetriaService.registrarChamada(nomeServico, elapsedMs);
        } catch (Exception ex) {
            // Telemetry is non-critical: do not break endpoint response due to metrics persistence issues.
            LOG.warnf("Falha ao registrar telemetria para %s: %s", nomeServico, ex.getMessage());
        }
    }
}
