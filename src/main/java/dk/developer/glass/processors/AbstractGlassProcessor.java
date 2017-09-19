package dk.developer.glass.processors;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import static javax.lang.model.SourceVersion.RELEASE_8;

@SupportedSourceVersion(RELEASE_8)
public abstract class AbstractGlassProcessor<T extends Annotation> extends AbstractProcessor {
    private final Class<T> supportedAnnotation;
    protected Messager messager;

    public AbstractGlassProcessor(Class<T> supportedAnnotation) {
        this.supportedAnnotation = supportedAnnotation;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return RELEASE_8;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedAnnotations = new HashSet<>();
        supportedAnnotations.add(supportedAnnotation.getName());
        return supportedAnnotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.stream()
                .map(roundEnv::getElementsAnnotatedWith)
                .flatMap(Set::stream)
                .forEach(element -> handleEach(element, element.getAnnotation(supportedAnnotation)));
        return false;
    }

    protected abstract void handleEach(Element element, T annotation);
}
