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
package org.graphwalker.core.conditions;

import net.sf.oval.constraint.Min;
import net.sf.oval.guard.Guarded;
import org.graphwalker.core.model.Element;
import org.graphwalker.core.model.Model;

@Guarded
/**
 * <p>Length class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class Length implements StopCondition {

    private final long myLength;

    /**
     * <p>Constructor for Length.</p>
     *
     * @param length a long.
     */
    public Length(@Min(1) long length) {
        myLength = length;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isFulfilled(Model model, Element element) {
        return model.getTotalVisitCount() >= myLength;
    }

    /** {@inheritDoc} */
    @Override
    public double getFulfilment(Model model, Element element) {
        return (double) model.getTotalVisitCount() / myLength;
    }
}
