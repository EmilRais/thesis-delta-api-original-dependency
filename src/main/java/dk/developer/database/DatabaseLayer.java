package dk.developer.database;

import com.fasterxml.jackson.core.type.TypeReference;
import dk.developer.clause.*;

import java.util.Map;

import static dk.developer.utility.Converter.converter;

public interface DatabaseLayer {
    In.Void<String> save(DatabaseObject databaseObject) throws RuntimeException;
    With<Object, From<String, As<Class<? extends DatabaseObject>, Map<String, Object>>>> load(String key);
    As<Class<? extends DatabaseObject>, Projection<Map<String, Object>>> loadAll(String collectionName);
    Matching<Object, As<Class<? extends DatabaseObject>, From.Bool<String>>> delete(String key);
    In.Bool<String> update(DatabaseObject databaseObject);

    default Map<String, Object> asMap(DatabaseObject databaseObject) {
        return converter().convert(databaseObject, new TypeReference<Map<String, Object>>() {
        });
    }
}
