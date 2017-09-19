package dk.developer.glass;

import org.testng.annotations.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;

import static dk.developer.testing.Truth.ASSERT;
import static dk.developer.glass.Handler.GLASS;
import static dk.developer.glass.Handler.JACKSON;

public class GlassParameterTest {
    @Test(expectedExceptions = IllegalArgumentException.class,
          expectedExceptionsMessageRegExp = "Parameter arg0 is not annotated @Bind")
    public void shouldReportErrorIfParameterIsNotAnnotatedBind() throws Exception {
        GlassParameter.of(parameter(0));
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
          expectedExceptionsMessageRegExp = "Field year specified by parameter arg1 does not exist")
    public void shouldReportErrorIfBoundFieldDoesNotExist() throws Exception {
        GlassParameter.of(parameter(1));
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
          expectedExceptionsMessageRegExp = "Field age is not annotated @As")
    public void shouldReportErrorIfBoundFieldIsNotAnnotatedAs() throws Exception {
        GlassParameter.of(parameter(2));
    }

    @Test
    public void shouldContainTheRightTypeAndJsonNameAndBeHandledByGlass() throws Exception {
        GlassParameter parameter = GlassParameter.of(parameter(3));
        ASSERT.that(parameter.getJsonName()).isEqualTo("legal");
        ASSERT.that(parameter.getType()).isSameAs(Parameters.class);
        ASSERT.that(parameter.getHandler()).isSameAs(GLASS);
    }

    @Test
    public void shouldBeHandledByJackson() throws Exception {
        GlassParameter parameter = GlassParameter.of(parameter(4));
        ASSERT.that(parameter.getJsonName()).isEqualTo("author");
        ASSERT.that(parameter.getType()).isEqualTo(String.class);
        ASSERT.that(parameter.getHandler()).isSameAs(JACKSON);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
          expectedExceptionsMessageRegExp = "Parameter arg5 is neither supported by Glass nor Jackson")
    public void shouldBeUnsupported() throws Exception {
        GlassParameter parameter = GlassParameter.of(parameter(5));
    }

    static Parameter parameter(int index) {
        Constructor<?> constructor = Parameters.class.getDeclaredConstructors()[0];
        return constructor.getParameters()[index];
    }

    @Shard
    static class Parameters {
        int age;
        @As("legal") String name;
        @As("author") Parameters authorName;
        @As("contract") @Default String contract;

        Parameters(int number, @Bind("year") int year,
                   @Bind("age") int age, @Bind("name") Parameters name,
                   @Bind("authorName") @Default String author, @Bind("contract") String contract) {
        }
    }
}