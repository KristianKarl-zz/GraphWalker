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
import org.graphwalker.core.conditions.StopCondition;
import org.graphwalker.core.configuration.Configuration;
import org.graphwalker.core.filter.EdgeFilter;
import org.graphwalker.core.generators.PathGenerator;
import org.graphwalker.core.generators.PathGeneratorException;
import org.graphwalker.core.model.*;
import org.graphwalker.core.util.Resource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    /**
     * <p>getConfiguration.</p>
     *
     * @return a {@link org.graphwalker.core.configuration.Configuration} object.
     */
    public Configuration getConfiguration() {
        return myConfiguration;
    }
    
    /**
     * <p>getCurrentElement.</p>
     *
     * @return a {@link org.graphwalker.core.model.Element} object.
     */
    public Element getCurrentElement() {
        return myCurrentElement;
    }

    public void setCurrentElement(Element element) {
        myCurrentElement = element; 
    }
    
    /**
     * <p>getCurrentModel.</p>
     *
     * @return a {@link org.graphwalker.core.model.Model} object.
     */
    public Model getCurrentModel() {
        return myCurrentModel;
    }
    
    public void setCurrentModel(Model model) {
        myCurrentModel = model;
    }

    /**
     * <p>hasNextStep.</p>
     *
     * @return a boolean.
     */
    public boolean hasNextStep() {
        Model model = getCurrentModel();
        Element element = getCurrentElement();
        if (isVertex(element)) {
            Vertex vertex = (Vertex)element;
            if (vertex.hasSwitchModel()) {
                model = getConfiguration().getModel(vertex.getSwitchModelId());
                element = model.getStartVertex();
            }
        }
        return !getStopCondition(getPathGenerator(model)).isFulfilled(model, element);
    }

    /**
     * <p>getNextStep.</p>
     *
     * @return a {@link org.graphwalker.core.model.Element} object.
     */
    public Element getNextStep() {
        do {
            executeActions(getCurrentElement());
            setRequirementStatus(getCurrentElement(), RequirementStatus.PASSED);
            setCurrentElement(getPathGenerator(getCurrentModel()).getNextStep(this));
            getCurrentElement().markAsVisited();
        } while (hasNextStep() && !getCurrentElement().hasName());
        return getCurrentElement();
    }

    
    
    /** {@inheritDoc} */
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

    /**
     * <p>executePath.</p>
     */
    public void executePath() {
        while (hasNextStep()) {
            Element element = getNextStep();
            if (element.hasName()) {
                if (getCurrentModel().hasImplementation()) {
                    try {
                        executeElement(element);
                        setRequirementStatus(element, RequirementStatus.PASSED);
                    } catch (RuntimeException e) {
                        setRequirementStatus(element, RequirementStatus.FAILED);
                        //backtrack();
                        //restart();
                        throw e;
                    }
                } else {
                    throw new MachineException(Resource.getText(Bundle.NAME, "exception.implementation.missing", getCurrentModel().getId()));
                }
            }
        }
    }
    
    private void restart() {
        setCurrentElement(getCurrentModel().getStartVertex());
    }
    /*
    private void backtrack() {
        if (isEdge(getCurrentElement())) {
            backtrack((Edge)getCurrentElement());
        } else if (isVertex(getCurrentElement())) {
            backtrack((Vertex)getCurrentElement());
        }
    }
    
    private void backtrack(Edge edge) {
        edge.skip();
    }

    private void backtrack(Vertex vertex) {
        for (Edge edge: getCurrentModel().getEdges()) {
            if (vertex.equals(edge.getSource()) || vertex.equals(edge.getTarget())) {
                backtrack(edge);
            }
        }
    }
    */
    private boolean isVertex(Element element) {
        return element instanceof Vertex;
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
    
    private void executeElement(Element element) {
        Object object = getCurrentModel().getImplementation();
        Class clazz = object.getClass();
        try {
            Method method = clazz.getMethod(element.getName());
            if (null != method) {
                method.invoke(object);
            }
        } catch (InvocationTargetException e) {
            throw new MachineException(Resource.getText(Bundle.NAME, "exception.method.invocation", element.getName()), e);
        } catch (NoSuchMethodException e) {
            throw new MachineException(Resource.getText(Bundle.NAME, "exception.method.missing", element.getName()));
        } catch (IllegalAccessException e) {
            throw new MachineException(Resource.getText(Bundle.NAME, "exception.method.access", element.getName()));
        }
    }
}
