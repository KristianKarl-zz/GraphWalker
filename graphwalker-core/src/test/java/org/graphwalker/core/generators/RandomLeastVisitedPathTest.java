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
import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.Element;
import org.graphwalker.core.model.Model;
import org.graphwalker.core.model.Vertex;
import org.graphwalker.core.utils.Assert;
import org.junit.Test;

public class RandomLeastVisitedPathTest {

    private Configuration createConfiguration() {
        Configuration configuration = new Configuration();
        Model model = configuration.addModel(new Model("m1"));
        Vertex v_start = model.addVertex(new Vertex("v_0", "Start"));
        Vertex v_1 = model.addVertex(new Vertex("v_1", "v_1"));
        model.addEdge(new Edge("e_0"), v_start, v_1);
        model.addEdge(new Edge("e_1"), v_1, v_1);
        model.addEdge(new Edge("e_2"), v_1, v_1);
        model.addEdge(new Edge("e_3"), v_1, v_1);
        model.addEdge(new Edge("e_4"), v_1, v_1);
        model.addEdge(new Edge("e_5"), v_1, v_1);
        model.setPathGenerator(PathGeneratorFactory.create("RandomLeastVisited"));
        model.getPathGenerator().setStopCondition(StopConditionFactory.create("EdgeCoverage", 100));
        model.afterElementsAdded();
        return configuration;
    }

    @Test
    public void executeTest() {
        GraphWalker graphWalker = GraphWalkerFactory.create(createConfiguration());
        Element element = graphWalker.getNextStep();
        Assert.assertNull(element.getName());
        element = graphWalker.getNextStep();
        Assert.assertEquals(element.getName(), "v_1");
        Assert.assertEquals(countUnvisitedEdges((Vertex) element), 5);
        graphWalker.getNextStep();
        graphWalker.getNextStep();
        Assert.assertEquals(countUnvisitedEdges((Vertex) element), 4);
        graphWalker.getNextStep();
        graphWalker.getNextStep();
        Assert.assertEquals(countUnvisitedEdges((Vertex) element), 3);
        graphWalker.getNextStep();
        graphWalker.getNextStep();
        Assert.assertEquals(countUnvisitedEdges((Vertex) element), 2);
        graphWalker.getNextStep();
        graphWalker.getNextStep();
        Assert.assertEquals(countUnvisitedEdges((Vertex) element), 1);
        graphWalker.getNextStep();
        Assert.assertEquals(countUnvisitedEdges((Vertex) element), 0);
        Assert.assertFalse(graphWalker.hasNextStep());
    }

    private int countUnvisitedEdges(Vertex vertex) {
        int count = 0;
        for (Edge edge : vertex.getEdges()) {
            if (!edge.isVisited()) {
                count++;
            }
        }
        return count;
    }
}
