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

import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.constraint.Range;
import net.sf.oval.guard.Guarded;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Edge class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
@Guarded
public class Edge extends AbstractElement {

    private Vertex mySource;
    private Vertex myTarget;
    private List<Action> myEdgeActions = new ArrayList<Action>();
    private Guard myEdgeGuard;
    private boolean myIsBlocked = false;
    private double myWeight = 1.0;

    /**
     * <p>Constructor for Edge.</p>
     */
    public Edge() {
    }

    /**
     * <p>Constructor for Edge.</p>
     *
     * @param name a {@link java.lang.String} object.
     */
    public Edge(String name) {
        super(name);
    }

    /**
     * <p>setSource.</p>
     *
     * @param source a {@link org.graphwalker.core.model.Vertex} object.
     */
    public void setSource(@NotNull Vertex source) {
        mySource = source;
    }

    /**
     * <p>getSource.</p>
     *
     * @return a {@link org.graphwalker.core.model.Vertex} object.
     */
    public @NotNull Vertex getSource() {
        return mySource;
    }

    /**
     * <p>setTarget.</p>
     *
     * @param target a {@link org.graphwalker.core.model.Vertex} object.
     */
    public void setTarget(@NotNull Vertex target) {
        myTarget = target;
    }

    /**
     * <p>getTarget.</p>
     *
     * @return a {@link org.graphwalker.core.model.Vertex} object.
     */
    public @NotNull Vertex getTarget() {
        return myTarget;
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean isVisited() {
        return super.isVisited()||isBlocked();
    }

    /**
     * <p>isBlocked.</p>
     *
     * @return true if this Edge is blocked, indicating that this edge should not be used when generating a path
     */
    public boolean isBlocked() {
        return myIsBlocked;
    }

    /**
     * <p>setBlocked.</p>
     *
     * @param blocked a boolean.
     */
    public void setBlocked(boolean blocked) {
        myIsBlocked = blocked;
    }

    /**
     * <p>getWeight.</p>
     *
     * @return a double.
     */
    public double getWeight() {
        return myWeight;
    }

    /**
     * <p>setWeight.</p>
     *
     * @param weight a double.
     */
    public void setWeight(@Range(min = 0.01, max = 1.0) double weight) {
        myWeight = weight;
    }

    /**
     * <p>getEdgeActions.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Action> getEdgeActions() {
        return myEdgeActions;
    }

    /**
     * <p>setEdgeActions.</p>
     *
     * @param edgeActions a {@link java.util.List} object.
     */
    public void setEdgeActions(@NotNull List<Action> edgeActions) {
        myEdgeActions = edgeActions;
    }

    /**
     * <p>getEdgeGuard.</p>
     *
     * @return a {@link org.graphwalker.core.model.Guard} object.
     */
    public Guard getEdgeGuard() {
        return myEdgeGuard;
    }

    /**
     * <p>setEdgeGuard.</p>
     *
     * @param edgeGuard a {@link Guard} object.
     */
    public void setEdgeGuard(@NotNull Guard edgeGuard) {
        myEdgeGuard = edgeGuard;
    }
}
