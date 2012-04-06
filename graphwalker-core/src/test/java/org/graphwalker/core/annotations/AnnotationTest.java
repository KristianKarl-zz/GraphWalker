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
import org.graphwalker.core.utils.Assert;
import org.junit.Test;

@GraphWalker(id = "AnnotationTest", model = "models/annotationModel.graphml")
public class AnnotationTest {

    private static boolean isBeforeGroupExecuted = false;
    private static boolean isBeforeModelExecuted = false;
    private static boolean isAfterModelExecuted = false;
    private static boolean isAfterGroupExecuted = false;

    @Test
    public void executeTest() {
        new GraphWalkerExecutor(GraphWalkerFactory.create(ConfigurationFactory.create(getClass()))).run();
        Assert.assertTrue(isBeforeGroupExecuted);
        Assert.assertTrue(isBeforeModelExecuted);
        Assert.assertTrue(isAfterModelExecuted);
        Assert.assertTrue(isAfterGroupExecuted);
    }

    @BeforeGroup
    public void beforeGroup() {
        Assert.assertFalse(isBeforeGroupExecuted);
        Assert.assertFalse(isBeforeModelExecuted);
        Assert.assertFalse(isAfterModelExecuted);
        Assert.assertFalse(isAfterGroupExecuted);
        isBeforeGroupExecuted = true;
    }

    @BeforeModel
    public void beforeModel() {
        Assert.assertTrue(isBeforeGroupExecuted);
        Assert.assertFalse(isBeforeModelExecuted);
        Assert.assertFalse(isAfterModelExecuted);
        Assert.assertFalse(isAfterGroupExecuted);
        isBeforeModelExecuted = true;
    }

    @AfterModel
    public void afterModel() {
        Assert.assertTrue(isBeforeGroupExecuted);
        Assert.assertTrue(isBeforeModelExecuted);
        Assert.assertFalse(isAfterModelExecuted);
        Assert.assertFalse(isAfterGroupExecuted);
        isAfterModelExecuted = true;
    }

    @AfterGroup
    public void afterGroup() {
        Assert.assertTrue(isBeforeGroupExecuted);
        Assert.assertTrue(isBeforeModelExecuted);
        Assert.assertTrue(isAfterModelExecuted);
        Assert.assertFalse(isAfterGroupExecuted);
        isAfterGroupExecuted = true;
    }
}
