/*
 * #%L
 * GraphWalker Core
 * %%
 * Copyright (C) 2011 - 2013 GraphWalker
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
package org.graphwalker.core.common;

import org.graphwalker.core.annotations.ExceptionHandler;
import org.graphwalker.core.machine.ExecutionContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public final class AnnotationUtils {

    private AnnotationUtils() {}

    public static Set<Annotation> getAnnotations(final Class<?> clazz, final Class<? extends Annotation> annotation) {
        Set<Annotation> annotations = new HashSet<Annotation>();
        for (Class<?> interfaceClass: clazz.getInterfaces()) {
            if (interfaceClass.isAnnotationPresent(annotation)) {
                annotations.add(interfaceClass.getAnnotation(annotation));
            }
        }
        return annotations;
    }

    public static void execute(Class<? extends Annotation> annotation, ExecutionContext executionContext) {
        for (Method method: executionContext.getImplementation().getClass().getMethods()) {
            if (method.isAnnotationPresent(annotation)) {
                ReflectionUtils.execute(executionContext.getImplementation(), method, executionContext.getEdgeFilter());
            }
        }
    }

    public static void execute(ExecutionContext executionContext, Throwable throwable) {
        for (Method method: executionContext.getImplementation().getClass().getMethods()) {
            if (method.isAnnotationPresent(ExceptionHandler.class)) {
                Class<? extends Throwable> filter = method.getAnnotation(ExceptionHandler.class).filter();
                if (filter.isAssignableFrom(throwable.getClass())) {
                    ReflectionUtils.execute(executionContext.getImplementation(), method, executionContext.getEdgeFilter());
                }
            }
        }
    }
}
