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
package org.graphwalker.maven.plugin.test;

import org.graphwalker.core.Model;
import org.graphwalker.core.PathGenerator;
import org.graphwalker.core.SimpleModel;
import org.graphwalker.core.StopCondition;
import org.graphwalker.maven.plugin.common.AnnotationUtils;
import org.graphwalker.maven.plugin.model.GraphMLModelFactory;
import org.graphwalker.maven.plugin.model.ModelFactory;

import java.lang.annotation.Annotation;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Nils Olsson
 */
public final class Execution {

    private final Class<?> testClass;
    private final Class<? extends PathGenerator> pathGenerator;
    private final Class<? extends StopCondition> stopCondition;
    private final String stopConditionValue;
    private final Model model;

    /**
     * <p>Constructor for Execution.</p>
     *
     * @param testClass a {@link java.lang.Class} object.
     * @param pathGenerator a {@link java.lang.Class} object.
     * @param stopCondition a {@link java.lang.Class} object.
     * @param stopConditionValue a {@link java.lang.String} object.
     */
    public Execution(final Class<?> testClass, final Class<? extends PathGenerator> pathGenerator, final Class<? extends StopCondition> stopCondition, String stopConditionValue) {
        this.testClass = testClass;
        this.stopCondition = stopCondition;
        this.pathGenerator = pathGenerator;
        this.stopConditionValue = stopConditionValue;
        this.model = createModel(testClass);
    }

    private Model createModel(final Class<?> testClass) {
        Model model = new SimpleModel();
        ModelFactory factory = new GraphMLModelFactory();
        for (Annotation annotation: AnnotationUtils.getAnnotations(testClass, org.graphwalker.core.annotation.Model.class)) {
            Path file = Paths.get(((org.graphwalker.core.annotation.Model) annotation).file());
            if (factory.accept(file)) {
                model = model.addModel(factory.create(file));
            }
        }
        return model;
    }

    /**
     * <p>getName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        return testClass.getName();
    }

    /**
     * <p>Getter for the field <code>testClass</code>.</p>
     *
     * @return a {@link java.lang.Class} object.
     */
    public Class<?> getTestClass() {
        return testClass;
    }

    /**
     * <p>Getter for the field <code>pathGenerator</code>.</p>
     *
     * @return a {@link java.lang.Class} object.
     */
    public Class<? extends PathGenerator> getPathGenerator() {
        return pathGenerator;
    }

    /**
     * <p>Getter for the field <code>stopCondition</code>.</p>
     *
     * @return a {@link java.lang.Class} object.
     */
    public Class<? extends StopCondition> getStopCondition() {
        return stopCondition;
    }

    /**
     * <p>Getter for the field <code>stopConditionValue</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getStopConditionValue() {
        return stopConditionValue;
    }

    /**
     * <p>Getter for the field <code>model</code>.</p>
     *
     * @return a {@link org.graphwalker.core.Model} object.
     */
    public Model getModel() {
        return model;
    }
}
