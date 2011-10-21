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

package org.graphwalker.core.generators;

import junit.framework.TestCase;
import org.graphwalker.core.Util;
import org.graphwalker.core.conditions.ReachedEdge;
import org.graphwalker.core.conditions.ReachedVertex;
import org.graphwalker.core.graph.Edge;
import org.graphwalker.core.graph.Graph;
import org.graphwalker.core.graph.Vertex;
import org.graphwalker.core.machines.ExtendedFiniteStateMachine;

public class A_StarPathGeneratorEFSMBeanShellTest extends TestCase {

    Graph graph;
    Vertex start;
    Vertex v1;
    Vertex v2;
    Edge e0;
    Edge e1;
    Edge e2;
    Edge e3;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        graph = new Graph();

        start = Util.addVertexToGraph(graph, "Start");
        v1 = Util.addVertexToGraph(graph, "V1");
        v2 = Util.addVertexToGraph(graph, "V2");

        e0 = Util.addEdgeToGraph(graph, start, v1, "E0", null, null, "x=1;y=new Vector()");
        e1 = Util.addEdgeToGraph(graph, v1, v2, "E1", null, null, "x=2");
        e2 = Util.addEdgeToGraph(graph, v2, v2, "E2", null, "x<4", "x++");
        e3 = Util.addEdgeToGraph(graph, v2, v1, "E3", null, "y.size()<3", "y.add(x)");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        graph = null;
        start = v1 = v2 = null;
        e0 = e1 = e2 = e3 = null;
    }

    public void test_EFSM_StateStop() throws InterruptedException {
        PathGenerator pathGenerator = new A_StarPathGenerator(new ReachedVertex("V1/x=3;y=\\[2, 3, 3\\];"));
        ExtendedFiniteStateMachine machine = new ExtendedFiniteStateMachine(false);
        machine.setModel(graph);
        pathGenerator.setMachine(machine);

        String[] stepPair;
        stepPair = pathGenerator.getNext();
        assertEquals("E0", stepPair[0]);
        assertEquals("V1/x=1;y=[];", stepPair[1]);
        stepPair = pathGenerator.getNext();
        assertEquals("E1", stepPair[0]);
        assertEquals("V2/x=2;y=[];", stepPair[1]);
        stepPair = pathGenerator.getNext();
        assertEquals("E3", stepPair[0]);
        assertEquals("V1/x=2;y=[2];", stepPair[1]);
        stepPair = pathGenerator.getNext();
        assertEquals("E1", stepPair[0]);
        assertEquals("V2/x=2;y=[2];", stepPair[1]);
        stepPair = pathGenerator.getNext();
        assertEquals("E2", stepPair[0]);
        assertEquals("V2/x=3;y=[2];", stepPair[1]);
        stepPair = pathGenerator.getNext();
        assertEquals("E3", stepPair[0]);
        assertEquals("V1/x=3;y=[2, 3];", stepPair[1]);
        stepPair = pathGenerator.getNext();
        assertEquals("E1", stepPair[0]);
        assertEquals("V2/x=2;y=[2, 3];", stepPair[1]);
        stepPair = pathGenerator.getNext();
        assertEquals("E2", stepPair[0]);
        assertEquals("V2/x=3;y=[2, 3];", stepPair[1]);
        stepPair = pathGenerator.getNext();
        assertEquals("E3", stepPair[0]);
        assertEquals("V1/x=3;y=[2, 3, 3];", stepPair[1]);
        assertFalse(pathGenerator.hasNext());

    }

    public void test_EFSM_EdgeStop() throws InterruptedException {
        PathGenerator pathGenerator = new A_StarPathGenerator(new ReachedEdge("E2"));
        ExtendedFiniteStateMachine machine = new ExtendedFiniteStateMachine(false);
        machine.setModel(graph);
        pathGenerator.setMachine(machine);

        String[] stepPair;
        stepPair = pathGenerator.getNext();
        assertEquals("E0", stepPair[0]);
        assertEquals("V1/x=1;y=[];", stepPair[1]);
        stepPair = pathGenerator.getNext();
        assertEquals("E1", stepPair[0]);
        assertEquals("V2/x=2;y=[];", stepPair[1]);
        stepPair = pathGenerator.getNext();
        assertEquals("E2", stepPair[0]);
        assertEquals("V2/x=3;y=[];", stepPair[1]);
        assertFalse(pathGenerator.hasNext());

    }
}
