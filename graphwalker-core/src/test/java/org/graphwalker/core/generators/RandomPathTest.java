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
package org.graphwalker.core.generators;

import org.graphwalker.core.GraphWalker;
import org.graphwalker.core.GraphWalkerFactory;
import org.graphwalker.core.conditions.StopConditionFactory;
import org.graphwalker.core.configuration.Configuration;
import org.graphwalker.core.configuration.ConfigurationImpl;
import org.graphwalker.core.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RandomPathTest {

    private Configuration createConfiguration() {
        Configuration configuration = new ConfigurationImpl();
        Model model = configuration.addModel(new ModelImpl("m1"));
        Vertex v_start = model.addVertex(new Vertex("Start"));
        Vertex v_1 = model.addVertex(new Vertex("v_1"));
        model.addEdge(new Edge(), v_start, v_1);
        model.setPathGenerator(PathGeneratorFactory.create("Random"));
        model.getPathGenerator().setStopCondition(StopConditionFactory.create("VertexCoverage", 100));
        model.afterElementsAdded();
        return configuration;
    }

    @Test
    public void executeTest() {
        GraphWalker graphWalker = GraphWalkerFactory.create(createConfiguration());
        Element element = graphWalker.getNextStep();
        Assert.assertNull(element.getName());
        element = graphWalker.getNextStep();
        Assert.assertEquals("v_1", element.getName());
        Assert.assertFalse(graphWalker.hasNextStep());
    }

    /*
    @Test(expected = PathGeneratorException.class)
    public void exceptionTest() {
        GraphWalker graphWalker = GraphWalkerFactory.create(createConfiguration());
        Element element = graphWalker.getNextStep();
        Assert.assertEquals("v_1", element.getName());
        Assert.assertFalse(graphWalker.hasNextStep());
        graphWalker.getNextStep();
    }

    @Test(expected = PathGeneratorException.class)
    public void culDeSacTest() {
        Configuration configuration = createConfiguration();
        Model model = configuration.getModel("m1");
        Vertex v_1 = model.getVertexById("v_1");
        Vertex v_2 = model.addVertex(new Vertex("v_2"));
        Vertex v_3 = model.addVertex(new Vertex("v_3"));
        model.addEdge(new Edge(), v_1, v_2);
        model.addEdge(new Edge(), v_1, v_3);
        model.afterElementsAdded();
        GraphWalker graphWalker = GraphWalkerFactory.create(configuration);
        Assert.assertTrue(graphWalker.hasNextStep());
        Assert.assertNotNull(graphWalker.getNextStep());
        Assert.assertTrue(graphWalker.hasNextStep());
        Assert.assertNotNull(graphWalker.getNextStep());
        Assert.assertTrue(graphWalker.hasNextStep());
        // Now we reached a cul de sac, either in the v_2 or the v_3 vertex, next call will generate an exception
        graphWalker.getNextStep();
    }
    */
}
