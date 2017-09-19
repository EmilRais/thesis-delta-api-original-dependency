package dk.developer.database;

import org.bson.types.ObjectId;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;

import static dk.developer.testing.Truth.ASSERT;
import static dk.developer.database.FakeDatabaseObject.COLLECTION;

public class DatabaseFrontTest {
    private MemoryDatabase database;
    private DatabaseFront front;

    @BeforeMethod
    public void setUp() throws Exception {
        database = new MemoryDatabase(new HashMap<>());
        front = DatabaseFront.create(database);
    }

    @Test
    public void shouldLoadCorrectType() throws Exception {
        database.save(new FakeDatabaseObject("someId", "Peter")).in(COLLECTION);
        FakeDatabaseObject object = front.load(FakeDatabaseObject.class).matching("name").with("Peter");
        ASSERT.that(object.getId()).isEqualTo("someId");
        ASSERT.that(object.getName()).isEqualTo("Peter");
    }

    @Test
    public void shouldNotLoadNonExistingObject() throws Exception {
        FakeDatabaseObject object = front.load(FakeDatabaseObject.class).matching("name").with("Peter");
        ASSERT.that(object).isNull();
    }

    @Test
    public void shouldLoadNoObjects() throws Exception {
        List<FakeDatabaseObject> objects = front.loadAll(FakeDatabaseObject.class).everything();
        ASSERT.that(objects).isEmpty();
    }

    @Test
    public void shouldLoadSeveralObjects() throws Exception {
        DatabaseObject peter = new FakeDatabaseObject("Peter");
        front.save(peter);
        DatabaseObject thomas = new FakeDatabaseObject("Thomas");
        front.save(thomas);

        List<FakeDatabaseObject> objects = front.loadAll(FakeDatabaseObject.class).everything();
        ASSERT.that(objects).containsExactly(peter, thomas);
    }

    @Test
    public void shouldLoadSeveralObjectsWithoutNonId() throws Exception {
        DatabaseObject peter = new FakeDatabaseObject("1", "Peter");
        front.save(peter);

        DatabaseObject thomas = new FakeDatabaseObject("2", "Thomas");
        front.save(thomas);

        List<FakeDatabaseObject> objects = front.loadAll(FakeDatabaseObject.class).excluding("name");

        FakeDatabaseObject peterWithoutName = objects.get(0);
        ASSERT.that(peterWithoutName.getId()).isEqualTo("1");
        ASSERT.that(peterWithoutName.getName()).isNull();

        FakeDatabaseObject thomasWithoutName = objects.get(1);
        ASSERT.that(thomasWithoutName.getId()).isEqualTo("2");
        ASSERT.that(thomasWithoutName.getName()).isNull();
    }

    @Test
    public void shouldSaveNonExistingObject() throws Exception {
        front.save(new FakeDatabaseObject("someId", "Peter"));
        FakeDatabaseObject object = front.load(FakeDatabaseObject.class).matching("_id").with("someId");
        ASSERT.that(object.getId()).isEqualTo("someId");
        ASSERT.that(object.getName()).isEqualTo("Peter");
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void shouldFailSavingSameObjectTwice() throws Exception {
        front.save(new FakeDatabaseObject("someId", "Peter"));
        front.save(new FakeDatabaseObject("someId", "Peter"));
    }

    @Test
    public void shouldNotUpdateNonExistingObject() throws Exception {
        boolean didUpdate = front.update(new FakeDatabaseObject("Patrick"));
        ASSERT.that(didUpdate).isFalse();
    }

    @Test
    public void shouldUpdateExistingObject() throws Exception {
        String id = ObjectId.get().toString();
        DatabaseObject patrick = new FakeDatabaseObject(id, "Patrick");
        database.save(patrick).in(COLLECTION);

        FakeDatabaseObject joseph = new FakeDatabaseObject(id, "Joseph");
        boolean didUpdate = front.update(joseph);
        ASSERT.that(didUpdate).isTrue();
    }

    @Test
    public void shouldNotDeleteNonExistingObject() throws Exception {
        boolean didDelete = front.delete(FakeDatabaseObject.class).matching("key").with("value");
        ASSERT.that(didDelete).isFalse();
    }

    @Test
    public void shouldDeleteExistingObject() throws Exception {
        FakeDatabaseObject patrick = new FakeDatabaseObject("Patrick");
        database.save(patrick).in(COLLECTION);

        boolean didDelete = front.delete(FakeDatabaseObject.class).matching("name").with(patrick.getName());
        ASSERT.that(didDelete).isTrue();
    }
}