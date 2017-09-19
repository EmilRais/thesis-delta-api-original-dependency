package dk.developer.glass.processors;

import dk.developer.glass.As;
import dk.developer.glass.Default;

import javax.lang.model.element.Element;

import static java.lang.String.format;
import static javax.tools.Diagnostic.Kind.ERROR;

public class GlassDefaultProcessor extends AbstractGlassProcessor<Default> {
    private static final Class<Default> DEFAULT = Default.class;

    public GlassDefaultProcessor() {
        super(DEFAULT);
    }

    @Override
    protected void handleEach(Element element, Default annotation) {
        if ( element.getAnnotation(As.class) == null )
            messager.printMessage(ERROR, placementErrorMessage(), element);
    }

    static String placementErrorMessage() {
        return format("@%s may only annotate @%s annotated fields", DEFAULT, As.class.getSimpleName());
    }
}
