package dk.developer.glass.processors;

import com.google.testing.compile.CompileTester;
import org.testng.annotations.Test;

import javax.tools.JavaFileObject;

import static dk.developer.testing.Truth.ASSERT;
import static com.google.testing.compile.JavaFileObjects.forSourceLines;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static dk.developer.glass.processors.GlassInputProcessor.*;

public class GlassInputProcessorTest {
    @Test
    public void shouldRejectNoArgumentInputConstructor() throws Exception {
        expectThat(noArgumentInputConstructor()).failsToCompile().withErrorContaining(noParametersErrorMessage());
    }

    @Test
    public void shouldRejectInputConstructorWithSomeParametersNotBound() throws Exception {
        expectThat(notFullyBindedInputConstructor()).failsToCompile().withErrorContaining(parametersNotBoundErrorMessage());
    }

    @Test
    public void shouldRejectDuplicateBindingsInInputConstructor() throws Exception {
        expectThat(duplicateBindedInputConstructor()).failsToCompile().withErrorContaining(duplicateValueErrorMessage());
    }

    @Test
    public void shouldAcceptValidInputConstructor() throws Exception {
        expectThat(validInputConstructor()).compilesWithoutError();
    }

    private CompileTester expectThat(JavaFileObject target) {
        return ASSERT.about(javaSource()).that(target).processedWith(new GlassInputProcessor());
    }

    private JavaFileObject noArgumentInputConstructor() {
        return forSourceLines("dk.developer.glass.SomeClass",
                "package dk.developer.glass;",
                "public class SomeClass {",
                "@Input(Class.class)",
                "\tSomeClass() {}",
                "}");
    }

    private JavaFileObject notFullyBindedInputConstructor() {
        return forSourceLines("dk.developer.glass.SomeClass",
                "package dk.developer.glass;",
                "public class SomeClass {",
                "@Input(Class.class)",
                "\tSomeClass(@Bind(\"title\") String name, int age) {}",
                "}");
    }

    private JavaFileObject duplicateBindedInputConstructor() {
        return forSourceLines("dk.developer.glass.SomeClass",
                "package dk.developer.glass;",
                "public class SomeClass {",
                "@Input(Class.class)",
                "\tSomeClass(@Bind(\"title\") String name, @Bind(\"title\") int age) {}",
                "}");
    }

    private JavaFileObject validInputConstructor() {
        return forSourceLines("dk.developer.glass.SomeClass",
                "package dk.developer.glass;",
                "public class SomeClass {",
                "@Input(Class.class)",
                "\tSomeClass(@Bind(\"title\") String name, @Bind(\"year\") int age) {}",
                "}");
    }
}