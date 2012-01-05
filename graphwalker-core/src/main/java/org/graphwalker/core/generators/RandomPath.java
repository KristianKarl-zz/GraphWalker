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

import org.graphwalker.core.machine.Machine;
import org.graphwalker.core.model.Element;
import org.graphwalker.core.util.Resource;

import java.util.List;
import java.util.Random;

/**
 * <p>RandomPath class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class RandomPath extends AbstractPathGenerator {

    private final Random myRandomGenerator = new Random(System.nanoTime());

    /** {@inheritDoc} */
    public Element getNextStep(Machine machine) {
        List<Element> childElements = machine.getPossibleElements(machine.getCurrentElement());
        if (0<childElements.size()) {
            return childElements.get(myRandomGenerator.nextInt(childElements.size()));
        }
        throw new PathGeneratorException(Resource.getText("exception.generator.path.missing"));
    }
}
