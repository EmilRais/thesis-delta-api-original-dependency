package dk.developer.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.developer.validation.single.NotEmpty;

import static dk.developer.utility.Converter.converter;

public class Credential {
    @NotEmpty(message = "The user id was empty")
    private String userId;

    @NotEmpty(message = "The token was empty")
    private String token;

    /**
     *  For @HeaderParam deserialisation
     * */
    public static Credential valueOf(String json) {
        return converter().fromJson(json, Credential.class);
    }

    @JsonCreator
    public Credential(@JsonProperty("userId") String userId, @JsonProperty("token") String token) {
        this.userId = userId;
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "Credential{" +
                "userId='" + userId + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
