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
import org.junit.Before;
import org.junit.Test;

public class LengthTest {

    private Configuration myConfiguration;
    private int myLength = 30;

    @Before
    public void createConfiguration() {
        myConfiguration = new ConfigurationImpl();
        Model model = myConfiguration.addModel(new ModelImpl("m1"));
        Vertex v_start = model.addVertex(new Vertex("Start"));
        Vertex v_1 = model.addVertex(new Vertex("v_1"));
        model.addEdge(new Edge(), v_start, v_1);
        model.addEdge(new Edge("e_1"), v_1, v_1);
        model.setPathGenerator(PathGeneratorFactory.create("Random"));
        model.getPathGenerator().setStopCondition(StopConditionFactory.create("Length", myLength));
    }

    @Test
    public void executeTest() {
        GraphWalker graphWalker = GraphWalkerFactory.create(myConfiguration);
        for (int i=0; i<myLength; i++) {
            graphWalker.getNextStep();
        }
        Assert.assertFalse(graphWalker.hasNextStep());
    }
}