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

import net.sf.oval.exception.ConstraintsViolatedException;
import org.junit.Assert;
import org.junit.Test;

public class VertexTest {

    @Test
    public void createVertexTest() {
        Vertex vertex = new Vertex("vertexName");
        Assert.assertEquals("vertexName", vertex.getName());
        Assert.assertFalse(vertex.hasSwitchModel());
        Assert.assertNull(vertex.getSwitchModelId());
    }

    @Test(expected = ConstraintsViolatedException.class)
    public void createNullVertexTest() {
        new Vertex(null);
    }

    @Test(expected = ConstraintsViolatedException.class)
    public void createEmptyVertexTest() {
        new Vertex("");
    }

    @Test
    public void setSwitchModelIdTest() {
        Vertex vertex = new Vertex();
        Assert.assertFalse(vertex.hasSwitchModel());
        vertex.setSwitchModelId("modelId");
        Assert.assertTrue(vertex.hasSwitchModel());
        Assert.assertEquals("modelId", vertex.getSwitchModelId());
    }

    @Test(expected = ConstraintsViolatedException.class)
    public void setNullSwitchModelIdTest() {
        Vertex vertex = new Vertex();
        vertex.setSwitchModelId(null);
    }

    @Test(expected = ConstraintsViolatedException.class)
    public void setEmptySwitchModelIdTest() {
        Vertex vertex = new Vertex();
        vertex.setSwitchModelId("");
    }
}
