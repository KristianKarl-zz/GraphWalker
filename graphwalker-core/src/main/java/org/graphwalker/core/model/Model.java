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

import org.graphwalker.core.algorithms.DepthFirstSearch;
import org.graphwalker.core.algorithms.FloydWarshall;

import java.util.*;

public final class Model extends Element {

    private final Set<ModelElement> modelElementsCache;
    private final Map<String, Edge> edgeIdCache;
    private final Map<String, Vertex> vertexIdCache;
    private final Map<Vertex, List<Edge>> vertexEdgeCache;

    private final Vertex startVertex;
    private final DepthFirstSearch depthFirstSearch;
    private final FloydWarshall floydWarshall;


    public Model(String id, List<Vertex> vertices, List<Edge> edges, Vertex startVertex) {
        super(id);
        Set<ModelElement> modelElementsCache = new HashSet<ModelElement>();
        Map<String, Vertex> vertexIdCache = new HashMap<String, Vertex>();
        Map<Vertex, List<Edge>> vertexEdgeCache = new HashMap<Vertex, List<Edge>>();
        if (null != vertices) {
            for (Vertex vertex: vertices) {
                vertexIdCache.put(vertex.getId(), vertex);
                vertexEdgeCache.put(vertex, new ArrayList<Edge>());
                modelElementsCache.add(vertex);
            }
        }
        Map<String, Edge> edgeIdCache = new HashMap<String, Edge>();
        if (null != edges) {
            for (Edge edge: edges) {
                edgeIdCache.put(edge.getId(), edge);
                modelElementsCache.add(edge);
                Vertex vertex = edge.getSource();
                if (vertexEdgeCache.containsKey(vertex)) {
                    vertexEdgeCache.get(vertex).add(edge);
                } else {
                    vertexEdgeCache.put(vertex, Arrays.asList(edge));
                }
            }
        }
        Map<Vertex, List<Edge>> unmodifiableVertexEdgeCache = new HashMap<Vertex, List<Edge>>();
        for (Vertex vertex: vertexEdgeCache.keySet()) {
            unmodifiableVertexEdgeCache.put(vertex, Collections.unmodifiableList(vertexEdgeCache.get(vertex)));
        }
        this.startVertex = startVertex;
        this.vertexIdCache = Collections.unmodifiableMap(vertexIdCache);
        this.edgeIdCache = Collections.unmodifiableMap(edgeIdCache);
        this.vertexEdgeCache = Collections.unmodifiableMap(unmodifiableVertexEdgeCache);
        this.modelElementsCache = Collections.unmodifiableSet(modelElementsCache);
        this.depthFirstSearch = new DepthFirstSearch(this);
        this.floydWarshall = new FloydWarshall(this);
    }

    public Set<ModelElement> getModelElements() {
        return modelElementsCache;
    }

    public List<Edge> getEdges(Vertex vertex) {
        return vertexEdgeCache.get(vertex);
    }

    public Edge getEdgeById(String id) {
        return edgeIdCache.get(id);
    }

    public Vertex getVertexById(String id) {
        return vertexIdCache.get(id);
    }

    public Vertex getStartVertex() {
        return startVertex;
    }

    public List<ModelElement> getConnectedComponent() {
        return depthFirstSearch.getConnectedComponent();
    }

    public int getShortestDistance(ModelElement source, ModelElement target) {
        return floydWarshall.getShortestDistance(source, target);
    }

    public int getMaximumDistance(ModelElement target) {
        return floydWarshall.getMaximumDistance(target);
    }

    public List<ModelElement> getShortestPath(ModelElement source, ModelElement target) {
        return floydWarshall.getShortestPath(source, target);
    }

}
