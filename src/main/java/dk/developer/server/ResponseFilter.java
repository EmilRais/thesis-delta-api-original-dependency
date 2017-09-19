package dk.developer.server;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

import static dk.developer.server.Server.STATUS_HEADER;
import static dk.developer.server.Server.Status.ERROR;
import static dk.developer.utility.Convenience.list;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.OK;

@Provider
public class ResponseFilter implements ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        setContentType(responseContext);
        Box box = createBox(responseContext);
        responseContext.setEntity(box);
    }

    private void setContentType(ContainerResponseContext responseContext) {
        responseContext.getHeaders().put("Content-Type", list(APPLICATION_JSON));
    }

    private Box createBox(ContainerResponseContext responseContext) {
        Response.StatusType statusInfo = responseContext.getStatusInfo();
        if ( statusInfo != OK ) {
            responseContext.setStatusInfo(OK);
            return new Box(ERROR, list("Wrong status code was set: " + statusInfo.getStatusCode() + ": " + statusInfo.getReasonPhrase() + ": " + responseContext.getEntity()));
        }

        String status = responseContext.getHeaderString(STATUS_HEADER);
        if ( status == null )
            return new Box(ERROR, list("Forgot to set status header"));

        Object entity = responseContext.getEntity();
        return new Box(status, entity);
    }

}
