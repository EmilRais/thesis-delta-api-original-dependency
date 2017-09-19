package dk.developer.glass.processors;

import dk.developer.glass.As;
import dk.developer.glass.Input;
import dk.developer.glass.Output;
import dk.developer.glass.Shard;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;
import static java.util.stream.Collectors.toSet;
import static javax.lang.model.element.ElementKind.CONSTRUCTOR;
import static javax.lang.model.element.ElementKind.FIELD;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.tools.Diagnostic.Kind.ERROR;

public class GlassShardProcessor extends AbstractGlassProcessor<Shard> {
    private static final Class<Shard> SHARD = Shard.class;
    private Elements elementUtils;
    private Types typeUtils;

    public GlassShardProcessor() {
        super(SHARD);
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
    }

    @Override
    protected void handleEach(Element element, Shard annotation) {
        enforceNoDuplicateChildren(element, FIELD, As.class);
        enforceNoDuplicateChildren(element, CONSTRUCTOR, Input.class);
        enforceNoDuplicateChildren(element, METHOD, Output.class);
    }

    private void enforceNoDuplicateChildren(Element element, ElementKind kind, Class<? extends Annotation> annotation) {
        Set<Element> constructors = extractChildren(element, kind, annotation);

        Set<Object> seenValues = new HashSet<>();
        for (Element constructor : constructors) {
            for (AnnotationMirror annotationMirror : constructor.getAnnotationMirrors()) {
                DeclaredType type = annotationMirror.getAnnotationType();
                TypeMirror annotationType = elementUtils.getTypeElement(annotation.getCanonicalName()).asType();
                if ( !typeUtils.isSameType(type, annotationType) )
                    continue;

                Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = annotationMirror.getElementValues();
                AnnotationValue annotationValue = elementValues.values().iterator().next();
                Object value = annotationValue.getValue();

                if ( seenValues.contains(value) )
                    messager.printMessage(ERROR, duplicateValueErrorMessage(annotation), constructor, annotationMirror, annotationValue);
                seenValues.add(value);
            }
        }
    }

    private Set<Element> extractChildren(Element element, ElementKind kind, Class<? extends Annotation> annotation) {
        return element.getEnclosedElements().stream()
                .filter(enclosedElement -> enclosedElement.getKind() == kind)
                .filter(constructor -> constructor.getAnnotation(annotation) != null)
                .collect(toSet());
    }

    static String duplicateValueErrorMessage(Class<? extends Annotation> annotation) {
        return format("@%s may not contain duplicated values", annotation.getSimpleName());
    }
}
