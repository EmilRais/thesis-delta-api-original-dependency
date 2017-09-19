package dk.developer.glass;

import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Objects;

import static java.lang.String.format;
import static java.util.Arrays.stream;

public class GlassParameter {
    private final String jsonName;
    private final Class<?> type;
    private final Handler handler;

    static GlassParameter of(Parameter parameter) {
        Bind bind = parameter.getDeclaredAnnotation(Bind.class);
        if ( bind == null )
            throw new IllegalArgumentException(notAnnotatedBindError(parameter));

        String fieldName = bind.value();
        Field field = findField(fieldName, parameter);
        As as = field.getDeclaredAnnotation(As.class);
        if ( as == null )
            throw new IllegalArgumentException(notAnnotatedAsError(fieldName));

        Handler handler = Handler.of(parameter);
        String jsonName = as.value();
        Class<?> type = parameter.getType();
        return new GlassParameter(jsonName, type, handler);
    }

    private static String notAnnotatedAsError(String fieldName) {
        String format = "Field %s is not annotated @%s";
        return format(format, fieldName, As.class.getSimpleName());
    }

    private static Field findField(String fieldName, Parameter parameter) {
        Executable constructor = parameter.getDeclaringExecutable();
        Class<?> declaringClass = constructor.getDeclaringClass();
        return stream(declaringClass.getDeclaredFields())
                .filter(field -> field.getName().equals(fieldName))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(boundFieldDoesNotExistError(fieldName, parameter)));
    }

    private static String boundFieldDoesNotExistError(String fieldName, Parameter parameter) {
        String format = "Field %s specified by parameter %s does not exist";
        return format(format, fieldName, parameter.getName());
    }

    private static String notAnnotatedBindError(Parameter parameter) {
        String format = "Parameter %s is not annotated @%s";
        return format(format, parameter.getName(), Bind.class.getSimpleName());
    }

    GlassParameter(String jsonName, Class<?> type, Handler handler) {
        this.jsonName = jsonName;
        this.type = type;
        this.handler = handler;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        GlassParameter that = (GlassParameter) o;
        return Objects.equals(jsonName, that.jsonName) &&
                Objects.equals(type, that.type) &&
                Objects.equals(handler, that.handler);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jsonName, type, handler);
    }

    @Override
    public String toString() {
        return "GlassParameter{" +
                "jsonName='" + jsonName + '\'' +
                ", type=" + type +
                ", handler=" + handler +
                '}';
    }

    public String getJsonName() {
        return jsonName;
    }

    public Class<?> getType() {
        return type;
    }

    public Handler getHandler() {
        return handler;
    }
}
