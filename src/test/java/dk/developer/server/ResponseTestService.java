package dk.developer.server;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;

@Path("response")
public class ResponseTestService {
    @GET
    @Path("/no/media/type")
    public Response noMediaType() {
        return Server.respond().as(null);
    }

    @GET
    @Path("/json/media/type")
    @Consumes(APPLICATION_JSON)
    public Response jsonMediaType() {
        return Server.respond().as(null);
    }

    @GET
    @Path("/xml/media/type")
    @Consumes(APPLICATION_XML)
    public Response xmlMediaType() {
        return Server.respond().as(null);
    }

    @GET
    @Path("/entity/none")
    public Response noEntity() {
        return Server.respond().as("Test");
    }

    @GET
    @Path("/entity/some")
    public Response someEntity() {
        Box box = new Box("Log", "Did improve world");
        return Server.respond(box).as("Test");
    }

    @GET
    @Path("/status/is/200")
    public Response statusCode200() {
        return Response.status(OK).build();
    }

    @GET
    @Path("/status/is/not/200")
    public Response statusCodeNot200() {
        return Response.status(NOT_FOUND).build();
    }

    @GET
    @Path("/header/without/status")
    public Response noStatusHeader() {
        return Response.status(OK).entity("Everything is fine").build();
    }

    @GET
    @Path("/header/with/status")
    public Response someStatusHeader() {
        return Response.status(OK).entity("Everything is fine").header("status", "Info").build();
    }
}
