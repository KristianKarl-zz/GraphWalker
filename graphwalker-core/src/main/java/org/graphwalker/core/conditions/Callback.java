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
package org.graphwalker.core.conditions;

import org.graphwalker.core.Bundle;
import org.graphwalker.core.model.Element;
import org.graphwalker.core.model.Model;
import org.graphwalker.core.utils.Reflection;
import org.graphwalker.core.utils.Resource;

public class Callback implements StopCondition {

    private final String methodName;

    public Callback(String methodName) {
        this.methodName = methodName;
    }

    public boolean isFulfilled(Model model, Element element) {
        if (isBoolean(model)) {
            return Reflection.execute(model.getImplementation(), methodName, Boolean.class);
        } else if (isDouble(model)) {
            return getFulfilment(model, element) >= FULFILLMENT_LEVEL;
        }
        throw new StopConditionException(Resource.getText(Bundle.NAME, "exception.condition.wrong.type"));
    }

    public double getFulfilment(Model model, Element element) {
        if (isBoolean(model)) {
            return Reflection.execute(model.getImplementation(), methodName, Boolean.class)?1.0:0.0;
        } else if (isDouble(model)) {
            return Reflection.execute(model.getImplementation(), methodName, Double.class);
        }
        throw new StopConditionException(Resource.getText(Bundle.NAME, "exception.condition.wrong.type"));
    }

    private boolean isDouble(Model model) {
        return Reflection.isReturnType(model.getImplementation(), methodName, double.class)
                || Reflection.isReturnType(model.getImplementation(), methodName, Double.class);
    }

    private boolean isBoolean(Model model) {
        return Reflection.isReturnType(model.getImplementation(), methodName, boolean.class)
                || Reflection.isReturnType(model.getImplementation(), methodName, Boolean.class);
    }
}
