/*
 * #%L
 * GraphWalker API
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
package org.graphwalker.api;

import org.graphwalker.api.model.Edge;
import org.graphwalker.api.model.ModelElement;
import org.graphwalker.api.model.Path;
import org.graphwalker.api.model.Vertex;

import java.util.Collection;
import java.util.Set;

/**
 * @author Nils Olsson
 */
public interface Model {
    <T extends Edge> T addEdge();
    <T extends Edge> Set<T> getEdges();
    <T extends Vertex> T addVertex();
    <T extends Vertex> Set<T> getVertices();
    <T extends ModelElement> Set<T> getModelElements();
    <T extends ModelElement> Collection<T> getConnectedComponent();
    <T extends ModelElement> int getShortestDistance(T source, T target);
    <T extends ModelElement> int getMaximumDistance(T target);
    <T extends ModelElement> Path<T> getShortestPath(T source, T target);
}
