package org.graphwalker.core.annotations;

import org.graphwalker.core.Bundle;
import org.graphwalker.core.machine.Machine;
import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.Element;
import org.graphwalker.core.model.Model;
import org.graphwalker.core.model.Vertex;
import org.graphwalker.core.utils.Reflection;
import org.graphwalker.core.utils.Resource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class AnnotationProcessor {

    private static final Set<Class<? extends Annotation>> mySupportedAnnotations = new HashSet<Class<? extends Annotation>>() {{
        add(AfterElement.class);
        add(AfterGroup.class);
        add(AfterModel.class);
        add(BeforeElement.class);
        add(BeforeGroup.class);
        add(BeforeModel.class);
    }};

    public AnnotationProcessor() {
    }

    public void process(Class<? extends Annotation> annotation, Machine machine, Model model, Element element) {
        if (getSupportedAnnotations().contains(annotation)) {
            if (model.hasImplementation()) {
                try {
                    Object implementation = model.getImplementation();
                    for (Method method : implementation.getClass().getMethods()) {
                        if (method.isAnnotationPresent(annotation)) {
                            if (hasNoReturnType(method)) {
                                if (hasNoParameter(method)) {
                                    Reflection.execute(implementation, method);
                                } else if (hasOneParameter(method)) {
                                    if (isElementAnnotation(annotation) && isElementArgument(method)) {
                                        Reflection.execute(implementation, method, element);
                                    } else if (isElementAnnotation(annotation) && isVertexArgument(method) && isVertex(element)) {
                                        Reflection.execute(implementation, method, (Vertex) element);
                                    } else if (isElementAnnotation(annotation) && isEdgeArgument(method) && isEdge(element)) {
                                        Reflection.execute(implementation, method, (Edge) element);
                                    } else if (isModelAnnotation(annotation) && isModelArgument(method)) {
                                        Reflection.execute(implementation, method, model);
                                    } else {
                                        throw new AnnotationException(Resource.getText(Bundle.NAME, "exception.not.supported.argument")); // not supported argument
                                    }
                                } else {
                                    throw new AnnotationException(Resource.getText(Bundle.NAME, "exception.not.supported.argument")); // wrong ammount of arguments
                                }
                            } else {
                                throw new AnnotationException(Resource.getText(Bundle.NAME, "exception.wrong.return.type")); // wrong type of return value
                            }
                        }
                    }
                } catch (Throwable throwable) {
                    model.getExceptionStrategy().handleException(machine, throwable);
                }
            }
        }
    }

    protected Set<Class<? extends Annotation>> getSupportedAnnotations() {
        return mySupportedAnnotations;
    }

    private boolean isVertex(Element element) {
        return element instanceof Vertex;
    }

    private boolean isEdge(Element element) {
        return element instanceof Edge;
    }

    private boolean isElementArgument(Method method) {
        return (1 == method.getParameterTypes().length) && (Element.class.equals(method.getParameterTypes()[0]));
    }

    private boolean isEdgeArgument(Method method) {
        return (1 == method.getParameterTypes().length) && (Edge.class.equals(method.getParameterTypes()[0]));
    }

    private boolean isVertexArgument(Method method) {
        return (1 == method.getParameterTypes().length) && (Vertex.class.equals(method.getParameterTypes()[0]));
    }

    private boolean isModelArgument(Method method) {
        return (1 == method.getParameterTypes().length) && (Model.class.equals(method.getParameterTypes()[0]));
    }

    private boolean hasNoReturnType(Method method) {
        return void.class.equals(method.getReturnType());
    }

    private boolean hasNoParameter(Method method) {
        return 0 == method.getParameterTypes().length;
    }

    private boolean hasOneParameter(Method method) {
        return 1 == method.getParameterTypes().length;
    }

    private boolean isModelAnnotation(Class<? extends Annotation> annotation) {
        return AfterModel.class.equals(annotation) || BeforeModel.class.equals(annotation);
    }

    private boolean isElementAnnotation(Class<? extends Annotation> annotation) {
        return AfterElement.class.equals(annotation) || BeforeElement.class.equals(annotation);
    }
}
