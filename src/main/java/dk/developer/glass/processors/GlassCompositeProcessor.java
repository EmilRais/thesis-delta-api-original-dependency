package dk.developer.glass.processors;

import dk.developer.glass.*;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static java.lang.String.format;
import static java.util.stream.Collectors.toSet;
import static javax.lang.model.SourceVersion.RELEASE_8;
import static javax.lang.model.element.ElementKind.PARAMETER;
import static javax.tools.Diagnostic.Kind.ERROR;

@SupportedSourceVersion(RELEASE_8)
public class GlassCompositeProcessor extends AbstractProcessor {
    private final Map<Class<? extends Annotation>, AbstractProcessor> processors = new HashMap<>();
    private Messager messager;

    public GlassCompositeProcessor() {
        processors.put(As.class, new GlassAsProcessor());
        processors.put(Bind.class, new GlassBindProcessor());
        processors.put(Default.class, new GlassDefaultProcessor());
        processors.put(Input.class, new GlassInputProcessor());
        processors.put(Output.class, new GlassOutputProcessor());
        processors.put(Shard.class, new GlassShardProcessor());

        GlassSpecifierProcessor specifierProcessor = new GlassSpecifierProcessor();
        processors.put(IncludeAll.class, specifierProcessor);
        processors.put(ExcludeAll.class, specifierProcessor);
        processors.put(Include.class, specifierProcessor);
        processors.put(Exclude.class, specifierProcessor);
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();

        Set<AbstractProcessor> distinctProcessors = processors.values().stream().collect(toSet());
        for (AbstractProcessor processor : distinctProcessors) {
            processor.init(processingEnv);
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedAnnotations = new HashSet<>();
        Set<AbstractProcessor> distinctProcessors = processors.values().stream().collect(toSet());
        for (AbstractProcessor processor : distinctProcessors) {
            supportedAnnotations.addAll(processor.getSupportedAnnotationTypes());
        }
        return supportedAnnotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.forEach(annotation -> {
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element element : elements) {
                if ( element.getAnnotation(Shard.class) != null )
                    return;

                Element typeOfClass = element.getEnclosingElement();
                if ( element.getKind() == PARAMETER )
                    typeOfClass = typeOfClass.getEnclosingElement();

                Class<? extends Annotation> annotationType = lookupAnnotation(annotation);
                if ( typeOfClass.getAnnotation(Shard.class) == null )
                    messager.printMessage(ERROR, placementErrorMessage(annotationType), annotation);
            }
        });

        delegateToProcessors(annotations, roundEnv);
        return false;
    }

    private void delegateToProcessors(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Entry<Class<? extends Annotation>, AbstractProcessor> entry : processors.entrySet()) {
            String annotationName = entry.getKey().getName();
            Set<TypeElement> matchingAnnotations = annotations.stream()
                    .filter(annotation -> annotation.getQualifiedName().contentEquals(annotationName))
                    .collect(toSet());

            entry.getValue().process(matchingAnnotations, roundEnv);
        }
    }

    @SuppressWarnings("unchecked")
    private Class<? extends Annotation> lookupAnnotation(TypeElement annotation) {
        try {
            return (Class<? extends Annotation>) Class.forName(annotation.getQualifiedName().toString());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    static String placementErrorMessage(Class<? extends Annotation> annotation) {
        return format("@%s may only be used inside a @%s annotated type", annotation.getSimpleName(), Shard.class.getSimpleName());
    }
}
