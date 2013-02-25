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
package org.graphwalker.core.configuration.impl;

import org.graphwalker.core.Bundle;
import org.graphwalker.core.configuration.Configuration;
import org.graphwalker.core.filter.EdgeFilter;
import org.graphwalker.core.filter.impl.EdgeFilterImpl;
import org.graphwalker.core.generators.PathGenerator;
import org.graphwalker.core.model.Model;
import org.graphwalker.core.utils.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>ConfigurationImpl class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class ConfigurationImpl implements Configuration {

    private String myDefaultModelId;
    private PathGenerator myDefaultGenerator;
    private final Map<String, Model> myModels = new HashMap<String, Model>();
    private EdgeFilter myEdgeFilter;

    /**
     * <p>Constructor for ConfigurationImpl.</p>
     */
    public ConfigurationImpl() {
        myEdgeFilter = new EdgeFilterImpl(Resource.getText(Bundle.NAME, "default.language"));
    }

    /**
     * <p>getDefaultModel.</p>
     *
     * @return a {@link org.graphwalker.core.model.Model} object.
     */
    public Model getDefaultModel() {
        return myModels.get(myDefaultModelId);
    }

    /**
     * {@inheritDoc}
     */
    public void setDefaultModelId(String id) {
        myDefaultModelId = id;
    }

    /**
     * <p>getDefaultPathGenerator.</p>
     *
     * @return a {@link org.graphwalker.core.generators.PathGenerator} object.
     */
    public PathGenerator getDefaultPathGenerator() {
        return myDefaultGenerator;
    }

    /**
     * {@inheritDoc}
     */
    public void setDefaultPathGenerator(PathGenerator pathGenerator) {
        myDefaultGenerator = pathGenerator;
    }

    /**
     * {@inheritDoc}
     */
    public Model addModel(Model model) {
        if (null == myDefaultModelId) {
            setDefaultModelId(model.getId());
        }
        myModels.put(model.getId(), model);
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public Model getModel(String id) {
        return myModels.get(id);
    }

    /**
     * <p>getModels.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Model> getModels() {
        return new ArrayList<Model>(myModels.values());
    }

    /**
     * <p>getEdgeFilter.</p>
     *
     * @return a {@link org.graphwalker.core.filter.EdgeFilter} object.
     */
    public EdgeFilter getEdgeFilter() {
        return myEdgeFilter;
    }

    /**
     * {@inheritDoc}
     */
    public void setEdgeFilter(EdgeFilter edgeFilter) {
        myEdgeFilter = edgeFilter;
    }

}
