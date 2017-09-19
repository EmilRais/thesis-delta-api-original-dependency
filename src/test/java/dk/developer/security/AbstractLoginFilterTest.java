package dk.developer.security;

import dk.developer.testing.MockClient;
import dk.developer.testing.Result;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.ext.Provider;

import static dk.developer.security.AbstractLoginFilter.Validity.*;
import static dk.developer.server.Server.Status.ERROR;
import static dk.developer.server.Server.Status.INVALID_CREDENTIAL;
import static dk.developer.testing.Truth.ASSERT;
import static dk.developer.utility.Convenience.list;

public class AbstractLoginFilterTest {
    private MockClient client;

    @BeforeMethod
    public void setUp() throws Exception {
        client = MockClient.create(LoginFilter.class);
    }

    @Test
    public void shouldAllowAll() throws Exception {
        Result result = client.from(SecurityTestService.class).get("/security/none");
        ASSERT.that(result.content()).isNull();
        ASSERT.that(result.status()).isEqualTo("Test");
    }

    @Test
    public void shouldDenyWrongLogin() throws Exception {
        client.setAuthorisationHeader("invalid");
        Result result = client.from(SecurityTestService.class).get("/security/login");
        ASSERT.that(result.content()).isEqualTo("Your credential is invalid");
        ASSERT.that(result.status()).isEqualTo(INVALID_CREDENTIAL);
    }

    @Test
    public void shouldDenyInsufficientPermissions() throws Exception {
        client.setAuthorisationHeader("insufficient");
        Result result = client.from(SecurityTestService.class).get("/security/login");
        ASSERT.that(result.content()).isEqualTo(list("You have insufficient permissions to access this resource"));
        ASSERT.that(result.status()).isEqualTo(ERROR);
    }

    @Test
    public void shouldAllowCorrectLogin() throws Exception {
        client.setAuthorisationHeader("valid");
        Result result = client.from(SecurityTestService.class).get("/security/login");
        ASSERT.that(result.content()).isNull();
        ASSERT.that(result.status()).isEqualTo("Test");
    }

    @Test
    public void shouldDenyIfNoSecurity() throws Exception {
        Result result = client.from(SecurityTestService.class).get("/security/no");
        ASSERT.that(result.content()).isEqualTo(list("Resource does not allow access"));
        ASSERT.that(result.status()).isEqualTo(ERROR);
    }

    @Test
    public void shouldDenyIfBlocked() throws Exception {
        Result result = client.from(SecurityTestService.class).get("/security/block");
        ASSERT.that(result.content()).isEqualTo(list("Resource does not allow access"));
        ASSERT.that(result.status()).isEqualTo(ERROR);
    }

    @Provider
    public static class LoginFilter extends AbstractLoginFilter {
        @Override
        protected Validity authenticate(String authorisationHeader) {
            if ( authorisationHeader.equals("valid") )
                return VALID;

            if ( authorisationHeader.equals("insufficient") )
                return INSUFFICIENT_PERMISSION;

            return INVALID;
        }
    }
}