package dk.developer.server;

import dk.developer.clause.As;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import static dk.developer.server.Server.Status.ERROR;
import static java.util.Arrays.asList;
import static javax.ws.rs.core.Response.Status.OK;

public class Server {
    static final String STATUS_HEADER = "status";

    private Server() {
    }

    public static As<String, Response> respond(Object object) {
        return status -> {
            ResponseBuilder builder = Response.status(OK);
            builder.entity(object);
            builder.header(STATUS_HEADER, status);
            return builder.build();
        };
    }

    public static As<String, Response> respond() {
        return respond(null);
    }

    public static Response reportError(String errorMessage) {
        return respond(asList(errorMessage)).as(ERROR);
    }

    public static class Status {
        public static final String ERROR = "Error";
        public static final String INVALID_CREDENTIAL = "InvalidCredential";
    }
}
