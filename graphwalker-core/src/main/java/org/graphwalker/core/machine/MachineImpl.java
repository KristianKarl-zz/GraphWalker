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
package org.graphwalker.core.machine;

import org.graphwalker.core.Bundle;
import org.graphwalker.core.annotations.After;
import org.graphwalker.core.annotations.Before;
import org.graphwalker.core.conditions.StopCondition;
import org.graphwalker.core.configuration.Configuration;
import org.graphwalker.core.filter.EdgeFilter;
import org.graphwalker.core.generators.PathGenerator;
import org.graphwalker.core.model.*;
import org.graphwalker.core.utils.Reflection;
import org.graphwalker.core.utils.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>MachineImpl class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class MachineImpl implements Machine {

    private final Configuration myConfiguration;
    private final EdgeFilter myEdgeFilter;
    private Model myCurrentModel;
    private Element myCurrentElement;

    /**
     * <p>Constructor for MachineImpl.</p>
     *
     * @param configuration a {@link org.graphwalker.core.configuration.Configuration} object.
     */
    public MachineImpl(Configuration configuration) {
        myConfiguration = configuration;
        myEdgeFilter = configuration.getEdgeFilter();
        setCurrentModel(configuration.getDefaultModel());
        setCurrentElement(getCurrentModel().getStartVertex());
        getCurrentElement().markAsVisited();
    }

    @Override
    public void after() {
        for (Model model: getConfiguration().getModels()) {
            if (model.hasImplementation()) {
                Reflection.execute(model.getImplementation(), After.class);
            }
        }
    }

    @Override
    public void before() {
        for (Model model: getConfiguration().getModels()) {
            if (model.hasImplementation()) {
                Reflection.execute(model.getImplementation(), Before.class);
            }
        }
    }

    /**
     * <p>getConfiguration.</p>
     *
     * @return a {@link org.graphwalker.core.configuration.Configuration} object.
     */
    @Override
    public Configuration getConfiguration() {
        return myConfiguration;
    }
    
    /**
     * <p>getCurrentElement.</p>
     *
     * @return a {@link org.graphwalker.core.model.Element} object.
     */
    @Override
    public Element getCurrentElement() {
        return myCurrentElement;
    }

    @Override
    public void setCurrentElement(Element element) {
        myCurrentElement = element;
    }
    
    /**
     * <p>getCurrentModel.</p>
     *
     * @return a {@link org.graphwalker.core.model.Model} object.
     */
    @Override
    public Model getCurrentModel() {
        return myCurrentModel;
    }

    @Override
    public void setCurrentModel(Model model) {
        myCurrentModel = model;
    }

    @Override
    public ExceptionStrategy getExceptionStrategy() {
        return getCurrentModel().getExceptionStrategy();
    }

    /**
     * <p>hasNextStep.</p>
     *
     * @return a boolean.
     */
    @Override
    public boolean hasNextStep() {
        Model model = getCurrentModel();
        Element element = getCurrentElement();
        if (isVertex(getCurrentElement())) {
            Vertex vertex = getVertex(getCurrentElement());
            if (vertex.hasSwitchModel()) {
                model = getConfiguration().getModel(vertex.getSwitchModelId());
                element = model.getStartVertex();
            }
        } 
        PathGenerator pathGenerator = getPathGenerator(model);
        StopCondition stopCondition = getStopCondition(pathGenerator);
        return !stopCondition.isFulfilled(model, element);
    }

    /**
     * <p>getNextStep.</p>
     *
     * @return a {@link org.graphwalker.core.model.Element} object.
     */
    @Override
    public Element getNextStep() {
        try {
            setCurrentElement(getPathGenerator(getCurrentModel()).getNextStep(this));
            getCurrentElement().markAsVisited();
            executeActions(getCurrentElement());
            if (getCurrentModel().hasImplementation() && getCurrentElement().hasName()) {
                Reflection.execute(getCurrentModel().getImplementation(), getCurrentElement().getName());
            }
            setRequirementStatus(getCurrentElement(), RequirementStatus.PASSED);
        } catch (Throwable throwable) {
            setRequirementStatus(getCurrentElement(), RequirementStatus.FAILED);
            getExceptionStrategy().handleException(this, throwable);
        }
        return getCurrentElement();
    }

    /** {@inheritDoc} */
    @Override
    public List<Element> getPossibleElements(Element element) {
        List<Element> possibleElements = new ArrayList<Element>();
        if (element instanceof Vertex) {
            Vertex vertex = (Vertex)element;
            if (vertex.hasSwitchModel()) {
                switchModel(vertex.getSwitchModelId());
                return getPossibleElements(myCurrentElement);
            } else {
                for (Edge edge: vertex.getEdges()) {
                    if (!edge.isBlocked() && myEdgeFilter.acceptEdge(edge)) {
                        possibleElements.add(edge);
                    }
                }
            }
        } else if (element instanceof Edge) {
            possibleElements.add(((Edge)element).getTarget());
        }
        return possibleElements;
    }

    private boolean isVertex(Element element) {
        return element instanceof Vertex;
    }
    
    private Vertex getVertex(Element element) {
        return (Vertex)element;
    }
    
    private boolean isEdge(Element element) {
        return element instanceof Edge;   
    }
    
    private void switchModel(String modelId) {
        setCurrentModel(myConfiguration.getModel(modelId));
        setCurrentElement(myCurrentModel.getStartVertex());
    }

    private PathGenerator getPathGenerator(Model model) {
        if (null != model.getPathGenerator()) {
            return model.getPathGenerator();
        } else if (null != getConfiguration().getDefaultPathGenerator()) {
            return getConfiguration().getDefaultPathGenerator();
        }
        throw new MachineException(Resource.getText(Bundle.NAME, "exception.generator.missing"));
    }

    private StopCondition getStopCondition(PathGenerator pathGenerator) {
        if (null != pathGenerator.getStopCondition()) {
            return pathGenerator.getStopCondition();
        }
        throw new MachineException(Resource.getText(Bundle.NAME, "exception.condition.missing"));
    }
  
    private void setRequirementStatus(Element element, RequirementStatus status) {
        if (element instanceof Vertex) {
            for (Requirement requirement: ((Vertex) element).getRequirements()) {
                setRequirementStatus(requirement, status);
            }
        }
    }

    private void setRequirementStatus(Requirement requirement, RequirementStatus status) {
        requirement.setStatus(status);
    }

    private void executeActions(Element element) {
        if (isEdge(element)) {
            myEdgeFilter.executeActions((Edge)element);
        }
    }

}
