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
package org.graphwalker.core.model;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphwalker.core.Model;
import org.junit.Test;

/**
 * @author Nils Olsson
 */
public class ModelTest {

    @Test
    public void createModel() {
        Model model = new Model("Single model");
        VerificationPoint v1 = new VerificationPoint("v1");
        model.addVertex(v1);
        VerificationPoint v2 = new VerificationPoint("v2");
        model.addVertex(v2);
        VerificationPoint v3 = new VerificationPoint("v3");
        model.addVertex(v3);
        model.addEdge(new Operation("e1", v1, v2));
        model.addEdge(new Operation("e2", v2, v3));
        model.addEdge(new Operation("e3", v3, v1));
        displayModel(model);
    }

    private void displayModel(Model model) {
        Graph graph = new SingleGraph();
        for (VerificationPoint verificationPoint: model.getVertices()) {
            graph.addNode(verificationPoint.getName());
        }
        for (Operation operation: model.getEdges()) {
            graph.addEdge(operation.getName(), operation.getSource().getName(), operation.getTarget().getName());
        }
        graph.display();
        try {
            Thread.sleep(10000l);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
