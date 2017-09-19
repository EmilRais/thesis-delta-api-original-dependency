package dk.developer.validation;

import dk.developer.security.Credential;
import dk.developer.server.Server;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("validate")
public class ValidationTestService {
    @POST
    @Path("/credential")
    @Consumes(APPLICATION_JSON)
    public Response credential(@Valid Credential credential) {
        return Server.respond().as("Test");
    }
}
