package com.investimento.api.resource;

import com.investimento.api.dto.ApiResponse;
import com.investimento.api.dto.TelemetriaResponse;
import com.investimento.service.TelemetriaService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/telemetria")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Telemetria", description = "Dados de volume e tempo de resposta por servico")
public class TelemetriaResource {

    @Inject
    TelemetriaService telemetriaService;

    @GET
    @Operation(
            summary = "Obter dados de telemetria",
            description = "Retorna volume de chamadas e tempo medio de resposta por servico."
    )
    public Response obterTelemetria() {
        TelemetriaResponse response = telemetriaService.obterTelemetria();
        return Response.ok(ApiResponse.ok(response)).build();
    }
}
