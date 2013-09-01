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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Nils Olsson
 */
public final class Edge extends ModelElement {

    private final Double weight;
    private final Vertex source;
    private final Vertex target;
    private final Guard guard;
    private final List<Action> actions;

    /**
     * <p>Constructor for Edge.</p>
     *
     * @param id a {@link java.lang.String} object.
     * @param name a {@link java.lang.String} object.
     * @param blocked a {@link java.lang.Boolean} object.
     * @param comment a {@link java.lang.String} object.
     * @param weight a {@link java.lang.Double} object.
     * @param source a {@link org.graphwalker.core.model.Vertex} object.
     * @param target a {@link org.graphwalker.core.model.Vertex} object.
     * @param guard a {@link org.graphwalker.core.model.Guard} object.
     * @param actions a {@link java.util.List} object.
     */
    public Edge(String id, String name, Boolean blocked, String comment, Double weight, Vertex source, Vertex target, Guard guard, List<Action> actions) {
        super(id, name, blocked, comment);
        this.weight = (null!=weight?weight:1.0);
        this.source = source;
        this.target = target;
        this.guard = guard;
        this.actions = Collections.unmodifiableList(null!=actions?actions:new ArrayList<Action>(0));
    }

    /**
     * <p>Getter for the field <code>weight</code>.</p>
     *
     * @return a {@link java.lang.Double} object.
     */
    public Double getWeight() {
        return weight;
    }

    /**
     * <p>Getter for the field <code>source</code>.</p>
     *
     * @return a {@link org.graphwalker.core.model.Vertex} object.
     */
    public Vertex getSource() {
        return source;
    }

    /**
     * <p>Getter for the field <code>target</code>.</p>
     *
     * @return a {@link org.graphwalker.core.model.Vertex} object.
     */
    public Vertex getTarget() {
        return target;
    }

    /**
     * <p>Getter for the field <code>guard</code>.</p>
     *
     * @return a {@link org.graphwalker.core.model.Guard} object.
     */
    public Guard getGuard() {
        return guard;
    }

    /**
     * <p>hasGuard.</p>
     *
     * @return a boolean.
     */
    public boolean hasGuard() {
        return null!=guard;
    }

    /**
     * <p>Getter for the field <code>actions</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Action> getActions() {
        return actions;
    }

}
