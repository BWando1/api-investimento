package com.investimento.api.resource;

import com.investimento.api.dto.ApiResponse;
import com.investimento.api.dto.PerfilRiscoResponse;
import com.investimento.service.PerfilRiscoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/perfil-risco")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Perfil de Risco", description = "Cálculo dinâmico do perfil de risco do investidor")
public class PerfilRiscoResource {

    @Inject
    PerfilRiscoService perfilRiscoService;

    @GET
    @Path("/{clienteId}")
    @Operation(
            summary = "Calcular perfil de risco",
            description = "Calcula o perfil de risco dinâmico do cliente com base em todo o histórico de investimentos."
    )
    public Response calcularPerfil(@PathParam("clienteId") Long clienteId) {
        PerfilRiscoResponse resultado = perfilRiscoService.calcularPerfil(clienteId);
        return Response.ok(ApiResponse.ok(resultado)).build();
    }
}
