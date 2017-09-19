package dk.developer.utility;

import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static dk.developer.testing.Truth.ASSERT;

public class ReflectionToolTest {
    @Test
    public void shouldBeAbleToGetValueFromFields() throws Exception {
        SimpleObject johnson = new SimpleObject("Johnson");
        SimpleObject doe = new SimpleObject("Doe");

        Field nameField = SimpleObject.class.getDeclaredField("lastName");

        Object johnsonsName = ReflectionTool.extract(nameField).of(johnson);
        ASSERT.that(johnsonsName).isEqualTo("Johnson");

        Object doesName = ReflectionTool.extract(nameField).of(doe);
        ASSERT.that(doesName).isEqualTo("Doe");
    }

    @Test
    public void shouldBeAbleToExecuteMethods() throws Exception {
        SimpleObject johnson = new SimpleObject("Johnson");
        SimpleObject doe = new SimpleObject("Doe");

        Method method = SimpleObject.class.getDeclaredMethod("name", String.class);

        Object marksName = ReflectionTool.execute(method).of(johnson).with("Mark");
        ASSERT.that(marksName).isEqualTo("Mark Johnson");

        Object johnsName = ReflectionTool.execute(method).of(doe).with("John");
        ASSERT.that(johnsName).isEqualTo("John Doe");
    }

    static class SimpleObject {
        final String lastName;

        SimpleObject(String lastName) {
            this.lastName = lastName;
        }

        String name(String firstName) {
            return firstName + " " + lastName;
        }
    }
}