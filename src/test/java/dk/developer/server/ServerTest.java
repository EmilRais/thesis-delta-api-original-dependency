package dk.developer.server;

import org.testng.annotations.Test;

import javax.ws.rs.core.Response;

import static dk.developer.server.Server.STATUS_HEADER;
import static dk.developer.server.Server.Status.ERROR;
import static dk.developer.testing.Truth.ASSERT;
import static java.util.Arrays.asList;
import static javax.ws.rs.core.Response.Status.OK;

public class ServerTest {
    @Test
    public void shouldRespondCorrectly() throws Exception {
        String entity = "SomeString";
        String status = "SomeStatus";

        Response response = Server.respond(entity).as(status);
        ASSERT.that(response.getEntity()).isEqualTo(entity);
        ASSERT.that(response.getHeaderString(STATUS_HEADER)).isEqualTo(status);
        ASSERT.that(response.getStatusInfo()).isSameAs(OK);
    }

    @Test
    public void shouldReportErrorAsList() throws Exception {
        String message = "Error message";
        Response response = Server.reportError(message);
        ASSERT.that(response.getEntity()).isEqualTo(asList(message));
        ASSERT.that(response.getHeaderString(STATUS_HEADER)).isEqualTo(ERROR);
        ASSERT.that(response.getStatusInfo()).isSameAs(OK);
    }
}