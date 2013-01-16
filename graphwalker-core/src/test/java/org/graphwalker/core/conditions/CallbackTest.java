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
package org.graphwalker.core.conditions;

import org.graphwalker.core.GraphWalker;
import org.graphwalker.core.GraphWalkerFactory;
import org.graphwalker.core.configuration.Configuration;
import org.graphwalker.core.configuration.ConfigurationImpl;
import org.graphwalker.core.generators.PathGeneratorFactory;
import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.Model;
import org.graphwalker.core.model.ModelImpl;
import org.graphwalker.core.model.Vertex;
import org.junit.Assert;
import org.junit.Test;

public class CallbackTest {

    private Configuration createConfiguration() {
        Configuration configuration = new ConfigurationImpl();
        Model model = configuration.addModel(new ModelImpl("m1"));
        Vertex v_start = model.addVertex(new Vertex("Start"));
        Vertex v_1 = model.addVertex(new Vertex("v_1"));
        Vertex v_2 = model.addVertex(new Vertex("v_2"));
        model.addEdge(new Edge(), v_start, v_1);
        model.addEdge(new Edge("e_1"), v_1, v_2);
        model.addEdge(new Edge("e_2"), v_1, v_2);
        model.addEdge(new Edge("e_3"), v_1, v_2);
        model.addEdge(new Edge("e_4"), v_1, v_2);
        model.addEdge(new Edge("e_5"), v_1, v_2);
        model.addEdge(new Edge("e_6"), v_1, v_2);
        model.addEdge(new Edge("e_7"), v_1, v_2);
        model.addEdge(new Edge("e_8"), v_1, v_2);
        model.addEdge(new Edge("e_9"), v_1, v_2);
        model.addEdge(new Edge(), v_2, v_1);
        model.setPathGenerator(PathGeneratorFactory.create("Random"));
        model.setImplementation(this);
        model.afterElementsAdded();
        return configuration;
    }

    public boolean returnPrimitiveBoolean() {
        return true;
    }

    public Boolean returnBoolean() {
        return true;
    }

    public double returnPrimitiveDouble() {
        return 1.0;
    }

    public Double returnDouble() {
        return 1.0;
    }

    public void returnVoid() {
        // Should generate exeception
    }

    @Test
    public void executeDoubleTest() {
        Configuration configuration = createConfiguration();
        configuration.getModel("m1").getPathGenerator().setStopCondition(StopConditionFactory.create("Callback", "returnDouble"));
        GraphWalker graphWalker = GraphWalkerFactory.create(configuration);
        while (graphWalker.hasNextStep()) {
            graphWalker.getNextStep();
        }
    }

    @Test
    public void executePrimitiveDoubleTest() {
        Configuration configuration = createConfiguration();
        configuration.getModel("m1").getPathGenerator().setStopCondition(StopConditionFactory.create("Callback", "returnPrimitiveDouble"));
        GraphWalker graphWalker = GraphWalkerFactory.create(configuration);
        while (graphWalker.hasNextStep()) {
            graphWalker.getNextStep();
        }
    }

    @Test
    public void executeBooleanTest() {
        Configuration configuration = createConfiguration();
        configuration.getModel("m1").getPathGenerator().setStopCondition(StopConditionFactory.create("Callback", "returnBoolean"));
        GraphWalker graphWalker = GraphWalkerFactory.create(configuration);
        while (graphWalker.hasNextStep()) {
            graphWalker.getNextStep();
        }
    }

    @Test
    public void executePrimitiveBooleanTest() {
        Configuration configuration = createConfiguration();
        configuration.getModel("m1").getPathGenerator().setStopCondition(StopConditionFactory.create("Callback", "returnPrimitiveBoolean"));
        GraphWalker graphWalker = GraphWalkerFactory.create(configuration);
        while (graphWalker.hasNextStep()) {
            graphWalker.getNextStep();
        }
    }

    @Test(expected = StopConditionException.class)
    public void executeVoidTest() {
        Configuration configuration = createConfiguration();
        configuration.getModel("m1").getPathGenerator().setStopCondition(StopConditionFactory.create("Callback", "returnVoid"));
        GraphWalker graphWalker = GraphWalkerFactory.create(configuration);
        while (graphWalker.hasNextStep()) {
            graphWalker.getNextStep();
        }
    }
}
