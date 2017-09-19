package dk.developer.glass;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static dk.developer.utility.Convenience.list;
import static java.util.stream.Collectors.toSet;

public interface Specifier {
    Set<GlassField> filter(Set<GlassField> fields);

    class IncludeAllSpecifier implements Specifier {
        @Override
        public Set<GlassField> filter(Set<GlassField> fields) {
            return fields;
        }
    }

    class ExcludeAllSpecifier implements Specifier {
        @Override
        public Set<GlassField> filter(Set<GlassField> fields) {
            return new HashSet<>();
        }
    }

    class IncludeSpecifier implements Specifier {
        private List<String> fieldNames;

        IncludeSpecifier(String... fieldNames) {
            this.fieldNames = list(fieldNames);
        }

        public List<String> getFieldNames() {
            return fieldNames;
        }

        @Override
        public Set<GlassField> filter(Set<GlassField> fields) {
            return fields.stream()
                    .filter(field -> fieldNames.contains(field.getFieldName()))
                    .collect(toSet());
        }
    }

    class ExcludeSpecifier implements Specifier {
        private List<String> fieldNames;

        ExcludeSpecifier(String... fieldNames) {
            this.fieldNames = list(fieldNames);
        }

        public List<String> getFieldNames() {
            return fieldNames;
        }

        @Override
        public Set<GlassField> filter(Set<GlassField> fields) {
            return fields.stream()
                    .filter(field -> !fieldNames.contains(field.getFieldName()))
                    .collect(toSet());
        }
    }
}
