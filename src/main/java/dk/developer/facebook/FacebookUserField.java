package dk.developer.facebook;

public enum FacebookUserField {
    ID ("id"),
    ABOUT ("about"),
    AGE_RANGE ("age_range"),
    BIRTHDAY ("birthday"),
    EMAIL ("email"),
    NAME ("name"),
    FIRST_NAME ("first_name"),
    MIDDLE_NAME ("middle_name"),
    LAST_NAME ("last_name"),
    GENDER ("gender"),
    LANGUAGES ("languages"),
    LINK ("link"),
    TIMEZONE ("timezone"),
    LOCALE ("locale"),
    LOCATION ("location"),
    HOMETOWN ("hometown"),
    CURRENCY ("currency"),
    BIO ("bio");

    private final String value;

    FacebookUserField(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
