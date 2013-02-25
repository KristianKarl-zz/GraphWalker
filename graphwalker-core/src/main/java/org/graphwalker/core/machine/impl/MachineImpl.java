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
package org.graphwalker.core.machine.impl;

import org.graphwalker.core.Bundle;
import org.graphwalker.core.annotations.*;
import org.graphwalker.core.conditions.StopCondition;
import org.graphwalker.core.configuration.Configuration;
import org.graphwalker.core.filter.EdgeFilter;
import org.graphwalker.core.generators.PathGenerator;
import org.graphwalker.core.machine.ExceptionStrategy;
import org.graphwalker.core.machine.Machine;
import org.graphwalker.core.machine.MachineException;
import org.graphwalker.core.model.*;
import org.graphwalker.core.model.status.ModelStatus;
import org.graphwalker.core.model.status.RequirementStatus;
import org.graphwalker.core.utils.Reflection;
import org.graphwalker.core.utils.Resource;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>MachineImpl class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class MachineImpl implements Machine {

    private final AnnotationProcessor myAnnotationProcessor = new AnnotationProcessorImpl();
    private final Configuration myConfiguration;
    private Model myCurrentModel;
    private Element myCurrentElement;

    /**
     * <p>Constructor for MachineImpl.</p>
     *
     * @param configuration a {@link org.graphwalker.core.configuration.Configuration} object.
     */
    public MachineImpl(Configuration configuration) {
        myConfiguration = configuration;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * <p>getConfiguration.</p>
     *
     * @return a {@link org.graphwalker.core.configuration.Configuration} object.
     */
    public Configuration getConfiguration() {
        return myConfiguration;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * <p>getCurrentElement.</p>
     *
     * @return a {@link org.graphwalker.core.model.Element} object.
     */
    public Element getCurrentElement() {
        return myCurrentElement;
    }

    /**
     * {@inheritDoc}
     */
    public void setCurrentElement(Element element) {
        myCurrentElement = element;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * <p>getCurrentModel.</p>
     *
     * @return a {@link org.graphwalker.core.model.Model} object.
     */
    public Model getCurrentModel() {
        if (null == myCurrentModel) {
            setCurrentModel(getConfiguration().getDefaultModel());
            setCurrentElement(getCurrentModel().getStartVertex());
            getCurrentElement().markAsVisited();
        }
        return myCurrentModel;
    }

    /**
     * {@inheritDoc}
     */
    public void setCurrentModel(Model model) {
        myCurrentModel = model;
        beforeGroup();
        if (isModelStatus(myCurrentModel, ModelStatus.NOT_EXECUTED)) {
            processAnnotation(BeforeModel.class, myCurrentModel, null);
            myCurrentModel.setModelStatus(ModelStatus.EXECUTING);
        }
    }

    /**
     * {@inheritDoc}
     * <p/>
     * <p>hasNextStep.</p>
     *
     * @return a boolean.
     */
    public boolean hasNextStep() {
        // if the current model's state is a vertex with a switch model statement
        if (isVertex(getCurrentElement()) && ((Vertex) getCurrentElement()).hasSwitchModel()) {
            // then we check if the switch model has any more steps to take
            if (hasVertexNextStep(getVertex(getCurrentElement()))) {
                return true;
            }
        }
        // next we check if the current model has any more steps to take
        if (hasModelNextStep(getCurrentModel(), getCurrentElement())) {
            return true;
        } else if (isModelStatus(getCurrentModel(), ModelStatus.EXECUTING)) {
            updateModelStatus(getCurrentModel());
            processAnnotation(AfterModel.class, getCurrentModel(), null);
            afterGroup();
        }
        // and finally we go through all the models in order to find any other step we can take
        for (Model model : getConfiguration().getModels()) {
            if (!getCurrentModel().equals(model)) {
                if (hasExecutableState(model)) {
                    return true;
                }
            }
        }
        // there is no more steps
        return false;
    }

    private void updateModelStatus(Model model) {
        ExceptionStrategy exceptionStrategy = model.getExceptionStrategy();
        if (exceptionStrategy.hasExceptions(model)) {
            model.setModelStatus(ModelStatus.FAILED);
        } else {
            model.setModelStatus(ModelStatus.COMPLETED);
        }
    }

    private boolean hasVertexNextStep(Vertex vertex) {
        if (vertex.hasSwitchModel()) {
            Model model = getConfiguration().getModel(vertex.getSwitchModelId());
            if (null == model) {
                throw new MachineException(Resource.getText(Bundle.NAME, "exception.model.missing", vertex.getSwitchModelId()));
            }
            if (hasModelNextStep(model, model.getStartVertex())) {
                return true;
            }
        }
        return false;
    }

    private boolean hasModelNextStep(Model model, Element element) {
        if (!isModelStatus(model, ModelStatus.FAILED) && !isModelStatus(model, ModelStatus.COMPLETED)) {
            PathGenerator pathGenerator = getPathGenerator(model);
            StopCondition stopCondition = getStopCondition(pathGenerator);
            return !stopCondition.isFulfilled(model, element);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * <p>getNextStep.</p>
     *
     * @return a {@link org.graphwalker.core.model.Element} object.
     */
    public Element getNextStep() {
        // if the current model's state is a vertex with a switch model statement
        if (isVertex(getCurrentElement()) && ((Vertex) getCurrentElement()).hasSwitchModel()) {
            switchModel(getVertex(getCurrentElement()).getSwitchModelId());
        }
        // if the current model doesn't have any more steps we try to find one model that have
        if (!hasModelNextStep(getCurrentModel(), getCurrentElement())) {
            for (Model model : getConfiguration().getModels()) {
                if (!getCurrentModel().equals(model)) {
                    if (hasExecutableState(model)) {
                        switchModel(model.getId());
                        break;
                    }
                }
            }
        }
        // now we will try to take the next step
        try {
            setCurrentElement(getPathGenerator(getCurrentModel()).getNextStep(this));
            getCurrentElement().markAsVisited();
            executeActions(getCurrentElement());
            if (getCurrentModel().hasImplementation() && getCurrentElement().hasName()) {
                executeElement(getCurrentModel(), getCurrentElement());
            }
            setRequirementStatus(getCurrentElement(), RequirementStatus.PASSED);
        } catch (Throwable throwable) {
            setRequirementStatus(getCurrentElement(), RequirementStatus.FAILED);
            getCurrentModel().getExceptionStrategy().handleException(this, throwable);
        }
        return getCurrentElement();
    }

    /**
     * {@inheritDoc}
     */
    public List<Element> getPossibleElements(Element element) {
        List<Element> possibleElements = new ArrayList<Element>();
        if (element instanceof Vertex) {
            Vertex vertex = (Vertex) element;
            EdgeFilter edgeFilter = getConfiguration().getEdgeFilter();
            for (Edge edge : vertex.getEdges()) {
                if (!edge.isBlocked() && edgeFilter.acceptEdge(getCurrentModel(), edge)) {
                    possibleElements.add(edge);
                }
            }
        } else if (element instanceof Edge) {
            possibleElements.add(((Edge) element).getTarget());
        }
        return possibleElements;
    }

    private boolean isVertex(Element element) {
        return element instanceof Vertex;
    }

    private Vertex getVertex(Element element) {
        return (Vertex) element;
    }

    private boolean isEdge(Element element) {
        return element instanceof Edge;
    }

    private boolean hasExecutableState(Model model) {
        return isModelStatus(model, ModelStatus.NOT_EXECUTED) || isModelStatus(model, ModelStatus.EXECUTING);
    }

    private void switchModel(String modelId) {
        if (!hasModelNextStep(getCurrentModel(), getCurrentElement()) && isModelStatus(getCurrentModel(), ModelStatus.EXECUTING)) {
            updateModelStatus(getCurrentModel());
            processAnnotation(AfterModel.class, getCurrentModel(), null);
            afterGroup();
        }
        setCurrentModel(getConfiguration().getModel(modelId));
        setCurrentElement(getCurrentModel().getStartVertex());
        getCurrentElement().markAsVisited();
    }

    private boolean isModelStatus(Model model, ModelStatus status) {
        return status.equals(model.getModelStatus());
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
            for (Requirement requirement : ((Vertex) element).getRequirements()) {
                setRequirementStatus(requirement, status);
            }
        }
    }

    private void setRequirementStatus(Requirement requirement, RequirementStatus newStatus) {
        if (!requirement.getStatus().equals(newStatus)) {
            RequirementStatus oldStatus = requirement.getStatus();
            requirement.setStatus(newStatus);
            if (getCurrentModel().hasImplementation() && getCurrentModel().getImplementation() instanceof RequirementStatusListener) {
                ((RequirementStatusListener) getCurrentModel().getImplementation()).requirementStatusChanged(requirement, oldStatus, newStatus);
            }
        }
    }

    private void executeActions(Element element) {
        if (isEdge(element)) {
            EdgeFilter edgeFilter = getConfiguration().getEdgeFilter();
            edgeFilter.executeActions(getCurrentModel(), (Edge) element);
        }
    }

    private void executeElement(Model model, Element element) {
        processAnnotation(BeforeElement.class, model, element);
        Reflection.execute(model.getImplementation(), element.getName());
        processAnnotation(AfterElement.class, model, element);
    }

    private void processAnnotation(Class<? extends Annotation> annotation, Model model, Element element) {
        if (model.hasImplementation()) {
            try {
                myAnnotationProcessor.process(annotation, this, model, element);
            } catch (Throwable throwable) {
                model.getExceptionStrategy().handleException(this, throwable);
            }
        }
    }

    private void beforeGroup() {
        boolean executeBeforeGroup = true;
        for (Model model : getConfiguration().getModels()) {
            executeBeforeGroup &= ModelStatus.NOT_EXECUTED.equals(model.getModelStatus());
        }
        if (executeBeforeGroup) {
            for (Model model : getConfiguration().getModels()) {
                processAnnotation(BeforeGroup.class, model, null);
            }
        }
    }

    private void afterGroup() {
        boolean executeAfterGroup = true;
        for (Model model : getConfiguration().getModels()) {
            executeAfterGroup &= (ModelStatus.COMPLETED.equals(model.getModelStatus()) || ModelStatus.FAILED.equals(model.getModelStatus()));
        }
        if (executeAfterGroup) {
            for (Model model : getConfiguration().getModels()) {
                processAnnotation(AfterGroup.class, model, null);
            }
        }
    }
}
