package org.graphwalker.core.generators;

import org.graphwalker.core.GraphWalker;
import org.graphwalker.core.GraphWalkerFactory;
import org.graphwalker.core.conditions.StopConditionFactory;
import org.graphwalker.core.configuration.Configuration;
import org.graphwalker.core.configuration.ConfigurationImpl;
import org.graphwalker.core.model.*;
import org.graphwalker.core.utils.Assert;
import org.junit.Test;

public class RandomUnvisitedFirstPathTest {

    private Configuration createConfiguration() {
        Configuration configuration = new ConfigurationImpl();
        Model model = configuration.addModel(new ModelImpl("m1"));
        Vertex v_start = model.addVertex(new Vertex("Start"));
        Vertex v_1 = model.addVertex(new Vertex("v_1"));
        model.addEdge(new Edge(), v_start, v_1);
        model.addEdge(new Edge(), v_1, v_1);
        model.addEdge(new Edge(), v_1, v_1);
        model.addEdge(new Edge(), v_1, v_1);
        model.addEdge(new Edge(), v_1, v_1);
        model.addEdge(new Edge(), v_1, v_1);
        model.setPathGenerator(PathGeneratorFactory.create("RandomUnvisitedFirst"));
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
        Assert.assertEquals(countUnvisitedEdges((Vertex)element), 5);
        graphWalker.getNextStep();
        graphWalker.getNextStep();
        Assert.assertEquals(countUnvisitedEdges((Vertex)element), 4);
        graphWalker.getNextStep();
        graphWalker.getNextStep();
        Assert.assertEquals(countUnvisitedEdges((Vertex)element), 3);
        graphWalker.getNextStep();
        graphWalker.getNextStep();
        Assert.assertEquals(countUnvisitedEdges((Vertex)element), 2);
        graphWalker.getNextStep();
        graphWalker.getNextStep();
        Assert.assertEquals(countUnvisitedEdges((Vertex)element), 1);
        graphWalker.getNextStep();
        Assert.assertEquals(countUnvisitedEdges((Vertex)element), 0);
        Assert.assertFalse(graphWalker.hasNextStep());
    }

    private int countUnvisitedEdges(Vertex vertex) {
        int count = 0;
        for (Edge edge: vertex.getEdges()) {
            if (!edge.isVisited()) {
                count++;
            }
        }
        return count;
    }
}
