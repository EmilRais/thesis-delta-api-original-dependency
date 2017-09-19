package dk.developer.testing;

import com.fasterxml.jackson.core.type.TypeReference;
import dk.developer.server.Box;
import org.jboss.resteasy.mock.MockHttpResponse;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import static dk.developer.utility.Converter.converter;
import static javax.ws.rs.core.Response.Status.fromStatusCode;

public class Result extends SimpleResult {
    private final Box box;

    static Result create(MockHttpResponse response) {
        String json = response.getContentAsString();
        Status httpStatusCode = fromStatusCode(response.getStatus());
        MediaType contentType = contentTypeFromHeaders(response);
        Box box = converter().fromJson(json, Box.class);
        return new Result(json, httpStatusCode, contentType, box);
    }

    private Result(String json, Status httpStatusCode, MediaType contentType, Box box) {
        super(json, httpStatusCode, contentType);
        this.box = box;
    }

    public String status() {
        return box.getStatus();
    }

    public Object content() {
        return box.getEntity();
    }

    public <T> T content(Class<T> type) {
        return converter().convert(box.getEntity(), type);
    }

    public <T> T content(TypeReference<T> type) {
        return converter().convert(box.getEntity(), type);
    }

    @Override
    public String toString() {
        return "SimpleResult{" +
                "json='" + json() + '\'' +
                ", httpStatusCode=" + httpStatusCode() +
                ", contentType=" + contentType() +
                ", status=" + status() +
                ", content=" + content() +
                '}';
    }
}
