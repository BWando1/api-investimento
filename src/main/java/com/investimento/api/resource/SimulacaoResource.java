package com.investimento.api.resource;

import com.investimento.api.dto.ApiResponse;
import com.investimento.api.dto.SimularInvestimentoRequest;
import com.investimento.api.dto.SimularInvestimentoResponse;
import com.investimento.service.SimulacaoService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/simular-investimento")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Simulacao", description = "Simulacao de investimento e persistencia de historico")
public class SimulacaoResource {

    @Inject
    SimulacaoService simulacaoService;

    @POST
    @Operation(
            summary = "Simular investimento",
            description = "Seleciona um produto elegivel, calcula o retorno estimado e persiste a simulacao."
    )
    public Response simular(@Valid SimularInvestimentoRequest request) {
        SimularInvestimentoResponse resultado = simulacaoService.simular(request);
        return Response.ok(ApiResponse.ok(resultado)).build();
    }
}
