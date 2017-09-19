package dk.developer.glass.processors;

import com.google.testing.compile.CompileTester;
import org.testng.annotations.Test;

import javax.tools.JavaFileObject;

import static dk.developer.testing.Truth.ASSERT;
import static com.google.testing.compile.JavaFileObjects.forSourceLines;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class GlassDefaultProcessorTest {
    @Test
    public void shouldAcceptDefaultOnAsAnnotatedFields() throws Exception {
        expectThat(validClass()).compilesWithoutError();
    }

    @Test
    public void shouldRejectDefaultOnNonAsAnnotatedFields() throws Exception {
        expectThat(invalidClass()).failsToCompile().withErrorContaining(GlassDefaultProcessor.placementErrorMessage());
    }

    private JavaFileObject validClass() {
        return forSourceLines(
                "dk.developer.glass.SomeClass",
                "package dk.developer.glass;",
                "class SomeClass {",
                "\t@As(\"name\") @Default String name;",
                "\t@As(\"city\") String city;",
                "\t@As(\"age\") @Default int age;",
                "}"
        );
    }

    private JavaFileObject invalidClass() {
        return forSourceLines(
                "dk.developer.glass.SomeClass",
                "package dk.developer.glass;",
                "class SomeClass {",
                "\t@As(\"name\") @Default String name;",
                "\t@As(\"city\") String city;",
                "\t@Default int age;",
                "}"
        );

    }

    private CompileTester expectThat(JavaFileObject target) {
        return ASSERT.about(javaSource()).that(target).processedWith(new GlassDefaultProcessor());
    }
}