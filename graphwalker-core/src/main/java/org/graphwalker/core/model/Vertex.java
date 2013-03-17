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
package org.graphwalker.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>Vertex class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public final class Vertex extends ModelElement {

    private final List<Requirement> requirements;
    private final String switchModelId;
    private final String comment;

    /**
     * <p>Constructor for Vertex.</p>
     *
     * @param id a {@link java.lang.String} object.
     * @param name a {@link java.lang.String} object.
     * @param blocked a {@link java.lang.Boolean} object.
     * @param comment a {@link java.lang.String} object.
     * @param switchModelId a {@link java.lang.String} object.
     * @param requirements a {@link java.util.List} object.
     */
    public Vertex(String id, String name, Boolean blocked, String comment, String switchModelId, List<Requirement> requirements) {
        super(id, name, blocked, comment);
        this.requirements = Collections.unmodifiableList(null!=requirements?requirements:new ArrayList<Requirement>(0));
        this.switchModelId = switchModelId;
        this.comment = comment;
    }

    /**
     * <p>Getter for the field <code>requirements</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Requirement> getRequirements() {
        return requirements;
    }

    /**
     * <p>hasSwitchModel.</p>
     *
     * @return a {@link java.lang.Boolean} object.
     */
    public Boolean hasSwitchModel() {
        return null != switchModelId && !"".equals(switchModelId);
    }

    /**
     * <p>Getter for the field <code>switchModelId</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getSwitchModelId() {
        return switchModelId;
    }

    /**
     * <p>Getter for the field <code>comment</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getComment() {
        return comment;
    }
}
