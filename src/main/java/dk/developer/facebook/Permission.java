package dk.developer.facebook;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Permission {
    PUBLIC_PROFILE("public_profile"),
    EMAIL("email"),
    USER_BIRTHDAY("user_birthday"),
    USER_LOCATION("user_location"),
    USER_FRIENDS("user_friends"),
    PUBLISH_ACTIONS("publish_actions");

    private String value;

    @JsonCreator
    Permission(String value) {
        this.value = value;
    }

    @JsonValue
    private String value() {
        return this.value;
    }
}
