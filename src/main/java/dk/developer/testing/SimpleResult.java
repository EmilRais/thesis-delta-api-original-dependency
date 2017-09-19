package dk.developer.testing;

import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.spi.HttpResponse;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

import static javax.ws.rs.core.Response.Status.fromStatusCode;

public class SimpleResult {
    private final String json;
    private final Response.Status httpStatusCode;
    private final MediaType contentType;

    static SimpleResult create(MockHttpResponse response) {
        String json = response.getContentAsString();
        Response.Status httpStatusCode = fromStatusCode(response.getStatus());
        MediaType contentType = contentTypeFromHeaders(response);
        return new SimpleResult(json, httpStatusCode, contentType);
    }

    protected SimpleResult(String json, Response.Status httpStatusCode, MediaType contentType) {
        this.json = json;
        this.httpStatusCode = httpStatusCode;
        this.contentType = contentType;
    }

    public String json() {
        return json;
    }

    public Response.Status httpStatusCode() {
        return httpStatusCode;
    }

    public MediaType contentType() {
        return contentType;
    }

    protected static MediaType contentTypeFromHeaders(HttpResponse response) {
        Map<String, List<Object>> headers = response.getOutputHeaders();
        List<Object> contentTypes = headers.get("Content-Type");
        if ( contentTypes == null || contentTypes.size() != 1 )
            throw new RuntimeException("Expected exactly one content type");

        Object contentType = contentTypes.get(0);
        if ( !(contentType instanceof String) )
            throw new RuntimeException("Expected the content type to be specified as a string");

        return MediaType.valueOf((String) contentType);
    }

    @Override
    public String toString() {
        return "SimpleResult{" +
                "json='" + json + '\'' +
                ", httpStatusCode=" + httpStatusCode +
                ", contentType=" + contentType +
                '}';
    }
}
