package dk.developer.database;

import dk.developer.clause.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class MemoryDatabase implements DatabaseLayer {
    private final Map<String, Map<Object, DatabaseObject>> database;

    public static DatabaseLayer memoryDatabase() {
        Map<String, Map<Object, DatabaseObject>> dataMap = new HashMap<>();
        return new MemoryDatabase(dataMap);
    }

    public MemoryDatabase(Map<String, Map<Object, DatabaseObject>> database) {
        this.database = database;
    }

    @Override
    public In.Void<String> save(DatabaseObject databaseObject) {
        return collectionName -> {
            database.putIfAbsent(collectionName, new HashMap<>());
            Map<Object, DatabaseObject> collection = database.get(collectionName);

            Object id = databaseObject.getId();
            if ( collection.containsKey(id) )
                throw new RuntimeException("Duplicate id for " + databaseObject);

            collection.put(id, databaseObject);
        };
    }

    @Override
    public With<Object, From<String, As<Class<? extends DatabaseObject>, Map<String, Object>>>> load(String key) {
        return value -> collectionName -> type -> {
            Map<Object, DatabaseObject> collection = database.getOrDefault(collectionName, new HashMap<>());
            for (DatabaseObject databaseObject : collection.values()) {
                Map<String, Object> objectMap = asMap(databaseObject);
                Object object = objectMap.get(key);
                if ( value.equals(object) )
                    return objectMap;
            }

            return null;
        };
    }

    @Override
    public As<Class<? extends DatabaseObject>, Projection<Map<String, Object>>> loadAll(String collectionName) {
        return type -> new Projection<Map<String, Object>>() {
            @Override
            public List<Map<String, Object>> everything() {
                Map<Object, DatabaseObject> collection = database.getOrDefault(collectionName, new HashMap<>());
                return collection.values().stream()
                        .map(MemoryDatabase.this::asMap)
                        .collect(Collectors.toList());
            }

            @Override
            public List<Map<String, Object>> excluding(String... fields) {
                List<Map<String, Object>> everything = everything();
                everything.forEach(object -> stream(fields).forEach(object::remove));
                return everything;
            }
        };
    }

    @Override
    public Matching<Object, As<Class<? extends DatabaseObject>, From.Bool<String>>> delete(String key) {
        return value -> type -> collectionName -> {
            Map<Object, DatabaseObject> collection = database.getOrDefault(collectionName, new HashMap<>());
            for (DatabaseObject databaseObject : collection.values()) {
                Map<String, Object> objectMap = asMap(databaseObject);
                Object object = objectMap.get(key);
                if ( value.equals(object) ) {
                    collection.remove(databaseObject.getId());
                    return true;
                }
            }

            return false;
        };
    }

    @Override
    public In.Bool<String> update(DatabaseObject databaseObject) {
        return collectionName -> {
            Map<Object, DatabaseObject> collection = database.getOrDefault(collectionName, new HashMap<>());

            Object id = databaseObject.getId();
            DatabaseObject oldValue = collection.put(id, databaseObject);
            return oldValue != null;
        };
    }
}
