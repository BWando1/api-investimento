package com.investimento.api.resource;

import com.investimento.api.dto.ApiResponse;
import com.investimento.api.dto.InvestimentoHistoricoResponse;
import com.investimento.service.InvestimentoHistoricoService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/investimentos")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Investimentos", description = "Histórico de investimentos por cliente")
public class InvestimentoHistoricoResource {

    @Inject
    InvestimentoHistoricoService investimentoHistoricoService;

    @GET
    @Path("/{clienteId}")
    @RolesAllowed({"usuario", "analista"})
    @Operation(
            summary = "Listar histórico de investimentos",
            description = "Retorna todos os investimentos registrados para o cliente informado."
    )
    public Response listarPorCliente(@PathParam("clienteId") Long clienteId) {
        List<InvestimentoHistoricoResponse> resultado = investimentoHistoricoService.listarPorCliente(clienteId);
        return Response.ok(ApiResponse.ok(resultado)).build();
    }
}
