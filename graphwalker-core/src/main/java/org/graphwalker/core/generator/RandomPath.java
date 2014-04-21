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
package org.graphwalker.core.generator;

import org.graphwalker.core.StopCondition;
import org.graphwalker.core.machine.ExecutionContext;
import org.graphwalker.core.model.Element;

import java.util.List;
import java.util.Random;

/**
 * @author Nils Olsson
 */
public final class RandomPath extends BasePathGenerator {

    private final Random random = new Random(System.nanoTime());

  public RandomPath() {
  }

  public RandomPath(StopCondition stopCondition) {
    super(stopCondition);
  }

  public Element getNextStep(ExecutionContext context) {
        List<Element> elements = context.getModel().getElements(context.getCurrentElement());
        if (elements.isEmpty()) {
            throw new NoPathFoundException();
        }
        return context.setCurrentElement(elements.get(random.nextInt(elements.size())));
    }
}
