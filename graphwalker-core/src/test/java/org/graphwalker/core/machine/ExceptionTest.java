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
package org.graphwalker.core.machine;

import org.graphwalker.core.conditions.StopConditionFactory;
import org.graphwalker.core.configuration.Configuration;
import org.graphwalker.core.configuration.ConfigurationImpl;
import org.graphwalker.core.generators.PathGeneratorFactory;
import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.Model;
import org.graphwalker.core.model.ModelImpl;
import org.graphwalker.core.model.Vertex;
import org.graphwalker.core.utils.Assert;
import org.junit.Test;

public class ExceptionTest {

    private boolean m1VertexNotExecuted = false;
    private boolean m2VertexNotExecuted = false;

    private Model createModel(String name) {
        Model model = new ModelImpl(name);
        model.setImplementation(this);
        Vertex v_start = model.addVertex(new Vertex("Start"));
        Vertex v_1 = model.addVertex(new Vertex("v_" + name));
        model.addEdge(new Edge("e_" + name), v_start, v_1);
        model.setPathGenerator(PathGeneratorFactory.create("Random"));
        model.getPathGenerator().setStopCondition(StopConditionFactory.create("VertexCoverage", 100));
        model.afterElementsAdded();
        return model;
    }

    private Configuration createConfiguration() {
        Configuration configuration = new ConfigurationImpl();
        configuration.addModel(createModel("m1"));
        configuration.addModel(createModel("m2"));
        return configuration;
    }

    @Test
    public void executeTest() {
        Machine machine = new MachineImpl(createConfiguration());
        while (machine.hasNextStep()) {
            machine.getNextStep();
        }
        Assert.assertFalse(m1VertexNotExecuted);
        Assert.assertTrue(m2VertexNotExecuted);
    }

    public void e_m1() {
        throw new AssertionError();
    }

    public void v_m1() {
        m1VertexNotExecuted = true;
    }

    public void e_m2() {
        Assert.assertFalse(m1VertexNotExecuted);
    }

    public void v_m2() {
        m2VertexNotExecuted = true;
    }
}
