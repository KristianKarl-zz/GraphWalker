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
package org.graphwalker.core.configuration;

import org.graphwalker.core.filter.EdgeFilter;
import org.graphwalker.core.generators.PathGenerator;
import org.graphwalker.core.model.Model;

import java.util.List;

/**
 * <p>Configuration interface.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public interface Configuration {

    /**
     * <p>getDefaultModel.</p>
     *
     * @return a {@link org.graphwalker.core.model.Model} object.
     */
    Model getDefaultModel();
    /**
     * <p>setDefaultModelId.</p>
     *
     * @param id a {@link java.lang.String} object.
     */
    void setDefaultModelId(String id);
    /**
     * <p>getDefaultPathGenerator.</p>
     *
     * @return a {@link org.graphwalker.core.generators.PathGenerator} object.
     */
    PathGenerator getDefaultPathGenerator();
    /**
     * <p>setDefaultPathGenerator.</p>
     *
     * @param pathGenerator a {@link org.graphwalker.core.generators.PathGenerator} object.
     */
    void setDefaultPathGenerator(PathGenerator pathGenerator);
    /**
     * <p>addModel.</p>
     *
     * @param model a {@link org.graphwalker.core.model.Model} object.
     * @return a {@link org.graphwalker.core.model.Model} object.
     */
    Model addModel(Model model);
    /**
     * <p>getModel.</p>
     *
     * @param id a {@link java.lang.String} object.
     * @return a {@link org.graphwalker.core.model.Model} object.
     */
    Model getModel(String id);
    /**
     * <p>getModels.</p>
     *
     * @return a {@link java.util.List} object.
     */
    List<Model> getModels();
    /**
     * <p>getEdgeFilter.</p>
     *
     * @return a {@link org.graphwalker.core.filter.EdgeFilter} object.
     */
    EdgeFilter getEdgeFilter();
    /**
     * <p>setEdgeFilter.</p>
     *
     * @param edgeFilter a {@link org.graphwalker.core.filter.EdgeFilter} object.
     */
    void setEdgeFilter(EdgeFilter edgeFilter);

}
