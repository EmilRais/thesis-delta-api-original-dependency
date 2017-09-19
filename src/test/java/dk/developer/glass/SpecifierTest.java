package dk.developer.glass;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

import static dk.developer.glass.Specifier.*;
import static dk.developer.testing.Truth.ASSERT;

public class SpecifierTest {
    private Set<GlassField> fields;
    private GlassField titleField;
    private GlassField firstNameField;
    private GlassField ageField;

    @BeforeMethod
    public void setUp() throws Exception {
        titleField = new GlassField("title", null, null, null);
        firstNameField = new GlassField("firstName", null, null, null);
        ageField = new GlassField("age", null, null, null);

        fields = new HashSet<>();
        fields.add(titleField);
        fields.add(firstNameField);
        fields.add(ageField);
    }

    @Test
    public void shouldIncludeAllGivenFields() throws Exception {
        Specifier specifier = new IncludeAllSpecifier();
        Iterable<GlassField> fields = specifier.filter(this.fields);
        ASSERT.that(fields).containsAllIn(this.fields);
    }

    @Test
    public void shouldExcludeAllGivenFields() throws Exception {
        Specifier specifier = new ExcludeAllSpecifier();
        Iterable<GlassField> fields = specifier.filter(this.fields);
        ASSERT.that(fields).isEmpty();
    }

    @Test
    public void shouldIncludeSpecifiedGivenFields() throws Exception {
        Specifier specifier = new IncludeSpecifier("title", "firstName");
        Iterable<GlassField> fields = specifier.filter(this.fields);
        ASSERT.that(fields).containsExactly(titleField, firstNameField);
    }

    @Test
    public void shouldExcludeSpecifiedGivenFields() throws Exception {
        Specifier specifier = new ExcludeSpecifier("title", "firstName");
        Iterable<GlassField> fields = specifier.filter(this.fields);
        ASSERT.that(fields).containsExactly(ageField);
    }
}