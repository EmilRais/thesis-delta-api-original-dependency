package dk.developer.facebook;

import com.fasterxml.jackson.annotation.*;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonTypeName(value = "data")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InspectedToken {
    @JsonProperty("app_id")
    private final String appId;
    @JsonProperty("expires_at")
    private final long expiresAt;
    @JsonProperty("is_valid")
    private final boolean isValid;
    private final List<Permission> scopes;
    @JsonProperty("user_id")
    private final String userId;

    @JsonCreator
    public InspectedToken(@JsonProperty("app_id") String appId,
                          @JsonProperty("expires_at") long expiresAt,
                          @JsonProperty("is_valid") boolean isValid,
                          @JsonProperty("scopes") List<Permission> scopes,
                          @JsonProperty("user_id") String userId) {
        this.appId = appId;
        this.expiresAt = expiresAt;
        this.isValid = isValid;
        this.scopes = scopes;
        this.userId = userId;
    }

    public String getAppId() {
        return appId;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    @JsonIgnore
    public boolean isValid() {
        return isValid;
    }

    public List<Permission> getScopes() {
        return scopes;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "InspectedToken{" +
                "appId=" + appId +
                ", expiresAt=" + expiresAt +
                ", isValid=" + isValid +
                ", scopes=" + scopes +
                ", userId='" + userId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        InspectedToken that = (InspectedToken) o;
        return Objects.equals(appId, that.appId) &&
                Objects.equals(expiresAt, that.expiresAt) &&
                Objects.equals(isValid, that.isValid) &&
                Objects.equals(scopes, that.scopes) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appId, expiresAt, isValid, scopes, userId);
    }
}

