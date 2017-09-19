package dk.developer.glass.processors;

import com.google.testing.compile.CompileTester;
import org.testng.annotations.Test;

import javax.tools.JavaFileObject;

import static dk.developer.testing.Truth.ASSERT;
import static com.google.testing.compile.JavaFileObjects.forSourceLines;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static dk.developer.glass.processors.GlassAsProcessor.emptyValueErrorMessage;

public class GlassAsProcessorTest {
    @Test
    public void shouldAcceptThePresenceOfValidAs() throws Exception {
        expectThat(someClass("name", "age")).compilesWithoutError();
    }

    @Test
    public void shouldRejectAsWithEmptyString() throws Exception {
        expectThat(someClass("name", " ")).failsToCompile().withErrorContaining(emptyValueErrorMessage());
    }

    private CompileTester expectThat(JavaFileObject target) {
        return ASSERT.about(javaSource()).that(target).processedWith(new GlassAsProcessor());
    }

    private JavaFileObject someClass(String firstValue, String secondValue) {
        return forSourceLines("dk.developer.glass.SomeClass",
                "package dk.developer.glass;",
                "public class SomeClass {",
                "\t@As(\"" + firstValue + "\") String name;",
                "\t@As(\"" + secondValue + "\") int age;",
                "}");
    }
}