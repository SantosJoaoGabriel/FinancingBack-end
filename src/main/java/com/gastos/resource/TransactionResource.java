package com.gastos.resource;

import com.gastos.dto.TransactionDTO;
import com.gastos.service.TransactionService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/transacoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransactionResource {

    @Inject
    TransactionService service;

    @GET
    public List<TransactionDTO> findAll() {
        return service.findAll();
    }

    @GET
    @Path("/recentes")
    public List<TransactionDTO> findRecent(@QueryParam("limite") @DefaultValue("5") int limit) {
        return service.findRecent(limit);
    }

    @GET
    @Path("/{id}")
    public TransactionDTO findById(@PathParam("id") Long id) {
        return service.findById(id);
    }

    @POST
    public Response create(@Valid TransactionDTO dto) {
        TransactionDTO created = service.create(dto);
        return Response.status(Response.Status.CREATED)
                .entity(created)
                .build();
    }

    @PUT
    @Path("/{id}")
    public TransactionDTO update(@PathParam("id") Long id, @Valid TransactionDTO dto) {
        return service.update(id, dto);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        service.delete(id);
        return Response.noContent().build();
    }
}