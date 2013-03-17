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
import org.graphwalker.core.generators.PathGenerator;
import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.ModelElement;
import org.graphwalker.core.model.Vertex;
import org.graphwalker.core.model.status.ModelStatus;
import org.graphwalker.core.model.support.ModelContext;
import org.graphwalker.core.utils.Resource;

import java.util.List;
import java.util.Stack;

/**
 * <p>MachineImpl class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class Machine {

    private Configuration configuration = null;
    private Stack<ModelContext> contexts = new Stack<ModelContext>();
    private ModelContext currentContext = null;

    public Machine() {}

    /**
     * <p>Constructor for Machine.</p>
     *
     * @param configuration a {@link org.graphwalker.core.configuration.Configuration} object.
     * @param context a {@link org.graphwalker.core.model.support.ModelContext} object.
     */
    public Machine(Configuration configuration, ModelContext context) {
        this.configuration = configuration;
        setContext(context);
    }

    /**
     * <p>Getter for the field <code>configuration</code>.</p>
     *
     * @return a {@link org.graphwalker.core.configuration.Configuration} object.
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    private void setContext(ModelContext context) {
        if (null != currentContext) {
            contexts.push(currentContext);
        }
        currentContext = context;
    }

    private ModelContext getContext() {
        if (null == currentContext) {
            currentContext = getNextContext();
        }
        return currentContext;
    }

    private ModelContext getNextContext() {
        if (!contexts.empty()) {
            currentContext = contexts.pop();
        } else {
            currentContext = null;
        }
        return currentContext;
    }

    //private final AnnotationProcessor annotationProcessor = new AnnotationProcessorImpl();
    //private final Configuration configuration;
    //private Model currentModel;
    //private Element currentElement;

    /**
     * <p>Constructor for MachineImpl.</p>
     *
     * @param configuration a {@link org.graphwalker.core.configuration.Configuration} object.
     */
    //public Machine(Configuration configuration) {
    //    this.configuration = configuration;
    //}

    /**
     * {@inheritDoc}
     * <p/>
     * <p>getConfiguration.</p>
     *
     * @return a {@link org.graphwalker.core.configuration.Configuration} object.
     */
    //public Configuration getConfiguration() {
    //    return configuration;
    //}

    /**
     * {@inheritDoc}
     * <p/>
     * <p>getCurrentElement.</p>
     *
     * @return a {@link org.graphwalker.core.model.Element} object.
     */
    //public Element getCurrentElement() {
    //    return currentElement;
    //}

    /**
     * {@inheritDoc}
     */
    //public void setCurrentElement(Element element) {
    //    currentElement = element;
    //}

    /**
     * {@inheritDoc}
     * <p/>
     * <p>getCurrentModel.</p>
     *
     * @return a {@link org.graphwalker.core.model.Model} object.
     */
    //public Model getCurrentModel() {
        /*
        if (null == currentModel) {
            setCurrentModel(getConfiguration().getDefaultModel());
            setCurrentElement(getCurrentModel().getStartVertex());
            getCurrentElement().markAsVisited();
        }
        return currentModel;
        */
    //    return null;
    //}

    /**
     * {@inheritDoc}
     */
    //public void setCurrentModel(Model model) {
        /*
        currentModel = model;
        beforeGroup();
        if (isModelStatus(currentModel, ModelStatus.NOT_EXECUTED)) {
            processAnnotation(BeforeModel.class, currentModel, null);
            currentModel.setModelStatus(ModelStatus.EXECUTING);
        }
        */
    //}

    /**
     * {@inheritDoc}
     * <p/>
     * <p>hasNextStep.</p>
     *
     * @return a boolean.
     */
    public boolean hasNextStep() {
        ModelElement currentElement = getContext().getCurrentElement();
        // if the current model is a vertex with a switch model statement
        if (isVertex(currentElement) && ((Vertex)currentElement).hasSwitchModel()) {
            String switchModelId = ((Vertex)currentElement).getSwitchModelId();
            if (!getConfiguration().hasModel(switchModelId)) {
                throw new MachineException(Resource.getText(Bundle.NAME, "exception.model.missing", switchModelId));
            }
            // then we create a new ModelContext and continue executing
            setContext(new ModelContext(getConfiguration().getModel(switchModelId)));
        }
        // next we check if the current model has any more steps to take
        if (hasModelNextStep()) {
            return true;
        } else if (isModelStatus(ModelStatus.EXECUTING)) {
            updateModelStatus();
            //processAnnotation(AfterModel.class, getCurrentModel(), null);
            //afterGroup();
        }
        // and finally we check if we have another context to execute
        if (null != getNextContext()) {
            return hasExecutableState();
        }
        // there is no more steps
        return false;



        /*
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
        */

    }
    /*
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


    */
    /**
     * {@inheritDoc}
     * <p/>
     * <p>getNextStep.</p>
     *
     * @return a {@link org.graphwalker.core.model.Element} object.
     * @param context a {@link org.graphwalker.core.model.support.ModelContext} object.
     */
    public ModelElement getNextStep(ModelContext context) {
        /*
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
        */
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @param context a {@link org.graphwalker.core.model.support.ModelContext} object.
     * @return a {@link java.util.List} object.
     */
    public List<ModelElement> getPossibleSteps(ModelContext context) {

        /*
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
        */
        return null;
    }

    private boolean hasModelNextStep() {
        if (!isModelStatus(ModelStatus.FAILED) && !isModelStatus(ModelStatus.COMPLETED)) {
            ModelContext context = getContext();
            PathGenerator pathGenerator = context.getPathGenerator();
            StopCondition stopCondition = pathGenerator.getStopCondition();
            return false; //!stopCondition.isFulfilled(context);
        }
        return false;
    }

    private boolean isVertex(ModelElement element) {
        return element instanceof Vertex;
    }

    private boolean isEdge(ModelElement element) {
        return element instanceof Edge;
    }

    private boolean isModelStatus(ModelStatus status) {
        return status.equals(getContext().getStatus());
    }

    private boolean hasExecutableState() {
        return isModelStatus(ModelStatus.NOT_EXECUTED) || isModelStatus(ModelStatus.EXECUTING);
    }

    private void updateModelStatus() {
        /*
        ModelContext context = getContext();
        ExceptionStrategy exceptionStrategy = context.getExceptionStrategy();
        if (exceptionStrategy.hasExceptions()) {
            context.setStatus(ModelStatus.FAILED);
        } else {
            context.setStatus(ModelStatus.COMPLETED);
        }
        */
    }

    /*
    private Vertex getVertex(Element element) {
        return (Vertex) element;
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
                annotationProcessor.process(annotation, this, model, element);
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
    */
}
