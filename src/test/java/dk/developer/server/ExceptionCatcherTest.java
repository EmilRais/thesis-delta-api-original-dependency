package dk.developer.server;

import com.fasterxml.jackson.core.type.TypeReference;
import dk.developer.testing.MockClient;
import dk.developer.testing.Result;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static dk.developer.server.Server.Status.ERROR;
import static dk.developer.testing.Truth.ASSERT;
import static dk.developer.utility.Convenience.list;
import static javax.ws.rs.core.Response.Status.OK;

public class ExceptionCatcherTest {
    private MockClient client;

    @BeforeMethod
    public void setUp() throws Exception {
        client = MockClient.create();
    }

    @Test
    public void shouldCaptureRegularException() throws Exception {
        Result result = client.from(ExceptionTestService.class).get("/exception/regular");
        ASSERT.that(result.httpStatusCode()).isSameAs(OK);
        ASSERT.that(result.status()).isEqualTo(ERROR);
        ASSERT.that(result.content()).isEqualTo(list("Something went wrong"));
    }

    @Test
    public void shouldCaptureSystemException() throws Exception {
        Result result = client.from(ExceptionTestService.class).get("/exception/system");
        ASSERT.that(result.httpStatusCode()).isSameAs(OK);
        ASSERT.that(result.status()).isEqualTo(ERROR);
        ASSERT.that(result.content()).isEqualTo(list("Something went wrong"));
    }

    @Test
    public void shouldCaptureNotFoundResource() throws Exception {
        Result result = client.from(ExceptionTestService.class).get("/exception/does/not/exist");
        ASSERT.that(result.httpStatusCode()).isSameAs(OK);
        ASSERT.that(result.status()).isEqualTo(ERROR);
        ASSERT.that(result.content()).isEqualTo(list("Could not find resource for full path: /exception/does/not/exist"));
    }

    @Test
    public void shouldCaptureMethodMismatch() throws Exception {
        Result result = client.from(ExceptionTestService.class).get("/exception/post/exclusively");
        ASSERT.that(result.httpStatusCode()).isSameAs(OK);
        ASSERT.that(result.status()).isEqualTo(ERROR);
        ASSERT.that(result.content()).isEqualTo(list("No resource method found for GET, return 405 with Allow header"));
    }

    @Test
    public void shouldCaptureSingleViolation() throws Exception {
        Result result = client.from(ExceptionTestService.class).get("/exception/single/violation");
        ASSERT.that(result.httpStatusCode()).isSameAs(OK);
        ASSERT.that(result.status()).isEqualTo(ERROR);
        ASSERT.that(result.content()).isEqualTo(list("Name was empty"));
    }

    @Test
    public void shouldCaptureSeveralViolations() throws Exception {
        Result result = client.from(ExceptionTestService.class).get("/exception/several/violations?name=Peter");
        ASSERT.that(result.httpStatusCode()).isSameAs(OK);
        ASSERT.that(result.status()).isEqualTo(ERROR);
        ASSERT.that(result.content(new TypeReference<List<String>>() {
        })).containsExactly("Name was not null", "Name was not empty");
    }
}