package org.graphwalker.io.factory;

import org.graphwalker.core.Model;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.core.Is.is;

public class GraphMLModelFactoryTest {

  @Test
  public void UC01() {
    GraphMLModelFactory factory = new GraphMLModelFactory();
    Model model = factory.create("graphml/UC01.graphml");
    Assert.assertThat("Number of vertices", model.getVertices().size(), is(8));
    Assert.assertThat("Number of edges", model.getEdges().size(), is(12));
    Assert.assertThat("Model description", model.getDescription(), is("This is a description of the test"));
  }

  @Test
  public void EFSM_with_REQTAGS() {
    GraphMLModelFactory factory = new GraphMLModelFactory();
    Model model = factory.create("graphml/EFSM_with_REQTAGS.graphml");
    Assert.assertThat("Number of vertices", model.getVertices().size(), is(7));
    Assert.assertThat("Number of edges", model.getEdges().size(), is(19));
    Assert.assertThat("Model description", model.getDescription(), is("This is a description of the test"));
  }

  @Test
  public void largeNumberOfSubGraphs() {
    GraphMLModelFactory factory = new GraphMLModelFactory();
    Model model = factory.create("graphml/largeNumberOfSubGraphs");
    Assert.assertThat("Number of vertices are wrong", model.getVertices().size(), is(8));
    Assert.assertThat("Number of edges are wrong", model.getEdges().size(), is(12));
    Assert.assertThat("Model description", model.getDescription(), is("This is a description of the test"));
  }
}
