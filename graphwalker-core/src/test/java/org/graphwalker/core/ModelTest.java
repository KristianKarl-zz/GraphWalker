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
import org.graphwalker.core.script.Context;
import org.junit.Assert;
import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Arrays;
import java.util.HashSet;

/**
 * @author Nils Olsson
 */
public class ModelTest {

    private ScriptEngine createScriptEngine(String language) {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName(language);
        scriptEngine.setContext(new Context());
        return scriptEngine;
    }

    @Test
    public void equals() {
        Assert.assertEquals(new Action("abc"), new Action("abc"));
        Assert.assertEquals(new Guard("abc"), new Guard("abc"));
        Assert.assertEquals(new Edge("abc"), new Edge("abc"));
        Assert.assertEquals(new Vertex("abc"), new Vertex("abc"));
    }

    @Test
    public void notSame() {
        Assert.assertNotSame(new Action("abc"), new Action("def"));
        Assert.assertNotSame(new Action("abc"), "def");
        Assert.assertNotSame(new Action("abc"), null);
        Assert.assertNotSame(new Guard("abc"), new Guard("def"));
        Assert.assertNotSame(new Guard("abc"), "def");
        Assert.assertNotSame(new Guard("abc"), null);
        Assert.assertNotSame(new Edge("abc"), new Edge("def"));
        Assert.assertNotSame(new Edge("abc"), "def");
        Assert.assertNotSame(new Edge("abc"), null);
        Assert.assertNotSame(new Vertex("abc"), new Vertex("def"));
        Assert.assertNotSame(new Vertex("abc"), "def");
        Assert.assertNotSame(new Vertex("abc"), null);
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
        new Edge(null);
    }

    @Test(expected = Throwable.class)
    public void nullVertex() {
        new Vertex(null);
    }

    @Test
    public void createEdge() throws ScriptException {
        // create script engine and initiate variable
        ScriptEngine scriptEngine = createScriptEngine("JavaScript");
        scriptEngine.eval("var i = 0;");
        // create edge with a guard and a action, that will change the current context
        Edge edge2 = new Edge("edge2", new Guard("i > 0"), new HashSet<Action>(Arrays.<Action>asList(new Action("i++"))));
        Assert.assertEquals("edge2", edge2.getName());
        Assert.assertFalse(edge2.getGuard().isFulfilled(scriptEngine));
        // execute actions
        for (Action action: edge2.getActions()) {
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
                , new HashSet<Requirement>(Arrays.<Requirement>asList(new Requirement("123")))
                , new HashSet<Action>(Arrays.<Action>asList(new Action("var x = 5;")))
                , new HashSet<Action>(Arrays.<Action>asList(new Action("x = 10;"))));
        // execute entry actions
        for (Action action: vertex2.getEntryActions()) {
            action.execute(scriptEngine);
        }
        // execute exit actions
        for (Action action: vertex2.getExitActions()) {
            action.execute(scriptEngine);
        }
        // verify that we can access the created variable and that it has the correct value
        Assert.assertEquals(10d, scriptEngine.getContext().getAttribute("x"));
    }

    @Test
    public void createModel() {
        Model model = new SimpleModel();
        model.addVertex(new Vertex("vertex1"));
        model.addEdge(new Edge("edge1"), new Vertex("vertex1"), new Vertex("vertex2"));
        // assert model contains the added edges and vertices
        Assert.assertEquals(2, model.getVertices().size());
        Assert.assertEquals(1, model.getEdges().size());
        Assert.assertNotNull(model.getEdges("edge1"));
        Assert.assertNotNull(model.getVertex("vertex1"));
        Assert.assertNotNull(model.getVertex("vertex2"));
        Assert.assertNull(model.getEdges("vertex1"));
        Assert.assertNull(model.getVertex("edge1"));
        Assert.assertNull(model.getEdges("NOT_FOUND"));
    }

    @Test
    public void mergeModel() {
        Model merged = new SimpleModel();
        Model scenario1 = new SimpleModel();
        scenario1.addEdge(new Edge("edge1"), new Vertex("vertex1"), new Vertex("vertex2"));
        Model scenario2 = new SimpleModel();
        scenario2.addEdge(new Edge("edge2"), new Vertex("vertex2"), new Vertex("vertex3"));
        merged.addModel(scenario1);
        merged.addModel(scenario2);
        // assert that the model contains both scenarios, but only one instance of the "same" element
        Assert.assertNotNull(merged.getVertex("vertex1"));
        Assert.assertNotNull(merged.getEdges("edge1"));
        Assert.assertNotNull(merged.getVertex("vertex2"));
        Assert.assertNotNull(merged.getEdges("edge2"));
        Assert.assertNotNull(merged.getVertex("vertex3"));
    }

    @Test
    public void connectedComponent() {

    }

    @Test
    public void shortestPath() {

    }

    @Test
    public void shortestDistance() {

    }

    @Test
    public void maximumDistance() {

    }

    @Test
    public void startVertices() {

    }















    /*
    @Test
    public void createModel() {

        SimpleModel model = new SimpleModel("Single model");

        model.addSink(this);

        VerificationPoint v1 = new VerificationPoint("v1");
        model.addVertex(v1);
        VerificationPoint v2 = new VerificationPoint("v2");
        model.addVertex(v2);
        VerificationPoint v3 = new VerificationPoint("v3");
        model.addVertex(v3);
        model.addEdge(new Operation("e1", v1, v2));
        model.addEdge(new Operation("e2", v2, v3));
        model.addEdge(new Operation("e3", v3, v1));
        model.addEdge(new Operation("e4", v3, v3));


        VerificationPoint v4 = new VerificationPoint("v4");
        model.addVertex(v4);
        VerificationPoint v5 = new VerificationPoint("v5");
        model.addVertex(v5);
        VerificationPoint v6 = new VerificationPoint("v6");
        model.addVertex(v6);
        model.addEdge(new Operation("e5", v4, v5));
        model.addEdge(new Operation("e6", v5, v6));
        model.addEdge(new Operation("e7", v6, v4));


        displayModel(model, 10000);
    }

    private void displayModel(SimpleModel model, long timeout) {
        Graph graph = new SingleGraph();
        for (VerificationPoint verificationPoint: model.getVertices()) {
            graph.addNode(verificationPoint.getName());
        }
        for (Operation operation: model.getEdges()) {
            graph.addEdge(operation.getName(), operation.getSourceVertex().getName(), operation.getTargetVertex().getName());
        }
        graph.display();
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void edgeAdded(Edge edge) {
        System.out.println("Edge added "+edge.getName());
    }

    public void vertexAdded(Vertex vertex) {
        System.out.println("Vertex added "+vertex.getName());
    }
    */
}
