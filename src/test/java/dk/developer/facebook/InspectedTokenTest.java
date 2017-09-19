package dk.developer.facebook;

import dk.developer.testing.JsonTool;
import dk.developer.utility.Converter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static dk.developer.testing.Truth.ASSERT;
import static dk.developer.facebook.Permission.*;
import static dk.developer.utility.Converter.converter;

public class InspectedTokenTest {
    private static final Converter converter = converter();
    private JsonTool tool;

    @BeforeClass
    public void setUp() throws Exception {
        tool = new JsonTool(InspectedTokenTest.class);
    }

    @Test
    public void fromInput() throws Exception {
        String json = tool.readFilteredJsonFile("InspectedTokenInput.json");
        InspectedToken token = converter.fromJson(json, InspectedToken.class);

        ASSERT.that(token.getAppId()).isEqualTo("1234");
        ASSERT.that(token.getExpiresAt()).isEqualTo(123);
        ASSERT.that(token.isValid()).isTrue();
        ASSERT.that(token.getScopes()).containsExactly(PUBLIC_PROFILE, PUBLISH_ACTIONS, USER_FRIENDS );
        ASSERT.that(token.getUserId()).isEqualTo("userId");
    }
}
