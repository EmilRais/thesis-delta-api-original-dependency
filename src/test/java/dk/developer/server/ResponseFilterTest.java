package dk.developer.server;

import dk.developer.testing.MockClient;
import dk.developer.testing.Result;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static dk.developer.server.Server.Status.ERROR;
import static dk.developer.testing.Truth.ASSERT;
import static dk.developer.utility.Convenience.list;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.Response.Status.OK;

public class ResponseFilterTest {
    private MockClient client;

    @BeforeMethod
    public void setUp() throws Exception {
        client = MockClient.create();
    }

    @Test
    public void shouldCorrectNoMediaType() throws Exception {
        Result result = client.from(ResponseTestService.class).get("/response/no/media/type");
        ASSERT.that(result.contentType()).isEqualTo(APPLICATION_JSON_TYPE);
    }

    @Test
    public void shouldKeepRightMediaType() throws Exception {
        Result result = client.from(ResponseTestService.class).get("/response/json/media/type");
        ASSERT.that(result.contentType()).isEqualTo(APPLICATION_JSON_TYPE);
    }

    @Test
    public void shouldCorrectWrongMediaType() throws Exception {
        Result result = client.from(ResponseTestService.class).get("/response/xml/media/type");
        ASSERT.that(result.contentType()).isEqualTo(APPLICATION_JSON_TYPE);
    }

    @Test
    public void shouldFunctionWithoutEntity() throws Exception {
        Result result = client.from(ResponseTestService.class).get("/response/entity/none");
        ASSERT.that(result.status()).isEqualTo("Test");
        ASSERT.that(result.content()).isEqualTo(null);
    }

    @Test
    public void shouldFunctionWithEntity() throws Exception {
        Result result = client.from(ResponseTestService.class).get("/response/entity/some");
        ASSERT.that(result.status()).isEqualTo("Test");

        Box entity = result.content(Box.class);
        ASSERT.that(entity.getStatus()).isEqualTo("Log");
        ASSERT.that(entity.getEntity()).isEqualTo("Did improve world");
    }

    @Test
    public void shouldKeepStatusCode200() throws Exception {
        Result result = client.from(ResponseTestService.class).get("/response/status/is/200");
        ASSERT.that(result.httpStatusCode()).isSameAs(OK);
    }

    @Test
    public void shouldCorrectStatusCodeNot200() throws Exception {
        Result result = client.from(ResponseTestService.class).get("/response/status/is/not/210");
        ASSERT.that(result.httpStatusCode()).isSameAs(OK);
    }

    @Test
    public void shouldFailWithNoStatusHeader() throws Exception {
        Result result = client.from(ResponseTestService.class).get("/response/header/without/status");
        ASSERT.that(result.status()).isEqualTo(ERROR);
        ASSERT.that(result.content()).isEqualTo(list("Forgot to set status header"));
    }

    @Test
    public void shouldSucceedWithStatusHeader() throws Exception {
        Result result = client.from(ResponseTestService.class).get("/response/header/with/status");
        ASSERT.that(result.status()).isEqualTo("Info");
        ASSERT.that(result.content()).isEqualTo("Everything is fine");
    }
}