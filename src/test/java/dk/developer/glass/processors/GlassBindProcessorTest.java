package dk.developer.glass.processors;

import com.google.testing.compile.CompileTester;
import org.testng.annotations.Test;

import javax.tools.JavaFileObject;

import static dk.developer.testing.Truth.ASSERT;
import static com.google.testing.compile.JavaFileObjects.forSourceLines;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static dk.developer.glass.processors.GlassBindProcessor.*;

public class GlassBindProcessorTest {
    @Test
    public void shouldAcceptThePresenceOfValidBind() throws Exception {
        expectThat(someClass("name", "age", "city")).compilesWithoutError();
    }

    @Test
    public void shouldRejectBindWithEmptyString() throws Exception {
        expectThat(someClass("name", "", "city")).failsToCompile().withErrorContaining(emptyValueErrorMessage());
    }

    @Test
    public void shouldRejectIfNotBoundToAnAsField() throws Exception {
        expectThat(someClass("name", "year", "city")).failsToCompile().withErrorContaining(notBoundErrorMessage());
    }

    @Test
    public void shouldRejectIfBindingNonInputParameters() throws Exception {
        expectThat(someOtherClass()).failsToCompile().withErrorContaining(placementErrorMessage());
    }

    private CompileTester expectThat(JavaFileObject target) {
        return ASSERT.about(javaSource()).that(target).processedWith(new GlassBindProcessor());
    }

    private JavaFileObject someClass(String firstValue, String secondValue, String thirdValue) {
        return forSourceLines("dk.developer.glass.SomeClass",
                "package dk.developer.glass;",
                "public class SomeClass {",
                "\t@As(\"title\") String name;",
                "\t@As(\"city\") String city;",
                "\t@As(\"year\") int age;",
                "\t@Input(Class.class)",
                "\tSomeClass(@Bind(\"" + firstValue + "\") String x, @Bind(\"" + secondValue + "\") int y, @Bind(\"" + thirdValue + "\") String city) {}",
                "}");
    }

    private JavaFileObject someOtherClass() {
        return forSourceLines("dk.developer.glass.SomeOtherClass",
                "package dk.developer.glass;",
                "public class SomeOtherClass {",
                "\t@As(\"title\") String name;",
                "\t@As(\"city\") String city;",
                "\t@As(\"year\") int age;",
                "\tSomeOtherClass(@Bind(\"city\") String x) {}",
                "}");
    }
}