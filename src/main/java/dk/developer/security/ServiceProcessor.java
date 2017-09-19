package dk.developer.security;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.ws.rs.Path;
import java.util.HashSet;
import java.util.Set;

import static java.lang.String.format;
import static java.util.stream.Collectors.toSet;
import static javax.lang.model.SourceVersion.RELEASE_8;
import static javax.lang.model.element.ElementKind.*;
import static javax.tools.Diagnostic.Kind.ERROR;

@SupportedSourceVersion(RELEASE_8)
public class ServiceProcessor extends AbstractProcessor {
    private static final Class<Security> SECURITY = Security.class;
    private static final Class<Path> PATH = Path.class;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new HashSet<>();
        annotations.add(PATH.getName());
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.stream()
                .map(roundEnv::getElementsAnnotatedWith)
                .flatMap(Set::stream)
                .filter(element -> element.getKind() == CLASS)
                .map(this::getResources)
                .flatMap(Set::stream)
                .filter(resource -> resource.getAnnotation(SECURITY) == null)
                .forEach(insecureResource -> messager.printMessage(ERROR, insecureErrorMessage(), insecureResource));
        return false;
    }

    private Set<Element> getResources(Element element) {
        return element.getEnclosedElements().stream()
                .filter(enclosedElement -> enclosedElement.getKind() == METHOD)
                .filter(method -> method.getAnnotation(Path.class) != null)
                .collect(toSet());
    }

    static String insecureErrorMessage() {
        return format("No @%s annotation is present on resource", SECURITY.getSimpleName());
    }


}
