package dk.developer.glass.processors;

import com.google.testing.compile.CompileTester;
import dk.developer.glass.As;
import dk.developer.glass.Include;
import org.testng.annotations.Test;

import javax.tools.JavaFileObject;

import static dk.developer.testing.Truth.ASSERT;
import static com.google.testing.compile.JavaFileObjects.forSourceLines;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class GlassCompositeProcessorTest {
    @Test
    public void shouldRejectAnnotationsOutsideShard() throws Exception {
        expectThat(asOutsideShard()).failsToCompile()
                .withErrorContaining(GlassCompositeProcessor.placementErrorMessage(As.class));
    }

    @Test
    public void shouldAcceptNonNestedShard() throws Exception {
        expectThat(shardOutsideShard()).compilesWithoutError();
    }

    @Test
    public void shouldAcceptComplexShard() throws Exception {
        expectThat(validComplexShard()).compilesWithoutError();
    }

    @Test
    public void shouldRejectShardWithEmptyAsField() throws Exception {
        expectThat(complexShardWithEmptyAsField())
                .failsToCompile().withErrorContaining(GlassAsProcessor.emptyValueErrorMessage());
    }

    @Test
    public void shouldRejectUnboundInputParameter() throws Exception {
        expectThat(complexShardWithUnboundInputParameter())
                .failsToCompile().withErrorContaining(GlassBindProcessor.notBoundErrorMessage());
    }

    @Test
    public void shouldRejectDefaultOnNonAsField() throws Exception {
        expectThat(complexShardWithMalplacedDefaultField())
                .failsToCompile().withErrorContaining(GlassDefaultProcessor.placementErrorMessage());
    }

    @Test
    public void shouldRejectInputConstructorWithDuplicateValues() throws Exception {
        expectThat(complexShardWithDuplicateInputParameters())
                .failsToCompile().withErrorContaining(GlassInputProcessor.duplicateValueErrorMessage());
    }

    @Test
    public void shouldRejectOutputDestructorWithoutSpecifier() throws Exception {
        expectThat(complexShardWithOutputDestructorWithoutSpecifier())
                .failsToCompile().withErrorContaining(GlassOutputProcessor.wrongNumberOfSpecifiersErrorMessage());
    }

    @Test
    public void shouldRejectEmptySpecificSpecifier() throws Exception {
        expectThat(complexShardWithEmptySpecifier())
                .failsToCompile().withErrorContaining(GlassSpecifierProcessor.noValueErrorMessage(Include.class));
    }

    private CompileTester expectThat(JavaFileObject target) {
        return ASSERT.about(javaSource()).that(target).processedWith(new GlassCompositeProcessor());
    }

    private JavaFileObject asOutsideShard() {
        return forSourceLines("dk.developer.glass.SomeClass",
                "package dk.developer.glass;",
                "public class SomeClass {",
                "\t@As(\"name\") String name;",
                "}");
    }

    private JavaFileObject shardOutsideShard() {
        return forSourceLines("dk.developer.glass.SomeClass",
                "package dk.developer.glass;",
                "public class SomeClass {",
                "\t@Shard",
                "\tstatic class SomeOtherClass {}",
                "}");
    }

    private JavaFileObject validComplexShard() {
        return forSourceLines("dk.developer.glass.SomeClass",
                "package dk.developer.glass;",
                "import java.util.Map;",
                "@Shard",
                "class SomeClass {",
                "\t@As(\"name\") String firstName;",
                "\tString lastName;",
                "\t@As(\"year\") int age;",
                "\t@As(\"networth\") @Default double money;",

                "\t@Input(String.class) SomeClass(@Bind(\"firstName\") String name, @Bind(\"age\") int age) {}",

                "\t@Input(Integer.class) SomeClass(@Bind(\"firstName\") String name, @Bind(\"age\") int age, @Bind(\"money\") int money) {}",

                "\t@Output(String.class)",
                "\t@Include({\"firstName\", \"age\"})",
                "\tMap<String, Object> stringContext(Map<String, Object> map) { return map; }",

                "\t@Output(Integer.class)",
                "\t@Exclude(\"money\")",
                "\tMap<String, Object> integerContext(Map<String, Object> map) { return map; }",
                "\t}");
    }

    private JavaFileObject complexShardWithEmptyAsField() {
        return forSourceLines("dk.developer.glass.SomeClass",
                "package dk.developer.glass;",
                "import java.util.Map;",
                "\t@Shard",
                "\tclass SomeClass {",
                "@As(\"\") String firstName;",
                "String lastName;",
                "@As(\"year\") int age;",
                "@As(\"networth\") @Default double money;",

                "\t\t@Input(String.class) SomeClass(@Bind(\"firstName\") String name, @Bind(\"age\") int age) {}",

                "\t\t@Input(Integer.class) SomeClass(@Bind(\"firstName\") String name, @Bind(\"age\") int age, @Bind(\"money\") int money) {}",

                "\t\t@Output(String.class)",
                "\t\t@Include({\"firstName\", \"age\"})",
                "\t\tMap<String, Object> stringContext(Map<String, Object> map) { return map; }",

                "\t\t@Output(Integer.class)",
                "\t\t@Exclude(\"money\")",
                "\t\tMap<String, Object> integerContext(Map<String, Object> map) { return map; }",
                "\t}");
    }

    private JavaFileObject complexShardWithUnboundInputParameter() {
        return forSourceLines("dk.developer.glass.SomeClass",
                "package dk.developer.glass;",
                "import java.util.Map;",
                "\t@Shard",
                "\tclass SomeClass {",
                "@As(\"name\") String firstName;",
                "String lastName;",
                "@As(\"year\") int age;",
                "@As(\"networth\") @Default double money;",

                "\t\t@Input(String.class) SomeClass(@Bind(\"name\") String name, @Bind(\"age\") int age) {}",

                "\t\t@Input(Integer.class) SomeClass(@Bind(\"firstName\") String name, @Bind(\"age\") int age, @Bind(\"money\") int money) {}",

                "\t\t@Output(String.class)",
                "\t\t@Include({\"firstName\", \"age\"})",
                "\t\tMap<String, Object> stringContext(Map<String, Object> map) { return map; }",

                "\t\t@Output(Integer.class)",
                "\t\t@Exclude(\"money\")",
                "\t\tMap<String, Object> integerContext(Map<String, Object> map) { return map; }",
                "\t}");
    }

    private JavaFileObject complexShardWithMalplacedDefaultField() {
        return forSourceLines("dk.developer.glass.SomeClass",
                "package dk.developer.glass;",
                "import java.util.Map;",
                "\t@Shard",
                "\tclass SomeClass {",
                "@As(\"name\") String firstName;",
                "@Default String lastName;",
                "@As(\"year\") int age;",
                "@As(\"networth\") @Default double money;",

                "\t\t@Input(String.class) SomeClass(@Bind(\"firstName\") String name, @Bind(\"age\") int age) {}",

                "\t\t@Input(Integer.class) SomeClass(@Bind(\"firstName\") String name, @Bind(\"age\") int age, @Bind(\"money\") int money) {}",

                "\t\t@Output(String.class)",
                "\t\t@Include({\"firstName\", \"age\"})",
                "\t\tMap<String, Object> stringContext(Map<String, Object> map) { return map; }",

                "\t\t@Output(Integer.class)",
                "\t\t@Exclude(\"money\")",
                "\t\tMap<String, Object> integerContext(Map<String, Object> map) { return map; }",
                "\t}");
    }

    private JavaFileObject complexShardWithDuplicateInputParameters() {
        return forSourceLines("dk.developer.glass.SomeClass",
                "package dk.developer.glass;",
                "import java.util.Map;",
                "@Shard",
                "class SomeClass {",
                "\t@As(\"name\") String firstName;",
                "\tString lastName;",
                "\t@As(\"year\") int age;",
                "\t@As(\"networth\") @Default double money;",

                "\t@Input(String.class) SomeClass(@Bind(\"firstName\") String name, @Bind(\"age\") int age) {}",

                "\t@Input(Integer.class) SomeClass(@Bind(\"firstName\") String name, @Bind(\"age\") int age, @Bind(\"age\") int money) {}",

                "\t@Output(String.class)",
                "\t@Include({\"firstName\", \"age\"})",
                "\tMap<String, Object> stringContext(Map<String, Object> map) { return map; }",

                "\t@Output(Integer.class)",
                "\t@Exclude(\"money\")",
                "\tMap<String, Object> integerContext(Map<String, Object> map) { return map; }",
                "}");
    }

    private JavaFileObject complexShardWithOutputDestructorWithoutSpecifier() {
        return forSourceLines("dk.developer.glass.SomeClass",
                "package dk.developer.glass;",
                "import java.util.Map;",
                "@Shard",
                "class SomeClass {",
                "\t@As(\"name\") String firstName;",
                "\tString lastName;",
                "\t@As(\"year\") int age;",
                "\t@As(\"networth\") @Default double money;",

                "\t@Input(String.class) SomeClass(@Bind(\"firstName\") String name, @Bind(\"age\") int age) {}",

                "\t@Input(Integer.class) SomeClass(@Bind(\"firstName\") String name, @Bind(\"age\") int age, @Bind(\"money\") int money) {}",

                "\t@Output(String.class)",
                "\t@Include({\"firstName\", \"age\"})",
                "\tMap<String, Object> stringContext(Map<String, Object> map) { return map; }",

                "\t@Output(Integer.class)",
                "\tMap<String, Object> integerContext(Map<String, Object> map) { return map; }",
                "}");
    }

    private JavaFileObject complexShardWithEmptySpecifier() {
        return forSourceLines("dk.developer.glass.SomeClass",
                "package dk.developer.glass;",
                "import java.util.Map;",
                "@Shard",
                "class SomeClass {",
                "\t@As(\"name\") String firstName;",
                "\tString lastName;",
                "\t@As(\"year\") int age;",
                "\t@As(\"networth\") @Default double money;",

                "\t@Input(String.class) SomeClass(@Bind(\"firstName\") String name, @Bind(\"age\") int age) {}",

                "\t@Input(Integer.class) SomeClass(@Bind(\"firstName\") String name, @Bind(\"age\") int age, @Bind(\"money\") int money) {}",

                "\t@Output(String.class)",
                "\t@Include({})",
                "\tMap<String, Object> stringContext(Map<String, Object> map) { return map; }",

                "\t@Output(Integer.class)",
                "\t@Exclude(\"money\")",
                "\tMap<String, Object> integerContext(Map<String, Object> map) { return map; }",
                "}");

    }
}