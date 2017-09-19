package dk.developer.glass.processors;

import dk.developer.glass.*;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static dk.developer.utility.Convenience.list;
import static java.lang.String.format;
import static java.util.stream.Collectors.toSet;
import static javax.lang.model.SourceVersion.RELEASE_8;
import static javax.lang.model.element.ElementKind.FIELD;
import static javax.tools.Diagnostic.Kind.ERROR;

@SupportedSourceVersion(RELEASE_8)
public class GlassSpecifierProcessor extends AbstractProcessor {
    private static final List<Class<? extends Annotation>> SPECIFIERS = list(IncludeAll.class, ExcludeAll.class, Include.class, Exclude
            .class);
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return SPECIFIERS.stream()
                .map(Class::getName)
                .collect(toSet());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.stream()
                .map(roundEnv::getElementsAnnotatedWith)
                .flatMap(Set::stream)
                .forEach(element -> {
                    Output outputAnnotation = element.getAnnotation(Output.class);
                    Annotation specifierAnnotation = extractSpecifier(element);
                    Class<? extends Annotation> type = specifierAnnotation.annotationType();

                    if ( outputAnnotation == null )
                        messager.printMessage(ERROR, placementErrorMessage(type), element);

                    if ( type == Include.class || type == Exclude.class )
                        handleSpecificSpecifier(element, specifierAnnotation);
                });
        return false;
    }

    private void handleSpecificSpecifier(Element element, Annotation annotation) {
        Class<? extends Annotation> type = annotation.annotationType();

        List<String> fieldNames = extractValue(annotation);


        boolean noFieldNames = fieldNames.isEmpty();
        boolean invalidFieldName = fieldNames.stream()
                .filter(fieldName -> fieldName.trim().isEmpty())
                .findAny().isPresent();

        if ( noFieldNames || invalidFieldName )
            messager.printMessage(ERROR, noValueErrorMessage(type), element);

        Set<String> seenValues = new HashSet<>();
        fieldNames.forEach(fieldName -> {
            if ( seenValues.contains(fieldName) )
                messager.printMessage(ERROR, duplicateValueErrorMessage(type), element);
            seenValues.add(fieldName);
        });

        Set<String> actualFieldNames = element.getEnclosingElement().getEnclosedElements().stream()
                .filter(enclosedElement -> enclosedElement.getKind() == FIELD)
                .filter(field -> field.getAnnotation(As.class) != null)
                .map(field -> field.getSimpleName().toString())
                .collect(toSet());

        boolean containsValueNotBoundToField = fieldNames.stream()
                .filter(fieldName -> !actualFieldNames.contains(fieldName))
                .findAny().isPresent();
        if ( containsValueNotBoundToField )
            messager.printMessage(ERROR, notBoundErrorMessage(type), element);
    }

    private List<String> extractValue(Annotation annotation) {
        if ( annotation instanceof Include )
            return list(((Include) annotation).value());

        return list(((Exclude) annotation).value());
    }

    private Annotation extractSpecifier(Element element) {
        return SPECIFIERS.stream()
                .map(element::getAnnotation)
                .filter(annotation -> annotation != null)
                .findAny().get();
    }

    static String placementErrorMessage(Class<? extends Annotation> annotation) {
        return format("@%s may only annotate an @%s destructor", annotation.getSimpleName(), Output.class.getSimpleName());
    }

    static String noValueErrorMessage(Class<? extends Annotation> annotation) {
        return format("@%s has to specify a value", annotation.getSimpleName());
    }

    static String duplicateValueErrorMessage(Class<? extends Annotation> annotation) {
        return format("@%s may not contain duplicated values", annotation.getSimpleName());
    }

    static String notBoundErrorMessage(Class<? extends Annotation> annotation) {
        return format("@%s has to bind to @%s annotated fields", annotation.getSimpleName(), As.class.getSimpleName());
    }
}
