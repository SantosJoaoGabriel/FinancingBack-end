package com.gastos.resource;

import com.gastos.model.Transacao;
import com.gastos.service.TransacaoService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/transacoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransacaoResource {

    @Inject
    TransacaoService service;

    @GET
    public List<Transacao> listarTodas() {
        return service.listarTodas();
    }

    @GET
    @Path("/recentes")
    public List<Transacao> listarRecentes(@QueryParam("limite") @DefaultValue("5") int limite) {
        return service.listarRecentes(limite);
    }

    @GET
    @Path("/{id}")
    public Transacao buscarPorId(@PathParam("id") Long id) {
        return service.buscarPorId(id);
    }

    @POST
    public Response criar(@Valid Transacao transacao) {
        transacao.id = null;
        Transacao criada = service.criar(transacao);
        return Response.status(Response.Status.CREATED).entity(criada).build();
    }

    @PUT
    @Path("/{id}")
    public Transacao atualizar(@PathParam("id") Long id, @Valid Transacao transacao) {
        return service.atualizar(id, transacao);
    }

    @DELETE
    @Path("/{id}")
    public Response deletar(@PathParam("id") Long id) {
        service.deletar(id);
        return Response.noContent().build();
    }
}
