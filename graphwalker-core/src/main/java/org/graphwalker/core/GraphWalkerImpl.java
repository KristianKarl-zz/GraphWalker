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
import org.graphwalker.core.configuration.ConfigurationFactory;
import org.graphwalker.core.machine.Machine;
import org.graphwalker.core.machine.MachineImpl;
import org.graphwalker.core.model.Element;

import java.io.File;

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
     * @param file a {@link java.lang.String} object.
     */
    public GraphWalkerImpl(String file) {
        this(ConfigurationFactory.create(file));
    }

    /**
     * <p>Constructor for GraphWalkerImpl.</p>
     *
     * @param file a {@link java.io.File} object.
     */
    public GraphWalkerImpl(File file) {
        this(ConfigurationFactory.create(file));
    }

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

    /** {@inheritDoc} */
    @Override
    public void after() {
        getMachine().after();
    }

    /** {@inheritDoc} */
    @Override
    public void before() {
        getMachine().before();
    }

    /**
     * {@inheritDoc}
     *
     * <p>hasNextStep.</p>
     */
    @Override
    public boolean hasNextStep() {
        return getMachine().hasNextStep();
    }

    /**
     * {@inheritDoc}
     *
     * <p>getNextStep.</p>
     */
    @Override
    public Element getNextStep() {
        return getMachine().getNextStep();
    }

    /**
     * {@inheritDoc}
     *
     * <p>getConfiguration.</p>
     */
    @Override
    public Configuration getConfiguration() {
        return getMachine().getConfiguration();
    }
}
