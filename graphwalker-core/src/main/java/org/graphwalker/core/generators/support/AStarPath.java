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
package org.graphwalker.core.generators.support;

import org.graphwalker.core.Bundle;
import org.graphwalker.core.algorithms.AStar;
import org.graphwalker.core.common.ResourceUtils;
import org.graphwalker.core.conditions.support.ReachedEdge;
import org.graphwalker.core.conditions.support.ReachedVertex;
import org.graphwalker.core.generators.AbstractPathGenerator;
import org.graphwalker.core.generators.PathGeneratorException;
import org.graphwalker.core.machine.ExecutionContext;
import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.Model;
import org.graphwalker.core.model.ModelElement;
import org.graphwalker.core.model.Vertex;

import java.util.List;

/**
 * @author Nils Olsson
 */
public final class AStarPath extends AbstractPathGenerator {

    private final AStar algorithm = new AStar();

    /**
     * <p>Constructor for AStarPath.</p>
     *
     * @param stopCondition a {@link org.graphwalker.core.conditions.StopCondition} object.
     */
    public AStarPath(ReachedVertex stopCondition) {
        super(stopCondition);
    }

    public AStarPath(ReachedEdge stopCondition) {
        super(stopCondition);
    }

    /** {@inheritDoc} */
    public ModelElement getNextStep(ExecutionContext executionContext, List<ModelElement> elements) {
        if (elements.isEmpty()) {
            throw new PathGeneratorException(ResourceUtils.getText(Bundle.NAME, "exception.generator.path.missing"));
        }

        ModelElement target = null;
        Model model = executionContext.getModel();
        int distance = Integer.MAX_VALUE;


        if (null != model.getEdgesByName(getStopCondition().getValue())) {
            for (Edge edge: model.getEdgesByName(getStopCondition().getValue())) {
                int edgeDistance = model.getShortestDistance(executionContext.getCurrentElement(), edge);
                if (edgeDistance < distance) {
                    distance = edgeDistance;
                    target = edge;
                }
            }
        }
        if (null != model.getVerticesByName(getStopCondition().getValue())) {
            for (Vertex vertex: model.getVerticesByName(getStopCondition().getValue())) {
                int vertexDistance = model.getShortestDistance(executionContext.getCurrentElement(), vertex);
                if (vertexDistance < distance) {
                    distance = vertexDistance;
                    target = vertex;
                }
            }
        }

        return algorithm.getPath(executionContext, executionContext.getCurrentElement(), target).get(1);

    }
}
