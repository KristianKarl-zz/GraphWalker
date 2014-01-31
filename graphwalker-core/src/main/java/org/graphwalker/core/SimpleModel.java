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
package org.graphwalker.core;

import org.graphwalker.core.algorithm.AStar;
import org.graphwalker.core.algorithm.DepthFirstSearch;
import org.graphwalker.core.algorithm.FloydWarshall;
import org.graphwalker.core.event.EventSource;
import org.graphwalker.core.event.ModelSink;
import org.graphwalker.core.model.*;

import java.util.*;

/**
 * @author Nils Olsson
 */
public final class SimpleModel extends EventSource<ModelSink> implements Model {

    private final Map<Vertex, Vertex> vertices;
    private final Map<Edge, Edge> edges;
    private AStar aStar;
    private DepthFirstSearch depthFirstSearch;
    private FloydWarshall floydWarshall;
    private final List<Vertex> startVertices;
    private final List<Element> elementCache;
    private final Map<Vertex, List<Edge>> vertexEdgeCache;
    private final Map<String, List<Edge>> edgeNameCache;
    private final List<Requirement> requirementCache;

    public SimpleModel() {
        this(true);
    }

    public SimpleModel(boolean refresh) {
        this(new HashSet<Vertex>(), new HashSet<Edge>(), refresh);
    }

    public SimpleModel(Set<Vertex> vertices, Set<Edge> edges) {
        this(vertices, edges, true);
    }

    public SimpleModel(Set<Vertex> vertices, Set<Edge> edges, boolean refresh) {
        this.vertices = asUnmodifiableMap(vertices);
        this.edges = asUnmodifiableMap(edges);
        this.startVertices = findStartVertices();
        this.elementCache = createElementCache();
        this.vertexEdgeCache = createVertexEdgeCache();
        this.edgeNameCache = createEdgeNameCache();
        this.requirementCache = aggregateRequirements();
        if (refresh) {
            refresh();
        }
    }

    public void refresh() {
        this.aStar = new AStar(this);
        this.depthFirstSearch = new DepthFirstSearch(this);
        this.floydWarshall = new FloydWarshall(this);
    }

    private Map<Vertex, List<Edge>> createVertexEdgeCache() {
        Map<Vertex, List<Edge>> vertexEdgeCache = new HashMap<Vertex, List<Edge>>();
        for (Vertex vertex: vertices.values()) {
            vertexEdgeCache.put(vertex, new ArrayList<Edge>());
        }
        for (Edge edge: edges.values()) {
            vertexEdgeCache.get(edge.getSourceVertex()).add(edge);
        }
        Map<Vertex, List<Edge>> unmodifiableVertexEdgeCache = new HashMap<Vertex, List<Edge>>();
        for (Vertex vertex: vertexEdgeCache.keySet()) {
            unmodifiableVertexEdgeCache.put(vertex, Collections.unmodifiableList(vertexEdgeCache.get(vertex)));
        }
        return Collections.unmodifiableMap(unmodifiableVertexEdgeCache);
    }

    private Map<String, List<Edge>> createEdgeNameCache() {
        Map<String, List<Edge>> edgeNameCache = new HashMap<String, List<Edge>>();
        for (Edge edge: edges.keySet()) {
            if (!edgeNameCache.containsKey(edge.getName())) {
                edgeNameCache.put(edge.getName(), new ArrayList<Edge>());
            }
            edgeNameCache.get(edge.getName()).add(edge);
        }
        Map<String, List<Edge>> unmodifiableEdgeNameCache = new HashMap<String, List<Edge>>();
        for (String name: edgeNameCache.keySet()) {
            unmodifiableEdgeNameCache.put(name, Collections.unmodifiableList(edgeNameCache.get(name)));
        }
        return Collections.unmodifiableMap(unmodifiableEdgeNameCache);
    }

    private List<Element> createElementCache() {
        Set<Element> elementSet = new HashSet<Element>();
        elementSet.addAll(vertices.values());
        elementSet.addAll(edges.values());
        return Collections.unmodifiableList(new ArrayList<Element>(elementSet));
    }

    private List<Vertex> findStartVertices() {
        Set<Vertex> vertexSet = new HashSet<Vertex>(vertices.values());
        for (Edge edge: edges.values()) {
            vertexSet.remove(edge.getTargetVertex());
        }
        return Collections.unmodifiableList(new ArrayList<Vertex>(vertexSet));
    }

    private List<Requirement> aggregateRequirements() {
        Set<Requirement> requirements = new HashSet<Requirement>();
        for (Vertex vertex: vertices.keySet()) {
            requirements.addAll(vertex.getRequirements());
        }
        return Collections.unmodifiableList(new ArrayList<Requirement>(requirements));
    }

    private <T> Map<T,T> asUnmodifiableMap(Set<T> set) {
        Map<T,T> map = new HashMap<T, T>();
        for (T entry: set) {
            map.put(entry, entry);
        }
        return Collections.unmodifiableMap(map);
    }

    public Model addEdge(Edge edge) {
        return addModel(new SimpleModel(new HashSet<Vertex>(Arrays.asList(edge.getSourceVertex()
                , edge.getTargetVertex())), new HashSet<Edge>(Arrays.asList(edge))));
    }

    public Model addVertex(Vertex vertex) {
        return addModel(new SimpleModel(new HashSet<Vertex>(Arrays.asList(vertex)), new HashSet<Edge>()));
    }

    public Model addModel(Model model) {
        return addModel(model, true);
    }

    public Model addModel(Model model, boolean refresh) {
        Map<Vertex,Vertex> vertexMap = new HashMap<Vertex,Vertex>(vertices);
        for (Vertex vertex: model.getVertices()) {
            if (vertexMap.containsKey(vertex)) {
                Vertex merged = merge(vertex.getName(), vertexMap.get(vertex), vertex);
                vertexMap.put(merged, merged);
            } else {
                vertexMap.put(vertex, vertex);
            }
        }
        Map<Edge, Edge> edgeMap = new HashMap<Edge,Edge>();
        for (Edge edge: edges.keySet()) {
            updateEdge(edge, edgeMap, vertexMap);
        }
        for (Edge edge: model.getEdges()) {
            if (!edgeMap.containsKey(edge)) {
                updateEdge(edge, edgeMap, vertexMap);
            }
        }
        return new SimpleModel(new HashSet<Vertex>(vertexMap.values()), new HashSet<Edge>(edgeMap.values()), refresh);
    }

    private void updateEdge(Edge edge, Map<Edge,Edge> edges, Map<Vertex,Vertex> vertices) {
        Vertex source = vertices.get(edge.getSourceVertex());
        Vertex target = vertices.get(edge.getTargetVertex());
        Edge updatedEdge = new Edge(edge.getName()
                , source
                , target
                , edge.getGuard()
                , edge.getActions()
                , edge.isBlocked()
                , edge.getWeight());
        edges.put(updatedEdge, updatedEdge);
    }

    private Vertex merge(String name, Vertex... vertices) {
        Set<Requirement> requirements = new HashSet<Requirement>();
        Set<Action> entryActions = new HashSet<Action>();
        Set<Action> exitActions = new HashSet<Action>();
        for (Vertex vertex: vertices) {
            requirements.addAll(vertex.getRequirements());
            entryActions.addAll(vertex.getEntryActions());
            exitActions.addAll(vertex.getExitActions());
        }
        return new Vertex(name, requirements, entryActions, exitActions);
    }

    public List<Edge> getEdges() {
        return new ArrayList<Edge>(edges.values());
    }

    public List<Edge> getEdges(Vertex vertex) {
        return vertexEdgeCache.get(vertex);
    }

    public List<Edge> getEdges(String name) {
        return edgeNameCache.get(name);
    }

    public List<Vertex> getVertices() {
        return new ArrayList<Vertex>(vertices.values());
    }

    public Vertex getVertex(String name) {
        return getVertex(new Vertex(name));
    }

    public Vertex getVertex(Vertex vertex) {
        return vertices.get(vertex);
    }

    public String getDescription() {
        return null;
    }

    public List<Element> getElements() {
        return elementCache;
    }

    public List<Element> getElements(Element element) {
        if (null == element) {
            return new ArrayList<Element>(getStartVertices());
        } else if (element instanceof Vertex) {
            Vertex vertex = (Vertex)element;
            return new ArrayList<Element>(getEdges(vertex));
        } else {
            Edge edge = (Edge)element;
            return Arrays.<Element>asList(edge.getTargetVertex());
        }
    }

    public List<Element> getConnectedComponent(Element element) {
        return depthFirstSearch.getConnectedComponent(element);
    }

    public Path<Element> getShortestPath(Element origin, Element destination) {
        return aStar.getShortestPath(origin, destination);
    }

    public int getShortestDistance(Element origin, Element destination) {
        return floydWarshall.getShortestDistance(origin, destination);
    }

    public int getMaximumDistance(Element destination) {
        return floydWarshall.getMaximumDistance(destination);
    }

    public List<Vertex> getStartVertices() {
        return startVertices;
    }

    public List<Requirement> getRequirements() {
        return requirementCache;
    }
}
