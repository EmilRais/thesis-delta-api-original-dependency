package dk.developer.security;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.ws.rs.Path;
import java.util.HashSet;
import java.util.Set;

import static java.lang.String.format;
import static javax.lang.model.SourceVersion.RELEASE_8;
import static javax.tools.Diagnostic.Kind.ERROR;

@SupportedSourceVersion(RELEASE_8)
public class SecurityProcessor extends AbstractProcessor {
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
        annotations.add(SECURITY.getName());
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.stream()
                .map(roundEnv::getElementsAnnotatedWith)
                .flatMap(Set::stream)
                .forEach(this::rejectIfNotInServiceOrOnResource);
        return false;
    }

    private void rejectIfNotInServiceOrOnResource(Element element) {
        Element enclosingElement = element.getEnclosingElement();
        boolean isInService = enclosingElement.getAnnotation(Path.class) != null;
        boolean isOnResource = element.getAnnotation(PATH) != null;

        if ( !isInService || !isOnResource)
            messager.printMessage(ERROR, invalidPlacement(), element);
    }

    static String invalidPlacement() {
        return format("@%s can only be used on resources in services", SECURITY.getSimpleName());
    }
}
