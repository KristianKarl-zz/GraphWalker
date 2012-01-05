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
package org.graphwalker.core.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ModelImplTest {

    private Model myModel;
    private Vertex mySource;
    private Vertex myTarget;
    private Edge myEdge;
    
    @Before
    public void createModel() {
        myModel = new ModelImpl("m1");
        mySource = myModel.addVertex(new Vertex());
        mySource.setName("v_1");
        myTarget = myModel.addVertex(new Vertex("v_2"));
        myEdge = myModel.addEdge(new Edge("e_1"), mySource, myTarget);
    }
    
    @Test
    public void getVertexByNameTest() {
        Vertex vertex = myModel.getVertexByName(myTarget.getName());
        Assert.assertNotNull(vertex);
        Assert.assertEquals(myTarget.getName(), vertex.getName());
        vertex = myModel.getVertexByName("NotFound");
        Assert.assertNull(vertex);
    }

    @Test
    public void getEdgeByNameTest() {
        Edge edge = myModel.getEdgeByName(myEdge.getName());
        Assert.assertNotNull(edge);
        Assert.assertEquals(myEdge.getName(), edge.getName());
        edge = myModel.getEdgeByName("NotFound");
        Assert.assertNull(edge);
    }        

    @Test
    public void getVertexByIdTest() {
        Vertex vertex = myModel.getVertexById(mySource.getId());
        Assert.assertNotNull(vertex);
        Assert.assertEquals(mySource.getId(), vertex.getId());
        
    }
    
    @Test
    public void getEdgeByIdTest() {
        Edge edge = myModel.getEdgeById(myEdge.getId());
        Assert.assertNotNull(edge);
        Assert.assertEquals(myEdge.getId(), edge.getId());
    }

    @Test
    public void getEdgeSourceTest() {
        Vertex vertex = myEdge.getSource();
        Assert.assertNotNull(vertex);
        Assert.assertEquals(mySource.getId(), vertex.getId());
    }

    @Test(expected = ModelException.class)
    public void exceptionTest() {
        Model model = new ModelImpl("m1");
        model.getStartVertex();
    }
    
    @Test
    public void getShortestPathToEdge() {
        List<Element> modelElements = myModel.getShortestPath(mySource, myEdge);
        Assert.assertNotNull(modelElements);
        Assert.assertEquals(2, modelElements.size());
        Assert.assertEquals(mySource.getName(), modelElements.get(0).getName());
        Assert.assertEquals(myEdge.getName(), modelElements.get(1).getName());
    }

    @Test
    public void getShortestPathToVertex() {
        List<Element> modelElements = myModel.getShortestPath(mySource, myTarget);
        Assert.assertNotNull(modelElements);
        Assert.assertEquals(3, modelElements.size());
        Assert.assertEquals(mySource.getName(), modelElements.get(0).getName());
        Assert.assertEquals(myEdge.getName(), modelElements.get(1).getName());
        Assert.assertEquals(myTarget.getName(), modelElements.get(2).getName());
    }
}
