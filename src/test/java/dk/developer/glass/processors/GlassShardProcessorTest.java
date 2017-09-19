package dk.developer.glass.processors;

import com.google.testing.compile.CompileTester;
import dk.developer.glass.As;
import dk.developer.glass.Input;
import dk.developer.glass.Output;
import org.testng.annotations.Test;

import javax.tools.JavaFileObject;

import static dk.developer.testing.Truth.ASSERT;
import static com.google.testing.compile.JavaFileObjects.forSourceLines;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static dk.developer.glass.processors.GlassShardProcessor.*;

public class GlassShardProcessorTest {
    // TODO: Should check for no duplication amongst @Input constructors
    // TODO: Should check for no duplication amongst @Output destructors

    @Test
    public void shouldRejectAsWithDuplicateValuesInShard() throws Exception {
        expectThat(someClass("name", "name")).failsToCompile().withErrorContaining(duplicateValueErrorMessage(As.class));
    }

    @Test
    public void shouldAcceptAsWithoutDuplicateValuesInShard() throws Exception {
        expectThat(someClass("name", "age")).compilesWithoutError();
    }

    @Test
    public void shouldRejectInputConstructorsWithDuplicateValues() throws Exception {
        expectThat(inputConstructorsWithValue("String.class", "String.class"))
                .failsToCompile().withErrorContaining(duplicateValueErrorMessage(Input.class));
    }

    @Test
    public void shouldAcceptInputConstructorsWithDistinctValues() throws Exception {
        expectThat(inputConstructorsWithValue("int.class", "Integer.class")).compilesWithoutError();
    }

    @Test
    public void shouldRejectOutputDestructorsWithDuplicateValues() throws Exception {
        expectThat(outputDestructorsWithValue("Integer.class", "Integer.class"))
                .failsToCompile().withErrorContaining(duplicateValueErrorMessage(Output.class));
    }

    @Test
    public void shouldAcceptOutputDestructorsWithDistinctValues() throws Exception {
        expectThat(outputDestructorsWithValue("float.class", "double.class")).compilesWithoutError();
    }

    private CompileTester expectThat(JavaFileObject target) {
        return ASSERT.about(javaSource()).that(target).processedWith(new GlassShardProcessor());
    }

    private JavaFileObject someClass(String firstValue, String secondValue) {
        return forSourceLines("dk.developer.glass.SomeClass",
                "package dk.developer.glass;",
                "@Shard",
                "public class SomeClass {",
                "\t@As(\"" + firstValue + "\") String name;",
                "\t@As(\"" + secondValue + "\") int age;",
                "}");
    }

    private JavaFileObject inputConstructorsWithValue(String firstValue, String secondValue) {
        return forSourceLines("dk.developer.glass.SomeClass",
                "package dk.developer.glass;",
                "import java.util.Map;",
                "@Shard",
                "public class SomeClass {",
                "\t@Input(" + firstValue + ")",
                "\tSomeClass(String name) {}",
                "\t@Input(" + secondValue + ")",
                "\tSomeClass(int age) {}",
                "}");
    }

    private JavaFileObject outputDestructorsWithValue(String firstValue, String secondValue) {
        return forSourceLines("dk.developer.glass.SomeClass",
                "package dk.developer.glass;",
                "import java.util.Map;",
                "@Shard",
                "public class SomeClass {",
                "\t@Output(" + firstValue + ")",
                "\tMap<String, Object> method(Map<String, Object> map) { return null; }",
                "\t@Output(" + secondValue + ")",
                "\tMap<String, Object> someMethod(Map<String, Object> map) { return null; }",
                "}");
    }
}