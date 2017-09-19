package dk.developer.security;

public interface SecurityProvider {
    boolean isValid(Credential credential);
}
