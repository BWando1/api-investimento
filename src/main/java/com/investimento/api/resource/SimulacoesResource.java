package com.investimento.api.resource;

import com.investimento.api.dto.ApiResponse;
import com.investimento.api.dto.PageResponse;
import com.investimento.api.dto.SimulacaoHistoricoResponse;
import com.investimento.api.dto.SimulacaoPorProdutoDiaResponse;
import com.investimento.service.SimulacaoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/simulacoes")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Simulacoes", description = "Consulta de historico e agregados de simulacao")
public class SimulacoesResource {

    @Inject
    SimulacaoService simulacaoService;

    @GET
    @Operation(
            summary = "Listar simulacoes realizadas",
            description = "Retorna historico paginado das simulacoes realizadas."
    )
    public Response listar(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("pageSize") @DefaultValue("10") int pageSize
    ) {
        PageResponse<SimulacaoHistoricoResponse> response = simulacaoService.listarSimulacoes(page, pageSize);
        return Response.ok(ApiResponse.ok(response)).build();
    }

    @GET
    @Path("/por-produto-dia")
    @Operation(
            summary = "Listar agregado por produto e dia",
            description = "Retorna quantidade e media de valor final por produto em cada dia."
    )
    public Response listarPorProdutoDia() {
        List<SimulacaoPorProdutoDiaResponse> response = simulacaoService.listarPorProdutoDia();
        return Response.ok(ApiResponse.ok(response)).build();
    }
}
