package dk.developer.glass;

import org.testng.annotations.Test;

import java.lang.reflect.Constructor;
import java.util.List;

import static dk.developer.glass.Handler.JACKSON;
import static dk.developer.testing.Truth.ASSERT;

public class GlassConstructorTest {
    @Test(expectedExceptions = IllegalArgumentException.class,
          expectedExceptionsMessageRegExp = "No @Input constructor found on class InputConstructorsOne in mode double")
    public void shouldReportErrorIfNoInputConstructor() throws Exception {
        GlassConstructor.findInputConstructor(InputConstructorsOne.class).in(double.class);
    }

    @Test
    public void shouldFindExactInputConstructor() throws Exception {
        Constructor<?> constructor = GlassConstructor.findInputConstructor(InputConstructorsOne.class).in(String.class);
        ASSERT.that(constructor).isNotNull();
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
          expectedExceptionsMessageRegExp = "Several @Input constructors found on class InputConstructorsOne in mode Integer")
    public void shouldReportErrorIfMoreThanOneInputConstructor() throws Exception {
        GlassConstructor.findInputConstructor(InputConstructorsOne.class).in(Integer.class);
    }

    @Test
    public void shouldFindAssignableInputConstructor() throws Exception {
        Constructor<?> constructor = GlassConstructor.findInputConstructor(InputConstructorsTwo.class).in(String.class);
        ASSERT.that(constructor).isNotNull();
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
          expectedExceptionsMessageRegExp = "@Input constructor for CharSequence found on InputConstructorsTwo does not accept parameters")
    public void shouldReportErrorIfNoArgumentInputConstructor() throws Exception {
        Constructor<?> constructor = InputConstructorsTwo.class.getDeclaredConstructors()[0];
        GlassConstructor.extractParameters(constructor);
    }

    @Test
    public void shouldExtractParametersFromInputConstructor() throws Exception {
        Constructor<?> constructor = InputConstructorsOne.class.getDeclaredConstructor(String.class, int.class);
        List<GlassParameter> parameters = GlassConstructor.extractParameters(constructor);
        ASSERT.that(parameters).containsExactly(
                new GlassParameter("title", String.class, JACKSON),
                new GlassParameter("age", int.class, JACKSON)
        );
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
          expectedExceptionsMessageRegExp = "@Input constructor for CharSequence found on InputConstructorsThree has several parameters pointing to the same field")
    public void shouldReportErrorIfParametersPointToSameField() throws Exception {
        Constructor<InputConstructorsThree> constructor = InputConstructorsThree.class.getDeclaredConstructor(String.class,
                String.class, int.class);
        GlassConstructor.extractParameters(constructor);
    }

    @Test
    public void shouldCreateGlassConstructor() throws Exception {
        GlassConstructor constructor = GlassConstructor.of(InputConstructorsTwo.class).in(Integer.class);
        ASSERT.that(constructor.getConstructor()).isNotNull();
        ASSERT.that(constructor.getParameters()).containsExactly(
                new GlassParameter("title", String.class, JACKSON),
                new GlassParameter("age", int.class, JACKSON)
        );
    }

    private static class InputConstructorsOne {
        @As("title") String name;
        @As("age") int age;

        @Input(String.class)
        InputConstructorsOne() {}

        @Input(Integer.class)
        InputConstructorsOne(@Bind("name") String firstName) {}

        @Input(Integer.class)
        InputConstructorsOne(@Bind("name") @Default String firstName, @Bind("age") @Default int age) {}
    }

    private static class InputConstructorsTwo {
        @As("title") String name;
        @As("age") int age;

        @Input(CharSequence.class)
        InputConstructorsTwo() {}

        @Input(Number.class)
        InputConstructorsTwo(@Bind("name") @Default String firstName, @Bind("age") @Default int age) {}
    }

    private static class InputConstructorsThree {
        @As("firstAndLastName") String name;
        @As("age") int age;

        @Input(CharSequence.class)
        InputConstructorsThree(@Bind("name") @Default String firstName, @Bind("name") @Default String lastName, @Bind("age") @Default int age) {}
    }
}