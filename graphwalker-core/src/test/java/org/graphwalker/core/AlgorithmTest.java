package org.graphwalker.core;

import org.graphwalker.core.algorithm.Eulerian;
import org.graphwalker.core.common.Assert;
import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.Vertex;
import org.junit.Test;

/**
 * @author Nils Olsson
 */
public class AlgorithmTest {

    @Test
    public void semiEuler() {
        Model model = new SimpleModel()
                .addEdge(new Edge("e0", new Vertex("v0"), new Vertex("v1")))
                .addEdge(new Edge("e1", new Vertex("v1"), new Vertex("v2")))
                .addEdge(new Edge("e2", new Vertex("v1"), new Vertex("v3")))
                .addEdge(new Edge("e3", new Vertex("v3"), new Vertex("v4")))
                .addEdge(new Edge("e4", new Vertex("v4"), new Vertex("v1")));
        Eulerian eulerian = new Eulerian(model);
        Assert.assertEquals(eulerian.getEulerianType(), Eulerian.EulerianType.SEMI_EULERIAN);
        Assert.assertEquals(model, eulerian.eulerize());
    }

    @Test
    public void notEuler() {
        Model model = new SimpleModel()
                .addEdge(new Edge("e0", new Vertex("v0"), new Vertex("v1")))
                .addEdge(new Edge("e1", new Vertex("v1"), new Vertex("v2")))
                .addEdge(new Edge("e2", new Vertex("v1"), new Vertex("v3")))
                .addEdge(new Edge("e3", new Vertex("v3"), new Vertex("v4")))
                .addEdge(new Edge("e4", new Vertex("v4"), new Vertex("v1")))
                .addEdge(new Edge("e5", new Vertex("v1"), new Vertex("v3")));
        Eulerian eulerian = new Eulerian(model);
        Assert.assertEquals(eulerian.getEulerianType(), Eulerian.EulerianType.NOT_EULERIAN);
        //Assert.assertNotSame(model, eulerian.eulerize());
    }

    @Test
    public void euler() {
        Model model = new SimpleModel()
                .addEdge(new Edge("e0", new Vertex("v2"), new Vertex("v1")))
                .addEdge(new Edge("e1", new Vertex("v1"), new Vertex("v2")))
                .addEdge(new Edge("e2", new Vertex("v1"), new Vertex("v3")))
                .addEdge(new Edge("e3", new Vertex("v3"), new Vertex("v4")))
                .addEdge(new Edge("e4", new Vertex("v4"), new Vertex("v1")));
        Eulerian eulerian = new Eulerian(model);
        Assert.assertEquals(eulerian.getEulerianType(), Eulerian.EulerianType.EULERIAN);
        //Assert.assertNotSame(model, eulerian.eulerize());
    }
}
