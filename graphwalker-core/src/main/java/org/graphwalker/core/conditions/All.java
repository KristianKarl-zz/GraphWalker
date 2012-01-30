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

import java.util.ArrayList;
import java.util.List;

/**
 * <p>All class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class All implements StopCondition {

    private final List<StopCondition> myStopConditions = new ArrayList<StopCondition>();

    /**
     * <p>addStopCondition.</p>
     *
     * @param stopCondition a {@link org.graphwalker.core.conditions.StopCondition} object.
     */
    public void addStopCondition(StopCondition stopCondition) {
        myStopConditions.add(stopCondition);
    }

    /**
     * <p>size.</p>
     *
     * @return a int.
     */
    public int size() {
        return myStopConditions.size();
    }

    /**
     * <p>getText.</p>
     *
     * @param index a int.
     * @return a {@link org.graphwalker.core.conditions.StopCondition} object.
     */
    public StopCondition get(int index) {
        return myStopConditions.get(index);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isFulfilled(Model model, Element element) {
		for (StopCondition stopCondition: myStopConditions) {
			if (!stopCondition.isFulfilled(model, element)) {
				return false;
			}
		}
		return true;
    }

    /** {@inheritDoc} */
    @Override
    public double getFulfilment(Model model, Element element) {
		double fulfilment = 0;
		for (StopCondition stopCondition: myStopConditions) {
			fulfilment += stopCondition.getFulfilment(model, element);
		}
		return fulfilment / myStopConditions.size();
    }
}
