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
package org.graphwalker.core;

import org.graphwalker.core.configuration.Configuration;
import org.graphwalker.core.machine.Machine;
import org.graphwalker.core.machine.impl.MachineImpl;
import org.graphwalker.core.model.Element;
import org.graphwalker.core.model.Model;

import java.util.List;

/**
 * <p>GraphWalkerImpl class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class GraphWalkerImpl implements GraphWalker {

    private final Machine myMachine;

    /**
     * <p>Constructor for GraphWalkerImpl.</p>
     *
     * @param configuration a {@link org.graphwalker.core.configuration.Configuration} object.
     */
    public GraphWalkerImpl(Configuration configuration) {
        myMachine = new MachineImpl(configuration);
    }

    private Machine getMachine() {
        return myMachine;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * <p>hasNextStep.</p>
     *
     * @return a boolean.
     */
    public boolean hasNextStep() {
        return getMachine().hasNextStep();
    }

    /**
     * {@inheritDoc}
     * <p/>
     * <p>getNextStep.</p>
     *
     * @return a {@link org.graphwalker.core.model.Element} object.
     */
    public Element getNextStep() {
        return getMachine().getNextStep();
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasExceptions(Model model) {
        return model.getExceptionStrategy().hasExceptions(model);
    }

    /**
     * {@inheritDoc}
     */
    public List<Throwable> getExceptions(Model model) {
        return model.getExceptionStrategy().getExceptions(model);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * <p>getConfiguration.</p>
     *
     * @return a {@link org.graphwalker.core.configuration.Configuration} object.
     */
    public Configuration getConfiguration() {
        return getMachine().getConfiguration();
    }
}
