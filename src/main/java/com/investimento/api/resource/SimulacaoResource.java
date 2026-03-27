package com.investimento.api.resource;

import com.investimento.api.dto.ApiResponse;
import com.investimento.api.dto.PageResponse;
import com.investimento.api.dto.SimularInvestimentoRequest;
import com.investimento.api.dto.SimularInvestimentoResponse;
import com.investimento.api.dto.SimulacaoHistoricoResponse;
import com.investimento.api.dto.SimulacaoPorProdutoDiaResponse;
import com.investimento.service.SimulacaoService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/simular-investimento")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Simulacao", description = "Simulacao de investimento e persistencia de historico")
public class SimulacaoResource {

    @Inject




    SimulacaoService simulacaoService;

    @POST
        @RolesAllowed({"usuario", "admin"})
    @Operation(
            summary = "Simular investimento",
            description = "Seleciona um produto elegivel, calcula o retorno estimado e persiste a simulacao."
    )
    public Response simular(@Valid SimularInvestimentoRequest request) {
        SimularInvestimentoResponse resultado = simulacaoService.simular(request);
        return Response.ok(ApiResponse.ok(resultado)).build();
    }

    @GET
    @Path("/historico")
        @RolesAllowed("admin")
    @Operation(
            summary = "Listar historico de simulacoes",
            description = "Retorna historico paginado das simulacoes realizadas, ordenado por data decrescente."
    )
    public Response listarHistorico(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("pageSize") @DefaultValue("10") int pageSize
    ) {
        PageResponse<SimulacaoHistoricoResponse> resultado = simulacaoService.listarSimulacoes(page, pageSize);
        return Response.ok(ApiResponse.ok(resultado)).build();
    }

    @GET
    @Path("/por-produto-dia")
        @RolesAllowed("admin")
    @Operation(
            summary = "Listar agregado por produto e dia",
            description = "Retorna quantidade de simulacoes e media do valor final agrupadas por produto e dia."
    )
    public Response listarPorProdutoDia() {
        List<SimulacaoPorProdutoDiaResponse> resultado = simulacaoService.listarPorProdutoDia();
        return Response.ok(ApiResponse.ok(resultado)).build();
    }
}
