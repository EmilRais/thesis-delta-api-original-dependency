package dk.developer.glass;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

import static java.lang.String.format;

enum Handler {
    JACKSON, GLASS;

    private static final String format = "%s %s is neither supported by Glass nor Jackson";

    static Handler of(Field field) {
        if ( field.getDeclaredAnnotation(Default.class) != null )
            return JACKSON;

        Class<?> type = field.getType();
        if ( type.getDeclaredAnnotation(Shard.class) != null )
            return GLASS;

        throw new IllegalArgumentException(format(format, "Field", field.getName()));
    }

    static Handler of(Parameter parameter) {
        if ( parameter.getDeclaredAnnotation(Default.class) != null )
            return JACKSON;

        Class<?> type = parameter.getType();
        if ( type.getDeclaredAnnotation(Shard.class) != null )
            return GLASS;

        throw new IllegalArgumentException(format(format, "Parameter", parameter.getName()));
    }
}
