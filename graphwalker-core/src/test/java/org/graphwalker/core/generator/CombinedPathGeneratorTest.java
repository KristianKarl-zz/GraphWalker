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
