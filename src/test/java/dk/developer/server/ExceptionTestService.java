package dk.developer.server;

import dk.developer.validation.single.Empty;
import dk.developer.validation.single.NotEmpty;

import javax.validation.constraints.Null;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

@Path("exception")
public class ExceptionTestService {
    @GET
    @Path("/regular")
    public Response regularException() {
        throw new RuntimeException("Something went wrong");
    }

    @GET
    @Path("/system")
    public Response systemException() {
        throw new WebApplicationException("Something went wrong", INTERNAL_SERVER_ERROR);
    }

    @POST
    @Path("/post/exclusively")
    public Response postOnly() {
        return Server.respond().as("TEST");
    }

    @GET
    @Path("/single/violation")
    public Response singleViolation(@QueryParam("name") @NotEmpty(message = "Name was empty") String name) {
        return Server.respond().as("TEST");
    }

    @GET
    @Path("/several/violations")
    public Response severalViolations(
            @QueryParam("name")
            @Null(message = "Name was not null")
            @Empty(message = "Name was not empty") String name) {
        return Server.respond().as("TEST");
    }
}
