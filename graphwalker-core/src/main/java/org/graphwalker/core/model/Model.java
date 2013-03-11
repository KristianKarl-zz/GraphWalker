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

import org.graphwalker.core.utils.Collection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Model extends Element {

    private final List<Vertex> vertices;
    private final List<Edge> edges;
    private final Map<String, Edge> edgeIdMap = new HashMap<String, Edge>();
    private final Map<String, Vertex> vertexIdMap = new HashMap<String, Vertex>();

    public Model(String id, List<Vertex> vertices, List<Edge> edges) {
        super(id);
        this.vertices = Collection.unmodifiableList(vertices);
        this.edges = Collection.unmodifiableList(edges);
        prepareEdgeIdMap();
        prepareVertexIdMap();
    }

    private void prepareEdgeIdMap() {
        for (Edge edge: edges) {
            edgeIdMap.put(edge.getId(), edge);
        }
    }

    private void prepareVertexIdMap() {
        for (Vertex vertex: vertices) {
            vertexIdMap.put(vertex.getId(), vertex);
        }
    }

    public Edge getEdgeById(String id) {
        return edgeIdMap.get(id);
    }

    public Vertex getVertexById(String id) {
        return vertexIdMap.get(id);
    }

    public Vertex getStartVertex() {
        /*
        List<Vertex> vertices = findByName(Resource.getText(Bundle.NAME, "start.vertex"));
        if (1 < vertices.size()) {
            throw new ModelException(Resource.getText(Bundle.NAME, "exception.duplicate.start.vertex"));
        }
        if (1 > vertices.size()) {
            throw new ModelException(Resource.getText(Bundle.NAME, "exception.start.vertex.missing"));
        }
        return vertices.get(0);
        */
        return null;
    }
}
