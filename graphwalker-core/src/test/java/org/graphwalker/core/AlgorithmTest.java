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
package org.graphwalker.core;

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
