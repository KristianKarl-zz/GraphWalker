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
package org.graphwalker.temp.model;

import org.graphwalker.api.model.Edge;
import org.graphwalker.api.model.ModelElement;
import org.graphwalker.api.model.Path;
import org.graphwalker.api.model.Vertex;

import java.util.Collection;
import java.util.Set;

/**
 * @author Nils Olsson
 */
public class Model implements org.graphwalker.api.Model {

    public <T extends Edge> T addEdge() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public <T extends Edge> Set<T> getEdges() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public <T extends Vertex> T addVertex() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public <T extends Vertex> Set<T> getVertices() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public <T extends ModelElement> Set<T> getModelElements() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public <T extends ModelElement> Collection<T> getConnectedComponent() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public <T extends ModelElement> int getShortestDistance(T source, T target) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public <T extends ModelElement> int getMaximumDistance(T target) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public <T extends ModelElement> Path<T> getShortestPath(T source, T target) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
