package dk.developer.glass.processors;

import dk.developer.glass.As;
import dk.developer.glass.Bind;
import dk.developer.glass.Input;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import java.util.Set;

import static java.lang.String.format;
import static java.util.stream.Collectors.toSet;
import static javax.lang.model.SourceVersion.RELEASE_8;
import static javax.lang.model.element.ElementKind.*;
import static javax.tools.Diagnostic.Kind.ERROR;

@SupportedSourceVersion(RELEASE_8)
public class GlassBindProcessor extends AbstractGlassProcessor<Bind> {
    private static final Class<Bind> BIND = Bind.class;

    public GlassBindProcessor() {
        super(BIND);
    }

    @Override
    protected void handleEach(Element element, Bind annotation) {
        Element parentMethod = element.getEnclosingElement();
        if ( parentMethod.getAnnotation(Input.class) == null )
            messager.printMessage(ERROR, placementErrorMessage(), element);

        String value = annotation.value();
        if ( value.isEmpty() )
            messager.printMessage(ERROR, emptyValueErrorMessage(), element);

        Element constructor = parentMethod;
        Element type = constructor.getEnclosingElement();
        Set<String> fieldNames = type.getEnclosedElements().stream()
                .filter(enclosedElement -> enclosedElement.getKind() == FIELD)
                .filter(field -> field.getAnnotation(As.class) != null)
                .map(field -> field.getSimpleName().toString())
                .collect(toSet());

        if ( !fieldNames.contains(value) )
            messager.printMessage(ERROR, notBoundErrorMessage(), element);
    }

    static String emptyValueErrorMessage() {
        return format("@%s may not contain the empty string", Bind.class.getSimpleName());
    }

    static String notBoundErrorMessage() {
        return format("@%s has to bind to an @%s annotated field", Bind.class.getSimpleName(), As.class.getSimpleName());
    }

    static String placementErrorMessage() {
        return format("@%s may only annotate parameters of an @%s constructor", Bind.class.getSimpleName(), Input.class.getSimpleName());
    }
}
