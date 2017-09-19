package dk.developer.glass.processors;

import com.google.testing.compile.CompileTester;
import dk.developer.glass.Exclude;
import dk.developer.glass.ExcludeAll;
import dk.developer.glass.Include;
import dk.developer.glass.IncludeAll;
import org.testng.annotations.Test;

import javax.tools.JavaFileObject;

import static dk.developer.testing.Truth.ASSERT;
import static com.google.testing.compile.JavaFileObjects.forSourceLines;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static dk.developer.glass.processors.GlassSpecifierProcessor.*;
import static java.lang.String.format;

public class GlassSpecifierProcessorTest {
    @Test
    public void shouldRejectIncludeAllNotPlacedOnOutputDestructor() throws Exception {
        Class<IncludeAll> annotation = IncludeAll.class;
        String representation = format("@%s", annotation.getSimpleName());
        expectThat(notOnOutputDestructor(representation)).failsToCompile().withErrorContaining(placementErrorMessage(annotation));
    }

    @Test
    public void shouldRejectExcludeAllNotPlacedOnOutputDestructor() throws Exception {
        Class<ExcludeAll> annotation = ExcludeAll.class;
        String representation = format("@%s", annotation.getSimpleName());
        expectThat(notOnOutputDestructor(representation)).failsToCompile().withErrorContaining(placementErrorMessage(annotation));
    }

    @Test
    public void shouldRejectIncludeNotPlacedOnOutputDestructor() throws Exception {
        Class<Include> annotation = Include.class;
        String representation = format("@%s({})", annotation.getSimpleName());
        expectThat(notOnOutputDestructor(representation)).failsToCompile().withErrorContaining(placementErrorMessage(annotation));
    }

    @Test
    public void shouldRejectExcludeNotPlacedOnOutputDestructor() throws Exception {
        Class<Exclude> annotation = Exclude.class;
        String representation = format("@%s({})", annotation.getSimpleName());
        expectThat(notOnOutputDestructor(representation)).failsToCompile().withErrorContaining(placementErrorMessage(annotation));
    }

    @Test
    public void shouldRejectTheEmptyString() throws Exception {
        Class<Include> annotation = Include.class;
        String representation = format("@%s(\"\")", annotation.getSimpleName());
        expectThat(outputDestructor(representation)).failsToCompile().withErrorContaining(noValueErrorMessage(annotation));
    }

    @Test
    public void shouldRejectTheTrimmedEmptyString() throws Exception {
        Class<Exclude> annotation = Exclude.class;
        String representation = format("@%s(\" \")", annotation.getSimpleName());
        expectThat(outputDestructor(representation)).failsToCompile().withErrorContaining(noValueErrorMessage(annotation));
    }

    @Test
    public void shouldRejectTheEmptyArray() throws Exception {
        Class<Include> annotation = Include.class;
        String representation = format("@%s({})", annotation.getSimpleName());
        expectThat(outputDestructor(representation)).failsToCompile().withErrorContaining(noValueErrorMessage(annotation));
    }

    @Test
    public void shouldRejectAnArrayContainingTrimmedEmptyString() throws Exception {
        Class<Exclude> annotation = Exclude.class;
        String representation = format("@%s({\"name\", \"city\", \" \"})", annotation.getSimpleName());
        expectThat(outputDestructor(representation)).failsToCompile().withErrorContaining(noValueErrorMessage(annotation));
    }

    @Test
    public void shouldRejectIfSpecifyingDuplicateFields() throws Exception {
        Class<Include> annotation = Include.class;
        String representation = format("@%s({\"name\", \"city\", \"age\", \"city\"})", annotation.getSimpleName());
        expectThat(outputDestructor(representation)).failsToCompile().withErrorContaining(duplicateValueErrorMessage(annotation));
    }

    @Test
    public void shouldRejectIfNotBoundToFields() throws Exception {
        Class<Exclude> annotation = Exclude.class;
        String representation = format("@%s({\"name\", \"town\", \"age\", \"city\"})", annotation.getSimpleName());
        expectThat(outputDestructor(representation)).failsToCompile().withErrorContaining(notBoundErrorMessage(annotation));
    }

    private CompileTester expectThat(JavaFileObject target) {
        return ASSERT.about(javaSource()).that(target).processedWith(new GlassSpecifierProcessor());
    }

    private JavaFileObject notOnOutputDestructor(String annotation) {
        return forSourceLines("dk.developer.glass.SomeClass",
                "package dk.developer.glass;",
                "public class SomeClass {",
                "\t" + annotation,
                "\tvoid someMethod() {}",
                "}");
    }

    private JavaFileObject outputDestructor(String annotation) {
        return forSourceLines("dk.developer.glass.SomeClass",
                "package dk.developer.glass;",
                "public class SomeClass {",
                "\t@As(\"title\") String name;",
                "\t@As(\"year\") int age;",
                "\t@As(\"city\") String city;",
                "\t@Output(Class.class)",
                "\t" + annotation,
                "\tvoid someMethod() {}",
                "}");
    }
}