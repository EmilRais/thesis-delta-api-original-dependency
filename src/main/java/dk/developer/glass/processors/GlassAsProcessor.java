package dk.developer.glass.processors;

import dk.developer.glass.As;

import javax.lang.model.element.Element;

import static java.lang.String.format;
import static javax.tools.Diagnostic.Kind.ERROR;

public class GlassAsProcessor extends AbstractGlassProcessor<As> {
    private static final Class<As> AS = As.class;

    public GlassAsProcessor() {
        super(AS);
    }

    @Override
    protected void handleEach(Element element, As annotation) {
        String value = annotation.value();

        if ( value.trim().isEmpty() )
            messager.printMessage(ERROR, emptyValueErrorMessage(), element);
    }

    static String emptyValueErrorMessage() {
        return format("@%s may not contain the empty string", AS.getSimpleName());
    }
}
