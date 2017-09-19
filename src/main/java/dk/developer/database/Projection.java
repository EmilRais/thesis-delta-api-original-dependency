package dk.developer.database;

import java.util.List;

public interface Projection<Type> {
    List<Type> everything();
    List<Type> excluding(String... fields);
}
