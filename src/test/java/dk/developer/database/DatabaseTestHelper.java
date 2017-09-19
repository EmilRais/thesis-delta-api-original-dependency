package dk.developer.database;

import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;

import static dk.developer.testing.Truth.ASSERT;

class DatabaseTestHelper {

    static void saveAndLoadObject(DatabaseLayer database) {
        String name = "Peter";
        DatabaseObject databaseObject = new FakeDatabaseObject(name);
        String id = databaseObject.getId();

        database.save(databaseObject).in(FakeDatabaseObject.COLLECTION);

        Map<String, Object> data = database.load("_id").with(id).from(FakeDatabaseObject.COLLECTION).as(FakeDatabaseObject.class);
        ASSERT.that(data.get("name")).isEqualTo(name);
    }

    static void saveSameObjectTwice(DatabaseLayer database) {
        DatabaseObject databaseObject = new FakeDatabaseObject(null);
        database.save(databaseObject).in(FakeDatabaseObject.COLLECTION);
        database.save(databaseObject).in(FakeDatabaseObject.COLLECTION);
    }

    static void loadNoObject(DatabaseLayer database) {
        Map<String, Object> data = database.load("_id").with(ObjectId.get()).from(FakeDatabaseObject.COLLECTION).as(FakeDatabaseObject.class);
        ASSERT.that(data).isNull();
    }

    static void loadZeroObjects(DatabaseLayer database) {
        List<Map<String, Object>> data = database.loadAll(FakeDatabaseObject.COLLECTION).as(FakeDatabaseObject.class).everything();
        ASSERT.that(data).isEmpty();
    }

    static void loadSeveralObjects(DatabaseLayer database) {
        FakeDatabaseObject oneObject = new FakeDatabaseObject(null);
        database.save(oneObject).in(FakeDatabaseObject.COLLECTION);

        FakeDatabaseObject someObject = new FakeDatabaseObject(null);
        database.save(someObject).in(FakeDatabaseObject.COLLECTION);

        List<Map<String, Object>> data = database.loadAll(FakeDatabaseObject.COLLECTION).as(FakeDatabaseObject.class).everything();
        ASSERT.that(data).hasSize(2);

        Object firstId = data.get(0).get("_id");
        ASSERT.that(firstId.equals(oneObject.getId()) || firstId.equals(someObject.getId())).isTrue();

        Object secondId = data.get(1).get("_id");
        ASSERT.that(secondId.equals(oneObject.getId()) || secondId.equals(someObject.getId())).isTrue();

        ASSERT.that(firstId).isNotEqualTo(secondId);
    }

    static void loadSeveralObjectsWithoutId(DatabaseLayer database) {
        FakeDatabaseObject oneObject = new FakeDatabaseObject("1", "Hans");
        database.save(oneObject).in(FakeDatabaseObject.COLLECTION);

        FakeDatabaseObject someObject = new FakeDatabaseObject("2", "Peter");
        database.save(someObject).in(FakeDatabaseObject.COLLECTION);

        List<Map<String, Object>> data = database.loadAll(FakeDatabaseObject.COLLECTION).as(FakeDatabaseObject.class).excluding("_id");
        ASSERT.that(data).hasSize(2);

        Map<String, Object> firstItem = data.get(0);
        ASSERT.that(firstItem.get("_id")).isNull();
        ASSERT.that(firstItem.get("name")).isEqualTo("Hans");

        Map<String, Object> secondItem = data.get(1);
        ASSERT.that(secondItem.get("_id")).isNull();
        ASSERT.that(secondItem.get("name")).isEqualTo("Peter");
    }

    static void deleteNoObject(DatabaseLayer database) {
        boolean didDelete = database.delete("_id").matching(ObjectId.get()).as(FakeDatabaseObject.class).from(FakeDatabaseObject.COLLECTION);
        ASSERT.that(didDelete).isFalse();
    }

    static void saveAndDeleteObject(DatabaseLayer database) {
        DatabaseObject databaseObject = new FakeDatabaseObject(null);
        String id = databaseObject.getId();

        database.save(databaseObject).in(FakeDatabaseObject.COLLECTION);
        boolean didDelete = database.delete("_id").matching(id).as(FakeDatabaseObject.class).from(FakeDatabaseObject.COLLECTION);
        ASSERT.that(didDelete).isTrue();

        Map<String, Object> map = database.load("_id").with(id).from(FakeDatabaseObject.COLLECTION).as(FakeDatabaseObject.class);
        ASSERT.that(map).isNull();
    }

    static void updateNoObject(DatabaseLayer database) {
        DatabaseObject databaseObject = new FakeDatabaseObject(null);
        boolean didUpdate = database.update(databaseObject).in(FakeDatabaseObject.COLLECTION);
        ASSERT.that(didUpdate).isFalse();
    }

    static void saveUpdateAndLoadObject(DatabaseLayer database) {
        String id = ObjectId.get().toString();
        database.save(new FakeDatabaseObject(id, "Ole")).in(FakeDatabaseObject.COLLECTION);

        boolean didUpdate = database.update(new FakeDatabaseObject(id, "Hans")).in(FakeDatabaseObject.COLLECTION);
        ASSERT.that(didUpdate).isTrue();

        Map<String, Object> data = database.load("_id").with(id).from(FakeDatabaseObject.COLLECTION).as(FakeDatabaseObject.class);
        ASSERT.that(data.get("name")).isEqualTo("Hans");
    }

}
