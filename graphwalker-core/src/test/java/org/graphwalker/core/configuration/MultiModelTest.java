/*
 * #%L
 * GraphWalker Core
 * %%
 * Copyright (C) 2011 GraphWalker
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
import org.graphwalker.core.GraphWalkerFactory;
import org.junit.Assert;
import org.junit.Test;

public class MultiModelTest {

    @Test
    public void executeTest() {
        String file = getClass().getResource("/multiModelTest.xml").getFile();
        GraphWalker graphWalker = GraphWalkerFactory.create(file);
        // defaultModelId in multiModelTest.xml makes the m3 model the current model
        Assert.assertTrue(graphWalker.hasNextStep());
        Assert.assertEquals(graphWalker.getNextStep().getName(), "e_0");
        Assert.assertTrue(graphWalker.hasNextStep());
        Assert.assertEquals(graphWalker.getNextStep().getName(), "v_0");
        Assert.assertTrue(graphWalker.hasNextStep());
        Assert.assertEquals(graphWalker.getNextStep().getName(), "e_1");
        Assert.assertTrue(graphWalker.hasNextStep());
        Assert.assertEquals(graphWalker.getNextStep().getName(), "v_1");
        // a switch tag change the current model to m1 and uses m1's pathGenerator to find the next step
        Assert.assertTrue(graphWalker.hasNextStep());
        Assert.assertEquals(graphWalker.getNextStep().getName(), "e_0");
        Assert.assertTrue(graphWalker.hasNextStep());
        Assert.assertEquals(graphWalker.getNextStep().getName(), "v_0");
        Assert.assertTrue(graphWalker.hasNextStep());
        Assert.assertEquals(graphWalker.getNextStep().getName(), "e_1");
        Assert.assertTrue(graphWalker.hasNextStep());
        Assert.assertEquals(graphWalker.getNextStep().getName(), "v_1");
        // a switch tag change the current model to m2 and uses m2's pathGenerator to find the next step
        Assert.assertTrue(graphWalker.hasNextStep());
        Assert.assertEquals(graphWalker.getNextStep().getName(), "e_0");
        Assert.assertTrue(graphWalker.hasNextStep());
        Assert.assertEquals(graphWalker.getNextStep().getName(), "v_0");
        Assert.assertTrue(graphWalker.hasNextStep());
        Assert.assertEquals(graphWalker.getNextStep().getName(), "e_1");
        Assert.assertTrue(graphWalker.hasNextStep());
        Assert.assertEquals(graphWalker.getNextStep().getName(), "v_1");
        Assert.assertFalse(graphWalker.hasNextStep());
    }
}
