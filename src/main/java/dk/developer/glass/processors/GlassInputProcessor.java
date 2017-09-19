package dk.developer.glass.processors;

import dk.developer.glass.Bind;
import dk.developer.glass.Input;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.SimpleElementVisitor8;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static javax.tools.Diagnostic.Kind.ERROR;

public class GlassInputProcessor extends AbstractGlassProcessor<Input> {
    private static final Class<Input> INPUT = Input.class;

    public GlassInputProcessor() {
        super(INPUT);
    }

    @Override
    protected void handleEach(Element element, Input annotation) {
        List<? extends VariableElement> parameters = extractParameters(element);

        if ( parameters.isEmpty() )
            messager.printMessage(ERROR, noParametersErrorMessage(), element);

        List<Bind> parameterAnnotations = parameters.stream()
                .map(parameter -> parameter.getAnnotation(Bind.class))
                .filter(bind -> bind != null)
                .collect(toList());
        if ( parameters.size() > parameterAnnotations.size() )
            messager.printMessage(ERROR, parametersNotBoundErrorMessage(), element);

        Set<String> seenValues = new HashSet<>();
        for (Bind bind : parameterAnnotations) {
            String value = bind.value();
            if ( seenValues.contains(value) )
                messager.printMessage(ERROR, duplicateValueErrorMessage());
            seenValues.add(value);
        }
    }

    private List<? extends VariableElement> extractParameters(Element element) {
        class Visitor extends SimpleElementVisitor8<ExecutableElement, Void> {
            @Override
            public ExecutableElement visitExecutable(ExecutableElement e, Void aVoid) {
                return e;
            }
        }
        ExecutableElement method = new Visitor().visit(element);
        return method.getParameters();
    }

    static String noParametersErrorMessage() {
        return format("@%s may not annotate no argument constructor", INPUT.getSimpleName());
    }

    static String parametersNotBoundErrorMessage() {
        return format("All parameters of an @%s constructor has to be annotated @%s", INPUT.getSimpleName(), Bind.class.getSimpleName());
    }

    static String duplicateValueErrorMessage() {
        return format("@%s may not contain duplicated values", Bind.class.getSimpleName());
    }
}
