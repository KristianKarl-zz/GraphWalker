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
package org.graphwalker.maven.plugin.common;

import org.graphwalker.core.annotation.ExceptionHandler;
import org.graphwalker.core.machine.ExecutionContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Nils Olsson
 */
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

    public static void execute(Class<? extends Annotation> annotation, ExecutionContext executionContext, Object implementation) {
        for (Method method: implementation.getClass().getMethods()) {
            if (method.isAnnotationPresent(annotation)) {
                ReflectionUtils.execute(implementation, method, executionContext.getScriptContext());
            }
        }
    }

    public static void execute(ExecutionContext executionContext, Object implementation, Throwable throwable) {
        for (Method method: implementation.getClass().getMethods()) {
            if (method.isAnnotationPresent(ExceptionHandler.class)) {
                Class<?> types[] = method.getParameterTypes();
                if (0 == types.length) {
                    ReflectionUtils.execute(implementation, method);
                } else if (1 == types.length) {
                    if (types[0].isInstance(throwable)) {
                        ReflectionUtils.execute(implementation, method, throwable);
                    }
                }
            }
        }
    }
}
