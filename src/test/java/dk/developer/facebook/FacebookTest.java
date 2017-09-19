package dk.developer.facebook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static dk.developer.facebook.Facebook.facebook;
import static dk.developer.facebook.FacebookUserField.ID;
import static dk.developer.facebook.FacebookUserField.NAME;
import static dk.developer.facebook.Permission.*;
import static dk.developer.testing.Truth.ASSERT;
import static dk.developer.utility.Convenience.list;

public class FacebookTest {
    private String appId = "12938984213123";
    private String appSecret = "92183289abc123ccc12384cceded2";
    private String testUserId = "5858236212";
    private Facebook facebook;
    private Client client;

    @BeforeClass
    public void beforeClass() throws Exception {
        client = ClientBuilder.newClient();
    }

    @BeforeMethod
    public void setUp() throws Exception {
        facebook = facebook(appId, appSecret);
    }

    @Test
    public void shouldRejectWrongAppId() throws Exception {
        InspectedToken token = new InspectedToken("WrongAppId", expiresTime(), true, permissions(), testUserId);
        boolean isValid = facebook.validateToken(token, testUserId, permissionArray());
        ASSERT.that(isValid).isFalse();
    }

    @Test
    public void shouldRejectIfExpired() throws Exception {
        InspectedToken token = new InspectedToken(appId, 1000, true, permissions(), testUserId);
        boolean isValid = facebook.validateToken(token, testUserId, permissionArray());
        ASSERT.that(isValid).isFalse();
    }

    @Test
    public void shouldRejectIfInvalid() throws Exception {
        InspectedToken token = new InspectedToken(appId, expiresTime(), false, permissions(), testUserId);
        boolean isValid = facebook.validateToken(token, testUserId, permissionArray());
        ASSERT.that(isValid).isFalse();
    }

    @Test
    public void shouldRejectWrongScopes() throws Exception {
        List<Permission> permissions = list(PUBLISH_ACTIONS, PUBLIC_PROFILE);
        InspectedToken token = new InspectedToken(appId, expiresTime(), true, permissions, testUserId);
        boolean isValid = facebook.validateToken(token, testUserId, permissionArray());
        ASSERT.that(isValid).isFalse();
    }

    @Test
    public void shouldRejectWrongUserId() throws Exception {
        InspectedToken token = new InspectedToken(appId, expiresTime(), true, permissions(), "WrongUserID");
        boolean isValid = facebook.validateToken(token, testUserId, permissionArray());
        ASSERT.that(isValid).isFalse();
    }

    @Test(enabled = false)
    public void acceptanceTest() throws Exception {
        String shortLivedToken = testUserToken();
        InspectedToken inspectedShortLivedToken = facebook.inspectAccessToken(shortLivedToken);
        boolean isValidShortLivedToken = facebook.validateToken(inspectedShortLivedToken, testUserId, permissionArray());
        ASSERT.that(isValidShortLivedToken).isTrue();

        String longLivedToken = extendShortLivedToken(shortLivedToken);
        InspectedToken inspectedLongLivedToken = facebook.inspectAccessToken(longLivedToken);
        boolean isValidLongLivedToken = facebook.validateToken(inspectedLongLivedToken, testUserId, permissionArray());
        ASSERT.that(isValidLongLivedToken).isTrue();
    }

    @Test(enabled = false)
    public void shouldGetIdAndName() throws Exception {
        Map<String, Object> userInformation = facebook.extractPersonalInformation(testUserToken(), ID.value(), NAME.value());
        ASSERT.that(userInformation.get("id")).isEqualTo(testUserId);
        ASSERT.that(userInformation.get("name")).isEqualTo("Open Graph Test User");
    }

    private String testUserToken() {
        String url = "https://graph.facebook.com/" + appId + "/accounts/test-users";

        Response response = client.target(url)
                .queryParam("access_token", appId + "|" + appSecret)
                .request().get();

        String json = facebook.responseToJson(response);
        response.close();
        return unpackAccessToken(json);

    }

    private long expiresTime() {
        return new Date().getTime() * 2;
    }

    private List<Permission> permissions() {
        return list(permissionArray());
    }

    private Permission[] permissionArray() {
        return new Permission[]{PUBLIC_PROFILE, PUBLISH_ACTIONS, USER_FRIENDS};
    }

    private  String extendShortLivedToken(String token) {
        String url = "https://graph.facebook.com/oauth/access_token?";

        Response response = client.target(url)
                .queryParam("grant_type", "fb_exchange_token")
                .queryParam("client_id", appId)
                .queryParam("client_secret", appSecret)
                .queryParam("fb_exchange_token", token)
                .request().get();

        String json = facebook.responseToJson(response);
        response.close();

        return unpackLongLivedToken(json);
    }

    private String unpackLongLivedToken(String result) {
        int accessTokenBegin = result.indexOf("access_token=") + "access_token=".length();
        int accessTokenEnd = result.indexOf("&");
        return result.substring(accessTokenBegin, accessTokenEnd);
    }

    private String unpackAccessToken(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readValue(json, JsonNode.class);
            return node.findValue("access_token").asText();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}