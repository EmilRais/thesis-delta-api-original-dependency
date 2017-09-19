package dk.developer.security;

import dk.developer.server.Server;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import static dk.developer.security.Security.Mechanism.BLOCK;
import static dk.developer.security.Security.Mechanism.LOGIN;
import static dk.developer.security.Security.Mechanism.NONE;

@Path("security")
public class SecurityTestService {
    @GET
    @Path("/login")
    @Security(LOGIN)
    public Response onlyForUsers() {
        return Server.respond().as("Test");
    }

    @GET
    @Path("/none")
    @Security(NONE)
    public Response freeForAll() {
        return Server.respond().as("Test");
    }

    @GET
    @Path("/no")
    public Response noSecurity() {
        return Server.respond().as("Test");
    }

    @GET
    @Path("/block")
    @Security(BLOCK)
    public Response block() {
        return Server.respond().as("Test");
    }
}