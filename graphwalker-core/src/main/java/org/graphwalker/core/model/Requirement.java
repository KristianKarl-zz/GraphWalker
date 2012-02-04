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

import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.guard.Guarded;

@Guarded
/**
 * <p>Requirement class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class Requirement {

    private final String myId;
    private RequirementStatus myRequirementStatus = RequirementStatus.NOT_COVERED;

    /**
     * <p>Constructor for Requirement.</p>
     *
     * @param id a {@link java.lang.String} object.
     */
    public Requirement(@NotNull @NotEmpty String id) {
        myId = id;
    }

    /**
     * <p>getId.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getId() {
        return myId;
    }
    
    /**
     * <p>isFulfilled.</p>
     *
     * @return a boolean.
     */
    public RequirementStatus getStatus() {
        return myRequirementStatus;
    }

    /**
     * <p>markAsFulfilled.</p>
     *
     * @param status a {@link org.graphwalker.core.model.RequirementStatus} object.
     */
    public void setStatus(RequirementStatus status) {
        myRequirementStatus = status;
    }

}
