package dk.developer.glass;

import dk.developer.clause.In;
import dk.developer.utility.ReflectionTool;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class GlassDestructor {
    private final Object object;
    private final Method method;
    private final Specifier specifier;

    static In<Class<?>, GlassDestructor> of(Object object) {
        return mode -> {
            Class<?> type = object.getClass();
            Method method = findOutputDestructor(type).in(mode);
            Specifier specifier = findSpecifier(method);
            return new GlassDestructor(object, method, specifier);
        };
    }

    static In<Class<?>, Method> findOutputDestructor(Class<?> type) {
        return mode -> {
            Set<Method> destructors = stream(type.getDeclaredMethods())
                    .filter(method -> isOutputDestructor(mode, method))
                    .collect(toSet());

            if ( destructors.isEmpty() )
                throw new IllegalArgumentException("No serialiser present on object: " + type);

            if ( destructors.size() > 1 )
                throw new IllegalArgumentException("Unable to uniquely determine serialiser on type: " + type);

            return destructors.iterator().next();
        };
    }

    private static boolean isOutputDestructor(Class<?> mode, Method method) {
        Output output = method.getDeclaredAnnotation(Output.class);
        return output != null && output.value().isAssignableFrom(mode);
    }

    static Specifier findSpecifier(Method output) {
        List<Annotation> specifiers = stream(output.getDeclaredAnnotations())
                .filter(GlassDestructor::isSpecifier)
                .collect(toList());

        if ( specifiers.isEmpty() )
            throw new IllegalArgumentException("No specifier present on serialiser: " + output.getName());

        if ( specifiers.size() > 1 )
            throw new IllegalArgumentException("Unable to uniquely determine specifier on serialiser: " + output.getName());

        Annotation annotation = specifiers.get(0);
        if ( annotation instanceof IncludeAll )
            return new Specifier.IncludeAllSpecifier();

        if ( annotation instanceof ExcludeAll )
            return new Specifier.ExcludeAllSpecifier();

        if ( annotation instanceof Include ) {
            String[] fieldNames = ((Include) annotation).value();
            validateSpecifierFieldNames(fieldNames, output);
            return new Specifier.IncludeSpecifier(fieldNames);
        }

        if ( annotation instanceof Exclude ) {
            String[] fieldNames = ((Exclude) annotation).value();
            validateSpecifierFieldNames(fieldNames, output);
            return new Specifier.ExcludeSpecifier(fieldNames);
        }

        throw new RuntimeException("This was not supposed to happen");
    }

    private static void validateSpecifierFieldNames(String[] fieldNames, Method method) {
        if ( fieldNames.length == 0 )
            throw new IllegalArgumentException(noFieldsSpecifiedError(method));

        Set<String> seenValues = new HashSet<>();
        boolean duplicateFieldsSpecified = stream(fieldNames).anyMatch(field -> {
                    if ( seenValues.contains(field) )
                        return true;
                    seenValues.add(field);
                    return false;
                }
        );

        if ( duplicateFieldsSpecified )
            throw new IllegalArgumentException(duplicateFieldsSpecifiedError(method));
    }

    private static String noFieldsSpecifiedError(Method method) {
        Class<?> type = method.getDeclaringClass();
        Class<?> mode = method.getDeclaredAnnotation(Output.class).value();

        String format = "@%s destructor for %s found on %s specifies no fields";
        return format(format, Output.class.getSimpleName(), mode.getSimpleName(), type.getSimpleName());
    }

    private static String duplicateFieldsSpecifiedError(Method method) {
        Class<?> type = method.getDeclaringClass();
        Class<?> mode = method.getDeclaredAnnotation(Output.class).value();

        String format = "@%s destructor for %s found on %s specifies the same field multiple times";
        return format(format, Output.class.getSimpleName(), mode.getSimpleName(), type.getSimpleName());
    }

    private static boolean isSpecifier(Annotation annotation) {
        if ( annotation instanceof IncludeAll )
            return true;

        if ( annotation instanceof ExcludeAll )
            return true;

        if ( annotation instanceof Include )
            return true;

        if ( annotation instanceof Exclude )
            return true;

        return false;
    }

    GlassDestructor(Object object, Method method, Specifier specifier) {
        this.object = object;
        this.method = method;
        this.specifier = specifier;
    }

    public Set<GlassField> filter(Set<GlassField> fields) {
        return specifier.filter(fields);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> process(Map<String, Object> unprocessedMap) {
        return (Map<String, Object>) ReflectionTool.execute(method).of(object).with(unprocessedMap);
    }
}
