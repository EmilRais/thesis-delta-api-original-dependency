package dk.developer.security;

import com.fasterxml.jackson.core.type.TypeReference;
import dk.developer.server.Server;
import dk.developer.testing.MockClient;
import dk.developer.testing.Result;
import dk.developer.utility.Converter;
import dk.developer.validation.ValidationTestService;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static dk.developer.testing.Truth.ASSERT;

public class CredentialValidationTest {
    private MockClient client;
    private Converter converter;

    @BeforeMethod
    public void setUp() throws Exception {
        client = MockClient.create();
        converter = Converter.converter();
    }

    @Test
    public void shouldAcceptCredential() throws Exception {
        String credential = converter.toJson(new Credential("id", "token"));
        Result result = client.to(ValidationTestService.class).with(credential).post("/validate/credential");
        ASSERT.that(result.status()).isEqualTo("Test");
        ASSERT.that(result.content()).isNull();
    }

    @Test
    public void shouldRejectCredential() throws Exception {
        String credential = converter.toJson(new Credential(null, ""));
        Result result = client.to(ValidationTestService.class).with(credential).post("/validate/credential");
        ASSERT.that(result.status()).isEqualTo(Server.Status.ERROR);
        ASSERT.that(result.content(new TypeReference<List<String>>() {})).containsExactly("The user id was empty", "The token was empty");
    }
}
