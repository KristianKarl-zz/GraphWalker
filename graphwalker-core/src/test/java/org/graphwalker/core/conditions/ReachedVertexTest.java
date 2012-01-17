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
package org.graphwalker.core.conditions;

import org.graphwalker.core.GraphWalker;
import org.graphwalker.core.GraphWalkerFactory;
import org.graphwalker.core.configuration.Configuration;
import org.graphwalker.core.configuration.ConfigurationImpl;
import org.graphwalker.core.generators.PathGeneratorFactory;
import org.graphwalker.core.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ReachedVertexTest {

    private Configuration createConfiguration() {
        Configuration configuration = new ConfigurationImpl();
        Model model = configuration.addModel(new ModelImpl("m1"));
        Vertex v_start = model.addVertex(new Vertex("Start"));
        Vertex v_1 = model.addVertex(new Vertex("v_1"));
        Vertex v_2 = model.addVertex(new Vertex("v_2"));
        Vertex v_3 = model.addVertex(new Vertex("v_3"));
        Vertex v_4 = model.addVertex(new Vertex("v_4"));
        Vertex v_5 = model.addVertex(new Vertex("v_5"));
        Vertex v_6 = model.addVertex(new Vertex("v_6"));
        model.addEdge(new Edge(), v_start, v_1);
        model.addEdge(new Edge("e_1"), v_1, v_2);
        model.addEdge(new Edge("e_2"), v_2, v_3);
        model.addEdge(new Edge("e_3"), v_3, v_4);
        model.addEdge(new Edge("e_4"), v_4, v_5);
        model.addEdge(new Edge("e_5"), v_5, v_6);
        model.addEdge(new Edge("e_6"), v_6, v_1);
        model.setPathGenerator(PathGeneratorFactory.create("Random"));
        model.getPathGenerator().setStopCondition(StopConditionFactory.create("ReachedVertex", "v_5"));
        return configuration;
    }

    @Test
    public void executeTest() {
        GraphWalker graphWalker = GraphWalkerFactory.create(createConfiguration());
        Element element = graphWalker.getNextStep();
        Assert.assertEquals("v_1", element.getName());
        element = graphWalker.getNextStep();
        Assert.assertEquals("e_1", element.getName());
        element = graphWalker.getNextStep();
        Assert.assertEquals("v_2", element.getName());
        element = graphWalker.getNextStep();
        Assert.assertEquals("e_2", element.getName());
        element = graphWalker.getNextStep();
        Assert.assertEquals("v_3", element.getName());
        element = graphWalker.getNextStep();
        Assert.assertEquals("e_3", element.getName());
        element = graphWalker.getNextStep();
        Assert.assertEquals("v_4", element.getName());
        element = graphWalker.getNextStep();
        Assert.assertEquals("e_4", element.getName());
        element = graphWalker.getNextStep();
        Assert.assertEquals("v_5", element.getName());
        Assert.assertFalse(graphWalker.hasNextStep());
    }
}
