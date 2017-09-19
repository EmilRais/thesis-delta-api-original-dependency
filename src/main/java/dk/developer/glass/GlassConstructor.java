package dk.developer.glass;

import dk.developer.clause.In;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class GlassConstructor {
    private final Constructor<?> constructor;
    private final List<GlassParameter> parameters;

    static In<Class<?>, GlassConstructor> of(Class<?> type) {
        return mode -> {
            Constructor<?> constructor = findInputConstructor(type).in(mode);
            List<GlassParameter> parameters = extractParameters(constructor);
            return new GlassConstructor(constructor, parameters);
        };
    }

    static List<GlassParameter> extractParameters(Constructor<?> constructor) {
        Parameter[] parameters = constructor.getParameters();
        if ( parameters.length == 0 )
            throw new IllegalArgumentException(noParametersError(constructor));

        if ( pointsToSameField(parameters) )
            throw new IllegalArgumentException(pointsToSameFieldError(constructor));

        return stream(parameters)
                .map(GlassParameter::of)
                .collect(toList());
    }

    private static boolean pointsToSameField(Parameter[] parameters) {
        Set<String> seenValues = new HashSet<>();
        return stream(parameters)
                .map(p -> p.getDeclaredAnnotation(Bind.class).value())
                .anyMatch(fieldName -> {
                    if ( seenValues.contains(fieldName) )
                        return true;
                    seenValues.add(fieldName);
                    return false;
                });
    }

    static In<Class<?>, Constructor<?>> findInputConstructor(Class<?> type) {
        return mode -> {
            Set<Constructor<?>> constructors = stream(type.getDeclaredConstructors())
                    .filter(constructor -> isInputConstructor(constructor, mode))
                    .collect(toSet());

            if ( constructors.isEmpty() )
                throw new IllegalArgumentException(noInputConstructorError(type, mode));

            if ( constructors.size() > 1 )
                throw new IllegalArgumentException(tooManyInputConstructorError(type, mode));
            return constructors.iterator().next();
        };
    }

    private static String pointsToSameFieldError(Constructor<?> constructor) {
        Class<?> type = constructor.getDeclaringClass();
        Class<?> mode = constructor.getDeclaredAnnotation(Input.class).value();

        String format = "@%s constructor for %s found on %s has several parameters pointing to the same field";
        return format(format, Input.class.getSimpleName(), mode.getSimpleName(), type.getSimpleName());
    }

    private static String noParametersError(Constructor<?> constructor) {
        Class<?> type = constructor.getDeclaringClass();
        Class<?> mode = constructor.getDeclaredAnnotation(Input.class).value();

        String format = "@%s constructor for %s found on %s does not accept parameters";
        return format(format, Input.class.getSimpleName(), mode.getSimpleName(), type.getSimpleName());
    }

    private static String tooManyInputConstructorError(Class<?> type, Class<?> mode) {
        String format = "Several @%s constructors found on class %s in mode %s";
        return format(format, Input.class.getSimpleName(), type.getSimpleName(), mode.getSimpleName());
    }

    private static String noInputConstructorError(Class<?> type, Class<?> mode) {
        String format = "No @%s constructor found on class %s in mode %s";
        return format(format, Input.class.getSimpleName(), type.getSimpleName(), mode.getSimpleName());
    }

    private static boolean isInputConstructor(Constructor<?> constructor, Class<?> mode) {
        Input input = constructor.getDeclaredAnnotation(Input.class);
        return input != null && input.value().isAssignableFrom(mode);
    }

    GlassConstructor(Constructor<?> constructor, List<GlassParameter> parameters) {
        this.constructor = constructor;
        this.parameters = parameters;
    }

    public Constructor<?> getConstructor() {
        return constructor;
    }

    public List<GlassParameter> getParameters() {
        return parameters;
    }
}
