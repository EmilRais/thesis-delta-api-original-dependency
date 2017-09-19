package dk.developer.glass.processors;

import dk.developer.glass.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.lang.model.util.SimpleTypeVisitor8;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static dk.developer.utility.Convenience.list;
import static java.lang.String.format;
import static java.util.stream.Collectors.toSet;
import static javax.tools.Diagnostic.Kind.ERROR;

public class GlassOutputProcessor extends AbstractGlassProcessor<Output> {
    private static final Class<Output> OUTPUT = Output.class;
    private Elements elementUtils;
    private Types typeUtils;

    public GlassOutputProcessor() {
        super(OUTPUT);
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
    }

    @Override
    protected void handleEach(Element element, Output annotation) {
        ExecutableElement method = convertToMethod(element);
        List<? extends VariableElement> parameters = method.getParameters();
        if ( parameters.size() != 1 || !isMapFromStringToObject(parameters.get(0).asType()) )
            messager.printMessage(ERROR, wrongParameterErrorMessage(), element);

        TypeMirror returnType = method.getReturnType();
        if ( !isMapFromStringToObject(returnType) )
            messager.printMessage(ERROR, wrongReturnTypeErrorMessage(), element);

        Set<Annotation> specifiers = extractSpecifiers(element);
        if ( specifiers.size() != 1 )
            messager.printMessage(ERROR, wrongNumberOfSpecifiersErrorMessage(), element);
    }

    private boolean isMapFromStringToObject(TypeMirror returnType) {
        class Visitor extends SimpleTypeVisitor8<DeclaredType, Void> {
            @Override
            public DeclaredType visitDeclared(DeclaredType t, Void aVoid) {
                return t;
            }
        }
        DeclaredType type = new Visitor().visit(returnType);
        if ( type == null )
            return false;

        TypeMirror mapType = elementUtils.getTypeElement(Map.class.getCanonicalName()).asType();
        if ( !typeUtils.isSameType(typeUtils.erasure(type), typeUtils.erasure(mapType)) )
            return false;

        List<? extends TypeMirror> typeArguments = type.getTypeArguments();
        if ( typeArguments.size() != 2 )
            return false;

        TypeMirror stringType = elementUtils.getTypeElement(String.class.getCanonicalName()).asType();
        if ( !typeUtils.isSameType(typeArguments.get(0), stringType) )
            return false;


        TypeMirror objectType = elementUtils.getTypeElement(Object.class.getCanonicalName()).asType();
        if ( !typeUtils.isSameType(typeArguments.get(1), objectType) )
            return false;

        return true;
    }

    private Set<Annotation> extractSpecifiers(Element element) {
        return list(IncludeAll.class, ExcludeAll.class, Include.class, Exclude.class).stream()
                .map(element::getAnnotation)
                .filter(annotation -> annotation != null)
                .collect(toSet());
    }

    private ExecutableElement convertToMethod(Element element) {
        class Visitor extends SimpleElementVisitor8<ExecutableElement, Void> {
            @Override
            public ExecutableElement visitExecutable(ExecutableElement e, Void aVoid) {
                return e;
            }
        }
        return new Visitor().visit(element);
    }

    static String wrongParameterErrorMessage() {
        return format("@%s destructor should have Map<String, Object> as only parameter", OUTPUT.getSimpleName());
    }

    static String wrongReturnTypeErrorMessage() {
        return format("@%s destructor should have Map<String, Object> as return type", OUTPUT.getSimpleName());
    }

    static String wrongNumberOfSpecifiersErrorMessage() {
        return format("@%s destructor should have exactly one specifier: @%s, @%s, @%s, or @%s", OUTPUT.getSimpleName(), IncludeAll.class
                .getSimpleName(), ExcludeAll.class.getSimpleName(), Include.class.getSimpleName(), Exclude.class.getSimpleName());
    }
}
