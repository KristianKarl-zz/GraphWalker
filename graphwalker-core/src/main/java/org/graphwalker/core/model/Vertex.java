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
package org.graphwalker.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Vertex class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class Vertex extends AbstractElement {

    private final static Map<String, Requirement> allRequirements = new HashMap<String, Requirement>();
    private final Map<String, Requirement> requirements = new HashMap<String, Requirement>();
    private final Map<String, Edge> edges = new HashMap<String, Edge>();
    private String switchModelId;

    /**
     * <p>Constructor for Vertex.</p>
     */
    public Vertex() {
    }

    /**
     * <p>Constructor for Vertex.</p>
     *
     * @param name a {@link java.lang.String} object.
     */
    public Vertex(String name) {
        super(name);
    }

    /**
     * <p>addRequirement.</p>
     *
     * @param requirement a {@link org.graphwalker.core.model.Requirement} object.
     */
    public void addRequirement(Requirement requirement) {
        if (allRequirements.containsKey(requirement.getId())) {
            Requirement existingRequirement = allRequirements.get(requirement.getId());
            requirements.put(existingRequirement.getId(), existingRequirement);
        } else {
            allRequirements.put(requirement.getId(), requirement);
            requirements.put(requirement.getId(), requirement);
        }
    }

    /**
     * <p>removeRequirement.</p>
     *
     * @param requirement a {@link org.graphwalker.core.model.Requirement} object.
     */
    public void removeRequirement(Requirement requirement) {
        if (requirements.containsKey(requirement.getId())) {
            requirements.remove(requirement.getId());
        }
    }

    /**
     * <p>getRequirements.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Requirement> getRequirements() {
        return new ArrayList<Requirement>(requirements.values());
    }

    /**
     * <p>addEdge.</p>
     *
     * @param edge a {@link Edge} object.
     */
    public void addEdge(Edge edge) {
        if (!edges.containsKey(edge.getId())) {
            edges.put(edge.getId(), edge);
        }
    }

    /**
     * <p>removeEdge.</p>
     *
     * @param edge a {@link Edge} object.
     */
    public void removeEdge(Edge edge) {
        if (edges.containsKey(edge.getId())) {
            edges.remove(edge.getId());
        }
    }

    /**
     * <p>getEdges.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Edge> getEdges() {
        return new ArrayList<Edge>(edges.values());
    }

    /**
     * <p>getSwitchModelId.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getSwitchModelId() {
        return switchModelId;
    }

    /**
     * <p>setSwitchModelId.</p>
     *
     * @param id a {@link java.lang.String} object.
     */
    public void setSwitchModelId(String id) {
        switchModelId = id;
    }

    /**
     * <p>hasSwitchModel.</p>
     *
     * @return a boolean.
     */
    public boolean hasSwitchModel() {
        return null != switchModelId;
    }
}
