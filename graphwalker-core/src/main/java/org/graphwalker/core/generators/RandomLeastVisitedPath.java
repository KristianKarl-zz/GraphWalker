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

import org.graphwalker.core.Bundle;
import org.graphwalker.core.machine.Machine;
import org.graphwalker.core.model.Element;
import org.graphwalker.core.utils.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * <p>RandomLeastVisitedPath class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class RandomLeastVisitedPath extends AbstractPathGenerator {

    private final Random randomGenerator = new Random(System.nanoTime());

    /**
     * {@inheritDoc}
     */
    public Element getNextStep(Machine machine) {
        List<Element> possibleElements = machine.getPossibleElements(machine.getCurrentElement());
        long leastVisitedCount = Long.MAX_VALUE;
        List<Element> leastVisitedElements = new ArrayList<Element>();
        for (Element element : possibleElements) {
            long visitCount = element.getVisitCount();
            if (visitCount < leastVisitedCount) {
                leastVisitedCount = visitCount;
                leastVisitedElements = new ArrayList<Element>();
                leastVisitedElements.add(element);
            } else if (visitCount == leastVisitedCount) {
                leastVisitedElements.add(element);
            }
        }
        if (0 < leastVisitedElements.size()) {
            return leastVisitedElements.get(randomGenerator.nextInt(leastVisitedElements.size()));
        } else if (0 < possibleElements.size()) {
            return possibleElements.get(randomGenerator.nextInt(possibleElements.size()));
        }
        throw new PathGeneratorException(Resource.getText(Bundle.NAME, "exception.generator.path.missing"));
    }
}
