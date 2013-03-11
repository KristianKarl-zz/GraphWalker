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
import org.graphwalker.core.machine.ExceptionStrategy;
import org.graphwalker.core.model.Element;
import org.graphwalker.core.model.Model;
import org.graphwalker.core.model.Requirement;
import org.graphwalker.core.model.status.ElementStatus;
import org.graphwalker.core.model.status.ModelStatus;
import org.graphwalker.core.model.status.RequirementStatus;

import java.util.HashMap;
import java.util.Map;

public class ModelContext {

    private Model model;
    private Element element;
    private Map<Element, ElementStatus> elementStatus = new HashMap<Element, ElementStatus>();
    private Map<Element, Long> elementCount = new HashMap<Element, Long>();
    private Map<Requirement, RequirementStatus> requirementStatus = new HashMap<Requirement, RequirementStatus>();
    private PathGenerator pathGenerator;
    private ModelStatus modelStatus;
    private ExceptionStrategy exceptionStrategy;

    public ModelContext(Model model) {
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    public ModelStatus getStatus() {
        return modelStatus;
    }

    public void setStatus(ModelStatus status) {
        this.modelStatus = status;
    }

    public ElementStatus getStatus(Element element) {
        return elementStatus.get(element);
    }

    public void setStatus(Element element, ElementStatus status) {
        elementStatus.put(element, status);
    }

    public Long getCount(Element element) {
        return elementCount.get(element);
    }

    public void visit(Element element) {
        Long count = elementCount.get(element);
        elementCount.put(element, ++count);
    }

    public boolean isBlocked(Element element) {
        ElementStatus status = elementStatus.get(element);
        return ElementStatus.BLOCKED.equals(status);
    }

    public boolean isVisited(Element element) {
        Long count = elementCount.get(element);
        return 0 < count;
    }

    public RequirementStatus getStatus(Requirement requirement) {
        return requirementStatus.get(requirement);
    }

    public void setStatus(Requirement requirement, RequirementStatus status) {
        requirementStatus.put(requirement, status);
    }

    public PathGenerator getPathGenerator() {
        return pathGenerator;
    }

    public void setPathGenerator(PathGenerator generator) {
        this.pathGenerator = generator;
    }

    public ExceptionStrategy getExceptionStrategy() {
        return exceptionStrategy;
    }

    public void setExceptionStrategy(ExceptionStrategy strategy) {
        this.exceptionStrategy = strategy;
    }
}
