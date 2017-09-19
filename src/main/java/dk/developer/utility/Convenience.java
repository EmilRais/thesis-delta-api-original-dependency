package dk.developer.utility;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;

public class Convenience {
    @SafeVarargs
    public static <T> List<T> list(T... elements) {
        return asList(elements);
    }

    @SafeVarargs
    public static <T> Set<T> set(T... elements) {
        return new HashSet<>(list(elements));
    }
}
