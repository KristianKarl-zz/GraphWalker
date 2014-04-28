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

import org.graphwalker.core.model.*;
import org.graphwalker.core.script.ScriptContext;
import org.junit.Assert;
import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Nils Olsson
 */
public class ModelTest {

    private ScriptEngine createScriptEngine(String language) {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName(language);
        scriptEngine.setContext(new ScriptContext());
        return scriptEngine;
    }

    @Test
    public void emptyEdgeName() {
        new Edge("", new Vertex("vertex1"), new Vertex("vertex2"));
    }

    @Test
    public void emptyVertexName() {
        new Vertex("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyGuard() {
        new Guard("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyAction() {
        new Action("");
    }

    @Test
    public void equals() {
        Vertex vertex1 = new Vertex("vertex1");
        Vertex vertex2 = new Vertex("vertex2");
        Assert.assertEquals(new Action("abc"), new Action("abc"));
        Assert.assertEquals(new Guard("abc"), new Guard("abc"));
        Assert.assertEquals(new Edge("abc", vertex1, vertex2), new Edge("abc", vertex1, vertex2));
        Assert.assertEquals(new Edge("abc", vertex1, vertex2, new Guard("true"), new HashSet<Action>())
                , new Edge("abc", vertex1, vertex2, new Guard("true"), new HashSet<Action>()));
        Assert.assertEquals(new Edge("abc", vertex1, vertex2, new Guard("true"), new HashSet<Action>(), false)
                , new Edge("abc", vertex1, vertex2, new Guard("true"), new HashSet<Action>(), false));
        Assert.assertEquals(new Edge("abc", vertex1, vertex2, new Guard("true"), new HashSet<Action>(), false, 2.0)
                , new Edge("abc", vertex1, vertex2, new Guard("true"), new HashSet<Action>(), false, 2.0));
        Assert.assertEquals(new Vertex("abc"), new Vertex("abc"));
    }

    @Test
    public void notSame() {
        Vertex vertex1 = new Vertex("vertex1");
        Vertex vertex2 = new Vertex("vertex2");
        Vertex vertex3 = new Vertex("vertex3");
        Assert.assertNotEquals(new Action("abc"), new Action("def"));
        Assert.assertNotEquals(new Action("abc"), "def");
        Assert.assertNotEquals(new Action("abc"), null);
        Assert.assertNotEquals(new Guard("abc"), new Guard("def"));
        Assert.assertNotEquals(new Guard("abc"), "def");
        Assert.assertNotEquals(new Guard("abc"), null);
        Assert.assertNotEquals(new Edge("abc", vertex1, vertex2), new Edge("abc", vertex1, vertex3));
        Assert.assertNotEquals(new Edge("abc", vertex1, vertex2), new Edge("def", vertex1, vertex2));
        Assert.assertNotEquals(new Edge("abc", vertex1, vertex2, new Guard("123")
                , new HashSet<>(Arrays.asList(new Action("1234"))), true, 2.0)
                , new Edge("abc", vertex1, vertex2, new Guard("123")
                , new HashSet<>(Arrays.asList(new Action("1234"))), true, 2.1));
        Assert.assertNotEquals(new Edge("abc", vertex1, vertex2, new Guard("123")
                , new HashSet<>(Arrays.asList(new Action("1234"))), false, 2.0)
                , new Edge("abc", vertex1, vertex2, new Guard("123")
                , new HashSet<>(Arrays.asList(new Action("1234"))), true, 2.0));
        Assert.assertNotEquals(new Edge("abc", vertex1, vertex2), "def");
        Assert.assertNotEquals(new Edge("abc", vertex1, vertex2), null);
        Assert.assertNotEquals(new Vertex("abc"), new Vertex("def"));
        Assert.assertNotEquals(new Vertex("abc"), "def");
        Assert.assertNotEquals(new Vertex("abc"), null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void modifyEdgeActions() {
        Vertex vertex1 = new Vertex("vertex1");
        Vertex vertex2 = new Vertex("vertex2");
        Set<Action> actions = new HashSet<>();
        actions.add(new Action("abc"));
        Edge edge = new Edge("edge1", vertex1, vertex2, actions);
        Assert.assertNotNull(edge.getActions());
        Assert.assertEquals(1, edge.getActions().size());
        edge.getActions().add(new Action("def"));
        Assert.assertEquals(1, edge.getActions().size());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void modifyVertexRequirements() {
        Vertex vertex = new Vertex("vertex", new HashSet<>(Arrays.asList(new Requirement("1"))));
        Assert.assertNotNull(vertex.getRequirements());
        Assert.assertEquals(1, vertex.getRequirements().size());
        vertex.getRequirements().clear();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void modifyVertexEntryActions() {
        Vertex vertex = new Vertex("vertex"
                , new HashSet<Requirement>()
                , new HashSet<>(Arrays.asList(new Action("1"))));
        Assert.assertNotNull(vertex.getEntryActions());
        Assert.assertEquals(1, vertex.getEntryActions().size());
        vertex.getEntryActions().clear();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void modifyVertexExitActions() {
        Vertex vertex = new Vertex("vertex"
                , new HashSet<Requirement>()
                , new HashSet<Action>()
                , new HashSet<>(Arrays.asList(new Action("1"))));
        Assert.assertNotNull(vertex.getExitActions());
        Assert.assertEquals(1, vertex.getExitActions().size());
        vertex.getEntryActions().clear();
    }

    @Test(expected = Throwable.class)
    public void nullAction() {
        new Action(null);
    }

    @Test(expected = Throwable.class)
    public void nullGuard() {
        new Guard(null);
    }

    @Test(expected = Throwable.class)
    public void nullEdge() {
        new Edge(null, null, null);
    }

    @Test(expected = Throwable.class)
    public void nullEdgeSourceVertex() {
        new Edge("abc", null, new Vertex("vertex2"));
    }

    @Test(expected = Throwable.class)
    public void nullEdgeTargetVertex() {
        new Edge("abc", new Vertex("vertex1"), null);
    }

    @Test
    public void nullVertex() {
        new Vertex(null);
    }

    @Test
    public void createEdge() throws ScriptException {
        // create script engine and initiate variable
        ScriptEngine scriptEngine = createScriptEngine("JavaScript");
        scriptEngine.eval("var i = 0;");
        // create edge with a guard and a action, that will change the current context
        Edge edge2 = new Edge("edge2"
                , new Vertex("vertex1")
                , new Vertex("vertex2")
                , new Guard("i > 0")
                , new HashSet<>(Arrays.<Action>asList(new Action("i++"))));
        Assert.assertEquals("edge2", edge2.getName());
        Assert.assertFalse(edge2.getGuard().isFulfilled(scriptEngine));
        // execute actions
        for (Action action : edge2.getActions()) {
            action.execute(scriptEngine);
        }
        // verify that the guard is satisfied
        Assert.assertTrue(edge2.getGuard().isFulfilled(scriptEngine));
    }

    @Test
    public void createVertex() throws ScriptException {
        // create script engine, and execute entry and exit actions
        ScriptEngine scriptEngine = createScriptEngine("JavaScript");
        Vertex vertex2 = new Vertex("vertex2"
                , new HashSet<>(Arrays.<Requirement>asList(new Requirement("123")))
                , new HashSet<>(Arrays.<Action>asList(new Action("var x = 5;")))
                , new HashSet<>(Arrays.<Action>asList(new Action("x = 10;"))));
        // execute entry actions
        for (Action action : vertex2.getEntryActions()) {
            action.execute(scriptEngine);
        }
        // execute exit actions
        for (Action action : vertex2.getExitActions()) {
            action.execute(scriptEngine);
        }
        // verify that we can access the created variable and that it has the correct value
        Assert.assertEquals(10d, scriptEngine.getContext().getAttribute("x"));
    }

    @Test
    public void createModel() {
        Model model = new SimpleModel()
                .addVertex(new Vertex("vertex1"))
                .addVertex(new Vertex("vertex2"))
                .addEdge(new Edge("edge1"
                        , new Vertex("vertex1")
                        , new Vertex("vertex2")));
        // assert model contains the added edges and vertices
        Assert.assertEquals(2, model.getVertices().size());
        Assert.assertEquals(1, model.getEdges().size());
        Assert.assertNotNull(model.getVertex("vertex1"));
        Assert.assertNotNull(model.getVertex("vertex2"));
        Assert.assertNull(model.getVertex("edge1"));
        Assert.assertEquals(model.getEdges().get(0).getSourceVertex(), model.getVertex("vertex1"));
        // add a new edge with the same name
        model = model.addEdge(new Edge("edge1", model.getVertex("vertex2"), model.getVertex("vertex1")));
        Assert.assertEquals(2, model.getEdges().size());
        Assert.assertEquals(model.getEdges().get(0).getSourceVertex(), model.getEdges().get(1).getTargetVertex());
        Assert.assertEquals(model.getEdges().get(0).getTargetVertex(), model.getEdges().get(1).getSourceVertex());
    }

    @Test
    public void mergeModel() {
        Model scenario1 = new SimpleModel();
        scenario1 = scenario1.addVertex(new Vertex("vertex1"));
        scenario1 = scenario1.addVertex(new Vertex("vertex2"
                , new HashSet<>(Arrays.<Requirement>asList(new Requirement("123")))));
        scenario1 = scenario1.addEdge(new Edge("edge1", scenario1.getVertex("vertex1"), scenario1.getVertex("vertex2")));
        Model scenario2 = new SimpleModel();
        scenario2 = scenario2.addVertex(new Vertex("vertex2"
                , new HashSet<>(Arrays.<Requirement>asList(new Requirement("123"), new Requirement("456")))));
        scenario2 = scenario2.addVertex(new Vertex("vertex3"));
        scenario2 = scenario2.addEdge(new Edge("edge2", scenario2.getVertex("vertex2"), scenario2.getVertex("vertex3")));
        Model merged = scenario1.addModel(scenario2);
        // assert that the model contains both scenarios, but only one instance of the "same" element
        Assert.assertNotNull(merged.getVertex("vertex1"));
        Assert.assertNotNull(merged.getVertex("vertex2"));
        Assert.assertNotNull(merged.getVertex("vertex3"));
        Assert.assertNotNull(merged.getVertex("vertex2").getRequirements());
        Assert.assertEquals(2, merged.getVertex("vertex2").getRequirements().size());
    }

    @Test
    public void connectedComponent() {
        Model model = new SimpleModel()
            .addEdge(new Edge("e1", new Vertex("v1"), new Vertex("v2")))
            .addEdge(new Edge("e1", new Vertex("v1"), new Vertex("v3")))
            .addEdge(new Edge("e1", new Vertex("v3"), new Vertex("v4")))
            .addEdge(new Edge("e1", new Vertex("v4"), new Vertex("v5")))
            .addEdge(new Edge("e1", new Vertex("v2"), new Vertex("v5")));
        Model anotherModel = new SimpleModel();
        for (Element element: model.getConnectedComponent(model.getVertex("v1"))) {
            if (element instanceof Edge) {
                anotherModel = anotherModel.addEdge((Edge)element);
            }
        }
        Assert.assertEquals(model.getElements().size(), anotherModel.getElements().size());
        Model smallerModel = new SimpleModel();
        for (Element element: model.getConnectedComponent(model.getVertex("v2"))) {
            if (element instanceof Edge) {
                smallerModel = smallerModel.addEdge((Edge) element);
            }
        }
        Assert.assertEquals(3, smallerModel.getElements().size());
    }

    @Test
    public void shortestPath() {
        Model model = new SimpleModel()
            .addEdge(new Edge("e1", new Vertex("v1"), new Vertex("v2")))
            .addEdge(new Edge("e2", new Vertex("v1"), new Vertex("v3")))
            .addEdge(new Edge("e2", new Vertex("v3"), new Vertex("v4")))
            .addEdge(new Edge("e2", new Vertex("v4"), new Vertex("v5")))
            .addEdge(new Edge("e2", new Vertex("v2"), new Vertex("v5")));
        Assert.assertEquals(5, model.getShortestPath(model.getVertex("v1"), model.getVertex("v5")).size());
    }

    @Test
    public void shortestDistance() {
        Model model = new SimpleModel()
            .addEdge(new Edge("e1", new Vertex("v1"), new Vertex("v2")))
            .addEdge(new Edge("e2", new Vertex("v1"), new Vertex("v3")))
            .addEdge(new Edge("e2", new Vertex("v3"), new Vertex("v4")))
            .addEdge(new Edge("e2", new Vertex("v4"), new Vertex("v5")))
            .addEdge(new Edge("e2", new Vertex("v2"), new Vertex("v5")));
        Assert.assertEquals(4, model.getShortestDistance(model.getVertex("v1"), model.getVertex("v5")));
        Model model2 = new SimpleModel().addEdge(new Edge("e1", new Vertex("v1"), new Vertex("v2")));
        Assert.assertEquals(model2.getShortestDistance(model2.getVertex("v1"), model2.getVertex("v2"))+1
                , model2.getShortestPath(model2.getVertex("v1"), model2.getVertex("v2")).size());
    }

    @Test
    public void maximumDistance() {
        Model model = new SimpleModel()
            .addEdge(new Edge("e1", new Vertex("v1"), new Vertex("v2")))
            .addEdge(new Edge("e2", new Vertex("v1"), new Vertex("v3")))
            .addEdge(new Edge("e2", new Vertex("v3"), new Vertex("v4")))
            .addEdge(new Edge("e2", new Vertex("v4"), new Vertex("v5")))
            .addEdge(new Edge("e2", new Vertex("v2"), new Vertex("v5")));
        Assert.assertEquals(5, model.getMaximumDistance(model.getVertex("v5")));
        Model model2 = new SimpleModel().addEdge(new Edge("e1", new Vertex("v1"), new Vertex("v2")));
        Assert.assertEquals(model2.getShortestDistance(model2.getVertex("v1"), model2.getVertex("v2"))
                , model2.getMaximumDistance(model2.getVertex("v2")));
    }

    @Test
    public void startVertices() {
        Model model = new SimpleModel()
            .addEdge(new Edge("e1", new Vertex("v1"), new Vertex("v2")))
            .addEdge(new Edge("e2", new Vertex("v1"), new Vertex("v3")))
            .addEdge(new Edge("e2", new Vertex("v3"), new Vertex("v4")))
            .addEdge(new Edge("e2", new Vertex("v4"), new Vertex("v5")))
            .addEdge(new Edge("e2", new Vertex("v2"), new Vertex("v5")));
        Assert.assertEquals(1, model.getStartVertices().size());
        model = model.addEdge(new Edge("e0", new Vertex("v0"), new Vertex("v2")));
        Assert.assertEquals(2, model.getStartVertices().size());
    }

    @Test
    public void aggregateRequirements() {
        Model model = new SimpleModel()
            .addVertex(new Vertex("A", new HashSet<>(Arrays.asList(new Requirement("A1"), new Requirement("A2")))))
            .addVertex(new Vertex("B", new HashSet<>(Arrays.asList(new Requirement("B1")))));
        Assert.assertEquals(3, model.getRequirements().size());
        Assert.assertTrue(model.getRequirements().contains(new Requirement("A1")));
        Assert.assertTrue(model.getRequirements().contains(new Requirement("A2")));
        Assert.assertTrue(model.getRequirements().contains(new Requirement("B1")));
    }
}
