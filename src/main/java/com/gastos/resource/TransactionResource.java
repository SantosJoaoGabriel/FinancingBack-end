package com.gastos.resource;

import com.gastos.dto.TransactionDTO;
import com.gastos.service.TransactionService;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/transacoes")
@Authenticated
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransactionResource {

    @Inject
    TransactionService service;

    @Inject
    SecurityIdentity securityIdentity;

    @GET
    public List<TransactionDTO> findAll() {
        return service.findAll(securityIdentity.getPrincipal().getName());
    }

    @GET
    @Path("/recentes")
    public List<TransactionDTO> findRecent(@QueryParam("limite") @DefaultValue("5") int limit) {
        return service.findRecent(securityIdentity.getPrincipal().getName(), limit);
    }

    @GET
    @Path("/{id}")
    public TransactionDTO findById(@PathParam("id") Long id) {
        return service.findById(securityIdentity.getPrincipal().getName(), id);
    }

    @POST
    public Response create(@Valid TransactionDTO dto) {
        TransactionDTO created = service.create(securityIdentity.getPrincipal().getName(), dto);
        return Response.status(Response.Status.CREATED)
                .entity(created)
                .build();
    }

    @PUT
    @Path("/{id}")
    public TransactionDTO update(@PathParam("id") Long id, @Valid TransactionDTO dto) {
        return service.update(securityIdentity.getPrincipal().getName(), id, dto);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        service.delete(securityIdentity.getPrincipal().getName(), id);
        return Response.noContent().build();
    }
}