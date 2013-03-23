/*
 * #%L
 * GraphWalker Core
 * %%
 * Copyright (C) 2011 - 2012 GraphWalker
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package org.graphwalker.java.annotations;

import org.graphwalker.core.Bundle;
import org.graphwalker.core.machine.Machine;
import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.ImmutableElement;
import org.graphwalker.core.model.Model;
import org.graphwalker.core.model.Vertex;
import org.graphwalker.core.utils.Resource;
import org.graphwalker.java.utils.Reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>AnnotationProcessorImpl class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class AnnotationProcessor {

    private static final Set<Class<? extends Annotation>> mySupportedAnnotations = new HashSet<Class<? extends Annotation>>() {{
        add(AfterElement.class);
        add(AfterModel.class);
        add(BeforeElement.class);
        add(BeforeModel.class);
    }};

    /**
     * <p>Constructor for AnnotationProcessorImpl.</p>
     */
    public AnnotationProcessor() {
    }

    /**
     * {@inheritDoc}
     */
    public void process(Class<? extends Annotation> annotation, Machine machine, Object object) {
        if (getSupportedAnnotations().contains(annotation)) {
            try {
                for (Method method : object.getClass().getMethods()) {
                    if (method.isAnnotationPresent(annotation)) {
                        if (hasNoReturnType(method)) {
                            Reflection.execute(object, method, machine.getCurrentContext());
                        } else {
                            throw new AnnotationException(Resource.getText(Bundle.NAME, "exception.wrong.return.type")); // wrong type of return value
                        }
                    }
                }
            } catch (Throwable throwable) {
                machine.getCurrentContext().getExceptionStrategy().handleException(machine, throwable);
            }
        }
    }

    /**
     * <p>getSupportedAnnotations.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    protected Set<Class<? extends Annotation>> getSupportedAnnotations() {
        return mySupportedAnnotations;
    }

    private boolean isVertex(ImmutableElement element) {
        return element instanceof Vertex;
    }

    private boolean isEdge(ImmutableElement element) {
        return element instanceof Edge;
    }

    private boolean isElementArgument(Method method) {
        return (1 == method.getParameterTypes().length) && (ImmutableElement.class.equals(method.getParameterTypes()[0]));
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
