package dk.developer.security;

import dk.developer.testing.JsonTool;
import dk.developer.utility.Converter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static dk.developer.testing.Truth.ASSERT;
import static dk.developer.utility.Converter.converter;

public class CredentialSerialisationTest {
    private JsonTool tool;
    private Converter converter;

    @BeforeClass
    public void setUp() throws Exception {
        tool = new JsonTool(Credential.class);
        converter = converter();
    }

    @Test
    public void fromJson() throws Exception {
        String json = tool.readFilteredJsonFile("CredentialInput.json");
        Credential credential = converter.fromJson(json, Credential.class);

        ASSERT.that(credential.getUserId()).isEqualTo("id");
        ASSERT.that(credential.getToken()).isEqualTo("token");
    }

    @Test
    public void toJson() throws Exception {
        Credential credential = new Credential("id", "token");
        String json = converter.toJson(credential);
        String expectedJson = tool.readFilteredJsonFile("CredentialOutput.json");

        ASSERT.that(json).isEqualTo(expectedJson);
    }
}