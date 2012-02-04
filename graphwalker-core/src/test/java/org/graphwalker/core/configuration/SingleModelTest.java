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
package org.graphwalker.core.configuration;

import org.graphwalker.core.GraphWalker;
import org.graphwalker.core.GraphWalkerExecutor;
import org.graphwalker.core.GraphWalkerFactory;
import org.graphwalker.core.GraphWalkerImpl;
import org.graphwalker.core.utils.Resource;
import org.junit.Assert;
import org.junit.Test;

public class SingleModelTest {

    @Test
    public void testStep() {
        GraphWalker graphWalker = GraphWalkerFactory.create(Resource.getFile("singleModelTest.xml"));
        Assert.assertTrue(graphWalker.hasNextStep());
        Assert.assertEquals(graphWalker.getNextStep().getName(), "e_0");
        Assert.assertTrue(graphWalker.hasNextStep());
        Assert.assertEquals(graphWalker.getNextStep().getName(), "v_0");
        Assert.assertTrue(graphWalker.hasNextStep());
        Assert.assertEquals(graphWalker.getNextStep().getName(), "e_1");
        Assert.assertTrue(graphWalker.hasNextStep());
        Assert.assertEquals(graphWalker.getNextStep().getName(), "v_1");
        Assert.assertTrue(graphWalker.hasNextStep());
        Assert.assertEquals(graphWalker.getNextStep().getName(), "e_2");
        Assert.assertTrue(graphWalker.hasNextStep());
        Assert.assertEquals(graphWalker.getNextStep().getName(), "v_2");
        Assert.assertTrue(graphWalker.hasNextStep());
        Assert.assertEquals(graphWalker.getNextStep().getName(), "e_3");
        Assert.assertTrue(graphWalker.hasNextStep());
        Assert.assertEquals(graphWalker.getNextStep().getName(), "v_3");
        Assert.assertTrue(graphWalker.hasNextStep());
        Assert.assertNull(graphWalker.getNextStep().getName());
        Assert.assertFalse(graphWalker.hasNextStep());
    }

    @Test
    public void executePath() {
        GraphWalker graphWalker = GraphWalkerFactory.create(Resource.getFile("singleModelTest.xml"));
        Assert.assertTrue(graphWalker.hasNextStep());
        GraphWalkerExecutor executor = new GraphWalkerExecutor(graphWalker);
        executor.run();
        Assert.assertFalse(graphWalker.hasNextStep());
    }
}
