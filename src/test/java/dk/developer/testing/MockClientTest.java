package dk.developer.testing;

import dk.developer.security.AbstractLoginFilter;
import dk.developer.security.Security;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

import static dk.developer.server.Server.Status.ERROR;
import static dk.developer.testing.Truth.ASSERT;
import static dk.developer.utility.Convenience.list;

public class MockClientTest {
    private MockClient client;

    @BeforeMethod
    public void setUp() throws Exception {
        this.client = MockClient.create();
    }

    @Test
    public void shouldSetAuthorisationHeader() throws Exception {
        client.setAuthorisationHeader("something");
        Result result = client.from(MockClientTestService.class).get("/mock/authorisation/works");
        ASSERT.that(result.status()).isEqualTo("Test");
        ASSERT.that(result.content()).isEqualTo("something");
    }

    @Test
    public void shouldSetAuthorisationHeaderThroughGenericHeader() throws Exception {
        client.setHeader(Security.HEADER, "something");
        Result result = client.from(MockClientTestService.class).get("/mock/authorisation/works");
        ASSERT.that(result.status()).isEqualTo("Test");
        ASSERT.that(result.content()).isEqualTo("something");
    }

    @Test
    public void shouldGetJson() throws Exception {
        Result result = client.from(MockClientTestService.class).get("/mock/get/json");
        ASSERT.that(result.status()).isEqualTo("Test");
        ASSERT.that(result.content()).isEqualTo("something");
    }

    @Test
    public void shouldPostWithJson() throws Exception {
        String json = "{name: something}";
        Result result = client.to(MockClientTestService.class).with(json).post("/mock/post/json");
        ASSERT.that(result.status()).isEqualTo("Test");
        ASSERT.that(result.content()).isEqualTo(json);
    }

    @Test
    public void shouldPostWithFormParams() throws Exception {
        Map<String, String> formParameters = new HashMap<>();
        formParameters.put("name", "something");

        Result result = client.form(MockClientTestService.class).with(formParameters).post("/mock/post/form");
        ASSERT.that(result.status()).isEqualTo("Test");
        ASSERT.that(result.content()).isEqualTo("something");
    }

    @Test
    public void shouldBeAbleToAddProvidersUponCreation() throws Exception {
        MockClient client = MockClient.create(LoginFilter.class);
        Result result = client.from(MockClientTestService.class).get("/mock/security/provider/used");
        ASSERT.that(result.status()).isEqualTo(ERROR);
        ASSERT.that(result.content()).isEqualTo(list("Resource does not allow access"));
    }

    @Test
    public void shouldBeAbleToAddProvidersDynamically() throws Exception {
        client.addProvider(LoginFilter.class);
        Result result = client.from(MockClientTestService.class).get("/mock/security/provider/used");
        ASSERT.that(result.status()).isEqualTo(ERROR);
        ASSERT.that(result.content()).isEqualTo(list("Resource does not allow access"));
    }

    @Provider
    public static class LoginFilter extends AbstractLoginFilter {
        @Override
        protected Validity authenticate(String authorisationHeader) {
            return Validity.VALID;
        }
    }
}