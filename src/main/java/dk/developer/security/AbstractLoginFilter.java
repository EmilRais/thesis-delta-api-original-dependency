package dk.developer.security;

import dk.developer.security.Security.Mechanism;
import dk.developer.server.Server;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.lang.reflect.Method;

import static dk.developer.security.Security.Mechanism.*;
import static dk.developer.server.Server.Status.ERROR;
import static dk.developer.server.Server.Status.INVALID_CREDENTIAL;
import static dk.developer.utility.Convenience.list;

public abstract class AbstractLoginFilter implements ContainerRequestFilter {
    @Context ResourceInfo resourceInfo;

    @Override
    public final void filter(ContainerRequestContext context) throws IOException {
        Action action = determineAction();

        if ( action == Action.ACCEPT )
            return;

        if ( action == Action.AUTHENTICATE )
            authenticate(context);

        if ( action == Action.DENY )
            context.abortWith(Server.respond(list("Resource does not allow access")).as(ERROR));
    }

    private Action determineAction() {
        Method method = resourceInfo.getResourceMethod();
        Security annotation = method.getDeclaredAnnotation(Security.class);
        Mechanism mechanism = annotation != null ? annotation.value() : BLOCK;

        if ( mechanism == NONE )
            return Action.ACCEPT;

        if ( mechanism == LOGIN )
            return Action.AUTHENTICATE;

        return Action.DENY;
    }

    private void authenticate(ContainerRequestContext context) {
        String authorisationHeader = context.getHeaderString(Security.HEADER);
        Validity validity = authenticate(authorisationHeader);

        if ( validity == null || validity == Validity.INVALID ) {
            context.abortWith(Server.respond("Your credential is invalid").as(INVALID_CREDENTIAL));
            return;
        }

        if ( validity == Validity.INSUFFICIENT_PERMISSION )
            context.abortWith(Server.respond(list("You have insufficient permissions to access this resource")).as(ERROR));
    }

    private enum Action {
        ACCEPT, DENY, AUTHENTICATE;
    }

    protected abstract Validity authenticate(String authorisationHeader);

    protected enum Validity {
        VALID, INSUFFICIENT_PERMISSION, INVALID
    }
}
