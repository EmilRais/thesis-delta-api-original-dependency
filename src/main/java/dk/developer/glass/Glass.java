package dk.developer.glass;

import dk.developer.clause.From;
import dk.developer.clause.In;
import dk.developer.clause.Of;
import dk.developer.utility.Converter;

import java.util.*;
import java.util.Map.Entry;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.*;

public class Glass {
    // TODO: We need to unit test the helper functionality
    // TODO: Main issue: Fix tests to get a good coverage
    // TODO: Validate that the shard has been specified properly

    private Converter converter;

    public Glass(Converter converter) {
        this.converter = converter;
    }

    public In<Class<?>, String> toJson(Object object) {
        ensureNoDuplicateAsFields(object.getClass());

        return mode -> {
            Map<String, Object> intermediateRepresentation = toIntermediateRepresentation(object).in(mode);
            return serialise(intermediateRepresentation);
        };
    }

    private void ensureNoDuplicateAsFields(Class<?> type) {
        Set<String> seenValues = new HashSet<>();
        boolean duplicateJsonName = stream(type.getDeclaredFields())
                .map(field -> field.getDeclaredAnnotation(As.class))
                .filter(Objects::nonNull)
                .map(As::value)
                .anyMatch(jsonName -> {
                            if ( seenValues.contains(jsonName) )
                                return true;
                            seenValues.add(jsonName);
                            return false;
                        }
                );

        if ( duplicateJsonName )
            throw new IllegalArgumentException(hasSameJsonNameError(type));
    }

    In<Class<?>, Map<String, Object>> toIntermediateRepresentation(Object object) {
        return mode -> {
            GlassDestructor destructor = GlassDestructor.of(object).in(mode);
            Set<GlassField> specifiedFields = destructor.filter(findFields(object));
            Map<String, Object> unprocessedMap = represent(specifiedFields).of(object).in(mode);
            return destructor.process(unprocessedMap);
        };
    }

    String serialise(Map<String, Object> representation) {
        return representation.entrySet().stream()
                .map(this::serialiseField)
                .collect(joining(", ", "{", "}"));
    }

    @SuppressWarnings("unchecked")
    private String serialiseField(Entry<String, Object> pair) {
        Object value = pair.getValue();
        if ( value instanceof Map )
            return serialise((Map<String, Object>) value);

        if ( value instanceof String )
            return format("\"%s\": \"%s\"", pair.getKey(), pair.getValue());

        return format("\"%s\": %s", pair.getKey(), pair.getValue());
    }

    Of<Object, In<Class<?>, Map<String, Object>>> represent(Set<GlassField> specifiedFields) {
        return object -> mode -> {
            Map<String, Object> representation = new HashMap<>();
            specifiedFields.stream().forEach(field -> {
                String jsonName = field.getJsonName();
                Object convertedValue = convertValue(field).in(mode);
                representation.put(jsonName, convertedValue);
            });
            return representation;
        };
    }

    In<Class<?>, Object> convertValue(GlassField field) {
        return mode -> {
            Object value = field.getValue();
            if ( value == null )
                return null;

            switch ( field.getHandler() ) {
                case JACKSON:
                    return converter.convert(value, value.getClass()); // This does nothing. Convert it to the right thing.
                case GLASS:
                    return toIntermediateRepresentation(value).in(mode);
            }

            throw new RuntimeException("Unable to convert value for field: " + field.getFieldName());
        };
    }

    Set<GlassField> findFields(Object object) {
        Class<?> type = object.getClass();
        return stream(type.getDeclaredFields())
                .filter(field -> field.getDeclaredAnnotation(As.class) != null)
                .map(field -> GlassField.of(field).in(object))
                .collect(toSet());
    }

    public <T> From<String, In<Class<?>, T>> create(Class<T> type) {
        // TODO: Make sure there are no duplicated values in GlassSpecifiers
        // TODO: Parse the string into a map
        // TODO: Apply the value to the parameters
        // TODO: Call the GlassConstructor with the parameters in the right order
        // TODO: Create a construct method


        // TODO: Pass boolean to destructor
        // TODO: Enums can be handled using the destructor methods ability to modify the map
        return json -> mode -> {
            ensureNoDuplicateAsFields(type);
            GlassConstructor constructor = GlassConstructor.of(type).in(mode);
            List<GlassParameter> parameters = constructor.getParameters();
            System.out.println("Not implemented yet");
            return null;
        };
    }

    private String hasSameJsonNameError(Class<?> type) {
            String format = "%s has several fields with the same json name";
            return format(format, type.getSimpleName());
    }
}
