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
import org.graphwalker.core.utils.Resource;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ModelTest {

    public Model createModel() {
        Model model = new Model("m1");
        Vertex source = model.addVertex(new Vertex("v_0"));
        source.setName("Start");
        Vertex target = model.addVertex(new Vertex("v_1"));
        target.setName("target");
        Edge edge = model.addEdge(new Edge("e_0"), source, target);
        edge.setName("edge");
        model.afterElementsAdded();
        return model;
    }
    
    @Test
    public void getVertexByNameTest() {
        Model model = createModel();
        Vertex vertex = model.getVertexByName("target");
        Assert.assertNotNull(vertex);
        Assert.assertEquals("target", vertex.getName());
        vertex = model.getVertexByName("NotFound");
        Assert.assertNull(vertex);
    }

    @Test
    public void getEdgeByNameTest() {
        Model model = createModel();
        Edge edge = model.getEdgeByName("edge");
        Assert.assertNotNull(edge);
        Assert.assertEquals(edge.getName(), edge.getName());
        edge = model.getEdgeByName("NotFound");
        Assert.assertNull(edge);
    }        

    @Test
    public void getVertexByIdTest() {
        Model model = createModel();
        Vertex source = model.getVertexByName("Start");
        Vertex vertex = model.getVertexById(source.getId());
        Assert.assertNotNull(vertex);
        Assert.assertEquals(source.getId(), vertex.getId());
        
    }
    
    @Test
    public void getEdgeByIdTest() {
        Model model = createModel();
        Edge edge = model.getEdgeByName("edge");
        Assert.assertNotNull(model.getEdgeById(edge.getId()));
    }

    @Test
    public void getEdgeSourceTest() {
        Model model = createModel();
        Vertex source = model.getVertexByName("Start");
        Edge edge = model.getEdgeByName("edge");
        Vertex vertex = edge.getSource();
        Assert.assertNotNull(vertex);
        Assert.assertEquals(source.getId(), vertex.getId());
    }

    @Test(expected = ModelException.class)
    public void exceptionTest() {
        Model model = new Model("m1");
        model.getStartVertex();
    }
    
    @Test
    public void getShortestPathToEdge() {
        Model model = createModel();
        Vertex source = model.getVertexByName("Start");
        Edge edge = model.getEdgeByName("edge");
        List<Element> modelElements = model.getShortestPath(source, edge);
        Assert.assertNotNull(modelElements);
        Assert.assertEquals(2, modelElements.size());
        Assert.assertEquals(source.getName(), modelElements.get(0).getName());
        Assert.assertEquals(edge.getName(), modelElements.get(1).getName());
    }

    @Test
    public void getShortestPathToVertex() {
        Model model = createModel();
        Vertex source = model.getVertexByName("Start");
        Vertex target = model.getVertexByName("target");
        Edge edge = model.getEdgeByName("edge");
        List<Element> modelElements = model.getShortestPath(source, target);
        Assert.assertNotNull(modelElements);
        Assert.assertEquals(3, modelElements.size());
        Assert.assertEquals(source.getName(), modelElements.get(0).getName());
        Assert.assertEquals(edge.getName(), modelElements.get(1).getName());
        Assert.assertEquals(target.getName(), modelElements.get(2).getName());
    }

    @Test
    public void testStartNode() {
        Model model = new Model("m1");
        Vertex vertex = new Vertex("v_0");
        vertex.setName(Resource.getText(Bundle.NAME, "start.vertex"));
        model.addVertex(vertex);
        Assert.assertNotNull(model.getStartVertex());
        Assert.assertEquals(Resource.getText(Bundle.NAME, "start.vertex"), model.getStartVertex().getName());
    }

    @Test
    public void testStartNodeWithDifferentCase() {
        Model model = new Model("m1");
        Vertex vertex = new Vertex("v_0");
        vertex.setName(Resource.getText(Bundle.NAME, "start.vertex").toLowerCase());
        model.addVertex(vertex);
        Assert.assertNotNull(model.getStartVertex());
    }

    @Test(expected = ModelException.class)
    public void testTwoStartNodes() {
        Model model = new Model("m1");
        Vertex vertex1 = new Vertex("v_1");
        vertex1.setName(Resource.getText(Bundle.NAME, "start.vertex"));
        model.addVertex(vertex1);
        Vertex vertex2 = new Vertex("v_2");
        vertex2.setName(Resource.getText(Bundle.NAME, "start.vertex"));
        model.addVertex(vertex2);
        model.getStartVertex();
    }

    @Test(expected = ModelException.class)
    public void testTwoStartNodesWithDifferentCase() {
        Model model = new Model("m1");
        Vertex vertex1 = new Vertex("v_1");
        vertex1.setName(Resource.getText(Bundle.NAME, "start.vertex").toLowerCase());
        model.addVertex(vertex1);
        Vertex vertex2 = new Vertex("v_2");
        vertex2.setName(Resource.getText(Bundle.NAME, "start.vertex").toUpperCase());
        model.addVertex(vertex2);
        model.getStartVertex();
    }

    @Test(expected = ModelException.class)
    public void testStartNodeWithSeveralOutEdges() {
        Model model = new Model("m1");
        Vertex start = new Vertex("v_0");
        start.setName(Resource.getText(Bundle.NAME, "start.vertex").toLowerCase());
        model.addVertex(start);
        Vertex vertex = new Vertex("v_1");
        vertex.setName(Resource.getText(Bundle.NAME, "start.vertex").toLowerCase());
        model.addVertex(vertex);
        model.addEdge(new Edge("e_0"), start, vertex);
        model.addEdge(new Edge("e_1"), start, vertex);
        model.getStartVertex();
    }

    @Test(expected = ModelException.class)
    public void testStartNodeWithInEdge() {
        Model model = new Model("m1");
        Vertex start = model.addVertex(new Vertex(Resource.getText(Bundle.NAME, "start.vertex").toLowerCase()));
        Vertex vertex = model.addVertex(new Vertex("v_0"));
        model.addEdge(new Edge("e_0"), start, vertex);
        model.addEdge(new Edge("e_1"), vertex, start);
        model.getStartVertex();
    }

    @Test(expected = ModelException.class)
    public void testStartNodeWithLoopEdge() {
        Model model = new Model("m1");
        Vertex start = model.addVertex(new Vertex(Resource.getText(Bundle.NAME, "start.vertex").toLowerCase()));
        Vertex vertex = model.addVertex(new Vertex("v_0"));
        model.addEdge(new Edge("e_0"), start, vertex);
        model.addEdge(new Edge("e_1"), start, start);
        model.getStartVertex();
    }
}
