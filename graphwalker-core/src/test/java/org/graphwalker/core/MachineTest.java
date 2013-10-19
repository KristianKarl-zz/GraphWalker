/*
 * #%L
 * GraphWalker Core
 * %%
 * Copyright (C) 2011 - 2013 GraphWalker
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

import org.graphwalker.core.condition.VertexCoverage;
import org.graphwalker.core.generator.RandomPath;
import org.graphwalker.core.machine.ExecutionContext;
import org.graphwalker.core.model.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;

/**
 * @author Nils Olsson
 */
public class MachineTest {

    @Test
    public void simpleMachine() {
        Model model = new SimpleModel().addEdge(new Edge("e1", new Vertex("v1"), new Vertex("v2")));
        PathGenerator pathGenerator = new RandomPath(new VertexCoverage());
        ExecutionContext context = new ExecutionContext(model, pathGenerator);
        Machine machine = new SimpleMachine(context);
        Deque<String> names = new ArrayDeque<String>(Arrays.asList("v1", "e1", "v2", "ERROR"));
        while (machine.hasNextStep()) {
            Element e = machine.getNextStep();
            Assert.assertEquals(names.pop(), machine.getCurrentStep().getName());
        }
    }

    @Test
    public void restartMachine() {
        Model model = new SimpleModel().addEdge(new Edge("e1", new Vertex("v1"), new Vertex("v2")));
        PathGenerator pathGenerator = new RandomPath(new VertexCoverage());
        ExecutionContext context = new ExecutionContext(model, pathGenerator);
        Machine machine = new SimpleMachine(context);
        Deque<String> names = new ArrayDeque<String>(Arrays.asList("v1", "e1", "v1", "e1", "v2", "ERROR"));
        Assert.assertEquals(names.pop(), machine.getNextStep().getName());
        Assert.assertEquals(names.pop(), machine.getNextStep().getName());
        // e.g. if an error occurs or we "reach the end of the road", the reset function can restart the machine and keep the current states
        machine.restart();
        Assert.assertEquals(names.pop(), machine.getNextStep().getName());
        Assert.assertEquals(2, machine.getCurrentExecutionContext().getVisitCount(machine.getCurrentStep()).longValue());
        while (machine.hasNextStep()) {
            Assert.assertEquals(names.pop(), machine.getNextStep().getName());
        }
    }

    @Test
    public void executeAction() {
        Model model = new SimpleModel().addEdge(new Edge("e1", new Vertex("v1"), new Vertex("v2")
                , new HashSet<Action>(Arrays.asList(new Action("var i = 3;")))));
        PathGenerator pathGenerator = new RandomPath(new VertexCoverage());
        ExecutionContext context = new ExecutionContext(model, pathGenerator);
        Machine machine = new SimpleMachine(context);
        Assert.assertEquals("v1", machine.getNextStep().getName());
        Assert.assertEquals("e1", machine.getNextStep().getName());
        Assert.assertEquals(3.0, machine.getScriptContext().getAttribute("i"));
    }

    @Test
    public void executeVertexActions() {
        Model model = new SimpleModel().addEdge(new Edge("e1"
                , new Vertex("v1", new HashSet<Requirement>(), new HashSet<Action>(), new HashSet<Action>(Arrays.asList(new Action("var i = 1;"))))
                , new Vertex("v2", new HashSet<Requirement>(), new HashSet<Action>(Arrays.asList(new Action("i = 2;"))))));
        PathGenerator pathGenerator = new RandomPath(new VertexCoverage());
        ExecutionContext context = new ExecutionContext(model, pathGenerator);
        Machine machine = new SimpleMachine(context);
        Assert.assertNull(context.getScriptContext().getAttribute("i"));
        Assert.assertEquals("v1", machine.getNextStep().getName());
        Assert.assertNull(context.getScriptContext().getAttribute("i"));
        Assert.assertEquals("e1", machine.getNextStep().getName());
        Assert.assertNotNull(context.getScriptContext().getAttribute("i"));
        Assert.assertEquals(1.0, context.getScriptContext().getAttribute("i"));
        Assert.assertEquals("v2", machine.getNextStep().getName());
        Assert.assertEquals(2.0, context.getScriptContext().getAttribute("i"));
        Assert.assertFalse(machine.hasNextStep());
    }

    @Test
    public void switchModel() {

    }
}
