package dk.developer.utility;

import dk.developer.clause.Of;
import dk.developer.clause.With;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionTool {
    public static Of<Object, Object> extract(Field field) {
        return object -> {
            boolean wasAccessible = field.isAccessible();
            field.setAccessible(true);
            try {
                return field.get(object);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } finally {
                field.setAccessible(wasAccessible);
            }
        };
    }

    public static Of<Object, With.Var<Object, Object>> execute(Method method) {
        return object -> arguments -> {
            boolean wasAccessible = method.isAccessible();
            method.setAccessible(true);
            try {
                return method.invoke(object, arguments);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            } finally {
                method.setAccessible(wasAccessible);
            }
        };
    }
}
