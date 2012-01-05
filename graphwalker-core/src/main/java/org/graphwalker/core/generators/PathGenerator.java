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

package org.graphwalker.core.generators;

import org.graphwalker.core.conditions.StopCondition;
import org.graphwalker.core.machine.Machine;
import org.graphwalker.core.model.Element;

/**
 * <p>PathGenerator interface.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public interface PathGenerator {

    /**
     * <p>getNextStep.</p>
     *
     * @param machine a {@link org.graphwalker.core.machine.Machine} object.
     * @return a {@link org.graphwalker.core.model.Element} object.
     */
    Element getNextStep(Machine machine);
    /**
     * <p>getStopCondition.</p>
     *
     * @return a {@link org.graphwalker.core.conditions.StopCondition} object.
     */
    StopCondition getStopCondition();
    /**
     * <p>setStopCondition.</p>
     *
     * @param stopCondition a {@link org.graphwalker.core.conditions.StopCondition} object.
     */
    void setStopCondition(StopCondition stopCondition);
}
