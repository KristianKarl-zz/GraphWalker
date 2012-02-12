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

/**
 * <p>Abstract AbstractElement class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public abstract class AbstractElement implements Element {

    private String myId;
    private String myName;
    private long myVisitCount = 0L;
    private ElementStatus myStatus;
    /**
     * <p>Constructor for AbstractElement.</p>
     */
    AbstractElement() {
    }
    
    /**
     * <p>Constructor for AbstractElement.</p>
     *
     * @param name a {@link java.lang.String} object.
     */
    AbstractElement(String name) {
        myName = name;
    }
    
    /** {@inheritDoc} */
    @Override
    public String getId() {
        if (null != myId) {
            return myId;
        } else {
            return getName();
        }
    }

    /**
     * <p>setId.</p>
     *
     * @param id a {@link java.lang.String} object.
     */
    public void setId(String id) {
        myId = id;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return myName;
    }

    /**
     * <p>setName.</p>
     *
     * @param name a {@link java.lang.String} object.
     */
    public void setName(String name) {
        myName = name;
    }

    /**
     * <p>hasName.</p>
     *
     * @return a boolean.
     */
    public boolean hasName() {
        return null != myName && !"".equals(myName);
    }

    /**
     * <p>markAsVisited.</p>
     */
    public void markAsVisited() {
        myVisitCount++;
    }

    /**
     * <p>isVisited.</p>
     *
     * @return a boolean.
     */
    public boolean isVisited() {
        return 0<myVisitCount;
    }

    /**
     * <p>getVisitCount.</p>
     *
     * @return a long.
     */
    public long getVisitCount() {
        return myVisitCount;
    }

    /** {@inheritDoc} */
    public void setStatus(ElementStatus status) {
        myStatus = status;
    }

    /**
     * <p>getStatus.</p>
     *
     * @return a {@link org.graphwalker.core.model.ElementStatus} object.
     */
    public ElementStatus getStatus() {
        return myStatus;
    }

    /**
     * <p>isBlocked.</p>
     *
     * @return a boolean.
     */
    public boolean isBlocked() {
        return ElementStatus.BLOCKED.equals(getStatus());
    }
}
