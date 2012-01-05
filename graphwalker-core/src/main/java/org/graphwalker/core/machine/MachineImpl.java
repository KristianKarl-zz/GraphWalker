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

import org.graphwalker.core.conditions.StopCondition;
import org.graphwalker.core.configuration.Configuration;
import org.graphwalker.core.filter.EdgeFilter;
import org.graphwalker.core.generators.PathGenerator;
import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.Element;
import org.graphwalker.core.model.Model;
import org.graphwalker.core.model.Vertex;
import org.graphwalker.core.util.Resource;

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
        throw new MachineException(Resource.getText("exception.generator.missing"));
    }

    private StopCondition getStopCondition(PathGenerator pathGenerator) {
        if (null != pathGenerator.getStopCondition()) {
            return pathGenerator.getStopCondition();
        }
        throw new MachineException(Resource.getText("exception.condition.missing"));
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
        myCurrentElement = getPathGenerator(getCurrentModel()).getNextStep(this);
        myCurrentElement.markAsVisited();
        if (myCurrentElement instanceof Edge) {
            myEdgeFilter.executeActions((Edge)myCurrentElement);
            if (null == myCurrentElement.getName() || "".equals(myCurrentElement.getName())) {
                myCurrentElement = getNextStep();
            }
        }
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
}
