package dk.developer.security;

import dk.developer.facebook.Facebook;
import dk.developer.facebook.Permission;

public class FacebookSecurityProvider implements SecurityProvider {
    private final Facebook facebook;
    private final Permission[] permissions;

    public FacebookSecurityProvider(Facebook facebook, Permission... permissions) {
        this.facebook = facebook;
        this.permissions = permissions;
    }

    @Override
    public boolean isValid(Credential credential) {
        if ( credential == null )
            return false;

        return facebook.validateCredential(credential, permissions);
    }
}
