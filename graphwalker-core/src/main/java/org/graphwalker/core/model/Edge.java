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

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Nils Olsson
 */
public final class Edge implements Element {

    private final String name;
    private final Vertex source;
    private final Vertex target;
    private final Boolean blocked;
    private final Double weight;
    private final Guard guard;
    private final Set<Action> actions;
    private final int cachedHashCode;

    public Edge(String name, Vertex source, Vertex target) {
        this(name, source, target, new Guard("true"), new HashSet<Action>());
    }

    public Edge(String name, Vertex source, Vertex target, Boolean blocked) {
        this(name, source, target, new Guard("true"), new HashSet<Action>(), blocked, 1.0);
    }

    public Edge(String name, Vertex source, Vertex target, Boolean blocked, Double weight) {
        this(name, source, target, new Guard("true"), new HashSet<Action>(), blocked, weight);
    }

    public Edge(String name, Vertex source, Vertex target, Set<Action> actions) {
        this(name, source, target, new Guard("true"), actions, false);
    }

    public Edge(String name, Vertex source, Vertex target, Guard guard, Set<Action> actions) {
        this(name, source, target, guard, actions, false);
    }

    public Edge(String name, Vertex source, Vertex target, Guard guard, Set<Action> actions, Boolean blocked) {
        this(name, source, target, guard, actions, blocked, 1.0);
    }

    public Edge(String name, Vertex source, Vertex target, Guard guard, Set<Action> actions, Boolean blocked, Double weight) {
        this.name = name;
        this.source = Validate.notNull(source);
        this.target = Validate.notNull(target);
        this.guard = Validate.notNull(guard);
        this.actions = Collections.unmodifiableSet(Validate.notNull(actions));
        this.blocked = Validate.notNull(blocked);
        this.weight = Validate.notNull(weight);
        this.cachedHashCode = new HashCodeBuilder(23, 47)
                .appendSuper(super.hashCode())
                .append(source.hashCode())
                .append(target.hashCode())
                .append(actions)
                .append(blocked)
                .append(weight)
                .toHashCode();
    }

    public String getName() {
        return name;
    }

    public Vertex getSourceVertex() {
        return source;
    }

    public Vertex getTargetVertex() {
        return target;
    }

    public Guard getGuard() {
        return guard;
    }

    public Set<Action> getActions() {
        return actions;
    }

    public Double getWeight() {
        return weight;
    }

    public Boolean isBlocked() {
        return blocked;
    }

    @Override
    public int hashCode() {
        return this.cachedHashCode;
    }

    @Override
    public boolean equals(Object object) {
        if (null == object) {
            return false;
        }
        if (this == object) {
            return true;
        }
        if (object instanceof Edge) {
            Edge edge = (Edge)object;
            return new EqualsBuilder()
                    .append(name, edge.getName())
                    .append(source, edge.getSourceVertex())
                    .append(target, edge.getTargetVertex())
                    .append(actions, edge.getActions())
                    .append(blocked, edge.isBlocked())
                    .append(weight, edge.getWeight())
                    .isEquals();
        }
        return false;
    }
}
