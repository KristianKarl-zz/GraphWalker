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

import org.graphwalker.core.model.status.ElementStatus;

/**
 * <p>ModelElement interface.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public interface Element {

    void setId(String id);
    /**
     * <p>getId.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getId();
    /**
     * <p>getName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getName();
    void setName(String name);
    /**
     * <p>hasName.</p>
     *
     * @return a boolean.
     */
    boolean hasName();
    /**
     * <p>markAsVisited.</p>
     */
    void markAsVisited();
    /**
     * <p>isVisited.</p>
     *
     * @return a boolean.
     */
    boolean isVisited();

    /**
     * <p>getVisitCount.</p>
     *
     * @return a long.
     */
    long getVisitCount();
    /**
     * <p>setStatus.</p>
     *
     * @param status a {@link org.graphwalker.core.model.status.ElementStatus} object.
     */
    void setStatus(ElementStatus status);
    /**
     * <p>getStatus.</p>
     *
     * @return a {@link org.graphwalker.core.model.status.ElementStatus} object.
     */
    ElementStatus getStatus();
    /**
     * <p>isBlocked.</p>
     *
     * @return a boolean.
     */
    boolean isBlocked();
}
