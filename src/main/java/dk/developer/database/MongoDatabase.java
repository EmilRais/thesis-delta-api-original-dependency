package dk.developer.database;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import dk.developer.clause.*;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.mongodb.client.model.Projections.exclude;

public class MongoDatabase implements DatabaseLayer {
    private final com.mongodb.client.MongoDatabase database;

    public MongoDatabase(com.mongodb.client.MongoDatabase database) {
        this.database = database;
    }

    @Override
    public In.Void<String> save(DatabaseObject databaseObject) {
        return collectionName -> {
            MongoCollection<Document> collection = database.getCollection(collectionName);
            Map<String, Object> data = asMap(databaseObject);
            collection.insertOne(new Document(data));
        };
    }

    @Override
    public With<Object, From<String, As<Class<? extends DatabaseObject>, Map<String, Object>>>> load(String key) {
        return value -> collectionName -> type -> {
            MongoCollection<Document> collection = database.getCollection(collectionName);
            BasicDBObject findByIdQuery = new BasicDBObject(key, value);

            MongoCursor<Document> iterator = collection.find(findByIdQuery).iterator();
            if ( !iterator.hasNext() )
                return null;

            return iterator.next();
        };
    }

    @Override
    public As<Class<? extends DatabaseObject>, Projection<Map<String, Object>>> loadAll(String collectionName) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        return type -> new Projection<Map<String, Object>>() {
            @Override
            public List<Map<String, Object>> everything() {
                List<Map<String, Object>> documents = new ArrayList<>();
                for (Document document : collection.find())
                    documents.add(document);

                return documents;
            }

            @Override
            public List<Map<String, Object>> excluding(String... fields) {
                List<Map<String, Object>> documents = new ArrayList<>();
                Bson filter = exclude(fields);
                for (Document document : collection.find().projection(filter))
                    documents.add(document);

                return documents;
            }
        };
    }

    @Override
    public Matching<Object, As<Class<? extends DatabaseObject>, From.Bool<String>>> delete(String key) {
        return value -> type -> collectionName -> {
            MongoCollection<Document> collection = database.getCollection(collectionName);
            DeleteResult result = collection.deleteOne(new BasicDBObject(key, value));
            return result.getDeletedCount() != 0;
        };
    }

    @Override
    public In.Bool<String> update(DatabaseObject databaseObject) {
        return collectionName -> {
            MongoCollection<Document> collection = database.getCollection(collectionName);
            BasicDBObject filter = new BasicDBObject("_id", databaseObject.getId());

            Map<String, Object> data = asMap(databaseObject);
            Document updatedDocument = new Document("$set", new BasicDBObject(data));
            UpdateResult result = collection.updateOne(filter, updatedDocument);
            return result.getModifiedCount() != 0;
        };
    }
}
