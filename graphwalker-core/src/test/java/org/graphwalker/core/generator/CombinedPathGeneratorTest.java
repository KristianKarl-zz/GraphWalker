/*
 * #%L
 * GraphWalker Core
 * %%
 * Copyright (C) 2011 - 2014 GraphWalker
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
package org.graphwalker.core.generator;

import com.sun.corba.se.spi.orbutil.fsm.FSM;
import org.graphwalker.core.Machine;
import org.graphwalker.core.Model;
import org.graphwalker.core.SimpleMachine;
import org.graphwalker.core.SimpleModel;
import org.graphwalker.core.condition.EdgeCoverage;
import org.graphwalker.core.condition.VertexCoverage;
import org.graphwalker.core.machine.ExecutionContext;
import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.Element;
import org.graphwalker.core.model.StartVertex;
import org.graphwalker.core.model.Vertex;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;

public class CombinedPathGeneratorTest {
  Model model;

  @Before
  public void createModel() throws Exception {
    model = new SimpleModel()
            .addEdge(new Edge("E1"
                    , new StartVertex()
                    , new Vertex("V1")))
            .addEdge(new Edge("E2"
                    , new Vertex("V1")
                    , new Vertex("V2")))
            .addEdge(new Edge("E3"
                    , new Vertex("V2")
                    , new Vertex("V1")));
  }

  @Test
  public void twoRandomPathGenerators() throws InterruptedException {
    RandomPath randomAllVertices = new RandomPath(new VertexCoverage());
    RandomPath randomAllEdges = new RandomPath(new EdgeCoverage());

    CombinedPathGenerator pathGenerator = new CombinedPathGenerator();
    pathGenerator.addPathGenerator(randomAllVertices);
    pathGenerator.addPathGenerator(randomAllEdges);

    ExecutionContext context = new ExecutionContext(model, pathGenerator);
    Machine machine = new SimpleMachine(context);
    Assert.assertNull(context.getCurrentElement());

    Element element = null;
    Assert.assertTrue(pathGenerator.hasNextStep(context));

    Assert.assertThat(pathGenerator.getNextStep(context).getName(), is("Start"));
    Assert.assertThat(pathGenerator.getNextStep(context).getName(), is("E1"));
    Assert.assertThat(pathGenerator.getNextStep(context).getName(), is("V1"));
    Assert.assertThat(pathGenerator.getNextStep(context).getName(), is("E2"));
    Assert.assertThat(pathGenerator.getNextStep(context).getName(), is("V2"));
    Assert.assertTrue(pathGenerator.hasNextStep(context));
    Assert.assertThat(pathGenerator.getNextStep(context).getName(), is("E3"));

    Assert.assertFalse(pathGenerator.hasNextStep(context));
    Assert.assertNull(pathGenerator.getNextStep(context));
  }
}
