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
package org.graphwalker.core.machine;

import org.graphwalker.core.conditions.StopCondition;
import org.graphwalker.core.generators.PathGenerator;
import org.graphwalker.core.model.Model;
import org.graphwalker.core.model.ModelFactory;
import org.graphwalker.core.model.support.DefaultModelFactory;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import static org.graphwalker.core.common.AnnotationUtils.getAnnotations;

/**
 * @author Nils Olsson
 */
public final class Execution {

    private final Class<?> testClass;
    private final Class<? extends PathGenerator> pathGenerator;
    private final Class<? extends StopCondition> stopCondition;
    private final String stopConditionValue;
    private final Set<Model> models;

    public Execution(final Class<?> testClass, final Class<? extends PathGenerator> pathGenerator, final Class<? extends StopCondition> stopCondition, String stopConditionValue) {
        this.testClass = testClass;
        this.stopCondition = stopCondition;
        this.pathGenerator = pathGenerator;
        this.stopConditionValue = stopConditionValue;
        this.models = createModels(testClass);
    }

    private Set<Model> createModels(final Class<?> testClass) {
        Set<Model> models = new HashSet<Model>();
        ModelFactory factory = new DefaultModelFactory();
        for (Annotation annotation: getAnnotations(testClass, org.graphwalker.core.annotations.Model.class)) {
            String file = ((org.graphwalker.core.annotations.Model)annotation).file();
            if (factory.accept(file)) {
                models.add(factory.create(file));
            }
        }
        return models;
    }

    public String getName() {
        return testClass.getName();
    }

    public Class<?> getTestClass() {
        return testClass;
    }

    public Class<? extends PathGenerator> getPathGenerator() {
        return pathGenerator;
    }

    public Class<? extends StopCondition> getStopCondition() {
        return stopCondition;
    }

    public String getStopConditionValue() {
        return stopConditionValue;
    }

    public Set<Model> getModels() {
        return models;
    }
}
