package dk.developer.facebook;

import com.fasterxml.jackson.core.type.TypeReference;
import dk.developer.security.Credential;
import dk.developer.utility.Converter;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import static dk.developer.utility.Convenience.list;
import static dk.developer.utility.Converter.converter;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

public class Facebook {
    private final String appId;
    private final String appSecret;
    private final Converter converter = converter();

    public static Facebook facebook(String appId, String appSecret) {
        return new Facebook(appId, appSecret);
    }

    private Facebook(String appId, String appSecret) {
        this.appId = appId;
        this.appSecret = appSecret;
    }

    public String getAppId() {
        return appId;
    }

    public String appToken() {
        return appId + "|" + appSecret;
    }

    public boolean validateCredential(Credential credential, Permission... permissions) {
        InspectedToken inspectedToken = inspectAccessToken(credential.getToken());
        return validateToken(inspectedToken, credential.getUserId(), permissions);
    }

    private Response request(Function<Client, Response> function) {
        Client client = ClientBuilder.newClient();
        Response result = function.apply(client);
        client.close();
        return result;
    }

    public Map<String, Object> extractPersonalInformation(String token, String... userFields) {
        String url = "https://graph.facebook.com/v2.5/me";

        String fields = stream(userFields).collect(joining(","));

        Response response = request(
                client -> client.target(url)
                        .queryParam("access_token", token)
                        .queryParam("fields", fields).request().get()
        );
        String json = responseToJson(response);
        return converter.fromJson(json, new TypeReference<Map<String, Object>>() {});
    }

    InspectedToken inspectAccessToken(String token) {
        String url = "https://graph.facebook.com/debug_token";
        Response response = request(
                client -> client.target(url)
                        .queryParam("input_token", token)
                        .queryParam("access_token", appToken()).request().get()
        );

        String json = responseToJson(response);
        response.close();

        return converter.fromJson(json, InspectedToken.class);
    }

    boolean validateToken(InspectedToken token, String userId, Permission... permissions) {
        boolean correctAppId = token.getAppId().equals(appId);
        boolean notExpires = token.getExpiresAt() > currentTime();
        boolean containsPermissions = token.getScopes().containsAll(list(permissions));
        boolean correctUserId = token.getUserId().equals(userId);

        return correctAppId && notExpires && containsPermissions && correctUserId && token.isValid();
    }

    private long currentTime() {
        return new Date().getTime() / 1000;
    }

    String responseToJson(Response response) {
        return response.readEntity(String.class);
    }
}
