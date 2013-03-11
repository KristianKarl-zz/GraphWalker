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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CachedElementFactory {

    private Map<Integer, Immutable> cache = new HashMap<Integer, Immutable>();

    private Immutable getCached(Immutable immutable) {
        if (!cache.containsKey(immutable.hashCode())) {
            cache.put(immutable.hashCode(), immutable);
        }
        return cache.get(immutable.hashCode());
    }

    public Model createModel(String id, List<Vertex> vertices, List<Edge> edges) {
        return (Model)getCached(new Model(id, vertices, edges));
    }

    public Vertex createVertex(String id, String name, String modelSwitchId, String comment, List<Requirement> requirements) {
        return (Vertex)getCached(new Vertex(id, name, modelSwitchId, comment, requirements));
    }

    public Edge createEdge(String id, String name, Vertex source, Vertex target, Guard guard, List<Action> actions, Boolean blocked, String comment) {
        return (Edge)getCached(new Edge(id, name, source, target, guard, actions, blocked, comment));
    }

    public Requirement createRequirement(String id, String name) {
        return (Requirement)getCached(new Requirement(id, name));
    }

    public Guard createGuard(String id, String script) {
        return (Guard)getCached(new Guard(id, script));
    }

    public Action createAction(String id, String script) {
        return (Action)getCached(new Action(id, script));
    }
}
