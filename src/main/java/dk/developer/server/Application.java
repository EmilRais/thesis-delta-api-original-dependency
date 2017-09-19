package dk.developer.server;

import dk.developer.database.DatabaseFront;
import dk.developer.database.DatabaseLayer;
import dk.developer.security.SecurityProvider;

public class Application {
    private static SecurityProvider securityProvider;
    private static DatabaseFront database;

    protected Application() {
    }

    public static SecurityProvider security() {
        return securityProvider;
    }

    public static SecurityProvider security(SecurityProvider securityProvider) {
        Application.securityProvider = securityProvider;
        return Application.securityProvider;
    }

    public static DatabaseFront database() {
        return database;
    }

    public static DatabaseFront database(DatabaseLayer database) {
        Application.database = DatabaseFront.create(database);
        return Application.database;
    }
}
