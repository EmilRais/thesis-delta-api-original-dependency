package dk.developer.glass;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static dk.developer.glass.TestObjects.*;
import static dk.developer.testing.Truth.ASSERT;
import static dk.developer.utility.Convenience.list;
import static dk.developer.utility.Converter.converter;
import static java.lang.String.format;

public class GlassTest {
    private Glass glass;

    @BeforeMethod
    public void setUp() throws Exception {
        glass = new Glass(converter());
    }

    @Test
    public void shouldFindZeroFields() throws Exception {
        Iterable<GlassField> fields = glass.findFields(new EmptyObject());
        ASSERT.that(fields).isEmpty();
    }

    @Test
    public void shouldFindTwoFields() throws Exception {
        SimpleObject object = new SimpleObject("Mark", "Johnson", 18);
        Iterable<GlassField> fields = glass.findFields(object);
        ASSERT.that(fields).containsExactly(
                GlassField.of(SimpleObject.class.getDeclaredField("firstName")).in(object),
                GlassField.of(SimpleObject.class.getDeclaredField("age")).in(object)
        );
    }

    @Test
    public void shouldOutputNameAndAge() throws Exception {
        Object object = new SimpleObject("Lars", "Hansen", 38);
        String json = glass.toJson(object).in(Database.class);
        ASSERT.that(json).isEqualTo("{\"name\": \"Lars Hansen\", \"age\": 38}");
    }

    @Test
    public void shouldOutputIntermediateRepresentation() throws Exception {
        Object object = new SimpleObject("Lars", "Hansen", 38);
        Map<String, Object> representation = glass.toIntermediateRepresentation(object).in(Database.class);
        ASSERT.that(representation).containsEntry("name", "Lars Hansen");
        ASSERT.that(representation).containsEntry("age", 38);
    }

    @Test
    public void shouldBeAbleToConvertNullValues() throws Exception {
        Object object = new StringContainer(null);
        Field field = StringContainer.class.getDeclaredField("firstName");
        Set<GlassField> fields = new HashSet<>(list(GlassField.of(field).in(object)));

        Map<String, Object> representation = glass.represent(fields).of(object).in(String.class);
        ASSERT.that(representation).containsEntry("name", null);
    }

    @Test
    public void shouldBeAbleToConvertDefaultValues() throws Exception {
        DefaultContainer container = new DefaultContainer("Mark", 20);
        String json = glass.toJson(container).in(Database.class);
        ASSERT.that(json).isEqualTo(format("{\"name\": \"%s\", \"age\": %s}", "Mark", 20));
    }

    @Test
    public void shouldBeAbleToOutputString() throws Exception {
        Object object = new StringContainer("Lars");
        Field field = StringContainer.class.getDeclaredField("firstName");
        Set<GlassField> fields = new HashSet<>(list(GlassField.of(field).in(object)));

        Map<String, Object> representation = glass.represent(fields).of(object).in(String.class);
        ASSERT.that(representation).containsEntry("name", "Lars");
    }

    @Test
    public void shouldBeAbleToOutputShards() throws Exception {
        Object object = new ShardContainer(new StringContainer("Lars"));
        Field field = ShardContainer.class.getDeclaredField("container");
        Set<GlassField> fields = new HashSet<>(list(GlassField.of(field).in(object)));

        Map<String, Object> representation = glass.represent(fields).of(object).in(String.class);
        ASSERT.that(representation).containsKey("value");
        ASSERT.that((Map<String, Object>) representation.get("value")).containsEntry("name", "Lars");
    }

    @Test
    public void shouldBeAbleToSerialiseIntermediateRepresentation() throws Exception {
        Map<String, Object> representation = new HashMap<>();
        representation.put("year", "1992");
        representation.put("name", null);
        representation.put("age", 3);
        String result = glass.serialise(representation);
        ASSERT.that(result).isEqualTo("{\"year\": \"1992\", \"name\": null, \"age\": 3}");
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
          expectedExceptionsMessageRegExp = "DuplicateJsonFields has several fields with the same json name")
    public void shouldReportErrorIfSerialisingTypeWithDuplicateJsonFields() throws Exception {
        glass.toJson(new DuplicateJsonFields()).in(Object.class);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
          expectedExceptionsMessageRegExp = "DuplicateJsonFields has several fields with the same json name")
    public void shouldReportErrorIfDeserialisingTypeWithDuplicateJsonfields() throws Exception {
        glass.create(DuplicateJsonFields.class).from("").in(Object.class);
    }
}