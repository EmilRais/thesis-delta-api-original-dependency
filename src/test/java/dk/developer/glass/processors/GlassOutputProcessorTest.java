package dk.developer.glass.processors;

import com.google.testing.compile.CompileTester;
import org.testng.annotations.Test;

import javax.tools.JavaFileObject;

import static dk.developer.testing.Truth.ASSERT;
import static com.google.testing.compile.JavaFileObjects.forSourceLines;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static dk.developer.glass.processors.GlassOutputProcessor.*;

public class GlassOutputProcessorTest {
    @Test
    public void shouldRejectOutputDestructorWithParameters() throws Exception {
        expectThat(outputDestructorWithWrongParameters()).failsToCompile().withErrorContaining(wrongParameterErrorMessage());
    }

    @Test
    public void shouldRejectOutputDestructorWithReturnType() throws Exception {
        expectThat(outputDestructorWithWrongReturnType()).failsToCompile().withErrorContaining(wrongReturnTypeErrorMessage());
    }

    @Test
    public void shouldAcceptValidOutputDestructor() throws Exception {
        expectThat(validOutputDestructor()).compilesWithoutError();
    }

    @Test
    public void shouldRejectIfNoSpecifierOnOutputDestructor() throws Exception {
        expectThat(outputDestructorWithoutSpecifier()).failsToCompile().withErrorContaining(wrongNumberOfSpecifiersErrorMessage());
    }

    @Test
    public void shouldRejectIfMoreThanOneSpecifierOnOutputDestructor() throws Exception {
        expectThat(outputDestructorWithSeveralSpecifiers()).failsToCompile().withErrorContaining(wrongNumberOfSpecifiersErrorMessage());
    }

    private CompileTester expectThat(JavaFileObject target) {
        return ASSERT.about(javaSource()).that(target).processedWith(new GlassOutputProcessor());
    }

    private JavaFileObject outputDestructorWithWrongParameters() {
        return forSourceLines("dk.developer.glass.SomeClass",
                "package dk.developer.glass;",
                "public class SomeClass {",
                "@Output(Class.class)",
                "@IncludeAll",
                "\tMap<String, Object> someMethod(String name) {}",
                "}");
    }

    private JavaFileObject outputDestructorWithWrongReturnType() {
        return forSourceLines("dk.developer.glass.SomeClass",
                "package dk.developer.glass;",
                "import java.util.Map;",
                "public class SomeClass {",
                "@Output(Class.class)",
                "@IncludeAll",
                "\tString someMethod(Map<String, Object> map) {",
                "\t\treturn null;",
                "\t}",
                "}");
    }

    private JavaFileObject validOutputDestructor() {
        return forSourceLines("dk.developer.glass.SomeClass",
                "package dk.developer.glass;",
                "import java.util.Map;",
                "public class SomeClass {",
                "@Output(Class.class)",
                "@IncludeAll",
                "\tMap<String, Object> someMethod(Map<String, Object> map) {",
                "\t\treturn null;",
                "\t}",
                "}");
    }

    private JavaFileObject outputDestructorWithoutSpecifier() {
        return forSourceLines("dk.developer.glass.SomeClass",
                "package dk.developer.glass;",
                "import java.util.Map;",
                "public class SomeClass {",
                "@Output(Class.class)",
                "\tMap<String, Object> someMethod(Map<String, Object> map) {}",
                "}");
    }

    private JavaFileObject outputDestructorWithSeveralSpecifiers() {
        return forSourceLines("dk.developer.glass.SomeClass",
                "package dk.developer.glass;",
                "import java.util.Map;",
                "public class SomeClass {",
                "@Output(Class.class)",
                "@IncludeAll",
                "@ExcludeAll",
                "\tMap<String, Object> someMethod(Map<String, Object> map) {}",
                "}");
    }
}
