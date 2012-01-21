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
import org.graphwalker.core.model.*;
import org.graphwalker.core.util.Resource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

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
    private List<Element> myElementStack = new ArrayList<Element>();

    /**
     * <p>Constructor for MachineImpl.</p>
     *
     * @param configuration a {@link org.graphwalker.core.configuration.Configuration} object.
     */
    public MachineImpl(Configuration configuration) {
        myConfiguration = configuration;
        myEdgeFilter = configuration.getEdgeFilter();
        myCurrentModel = configuration.getDefaultModel();
        myCurrentElement = myCurrentModel.getStartVertex();
        myCurrentElement.markAsVisited();
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

    /**
     * <p>getCurrentModel.</p>
     *
     * @return a {@link org.graphwalker.core.model.Model} object.
     */
    public Model getCurrentModel() {
        return myCurrentModel;
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

    /**
     * <p>hasNextStep.</p>
     *
     * @return a boolean.
     */
    public boolean hasNextStep() {
        Model model = getCurrentModel();
        Element element = getCurrentElement();
        if (element instanceof Vertex) {
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
            myElementStack.add(myCurrentElement);
            myCurrentElement = getPathGenerator(getCurrentModel()).getNextStep(this);
            myCurrentElement.markAsVisited();
            if (myCurrentElement instanceof Edge) {
                myEdgeFilter.executeActions((Edge)myCurrentElement);
            }
            if (myCurrentElement instanceof Vertex) {
                for (Requirement requirement: ((Vertex) myCurrentElement).getRequirements()) {
                    requirement.setStatus(RequirementStatus.PASSED);
                }
            }
        } while (!myCurrentElement.hasName() && hasNextStep());
        return myCurrentElement;
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

    private void switchModel(String modelId) {
        myCurrentModel = myConfiguration.getModel(modelId);
        myCurrentElement = myCurrentModel.getStartVertex();
    }

    private void backtrack() {
        if (0<myElementStack.size()) {
            do {
                if (myCurrentElement instanceof Edge) {
                    ((Edge) myCurrentElement).setBlocked(true);
                }
                myCurrentElement = myElementStack.remove(myElementStack.size()-1);
            } while (0<myElementStack.size() && !hasNextStep());
        }
    }
    
    /**
     * <p>executePath.</p>
     */
    public void executePath() {
        while (hasNextStep()) {
            Element element = getNextStep();
            if (null != element.getName() && !"".equals(element.getName())) {
                if (getCurrentModel().hasImplementation()) {
                    //try
                    executeElement(element);
                    //catch
                    /*
                    if (element instanceof Vertex) {
                        Vertex vertex = (Vertex)element;
                        for (Requirement requirement: vertex.getRequirements()) {
                            requirement.setStatus(RequirementStatus.FAILED);
                        }
                    }
                    backtrack();
                    */
                } else {
                    throw new MachineException(Resource.getText(Bundle.NAME, "exception.implementation.missing", getCurrentModel().getId()));
                }
            }
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
