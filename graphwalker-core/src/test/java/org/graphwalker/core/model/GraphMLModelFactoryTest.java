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

import org.graphwalker.core.model.support.GraphMLModelFactory;
import org.graphwalker.core.common.Assert;
import org.graphwalker.core.common.ResourceException;
import org.junit.Test;

public class GraphMLModelFactoryTest {
/*
    @Test
    public void singleModelTest() {
        //ModelFactory modelFactory = new GraphMLModelFactory();
        //Model model = modelFactory.create("m1", "/models/singleModel.graphml", "graphml");
        //Assert.assertNotNull(model);
    }

    @Test
    public void multiModelATest() {
        //ModelFactory modelFactory = new GraphMLModelFactory();
        //Model model = modelFactory.create("m1", "/models/multiModelA.graphml", "graphml");
        //Assert.assertNotNull(model);
    }

    @Test
    public void edgeFilterModelATest() {
        //ModelFactory modelFactory = new GraphMLModelFactory();
        //Model model = modelFactory.create("m1", "/models/edgeFilterModelA.graphml", "graphml");
        //Assert.assertNotNull(model);
        //Assert.assertNotNull(model.getEdges(model.getStartVertex()));
        //Assert.assertEquals(1, model.getEdges(model.getStartVertex()).size());
        //Assert.assertNotNull(model.getEdges(model.getStartVertex()).get(0).getActions());
        //Assert.assertEquals(1, model.getEdges(model.getStartVertex()).get(0).getActions().size());
    }

    @Test(expected = ResourceException.class)
    public void fileNotFoundTest() {
        ModelFactory modelFactory = new GraphMLModelFactory();
        modelFactory.create("m1", "FileNotFound", "graphml");
    }

    @Test(expected = ModelException.class)
    public void brokenModelTest() {
        ModelFactory modelFactory = new GraphMLModelFactory();
        modelFactory.create("m1", "/models/brokenModel.graphml", "graphml");
    }

    @Test
    public void readModelWithRequirementsTest() {
        ModelFactory modelFactory = new GraphMLModelFactory();
        Model model = modelFactory.create("m1", "/models/requirementModel.graphml", "graphml");
        Assert.assertNotNull(model);
    }

    @Test
    public void keyWordsTest() {
        ModelFactory modelFactory = new GraphMLModelFactory();
        Model model = modelFactory.create("m1", "/models/keywords.graphml", "graphml");

        Edge edge = model.getEdgeById("e1");
        Assert.assertNotNull(edge);
        Assert.assertNotNull(edge.getGuard());
        Assert.assertEquals(edge.getGuard().getScript(), "test3 == 3");
        Assert.assertEquals(2, edge.getActions().size());
        Assert.assertEquals("test1 =1", edge.getActions().get(0).getScript());
        Assert.assertEquals("test2= 2", edge.getActions().get(1).getScript());

        edge = model.getEdgeById("e2");
        Assert.assertNotNull(edge);
        Assert.assertNull(edge.getGuard());
        Assert.assertEquals(1, edge.getActions().size());
        Assert.assertEquals("test =1", edge.getActions().get(0).getScript());
        
        edge = model.getEdgeById("e3");
        Assert.assertNotNull(edge);
        Assert.assertNotNull(edge.getGuard());
        Assert.assertEquals("test1 != 0", edge.getGuard().getScript());
        Assert.assertTrue(edge.isBlocked());
        Assert.assertEquals(1, edge.getActions().size());
        Assert.assertEquals("test3 = 1", edge.getActions().get(0).getScript());
        
        edge = model.getEdgeById("e4");
        Assert.assertNotNull(edge);
        Assert.assertNull(edge.getGuard());
        Assert.assertTrue(edge.isBlocked());
        
        Vertex vertex = model.getVertexById("n1");
        Assert.assertNotNull(vertex);
        Assert.assertNotNull(vertex.getRequirements());
        Assert.assertEquals(1, vertex.getRequirements().size());
        Assert.assertEquals("123", vertex.getRequirements().get(0).getId());

    }
*/
}
