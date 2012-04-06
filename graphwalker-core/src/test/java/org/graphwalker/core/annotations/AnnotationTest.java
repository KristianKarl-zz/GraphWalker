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

    private static boolean beforeGroupExecuted = false;
    private static boolean beforeModelExecuted = false;
    private static boolean afterModelExecuted = false;
    private static boolean afterGroupExecuted = false;

    @Test
    public void executeTest() {
        GraphWalkerExecutor executor = new GraphWalkerExecutor(GraphWalkerFactory.create(ConfigurationFactory.create(getClass())));
        executor.run();
        Assert.assertTrue(beforeGroupExecuted);
        Assert.assertTrue(beforeModelExecuted);
        Assert.assertTrue(afterModelExecuted);
        Assert.assertTrue(afterGroupExecuted);
    }

    @BeforeGroup
    public void beforeGroup() {
        Assert.assertFalse(beforeGroupExecuted);
        Assert.assertFalse(beforeModelExecuted);
        Assert.assertFalse(afterModelExecuted);
        Assert.assertFalse(afterGroupExecuted);
        beforeGroupExecuted = true;
    }

    @BeforeModel
    public void beforeModel() {
        Assert.assertTrue(beforeGroupExecuted);
        Assert.assertFalse(beforeModelExecuted);
        Assert.assertFalse(afterModelExecuted);
        Assert.assertFalse(afterGroupExecuted);
        beforeModelExecuted = true;
    }

    @AfterModel
    public void afterModel() {
        Assert.assertTrue(beforeGroupExecuted);
        Assert.assertTrue(beforeModelExecuted);
        Assert.assertFalse(afterModelExecuted);
        Assert.assertFalse(afterGroupExecuted);
        afterModelExecuted = true;
    }

    @AfterGroup
    public void afterGroup() {
        Assert.assertTrue(beforeGroupExecuted);
        Assert.assertTrue(beforeModelExecuted);
        Assert.assertTrue(afterModelExecuted);
        Assert.assertFalse(afterGroupExecuted);
        afterGroupExecuted = true;
    }

}
