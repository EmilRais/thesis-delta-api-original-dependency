package dk.developer.glass;

import dk.developer.glass.TestObjects.DuplicateSpecifiers;
import dk.developer.glass.TestObjects.NoFieldsSpecified;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static dk.developer.glass.TestObjects.Database;
import static dk.developer.glass.TestObjects.SimpleObject;
import static dk.developer.testing.Truth.ASSERT;

public class GlassDestructorTest {
    // TODO: Test that it uses Specifier to filter and return fields
    // TODO: Test that it calls the method with the given parameters on the object and returns the result

    @Test
    public void shouldFindOutputDestructor() throws Exception {
        Class<SimpleObject> type = SimpleObject.class;
        Method destructor = GlassDestructor.findOutputDestructor(type).in(Database.class);
        ASSERT.that(destructor).isEqualTo(SimpleObject.class.getDeclaredMethod("output", Map.class));
    }

    @Test
    public void shouldFindIncludeSpecifier() throws Exception {
        Specifier specifier = GlassDestructor.findSpecifier(SimpleObject.class.getDeclaredMethod("output", Map.class));
        ASSERT.that(specifier).isInstanceOf(Specifier.IncludeSpecifier.class);
        List<String> fieldNames = ((Specifier.IncludeSpecifier) specifier).getFieldNames();
        ASSERT.that(fieldNames).containsExactly("firstName", "age");
    }

    @Test
    public void shouldFindIncludeAllSpecifier() throws Exception {
        Specifier specifier = GlassDestructor.findSpecifier(SimpleObject.class.getDeclaredMethod("method", Map.class));
        ASSERT.that(specifier).isInstanceOf(Specifier.IncludeAllSpecifier.class);
    }

    @Test
    public void shouldFindExcludeSpecifier() throws Exception {
        Specifier specifier = GlassDestructor.findSpecifier(SimpleObject.class.getDeclaredMethod("otherMethod", Map.class));
        ASSERT.that(specifier).isInstanceOf(Specifier.ExcludeSpecifier.class);
        List<String> fieldNames = ((Specifier.ExcludeSpecifier) specifier).getFieldNames();
        ASSERT.that(fieldNames).containsExactly("firstName", "age");
    }

    @Test
    public void shouldFindExcludeAllSpecifier() throws Exception {
        Specifier specifier = GlassDestructor.findSpecifier(SimpleObject.class.getDeclaredMethod("someMethod", Map.class));
        ASSERT.that(specifier).isInstanceOf(Specifier.ExcludeAllSpecifier.class);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
          expectedExceptionsMessageRegExp = "@Output destructor for String found on DuplicateSpecifiers specifies the same field multiple times")
    public void shouldReportErrorIfDuplicatedValuesInIncludeSpecifier() throws Exception {
        GlassDestructor.of(new DuplicateSpecifiers()).in(String.class);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
          expectedExceptionsMessageRegExp = "@Output destructor for Integer found on DuplicateSpecifiers specifies the same field multiple times")
    public void shouldReportErrorIfDuplicatedValuesInExcludeSpecifier() throws Exception {
        GlassDestructor.of(new DuplicateSpecifiers()).in(Integer.class);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
          expectedExceptionsMessageRegExp = "@Output destructor for String found on NoFieldsSpecified specifies no fields")
    public void shouldReportErrorIfNoFieldsInIncludeSpecifier() throws Exception {
        GlassDestructor.of(new NoFieldsSpecified()).in(String.class);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
          expectedExceptionsMessageRegExp = "@Output destructor for Integer found on NoFieldsSpecified specifies no fields")
    public void shouldReportErrorIfNoFieldsInExcludeSpecifier() throws Exception {
        GlassDestructor.of(new NoFieldsSpecified()).in(Integer.class);
    }
}