package dk.developer.glass;

import java.util.Map;

public class TestObjects {
    static class EmptyObject {}

    static class SimpleObject {
        @As("name") @Default String firstName;
        String lastName;
        @As("age") @Default int age;

        SimpleObject(String firstName, String lastName, int age) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
        }

        @Output(Database.class)
        @Include({"firstName", "age"})
        Map<String, Object> output(Map<String, Object> map) {
            map.put("name", firstName + " " + lastName);
            return map;
        }

        @Output(IncludeAll.class)
        @IncludeAll
        Map<String, Object> method(Map<String, Object> map) {
            return map;
        }

        @Output(ExcludeAll.class)
        @ExcludeAll
        Map<String, Object> someMethod(Map<String, Object> map) {
            return map;
        }

        @Output(Exclude.class)
        @Exclude({"firstName", "age"})
        Map<String, Object> otherMethod(Map<String, Object> map) {
            return map;
        }
    }

    @Shard
    static class StringContainer {
        final @As("name") @Default String firstName;

        StringContainer(String firstName) {
            this.firstName = firstName;
        }

        @Output(String.class)
        @Include("firstName")
        Map<String, Object> output(Map<String, Object> map) {
            return map;
        }
    }

    static class ShardContainer {
        @As("value") StringContainer container;

        ShardContainer(StringContainer container) {
            this.container = container;
        }

        @Output(String.class)
        @IncludeAll
        Map<String, Object> output(Map<String, Object> map) {
            return map;
        }
    }

    static class Database {}

    @Shard
    static class Fields {
        Fields justShard;
        @Default Fields defaultShard;
    }

    @Shard
    static class DefaultContainer {
        // TODO: Should test with a default-field that is Jackson-annotated
        final @As("name") @Default String firstName;
        final @As("age") @Default int age;

        DefaultContainer(String firstName, int age) {
            this.firstName = firstName;
            this.age = age;
        }

        @Output(Database.class)
        @Include({"firstName", "age"})
        Map<String, Object> output(Map<String, Object> map) {
            return map;
        }
    }

    static class DuplicateJsonFields {
        @As("name") String firstName;
        @As("name") String lastName;
        @As("age") int age;
    }

    static class DuplicateSpecifiers {
        @As("first") String firstName;
        @As("last") String lastName;
        @As("age") int age;

        @Output(String.class)
        @Include({"first", "first"})
        Map<String, Object> includeStuff(Map<String, Object> representation) {
            return representation;
        }

        @Output(Integer.class)
        @Exclude({"last", "last"})
        Map<String, Object> excludeStuff(Map<String, Object> representation) {
            return representation;
        }
    }

    static class NoFieldsSpecified {
        @As("first") String firstName;
        @As("last") String lastName;
        @As("age") int age;

        @Output(String.class)
        @Include({})
        Map<String, Object> includeStuff(Map<String, Object> representation) {
            return representation;
        }

        @Output(Integer.class)
        @Exclude({})
        Map<String, Object> excludeStuff(Map<String, Object> representation) {
            return representation;
        }
    }
}
