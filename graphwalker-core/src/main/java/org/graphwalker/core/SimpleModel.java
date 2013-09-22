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

import org.graphwalker.core.model.*;

import java.util.*;

/**
 * @author Nils Olsson
 */
public final class SimpleModel implements Model {

    private final Map<Vertex, Vertex> vertices;
    private final Map<Edge, Edge> edges;

    public SimpleModel() {
        this(new HashSet<Vertex>(), new HashSet<Edge>());
    }

    public SimpleModel(Set<Vertex> vertices, Set<Edge> edges) {
        this.vertices = Collections.unmodifiableMap(asMap(vertices));
        this.edges = Collections.unmodifiableMap(asMap(edges));
        // TODO: compute algorithms
    }

    private <T> Map<T,T> asMap(Set<T> set) {
        Map<T,T> map = new HashMap<T, T>();
        for (T entry: set) {
            map.put(entry, entry);
        }
        return map;
    }

    public Model addEdge(Edge edge) {
        return addModel(new SimpleModel(new HashSet<Vertex>(), new HashSet<Edge>(Arrays.asList(edge))));
    }

    public Model addVertex(Vertex vertex) {
        return addModel(new SimpleModel(new HashSet<Vertex>(Arrays.asList(vertex)), new HashSet<Edge>()));
    }

    public Model addModel(Model model) {
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
        return new SimpleModel(new HashSet<Vertex>(vertexMap.values()), new HashSet<Edge>(edgeMap.values()));
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

    public List<Vertex> getVertices() {
        return new ArrayList<Vertex>(vertices.values());
    }

    public Vertex getVertex(String name) {
        return getVertex(new Vertex(name));
    }

    public Vertex getVertex(Vertex vertex) {
        return vertices.get(vertex);
    }

    public List<Element> getConnectedComponent(Element element) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Path getShortestPath() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getShortestDistance() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getMaximumDistance() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<Vertex> getStartVertices() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
