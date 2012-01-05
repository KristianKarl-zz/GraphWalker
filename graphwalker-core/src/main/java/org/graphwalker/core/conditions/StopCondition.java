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

import org.graphwalker.core.model.Element;
import org.graphwalker.core.model.Model;

/**
 * <p>StopCondition interface.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public interface StopCondition {

    /**
     * <p>isFulfilled.</p>
     *
     * @return a boolean.
     * @param model a {@link org.graphwalker.core.model.Model} object.
     * @param element a {@link org.graphwalker.core.model.Element} object.
     */
    boolean isFulfilled(Model model, Element element);

    /**
     * <p>getFulfilment.</p>
     *
     * @return a double.
     * @param model a {@link org.graphwalker.core.model.Model} object.
     * @param element a {@link org.graphwalker.core.model.Element} object.
     */
    double getFulfilment(Model model, Element element);
}
