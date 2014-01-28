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
public class Vertex implements Element {

    private final String name;
    private final Set<Requirement> requirements;
    private final Set<Action> entryActions;
    private final Set<Action> exitActions;
    private final int cachedHashCode;

    public Vertex(String name) {
        this(name, new HashSet<Requirement>());
    }

    public Vertex(String name, Set<Requirement> requirements) {
        this(name, requirements, new HashSet<Action>());
    }

    public Vertex(String name, Set<Requirement> requirements, Set<Action> entryActions) {
        this(name, requirements, entryActions, new HashSet<Action>());
    }

    public Vertex(String name, Set<Requirement> requirements, Set<Action> entryActions, Set<Action> exitActions) {
        this.name = Validate.notEmpty(Validate.notNull(name));
        this.requirements = Collections.unmodifiableSet(Validate.notNull(requirements));
        this.entryActions = Collections.unmodifiableSet(Validate.notNull(entryActions));
        this.exitActions = Collections.unmodifiableSet(Validate.notNull(exitActions));
        this.cachedHashCode = new HashCodeBuilder(17, 43)
                .append(name)
                .hashCode();
    }

    public String getName() {
        return name;
    }

    public Set<Requirement> getRequirements() {
        return requirements;
    }

    public Set<Action> getEntryActions() {
        return entryActions;
    }

    public Set<Action> getExitActions() {
        return exitActions;
    }

    @Override
    public int hashCode() {
        return cachedHashCode;
    }

    @Override
    public boolean equals(Object object) {
        if (null == object) {
            return false;
        }
        if (this == object) {
            return true;
        }
        if (object instanceof Vertex) {
            Vertex element = (Vertex)object;
            return new EqualsBuilder().append(name, element.getName()).isEquals();
        }
        return false;
    }
}
