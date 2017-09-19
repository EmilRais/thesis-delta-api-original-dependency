package dk.developer.testing;

import dk.developer.database.DatabaseLayer;
import dk.developer.database.HibernateDatabase;
import dk.developer.database.HibernateDatabase.SessionFactoryBuilder;
import org.hibernate.SessionFactory;
import org.testng.annotations.AfterSuite;

import static java.util.Arrays.stream;

public class EmbeddedHibernateDatabaseTest {
    private SessionFactory factory;

    protected final DatabaseLayer embeddedDatabase(String configurationFileName) {
        factory = new SessionFactoryBuilder(configurationFileName).create();
        return new HibernateDatabase(factory);
    }

    @AfterSuite(enabled = false)
    public void tearDown() throws Exception {
        System.out.println("Done with testing, shutting down");
        factory.close();
        System.out.println("Succesfully shut down the embedded hibernate test database");
    }
}
