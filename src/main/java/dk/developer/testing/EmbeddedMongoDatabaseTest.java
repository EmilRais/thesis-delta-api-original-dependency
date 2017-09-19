package dk.developer.testing;

import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.tests.MongodForTestsFactory;
import dk.developer.database.*;
import org.testng.annotations.AfterSuite;

import java.io.IOException;
import java.util.UUID;

import static de.flapdoodle.embed.mongo.distribution.Version.Main.PRODUCTION;

public class EmbeddedMongoDatabaseTest {
    private static MongodForTestsFactory testFactory;
    private static MongoClient client;

    @AfterSuite(enabled = false)
    public void tearDown() throws Exception {
        shutdownAll();
    }

    protected DatabaseLayer embeddedDatabase() {
        if ( testFactory == null && client == null )
            initialisePersistedTestDatabase();

        UUID uuid = UUID.randomUUID();
        com.mongodb.client.MongoDatabase mongoDatabase = client.getDatabase(uuid.toString());
        return new MongoDatabase(mongoDatabase);
    }

    private static void initialisePersistedTestDatabase() {
        try {
            testFactory = MongodForTestsFactory.with(PRODUCTION);
            client = testFactory.newMongo();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void shutdownAll() {
        System.out.println("Done with testing, shutting down");
        testFactory.shutdown();
        System.out.println("Succesfully shut down the persisted test database");
    }
}
