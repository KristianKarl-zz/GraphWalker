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
package org.graphwalker.core.annotations;

import org.graphwalker.core.GraphWalkerExecutor;
import org.graphwalker.core.GraphWalkerFactory;
import org.graphwalker.core.configuration.ConfigurationFactory;
import org.junit.Test;

@GraphWalker(id = "AnnotationTest", model = "models/annotationModel.graphml")
public class ExceptionTest {

    private static boolean castBeforeGroupException = false;
    private static boolean castBeforeModelException = false;
    private static boolean castAfterModelException = false;
    private static boolean castAfterGroupException = false;

    @Test(expected = Exception.class)
    public void executeBeforeGroupTest() {
        exceptionSetup(true, false, false, false);
        new GraphWalkerExecutor(GraphWalkerFactory.create(ConfigurationFactory.create(getClass()))).run();
    }

    @Test(expected = Exception.class)
    public void executeBeforeModelTest() {
        exceptionSetup(false, true, false, false);
        new GraphWalkerExecutor(GraphWalkerFactory.create(ConfigurationFactory.create(getClass()))).run();
    }

    @Test(expected = Exception.class)
    public void executeAfterModelTest() {
        exceptionSetup(false, false, true, false);
        new GraphWalkerExecutor(GraphWalkerFactory.create(ConfigurationFactory.create(getClass()))).run();
    }

    @Test(expected = Exception.class)
    public void executeAfterGroupTest() {
        exceptionSetup(false, false, false, true);
        new GraphWalkerExecutor(GraphWalkerFactory.create(ConfigurationFactory.create(getClass()))).run();
    }

    private void exceptionSetup(boolean beforeGroup, boolean beforeModel, boolean afterModel, boolean afterGroup) {
        castBeforeGroupException = beforeGroup;
        castBeforeModelException = beforeModel;
        castAfterModelException = afterModel;
        castAfterGroupException = afterGroup;
    }

    @BeforeGroup
    public void beforeGroup() {
        if (castBeforeGroupException) {
            throw new RuntimeException("a exception thrown during a beforeGroup method");
        }
    }

    @BeforeModel
    public void beforeModel() {
        if (castBeforeModelException) {
            throw new RuntimeException("a exception thrown during a beforeModel method");
        }
    }

    @AfterModel
    public void afterModel() {
        if (castAfterModelException) {
            throw new RuntimeException("a exception thrown during a afterModel method");
        }
    }

    @AfterGroup
    public void afterGroup() {
        if (castAfterGroupException) {
            throw new RuntimeException("a exception thrown during a afterGroup method");
        }
    }
}
