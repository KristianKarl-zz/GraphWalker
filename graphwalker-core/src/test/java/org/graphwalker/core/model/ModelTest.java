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
package org.graphwalker.core.model;

import org.graphwalker.core.Bundle;
import org.graphwalker.core.utils.Assert;
import org.graphwalker.core.utils.Resource;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModelTest {

    public Model createModel() {
        Vertex source = new Vertex("v_0", "Start", null, null, null, null);
        Vertex target = new Vertex("v_1", "target", null, null, null, null);
        Edge edge = new Edge("e_0", "edge", null, null, null, source, target, null, null);
        List<Vertex> vertices = new ArrayList<Vertex>();
        vertices.add(source);
        vertices.add(target);
        List<Edge> edges = new ArrayList<Edge>();
        edges.add(edge);
        return new Model("m1", vertices, edges, source);
    }

    @Test
    public void getVertexByIdTest() {
        Model model = createModel();
        Vertex source = model.getVertexById("v_0");
        Vertex vertex = model.getVertexById(source.getId());
        Assert.assertNotNull(vertex);
        Assert.assertEquals(source.getId(), vertex.getId());
        
    }
    
    @Test
    public void getEdgeByIdTest() {
        Model model = createModel();
        Edge edge = model.getEdgeById("e_0");
        Assert.assertNotNull(model.getEdgeById(edge.getId()));
    }

    @Test
    public void getEdgeSourceTest() {
        Model model = createModel();
        Vertex source = model.getVertexById("v_0");
        Edge edge = model.getEdgeById("e_0");
        Vertex vertex = edge.getSource();
        Assert.assertNotNull(vertex);
        Assert.assertEquals(source.getId(), vertex.getId());
    }

    @Test
    public void exceptionTest() {
        Model model = new Model("m1", null, null, null);
        Assert.assertNull(model.getStartVertex());
    }

    @Test
    public void testStartNode() {
        Vertex vertex = new Vertex("v_0", Resource.getText(Bundle.NAME, "start.vertex"), null, null, null, null);
        Model model = new Model("m1", Arrays.asList(vertex), null, vertex);
        Assert.assertNotNull(model.getStartVertex());
        Assert.assertEquals(Resource.getText(Bundle.NAME, "start.vertex"), model.getStartVertex().getName());
    }

    @Test
    public void testStartNodeWithDifferentCase() {
        Vertex vertex1 = new Vertex("v_0", Resource.getText(Bundle.NAME, "start.vertex").toLowerCase(), null, null, null, null);
        Model model = new Model("m1", Arrays.asList(vertex1), null, vertex1);
        Assert.assertNotNull(model.getStartVertex());
    }

    @Test(expected = ModelException.class)
    public void testStartNodeWithSeveralOutEdges() {
        Vertex vertex1 = new Vertex("v_1", Resource.getText(Bundle.NAME, "start.vertex"), null, null, null, null);
        Vertex vertex2 = new Vertex("v_2", "v_2", null, null, null, null);
        Edge edge1 = new Edge("e_1", "e_1", null, null, null, vertex1, vertex2, null, null);
        Edge edge2 = new Edge("e_2", "e_2", null, null, null, vertex1, vertex2, null, null);
        Model model = new Model("m1", Arrays.asList(vertex1, vertex2), Arrays.asList(edge1, edge2), vertex1);
        model.getStartVertex();
    }

    @Test(expected = ModelException.class)
    public void testStartNodeWithInEdge() {
        Vertex vertex1 = new Vertex("v_1", Resource.getText(Bundle.NAME, "start.vertex"), null, null, null, null);
        Vertex vertex2 = new Vertex("v_2", "v_2", null, null, null, null);
        Edge edge1 = new Edge("e_1", "e_1", null, null, null, vertex1, vertex2, null, null);
        Edge edge2 = new Edge("e_2", "e_2", null, null, null, vertex2, vertex1, null, null);
        Model model = new Model("m1", Arrays.asList(vertex1, vertex2), Arrays.asList(edge1, edge2), vertex1);
        model.getStartVertex();
    }

    @Test(expected = ModelException.class)
    public void testStartNodeWithLoopEdge() {
        Vertex vertex1 = new Vertex("v_1", Resource.getText(Bundle.NAME, "start.vertex"), null, null, null, null);
        Vertex vertex2 = new Vertex("v_2", "v_2", null, null, null, null);
        Edge edge1 = new Edge("e_1", "e_1", null, null, null, vertex1, vertex2, null, null);
        Edge edge2 = new Edge("e_2", "e_2", null, null, null, vertex1, vertex1, null, null);
        Model model = new Model("m1", Arrays.asList(vertex1, vertex2), Arrays.asList(edge1, edge2), vertex1);
        model.getStartVertex();
    }

    @Test
    public void getShortestPathToEdge() {
        Model model = createModel();
        Vertex source = model.getVertexById("v_0");
        Edge edge = model.getEdgeById("e_0");
        List<ModelElement> modelElements = model.getShortestPath(source, edge);
        Assert.assertNotNull(modelElements);
        Assert.assertEquals(2, modelElements.size());
        Assert.assertEquals(source.getName(), modelElements.get(0).getName());
        Assert.assertEquals(edge.getName(), modelElements.get(1).getName());
    }

    @Test
    public void getShortestPathToVertex() {
        Model model = createModel();
        Vertex source = model.getVertexById("v_0");
        Vertex target = model.getVertexById("v_1");
        Edge edge = model.getEdgeById("e_0");
        List<ModelElement> modelElements = model.getShortestPath(source, target);
        Assert.assertNotNull(modelElements);
        Assert.assertEquals(3, modelElements.size());
        Assert.assertEquals(source.getName(), modelElements.get(0).getName());
        Assert.assertEquals(edge.getName(), modelElements.get(1).getName());
        Assert.assertEquals(target.getName(), modelElements.get(2).getName());
    }
}
