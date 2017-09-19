package dk.developer.server;

import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ResteasyViolationException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;

import static dk.developer.server.Server.Status.ERROR;
import static dk.developer.utility.Convenience.list;
import static java.util.stream.Collectors.toList;

public class ExceptionCatcher {
    private ExceptionCatcher() {
    }

    @Provider
    public static class GeneralExceptionCatcher implements ExceptionMapper<Exception> {
        @Override
        public Response toResponse(Exception exception) {
            exception.printStackTrace();

            List<String> messages = list(exception.getMessage());
            return respond(messages);
        }
    }

    @Provider
    public static class ValidationExceptionCatcher implements ExceptionMapper<ResteasyViolationException> {
        @Override
        public Response toResponse(ResteasyViolationException exception) {
            exception.printStackTrace();

            List<String> messages = extractMessages(exception);
            return respond(messages);
        }

        private List<String> extractMessages(ResteasyViolationException exception) {
            return exception.getViolations().stream()
                    .map(ResteasyConstraintViolation::getMessage)
                    .collect(toList());
        }
    }

    private static Response respond(List<String> messages) {
        return Server.respond(messages).as(ERROR);
    }
}
