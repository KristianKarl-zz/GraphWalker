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

import org.graphwalker.core.generators.PathGenerator;
import org.graphwalker.core.machine.ExceptionStrategy;

import java.util.List;

/**
 * <p>Model interface.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public interface Model {

    /**
     * <p>getId.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getId();
    /**
     * <p>getElements.</p>
     *
     * @return a {@link java.util.List} object.
     */
    List<Element> getElements();
    /**
     * <p>getConnectedComponent.</p>
     *
     * @return a {@link java.util.List} object.
     */
    List<Element> getConnectedComponent();
    /**
     * <p>getRequirements.</p>
     *
     * @return a {@link java.util.List} object.
     */
    List<Requirement> getRequirements();
    /**
     * <p>getRequirements.</p>
     *
     * @param filter a {@link org.graphwalker.core.model.RequirementStatus} object.
     * @return a {@link java.util.List} object.
     */
    List<Requirement> getRequirements(RequirementStatus filter);
    /**
     * <p>afterElementsAdded.</p>
     */
    void afterElementsAdded();
    /**
     * <p>getVertexById.</p>
     *
     * @param id a {@link java.lang.String} object.
     * @return a {@link org.graphwalker.core.model.Vertex} object.
     */
    Vertex getVertexById(String id);
    /**
     * <p>getVertexByName.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @return a {@link org.graphwalker.core.model.Vertex} object.
     */
    Vertex getVertexByName(String name);
    /**
     * <p>getVertices.</p>
     *
     * @return a {@link java.util.List} object.
     */
    List<Vertex> getVertices();
    /**
     * <p>addVertex.</p>
     *
     * @param vertex a {@link org.graphwalker.core.model.Vertex} object.
     * @return a {@link org.graphwalker.core.model.Vertex} object.
     */
    Vertex addVertex(Vertex vertex);
    /**
     * <p>getEdgeById.</p>
     *
     * @param id a {@link java.lang.String} object.
     * @return a {@link org.graphwalker.core.model.Edge} object.
     */
    Edge getEdgeById(String id);
    /**
     * <p>getEdgeByName.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @return a {@link org.graphwalker.core.model.Edge} object.
     */
    Edge getEdgeByName(String name);
    /**
     * <p>getEdges.</p>
     *
     * @return a {@link java.util.List} object.
     */
    List<Edge> getEdges();
    /**
     * <p>addEdge.</p>
     *
     * @param edge a {@link org.graphwalker.core.model.Edge} object.
     * @param source a {@link org.graphwalker.core.model.Vertex} object.
     * @param target a {@link org.graphwalker.core.model.Vertex} object.
     * @return a {@link org.graphwalker.core.model.Edge} object.
     */
    Edge addEdge(Edge edge, Vertex source, Vertex target);
    /**
     * <p>getStartVertex.</p>
     *
     * @return a {@link org.graphwalker.core.model.Vertex} object.
     */
    Vertex getStartVertex();
    /**
     * <p>getVisitedEdges.</p>
     *
     * @return a {@link java.util.List} object.
     */
    List<Edge> getVisitedEdges();

    /**
     * <p>getTotalVisitCount.</p>
     *
     * @return a long.
     */
    long getTotalVisitCount();
    /**
     * <p>getVisitedVertices.</p>
     *
     * @return a {@link java.util.List} object.
     */
    List<Vertex> getVisitedVertices();
    /**
     * <p>getPathGenerator.</p>
     *
     * @return a {@link org.graphwalker.core.generators.PathGenerator} object.
     */
    PathGenerator getPathGenerator();
    /**
     * <p>setPathGenerator.</p>
     *
     * @param pathGenerator a {@link org.graphwalker.core.generators.PathGenerator} object.
     */
    void setPathGenerator(PathGenerator pathGenerator);
    /**
     * <p>getShortestDistance.</p>
     *
     * @param source a {@link Element} object.
     * @param target a {@link org.graphwalker.core.model.Edge} object.
     * @return a int.
     */
    int getShortestDistance(Element source, Edge target);
    /**
     * <p>getMaximumDistance.</p>
     *
     * @param target a {@link org.graphwalker.core.model.Edge} object.
     * @return a int.
     */
    int getMaximumDistance(Edge target);
    /**
     * <p>getShortestPath.</p>
     *
     * @param source a {@link Element} object.
     * @param target a {@link org.graphwalker.core.model.Edge} object.
     * @return a {@link java.util.List} object.
     */
    List<Element> getShortestPath(Element source, Edge target);
    /**
     * <p>getShortestDistance.</p>
     *
     * @param source a {@link Element} object.
     * @param target a {@link org.graphwalker.core.model.Vertex} object.
     * @return a int.
     */
    int getShortestDistance(Element source, Vertex target);
    /**
     * <p>getMaximumDistance.</p>
     *
     * @param target a {@link org.graphwalker.core.model.Vertex} object.
     * @return a int.
     */
    int getMaximumDistance(Vertex target);
    /**
     * <p>getShortestPath.</p>
     *
     * @param source a {@link Element} object.
     * @param target a {@link org.graphwalker.core.model.Vertex} object.
     * @return a {@link java.util.List} object.
     */
    List<Element> getShortestPath(Element source, Vertex target);
    /**
     * <p>hasImplementation.</p>
     *
     * @return a boolean.
     */
    boolean hasImplementation();
    /**
     * <p>setImplementation.</p>
     *
     * @param implementation a {@link java.lang.Object} object.
     */
    void setImplementation(Object implementation);
    /**
     * <p>getImplementation.</p>
     *
     * @return a {@link java.lang.Object} object.
     */
    Object getImplementation();
    /**
     * <p>getGroup.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getGroup();
    /**
     * <p>setGroup.</p>
     *
     * @param group a {@link java.lang.String} object.
     */
    void setGroup(String group);
    /**
     * <p>getExceptionStrategy.</p>
     *
     * @return a {@link org.graphwalker.core.machine.ExceptionStrategy} object.
     */
    ExceptionStrategy getExceptionStrategy();
    /**
     * <p>setExceptionStrategy.</p>
     *
     * @param exceptionStrategy a {@link org.graphwalker.core.machine.ExceptionStrategy} object.
     */
    void setExceptionStrategy(ExceptionStrategy exceptionStrategy);
}
