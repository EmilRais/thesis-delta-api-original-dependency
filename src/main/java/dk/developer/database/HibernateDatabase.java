package dk.developer.database;

import com.fasterxml.jackson.core.type.TypeReference;
import dk.developer.clause.*;
import dk.developer.utility.Converter;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.criterion.Restrictions;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import static dk.developer.utility.Convenience.set;
import static java.lang.String.format;
import static java.util.Arrays.stream;

public class HibernateDatabase implements DatabaseLayer {
    private final SessionFactory factory;

    public static class SessionFactoryBuilder {
        private final String configurationFileName;
        private Set<Class<? extends DatabaseObject>> annotatedClasses;
        private Set<String> resources;

        public SessionFactoryBuilder(String configurationFileName) {
            this.configurationFileName = configurationFileName;
            this.annotatedClasses = set();
            this.resources = set();
        }

        @SafeVarargs
        public final SessionFactoryBuilder annotatedClasses(Class<? extends DatabaseObject>... annotatedClasses) {
            this.annotatedClasses.addAll(set(annotatedClasses));
            return this;
        }

        public SessionFactoryBuilder resources(String... resources) {
            this.resources.addAll(set(resources));
            return this;
        }

        public SessionFactory create() {
            StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure(configurationFileName).build();

            MetadataSources sources = new MetadataSources(registry);
            annotatedClasses.forEach(sources::addAnnotatedClass);
            resources.forEach(sources::addResource);
            return sources.buildMetadata().buildSessionFactory();
        }
    }

    public HibernateDatabase(SessionFactory factory) {
        this.factory = factory;
    }

    private void executeVoid(Consumer<Session> consumer) {
        execute(manager -> {
            consumer.accept(manager);
            return null;
        });
    }

    private <Result> Result execute(Function<Session, Result> function) {
        Session session = factory.openSession();
        session.beginTransaction();
        Result result = function.apply(session);

        session.getTransaction().commit();
        session.close();
        return result;
    }

    @Override
    public In.Void<String> save(DatabaseObject databaseObject) throws RuntimeException {
        return collection -> executeVoid(session -> {
            session.save(databaseObject);
        });
    }

    @Override
    public With<Object, From<String, As<Class<? extends DatabaseObject>, Map<String, Object>>>> load(String key) {
        return value -> collectionName -> type -> execute(session -> {
            List<?> matches;
            try {
                matches = session.createCriteria(type).add(Restrictions.eq(key, value)).list();
            } catch (Exception e) {
                return null;
            }

            if ( matches.isEmpty() )
                return null;

            Object result = matches.get(0);
            TypeReference<Map<String, Object>> mapTypeReference = new TypeReference<Map<String, Object>>() {
            };
            return Converter.converter().convert(result, mapTypeReference);
        });
    }

    @Override
    public As<Class<? extends DatabaseObject>, Projection<Map<String, Object>>> loadAll(String collectionName) {
        return type -> new Projection<Map<String, Object>>() {
            @Override
            public List<Map<String, Object>> everything() {
                return execute(session -> {
                    List<?> results = session.createCriteria(type).list();

                    TypeReference<List<Map<String, Object>>> listMapTypeReference = new TypeReference<List<Map<String, Object>>>() {
                    };
                    return Converter.converter().convert(results, listMapTypeReference);
                });
            }

            @Override
            public List<Map<String, Object>> excluding(String... keys) {
                throw new UnsupportedOperationException("We do not support exclusion yet");
            }
        };
    }

    @Override
    public Matching<Object, As<Class<? extends DatabaseObject>, From.Bool<String>>> delete(String key) {
        return value -> type -> collectionName -> execute(session -> {
            Query query = session.createQuery(format("delete from %s where %s = '%s'", type.getName(), key, value));
            int numberOfAffectedRows = query.executeUpdate();
            return numberOfAffectedRows > 0;
        });
    }

    @Override
    public In.Bool<String> update(DatabaseObject databaseObject) {
        return collection -> {
            try {
                executeVoid(session -> session.update(databaseObject));
                return true;
            } catch (Exception e) {
                return false;
            }
        };
    }
}