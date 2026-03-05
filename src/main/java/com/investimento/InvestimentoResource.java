package com.investimento;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

@Path("/investimentos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InvestimentoResource {

    @GET
    public List<Investimento> listAll() {
        return Investimento.listAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Investimento investimento = Investimento.findById(id);
        if (investimento == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(investimento).build();
    }

    @POST
    @Transactional
    public Response create(@Valid Investimento investimento) {
        investimento.persist();
        return Response.created(URI.create("/investimentos/" + investimento.id))
                .entity(investimento)
                .build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Long id, @Valid Investimento updated) {
        Investimento investimento = Investimento.findById(id);
        if (investimento == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        investimento.nome = updated.nome;
        investimento.tipo = updated.tipo;
        investimento.valor = updated.valor;
        investimento.dataAplicacao = updated.dataAplicacao;
        return Response.ok(investimento).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = Investimento.deleteById(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}
