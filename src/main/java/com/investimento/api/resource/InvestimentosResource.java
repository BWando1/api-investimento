package com.investimento.api.resource;

import com.investimento.api.dto.ApiResponse;
import com.investimento.api.dto.InvestimentoHistoricoResponse;
import com.investimento.repository.InvestimentoHistoricoRepository;
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
@Tag(name = "Investimentos", description = "Historico de investimentos por cliente")
public class InvestimentosResource {

    @Inject
    InvestimentoHistoricoRepository investimentoHistoricoRepository;

    @GET
    @Path("/{clienteId}")
    @Operation(
            summary = "Listar historico de investimentos",
            description = "Retorna o historico de investimentos do cliente ordenado por data decrescente."
    )
    public Response listarPorCliente(@PathParam("clienteId") Long clienteId) {
        List<InvestimentoHistoricoResponse> response = investimentoHistoricoRepository.findByClienteId(clienteId).stream()
                .map(i -> new InvestimentoHistoricoResponse(
                        i.id,
                        i.tipo,
                        i.valor,
                        i.rentabilidade,
                        i.data
                ))
                .toList();

        return Response.ok(ApiResponse.ok(response)).build();
    }
}
