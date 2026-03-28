package com.investimento.api.resource;

import com.investimento.api.dto.ApiResponse;
import com.investimento.api.dto.ProdutoResumoResponse;
import com.investimento.service.ProdutoRecomendadoService;
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

@Path("/produtos-recomendados")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Produtos", description = "Recomendacao de produtos por perfil de risco")
public class ProdutosRecomendadosResource {

    @Inject
    ProdutoRecomendadoService produtoRecomendadoService;

    @GET
    @Path("/{perfil}")
        @RolesAllowed({"usuario", "analista"})
    @Operation(
            summary = "Listar produtos recomendados",
            description = "Retorna produtos ativos recomendados para um perfil de risco."
    )
    public Response listarRecomendados(@PathParam("perfil") String perfilParam) {
        List<ProdutoResumoResponse> response = produtoRecomendadoService.listarPorPerfil(perfilParam);

        return Response.ok(ApiResponse.ok(response)).build();
    }
}
