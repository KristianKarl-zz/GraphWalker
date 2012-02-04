/*
 * #%L
 * GraphWalker Core
 * %%
 * Copyright (C) 2011 - 2012 GraphWalker
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

import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.guard.Guarded;
import org.graphwalker.core.Bundle;
import org.graphwalker.core.algorithms.DepthFirstSearch;
import org.graphwalker.core.algorithms.FloydWarshall;
import org.graphwalker.core.generators.PathGenerator;
import org.graphwalker.core.machine.ExceptionStrategy;
import org.graphwalker.core.utils.Resource;

import java.util.*;

/**
 * <p>ModelImpl class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
@Guarded
public class ModelImpl implements Model {

    private final String myId;
    private final Random myIdGenerator = new Random(System.nanoTime());
    private final Map<String, Vertex> myVertexMap = new HashMap<String, Vertex>();
    private final Map<String, Edge> myEdgeMap = new HashMap<String, Edge>();
    private DepthFirstSearch myDepthFirstSearch;
    private FloydWarshall myFloydWarshall;
    private PathGenerator myPathGenerator;
    private Object myImplementation;
    private String myGroup;
    private ExceptionStrategy myExceptionStrategy;
    
    /**
     * <p>Constructor for ModelImpl.</p>
     *
     * @param id a {@link java.lang.String} object.
     */
    public ModelImpl(@NotNull @NotEmpty String id) {
        myId = id;
    }

    /**
     * <p>getId.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getId() {
        return myId;
    }

    /**
     * <p>getPathGenerator.</p>
     *
     * @return a {@link org.graphwalker.core.generators.PathGenerator} object.
     */
    public PathGenerator getPathGenerator() {
        return myPathGenerator;
    }

    /** {@inheritDoc} */
    public void setPathGenerator(@NotNull PathGenerator pathGenerator) {
        myPathGenerator = pathGenerator;
    }

    public void afterElementsAdded() {
        myDepthFirstSearch = new DepthFirstSearch(this);
        myFloydWarshall = new FloydWarshall(this);
    }

    /**
     * <p>getElements.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Element> getElements() {
        List<Element> elements = new ArrayList<Element>(myVertexMap.size()+myEdgeMap.size());
        elements.addAll(myVertexMap.values());
        elements.addAll(myEdgeMap.values());
        return elements;
    }

    public List<Requirement> getRequirements() {
        Map<String, Requirement> requirements = new HashMap<String, Requirement>();
        for (Vertex vertex: getVertices()) {
            for (Requirement requirement: vertex.getRequirements()) {
                if (!requirements.containsKey(requirement.getId())) {
                    requirements.put(requirement.getId(), requirement);
                }
            }
        }
        return new ArrayList<Requirement>(requirements.values());
    }

    public List<Requirement> getRequirements(RequirementStatus filter) {
        List<Requirement> requirements = new ArrayList<Requirement>();
        for (Requirement requirement: getRequirements()) {
            if (filter.equals(requirement.getStatus())) {
                requirements.add(requirement);
            }
        }
        return requirements;
    }

    /** {@inheritDoc} */
    public Vertex getVertexById(@NotNull @NotEmpty String id) {
        return myVertexMap.get(id);
    }

    /** {@inheritDoc} */
    public Vertex getVertexByName(@NotNull @NotEmpty String name) {
        for (Vertex vertex: myVertexMap.values()) {
            String vertexName = vertex.getName();
            if (null != vertexName && vertexName.equalsIgnoreCase(name)) {
                return vertex;
            }
        }
        return null;
    }
    
    private List<Vertex> findByName(@NotNull @NotEmpty String name) {
        List<Vertex> vertices = new ArrayList<Vertex>();
        for (Vertex vertex: myVertexMap.values()) {
            String vertexName = vertex.getName();
            if (null != vertexName && vertexName.equalsIgnoreCase(name)) {
                vertices.add(vertex);
            }
        }
        return vertices;
    }
    
    /**
     * <p>getVertices.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Vertex> getVertices() {
        List<Vertex> vertices = new ArrayList<Vertex>();
        for (Element element: getConnectedComponent()) {
            if (element instanceof Vertex) {
                vertices.add((Vertex)element);
            }
        }
        return vertices;
    }

    private void verifyId(@NotNull Edge edge) {
        if (null == edge.getId()) {
            edge.setId(generateId("e_"));
        }
    }

    private void verifyId(@NotNull Vertex vertex) {
        if (null == vertex.getId()) {
            vertex.setId(generateId("v_"));
        }
    }

    private String generateId(@NotNull @NotEmpty String prefix) {
        return prefix+myIdGenerator.nextLong();
    }
    
    /** {@inheritDoc} */
    public Vertex addVertex(@NotNull Vertex vertex) {
        verifyId(vertex);
        if (hasStartVertex() && (getStartVertex().equals(vertex) || getStartVertex().getName().equalsIgnoreCase(vertex.getName()))) {
            throw new ModelException(Resource.getText(Bundle.NAME, "exception.duplicate.start.vertex"));
        }
        myVertexMap.put(vertex.getId(), vertex);
        return vertex;
    }

    /** {@inheritDoc} */
    public Edge getEdgeById(@NotNull @NotEmpty String id) {
        return myEdgeMap.get(id);
    }

    /** {@inheritDoc} */
    public Edge getEdgeByName(@NotNull @NotEmpty String name) {
        for (Edge edge: myEdgeMap.values()) {
            String edgeName = edge.getName();
            if (null != edgeName && edgeName.equalsIgnoreCase(name)) {
                return edge;
            }
        }
        return null;
    }

    /**
     * <p>getEdges.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Edge> getEdges() {
        List<Edge> edges = new ArrayList<Edge>();
        for (Element element: getConnectedComponent()) {
            if (element instanceof Edge) {
                edges.add((Edge)element);
            }
        }
        return edges;
    }

    /** {@inheritDoc} */
    public Edge addEdge(@NotNull Edge edge, @NotNull Vertex source, @NotNull Vertex target) {
        verifyId(edge);
        if (hasStartVertex() && getStartVertex().equals(target)) {
            throw new ModelException(Resource.getText(Bundle.NAME, "exception.start.vertex.in.edge"));
        }
        if (hasStartVertex() && getStartVertex().equals(source) && 1 == getStartVertex().getEdges().size()) {
            throw new ModelException(Resource.getText(Bundle.NAME, "exception.start.vertex.out.edges"));
        }
        if (!myEdgeMap.containsKey(edge.getId())) {
            source.addEdge(edge);
            edge.setSource(source);
            edge.setTarget(target);
            myEdgeMap.put(edge.getId(), edge);
        } else {
            Edge existingEdge = myEdgeMap.get(edge.getId());
            if (!existingEdge.getSource().getId().equals(edge.getSource().getId())) {
                existingEdge.setSource(edge.getSource());
            }
            if (!existingEdge.getTarget().getId().equals(edge.getTarget().getId())) {
                existingEdge.setTarget(edge.getTarget());
            }
        }
        return edge;
    }

    /**
     * <p>getStartVertex.</p>
     *
     * @return a {@link org.graphwalker.core.model.Vertex} object.
     */
    public Vertex getStartVertex() {
        List<Vertex> vertices = findByName(Resource.getText(Bundle.NAME, "start.vertex"));
        if (1<vertices.size()) {
            throw new ModelException(Resource.getText(Bundle.NAME, "exception.duplicate.start.vertex"));
        } if (1>vertices.size()) {
            throw new ModelException(Resource.getText(Bundle.NAME, "exception.start.vertex.missing"));
        }
        return vertices.get(0);
    }

    public boolean hasStartVertex() {
        return 0<findByName(Resource.getText(Bundle.NAME, "start.vertex")).size();
    }

    /**
     * <p>getVisitedEdges.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Edge> getVisitedEdges() {
        List<Edge> visitedEdges = new ArrayList<Edge>();
        for (Edge edge: getEdges()) {
            if (edge.isVisited()) {
                visitedEdges.add(edge);
            }
        }
        return visitedEdges;
    }

    /**
     * <p>getTotalVisitCount.</p>
     *
     * @return a long.
     */
    public long getTotalVisitCount() {
        long totalCount = 0L;
        for (Element modelElement: getElements()) {
            totalCount += modelElement.getVisitCount();
        }
        return totalCount;
    }

    /**
     * <p>getVisitedVertices.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Vertex> getVisitedVertices() {
        List<Vertex> visitedVertices = new ArrayList<Vertex>();
        for (Vertex vertex: getVertices()) {
            if (vertex.isVisited()) {
                visitedVertices.add(vertex);
            }
        }
        return visitedVertices;
    }
    
    public List<Element> getConnectedComponent() {
        return myDepthFirstSearch.getConnectedComponent();
    }
    
    /** {@inheritDoc} */
    public int getShortestDistance(@NotNull Element source, @NotNull Edge target) {
        return myFloydWarshall.getShortestDistance(source, target);
    }

    /**
     * {@inheritDoc}
     *
     * <p>getMaximumDistance.</p>
     */
    public int getMaximumDistance(@NotNull Edge target) {
        return myFloydWarshall.getMaximumDistance(target);
    }

    /**
     * {@inheritDoc}
     *
     * @param target a {@link org.graphwalker.core.model.Vertex} object.
     * @return a int.
     */
    public int getMaximumDistance(@NotNull Vertex target) {
        return myFloydWarshall.getMaximumDistance(target);
    }

    /**
     * {@inheritDoc}
     *
     * @param source a {@link org.graphwalker.core.model.Element} object.
     * @param target a {@link org.graphwalker.core.model.Edge} object.
     * @return a {@link java.util.List} object.
     */
    public List<Element> getShortestPath(@NotNull Element source, @NotNull Edge target) {
        return myFloydWarshall.getShortestPath(source, target);
    }

    /**
     * {@inheritDoc}
     *
     * <p>getShortestDistance.</p>
     *
     * @param source a {@link org.graphwalker.core.model.Element} object.
     * @param target a {@link org.graphwalker.core.model.Vertex} object.
     * @return a int.
     */
    public int getShortestDistance(@NotNull Element source, @NotNull Vertex target) {
        return myFloydWarshall.getShortestDistance(source, target);
    }

    /**
     * {@inheritDoc}
     *
     * <p>getShortestPath.</p>
     */
    public List<Element> getShortestPath(@NotNull Element source, @NotNull Vertex target) {
        return myFloydWarshall.getShortestPath(source, target);
    }

    /**
     * <p>hasImplementation.</p>
     *
     * @return a boolean.
     */
    public boolean hasImplementation() {
        return null != getImplementation();
    }

    /** {@inheritDoc} */
    public void setImplementation(Object implementation) {
        myImplementation = implementation;    
    }
    
    /**
     * <p>getImplementation.</p>
     *
     * @return a {@link java.lang.Object} object.
     */
    public Object getImplementation() {
        return myImplementation;
    }

    @Override
    public String getGroup() {
        return myGroup;  
    }

    @Override
    public void setGroup(String group) {
        myGroup = group;
    }

    @Override
    public ExceptionStrategy getExceptionStrategy() {
        return myExceptionStrategy;
    }

    @Override
    public void setExceptionStrategy(ExceptionStrategy exceptionStrategy) {
        myExceptionStrategy = exceptionStrategy;
    }
}
