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

import org.graphwalker.core.common.Assert;
import org.graphwalker.core.condition.EdgeCoverage;
import org.graphwalker.core.condition.Never;
import org.graphwalker.core.condition.ReachedVertex;
import org.graphwalker.core.generator.AStarPath;
import org.graphwalker.core.generator.RandomLeastVisitedPath;
import org.graphwalker.core.generator.RandomPath;
import org.graphwalker.core.generator.RandomUnvisitedFirstPath;
import org.graphwalker.core.machine.ExecutionContext;
import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.Vertex;
import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

/**
 * @author Nils Olsson
 */
public class PathGeneratorTest {

    @Test
    public void AStarPathTest() {
        Model model = new SimpleModel()
                .addEdge(new Edge("e0", new Vertex("v0"), new Vertex("v1")))
                .addEdge(new Edge("e1", new Vertex("v1"), new Vertex("v2")))
                .addEdge(new Edge("e2", new Vertex("v1"), new Vertex("v3")))
                .addEdge(new Edge("e3", new Vertex("v2"), new Vertex("v5")))
                .addEdge(new Edge("e4", new Vertex("v3"), new Vertex("v4")))
                .addEdge(new Edge("e5", new Vertex("v1"), new Vertex("v5")));
        PathGenerator pathGenerator = new AStarPath(new ReachedVertex("v5"));
        ExecutionContext context = new ExecutionContext(model, pathGenerator);
        Machine machine = new SimpleMachine(context);
        Deque<String> expectedPath = new ArrayDeque<>(Arrays.asList("e0", "v1", "e5", "v5"));
        Deque<String> path = new ArrayDeque<>();
        while (machine.hasNextStep()) {
            path.push(machine.getNextStep().getName());
            Assert.assertEquals(expectedPath.pop(), path.peek(), path.toString());
        }
        System.out.println(path.toArray());
    }

    @Test
    public void AStarPathTestBlocked() {
        Model model = new SimpleModel()
                .addEdge(new Edge("e0", new Vertex("v0"), new Vertex("v1")))
                .addEdge(new Edge("e1", new Vertex("v1"), new Vertex("v2")))
                .addEdge(new Edge("e2", new Vertex("v1"), new Vertex("v3")))
                .addEdge(new Edge("e3", new Vertex("v2"), new Vertex("v5")))
                .addEdge(new Edge("e4", new Vertex("v3"), new Vertex("v4")))
                .addEdge(new Edge("e5", new Vertex("v1"), new Vertex("v5"), true));
        PathGenerator pathGenerator = new AStarPath(new ReachedVertex("v5"));
        ExecutionContext context = new ExecutionContext(model, pathGenerator);
        Machine machine = new SimpleMachine(context);
        Deque<String> expectedPath = new ArrayDeque<>(Arrays.asList("e0", "v1", "e1", "v2", "e3", "v5"));
        Deque<String> path = new ArrayDeque<>();
        while (machine.hasNextStep()) {
            path.push(machine.getNextStep().getName());
            Assert.assertEquals(expectedPath.pop(), path.peek(), path.toString());
        }
        System.out.println(path.toArray());
    }

    @Test
    public void RandomLeastVisitedPath() {
        Model model = new SimpleModel()
                .addEdge(new Edge("e0", new Vertex("v0"), new Vertex("v1")))
                .addEdge(new Edge("e1", new Vertex("v1"), new Vertex("v1")))
                .addEdge(new Edge("e2", new Vertex("v1"), new Vertex("v1")));
        PathGenerator pathGenerator = new RandomLeastVisitedPath(new Never());
        ExecutionContext context = new ExecutionContext(model, pathGenerator);
        Machine machine = new SimpleMachine(context);
        Deque<String> names = new ArrayDeque<>(Arrays.asList("v0", "e0", "v1", "ANY", "v1", "OTHER", "v1", "ANY", "v1", "OTHER"));
        String visited = "NONE";
        Deque<String> path = new ArrayDeque<>();
        while (machine.hasNextStep()) {
            path.push(machine.getNextStep().getName());
            String expected = names.pop();
            switch (expected) {
                case "ANY": {
                    visited = path.peek();
                }
                break;
                case "OTHER": {
                    switch (visited) {
                        case "e1":
                            Assert.assertEquals(path.peek(), "e2", path.toString());
                            break;
                        case "e2":
                            Assert.assertEquals(path.peek(), "e1", path.toString());
                            break;
                    }
                    visited = path.peek();
                }
                break;
                default:
                    Assert.assertEquals(path.peek(), expected, path.toString());
            }
            if (names.isEmpty()) {
                break;
            }
        }
    }

    @Test
    public void RandomPathReachedVertex() {
        Model model = new SimpleModel()
                .addEdge(new Edge("e0", new Vertex("v0"), new Vertex("v1")))
                .addEdge(new Edge("e1", new Vertex("v1"), new Vertex("v1")))
                .addEdge(new Edge("e2", new Vertex("v1"), new Vertex("v1")));
        PathGenerator pathGenerator = new RandomPath(new ReachedVertex("v1"));
        ExecutionContext context = new ExecutionContext(model, pathGenerator);
        Machine machine = new SimpleMachine(context);
        boolean e1Visited = false, e2Visited = false;
        while (machine.hasNextStep()) {
            switch (machine.getNextStep().getName()) {
                case "e1":
                    e1Visited = true;
                    break;
                case "e2":
                    e2Visited = true;
                    break;
            }
        }
        Assert.assertFalse(e1Visited || e2Visited);
    }

    @Test
    public void RandomPath() {
        Model model = new SimpleModel()
                .addEdge(new Edge("e0", new Vertex("v0"), new Vertex("v1")))
                .addEdge(new Edge("e1", new Vertex("v1"), new Vertex("v1")))
                .addEdge(new Edge("e2", new Vertex("v1"), new Vertex("v1")));
        PathGenerator pathGenerator = new RandomPath(new EdgeCoverage());
        ExecutionContext context = new ExecutionContext(model, pathGenerator);
        Machine machine = new SimpleMachine(context);
        boolean e1Visited = false, e2Visited = false;
        while (machine.hasNextStep()) {
            switch (machine.getNextStep().getName()) {
                case "e1":
                    e1Visited = true;
                    break;
                case "e2":
                    e2Visited = true;
                    break;
            }
        }
        Assert.assertTrue(e1Visited && e2Visited);
    }

    @Test
    public void RandomUnvisitedFirstPath() {
        Model model = new SimpleModel()
                .addEdge(new Edge("e0", new Vertex("v0"), new Vertex("v1")))
                .addEdge(new Edge("e1", new Vertex("v1"), new Vertex("v1")))
                .addEdge(new Edge("e2", new Vertex("v1"), new Vertex("v1")));
        PathGenerator pathGenerator = new RandomUnvisitedFirstPath(new EdgeCoverage());
        ExecutionContext context = new ExecutionContext(model, pathGenerator);
        Machine machine = new SimpleMachine(context);
        Deque<String> names = new ArrayDeque<>(Arrays.asList("v0", "e0", "v1", "ANY", "v1", "OTHER"));
        Deque<String> visited = new ArrayDeque<>();
        while (machine.hasNextStep()) {
            String current = machine.getNextStep().getName();
            String expected = names.pop();
            switch (expected) {
                case "ANY": {
                }
                break;
                case "OTHER": {
                    switch (visited.peek()) {
                        case "e1":
                            Assert.assertEquals(current, "e2", visited.toString());
                            break;
                        case "e2":
                            Assert.assertEquals(current, "e1", visited.toString());
                            break;
                    }
                }
                break;
                default:
                    Assert.assertEquals(current, expected, visited.toString());
            }
            visited.push(current);
        }
    }
}
