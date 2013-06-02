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
package org.graphwalker.core.model.support;

import org.graphwalker.core.Bundle;
import org.graphwalker.core.filter.EdgeFilter;
import org.graphwalker.core.generators.PathGenerator;
import org.graphwalker.core.machine.strategy.ExceptionStrategy;
import org.graphwalker.core.model.*;
import org.graphwalker.core.model.status.ElementStatus;
import org.graphwalker.core.model.status.ModelStatus;
import org.graphwalker.core.model.status.RequirementStatus;
import org.graphwalker.core.utils.Resource;

import java.util.*;

/**
 * <p>ModelContext class.</p>
 */
public final class ModelContext {

    private Model model;
    private ModelElement currentElement;
    private ModelStatus modelStatus;
    private Map<ModelElement, ElementStatus> elementStatus = new HashMap<ModelElement, ElementStatus>();
    private Map<ModelElement, Long> elementVisitCount = new HashMap<ModelElement, Long>();
    private Set<Edge> visitedEdges = new HashSet<Edge>();
    private Set<Vertex> visitedVertices = new HashSet<Vertex>();
    private Map<Requirement, RequirementStatus> requirementStatus = new HashMap<Requirement, RequirementStatus>();
    private PathGenerator pathGenerator;
    private ExceptionStrategy exceptionStrategy;
    private EdgeFilter edgeFilter;
    private Long visitCount = 0L;

    /**
     * <p>Constructor for ModelContext.</p>
     *
     * @param model a {@link org.graphwalker.core.model.Model} object.
     */
    public ModelContext(Model model) {
        this.model = model;
    }

    /**
     * <p>Getter for the field <code>model</code>.</p>
     *
     * @return a {@link org.graphwalker.core.model.Model} object.
     */
    public Model getModel() {
        return model;
    }

    /**
     * <p>Getter for the field <code>edgeFilter</code>.</p>
     *
     * @return a {@link org.graphwalker.core.filter.EdgeFilter} object.
     */
    public EdgeFilter getEdgeFilter() {
        if (null == edgeFilter) {
            edgeFilter = new EdgeFilter(Resource.getText(Bundle.NAME, "default.language"));
        }
        return edgeFilter;
    }

    /**
     * <p>Setter for the field <code>edgeFilter</code>.</p>
     *
     * @param edgeFilter a {@link org.graphwalker.core.filter.EdgeFilter} object.
     */
    public void setEdgeFilter(EdgeFilter edgeFilter) {
        this.edgeFilter = edgeFilter;
    }

    /**
     * <p>setCurrentElement.</p>
     *
     * @param element a {@link org.graphwalker.core.model.ModelElement} object.
     */
    public void setCurrentElement(ModelElement element) {
        currentElement = element;
    }

    /**
     * <p>getCurrentElement.</p>
     *
     * @return a {@link org.graphwalker.core.model.ModelElement} object.
     */
    public ModelElement getCurrentElement() {
        if (null == currentElement) {
            currentElement = getModel().getStartVertex();
        }
        return currentElement;
    }

    /**
     * <p>getStatus.</p>
     *
     * @return a {@link org.graphwalker.core.model.status.ModelStatus} object.
     */
    public ModelStatus getStatus() {
        return modelStatus;
    }

    /**
     * <p>setStatus.</p>
     *
     * @param status a {@link org.graphwalker.core.model.status.ModelStatus} object.
     */
    public void setStatus(ModelStatus status) {
        this.modelStatus = status;
    }

    /**
     * <p>getStatus.</p>
     *
     * @param element a {@link org.graphwalker.core.model.ModelElement} object.
     * @return a {@link org.graphwalker.core.model.status.ElementStatus} object.
     */
    public ElementStatus getStatus(ModelElement element) {
        return elementStatus.get(element);
    }

    /**
     * <p>setStatus.</p>
     *
     * @param element a {@link org.graphwalker.core.model.ModelElement} object.
     * @param status a {@link org.graphwalker.core.model.status.ElementStatus} object.
     */
    public void setStatus(ModelElement element, ElementStatus status) {
        elementStatus.put(element, status);
    }

    /**
     * <p>Getter for the field <code>visitedEdges</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Edge> getVisitedEdges() {
        return new ArrayList<Edge>(visitedEdges);
    }

    /**
     * <p>Getter for the field <code>visitedVertices</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Vertex> getVisitedVertices() {
        return new ArrayList<Vertex>(visitedVertices);
    }

    /**
     * <p>getRequirements.</p>
     *
     * @param status a {@link org.graphwalker.core.model.status.RequirementStatus} object.
     * @return a {@link java.util.Set} object.
     */
    public Set<Requirement> getRequirements(RequirementStatus status) {
        Set<Requirement> requirements = new HashSet<Requirement>();
        for (Requirement requirement: getModel().getRequirements()) {
            if (requirementStatus.get(requirement).equals(status)) {
                requirements.add(requirement);
            }
        }
        return requirements;
    }

    /**
     * <p>getCount.</p>
     *
     * @param element a {@link org.graphwalker.core.model.ModelElement} object.
     * @return a {@link java.lang.Long} object.
     */
    public Long getVisitCount(ModelElement element) {
        Long visitCount = 0L;
        if (elementVisitCount.containsKey(element)) {
            visitCount = elementVisitCount.get(element);
        }
        return visitCount;
    }

    /**
     * <p>Getter for the field <code>visitCount</code>.</p>
     *
     * @return a {@link java.lang.Long} object.
     */
    public Long getVisitCount() {
        return visitCount;
    }

    /**
     * <p>visit.</p>
     *
     * @param element a {@link org.graphwalker.core.model.ModelElement} object.
     */
    public void visit(ModelElement element) {
        this.visitCount++;
        this.elementVisitCount.put(element, getVisitCount(element)+1);
        if (element instanceof Edge) {
            visit((Edge)element);
        } else {
            visit((Vertex)element);
        }
    }

    private void visit(Edge edge) {
        if (!visitedEdges.contains(edge)) {
            visitedEdges.add(edge);
        }
    }

    private void visit(Vertex vertex) {
        if (!visitedVertices.contains(vertex)) {
            visitedVertices.add(vertex);
        }
    }

    /**
     * <p>isBlocked.</p>
     *
     * @param element a {@link org.graphwalker.core.model.ModelElement} object.
     * @return a boolean.
     */
    public boolean isBlocked(ModelElement element) {
        ElementStatus status = elementStatus.get(element);
        return ElementStatus.BLOCKED.equals(status);
    }

    /**
     * <p>isVisited.</p>
     *
     * @param element a {@link org.graphwalker.core.model.ModelElement} object.
     * @return a boolean.
     */
    public boolean isVisited(ModelElement element) {
        Long count = elementVisitCount.get(element);
        return 0 < count;
    }

    /**
     * <p>getStatus.</p>
     *
     * @param requirement a {@link org.graphwalker.core.model.Requirement} object.
     * @return a {@link org.graphwalker.core.model.status.RequirementStatus} object.
     */
    public RequirementStatus getStatus(Requirement requirement) {
        return requirementStatus.get(requirement);
    }

    /**
     * <p>setStatus.</p>
     *
     * @param requirement a {@link org.graphwalker.core.model.Requirement} object.
     * @param status a {@link org.graphwalker.core.model.status.RequirementStatus} object.
     */
    public void setStatus(Requirement requirement, RequirementStatus status) {
        requirementStatus.put(requirement, status);
    }

    /**
     * <p>Getter for the field <code>pathGenerator</code>.</p>
     *
     * @return a {@link org.graphwalker.core.generators.PathGenerator} object.
     */
    public PathGenerator getPathGenerator() {
        return pathGenerator;
    }

    /**
     * <p>Setter for the field <code>pathGenerator</code>.</p>
     *
     * @param generator a {@link org.graphwalker.core.generators.PathGenerator} object.
     */
    public void setPathGenerator(PathGenerator generator) {
        this.pathGenerator = generator;
    }

    /**
     * <p>Getter for the field <code>exceptionStrategy</code>.</p>
     *
     * @return a {@link org.graphwalker.core.machine.strategy.ExceptionStrategy} object.
     */
    public ExceptionStrategy getExceptionStrategy() {
        return exceptionStrategy;
    }

    /**
     * <p>Setter for the field <code>exceptionStrategy</code>.</p>
     *
     * @param strategy a {@link org.graphwalker.core.machine.strategy.ExceptionStrategy} object.
     */
    public void setExceptionStrategy(ExceptionStrategy strategy) {
        this.exceptionStrategy = strategy;
    }
}
