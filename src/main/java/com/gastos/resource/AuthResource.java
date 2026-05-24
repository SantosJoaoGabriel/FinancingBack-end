package com.gastos.resource;

import com.gastos.dto.RegisterUserDTO;
import com.gastos.dto.UserDTO;
import com.gastos.service.AuthService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    AuthService authService;

    @POST
    @Path("/register")
    public Response register(@Valid RegisterUserDTO dto) {
        UserDTO created = authService.register(dto);
        return Response.status(Response.Status.CREATED)
                .entity(created)
                .build();
    }
}