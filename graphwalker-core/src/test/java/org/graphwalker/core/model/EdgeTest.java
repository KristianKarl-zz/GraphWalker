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
import org.junit.Test;

import java.util.ArrayList;

public class EdgeTest {

    @Test
    public void createEdgeTest() {
        new Edge("edgeName");
    }

    @Test(expected = ConstraintsViolatedException.class)
    public void createNullEdgeTest() {
        new Edge(null);
    }

    @Test(expected = ConstraintsViolatedException.class)
    public void createEmptyEdgeTest() {
        new Edge("");
    }

    @Test
    public void setBlockedTest() {
        Edge edge = new Edge();
        edge.setBlocked(true);
    }

    @Test
    public void setWeightTest() {
        Edge edge = new Edge();
        edge.setWeight(0.5);
    }

    @Test(expected = ConstraintsViolatedException.class)
    public void setToLargeWeightTest() {
        Edge edge = new Edge();
        edge.setWeight(1.01);
    }

    @Test(expected = ConstraintsViolatedException.class)
    public void setToSmallWeightTest() {
        Edge edge = new Edge();
        edge.setWeight(0.001);
    }

    @Test
    public void setEdgeActionsTest() {
        Edge edge = new Edge();
        edge.setEdgeActions(new ArrayList<Action>());
    }

    @Test(expected = ConstraintsViolatedException.class)
    public void setNullEdgeActionsTest() {
        Edge edge = new Edge();
        edge.setEdgeActions(null);
    }

    @Test
    public void setEdgeGuardTest() {
        Edge edge = new Edge();
        edge.setEdgeGuard(new Guard("guardScript"));
    }

    @Test(expected = ConstraintsViolatedException.class)
    public void setNullEdgeGuardTest() {
        Edge edge = new Edge();
        edge.setEdgeGuard(null);
    }

    @Test
    public void setIdTest() {
        Edge edge = new Edge();
        edge.setId("edgeName");
    }

    @Test(expected = ConstraintsViolatedException.class)
    public void setNullIdTest() {
        Edge edge = new Edge();
        edge.setId(null);
    }

    @Test(expected = ConstraintsViolatedException.class)
    public void setEmptyIdTest() {
        Edge edge = new Edge();
        edge.setId("");
    }

    @Test
    public void setNameTest() {
        Edge edge = new Edge();
        edge.setName("edgeName");
    }

    @Test(expected = ConstraintsViolatedException.class)
    public void setNullNameTest() {
        Edge edge = new Edge();
        edge.setName(null);
    }

    @Test(expected = ConstraintsViolatedException.class)
    public void setEmptyNameTest() {
        Edge edge = new Edge();
        edge.setName("");
    }    

}
