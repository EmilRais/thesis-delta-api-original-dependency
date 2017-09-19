package dk.developer.testing;

import dk.developer.security.Security;
import dk.developer.server.Server;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("mock")
public class MockClientTestService {
    @GET
    @Path("/authorisation/works")
    public Response authorisationHeader(@HeaderParam(Security.HEADER) String authorisation) {
        return Server.respond(authorisation).as("Test");
    }

    @GET
    @Path("/get/json")
    public Response getJson() {
        return Server.respond("something").as("Test");
    }

    @POST
    @Path("/post/json")
    @Consumes(APPLICATION_JSON)
    public Response postJson(String json) {
        return Server.respond(json).as("Test");
    }

    @POST
    @Path("/post/form")
    public Response postForm(@FormParam("name") String name) {
        return Server.respond(name).as("Test");
    }

    @GET
    @Path("/exception/handled/by/providers")
    public Response shouldThrowExceptionAndBeHandledByProviders() throws Exception {
        throw new RuntimeException("The providers caught this exception");
    }

    @GET
    @Path("/security/provider/used")
    public Response shouldBeDeniedAccess() {
        return Server.respond("Should not be able to get here").as("Test");
    }
}
