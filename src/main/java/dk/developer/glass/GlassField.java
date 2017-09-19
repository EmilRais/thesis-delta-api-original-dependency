package dk.developer.glass;

import dk.developer.clause.In;
import dk.developer.utility.ReflectionTool;

import java.lang.reflect.Field;
import java.util.Objects;

public class GlassField {
    private final String fieldName;
    private final String jsonName;
    private final Handler handler;
    private final Object value;

    static In<Object, GlassField> of(Field field) {
        return owner -> {
            String fieldName = field.getName();
            String jsonName = field.getDeclaredAnnotation(As.class).value();
            Object fieldValue = ReflectionTool.extract(field).of(owner);
            Handler handler = Handler.of(field);
            return new GlassField(fieldName, jsonName, handler, fieldValue);
        };
    }

    GlassField(String fieldName, String jsonName, Handler handler, Object value) {
        this.fieldName = fieldName;
        this.jsonName = jsonName;
        this.handler = handler;
        this.value = value;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getJsonName() {
        return jsonName;
    }

    public Handler getHandler() {
        return handler;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        GlassField that = (GlassField) o;
        return Objects.equals(fieldName, that.fieldName) &&
                Objects.equals(jsonName, that.jsonName) &&
                Objects.equals(handler, that.handler) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldName, jsonName, handler, value);
    }
}
