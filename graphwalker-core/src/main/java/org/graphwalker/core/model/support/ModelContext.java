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

import org.graphwalker.core.generators.PathGenerator;
import org.graphwalker.core.machine.strategy.ExceptionStrategy;
import org.graphwalker.core.model.Element;
import org.graphwalker.core.model.Model;
import org.graphwalker.core.model.ModelElement;
import org.graphwalker.core.model.Requirement;
import org.graphwalker.core.model.status.ElementStatus;
import org.graphwalker.core.model.status.ModelStatus;
import org.graphwalker.core.model.status.RequirementStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>ModelContext class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class ModelContext {

    private Model model;
    private ModelElement currentElement;
    private ModelStatus modelStatus;
    private Map<ModelElement, ElementStatus> elementStatus = new HashMap<ModelElement, ElementStatus>();
    private Map<Element, Long> elementCount = new HashMap<Element, Long>();
    private Map<Requirement, RequirementStatus> requirementStatus = new HashMap<Requirement, RequirementStatus>();
    private PathGenerator pathGenerator;
    private ExceptionStrategy exceptionStrategy;

    public ModelContext(Model model) {
        this.model = model;
    }

    public Model getModel() {
        return model;
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
     * <p>getCount.</p>
     *
     * @param element a {@link org.graphwalker.core.model.ModelElement} object.
     * @return a {@link java.lang.Long} object.
     */
    public Long getVisitCount(ModelElement element) {
        return elementCount.get(element);
    }

    /**
     * <p>visit.</p>
     *
     * @param element a {@link org.graphwalker.core.model.ModelElement} object.
     */
    public void visit(ModelElement element) {
        Long count = elementCount.get(element);
        elementCount.put(element, ++count);
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
        Long count = elementCount.get(element);
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
