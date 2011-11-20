/*
 * #%L
 * GraphWalker Core
 * %%
 * Copyright (C) 2011 GraphWalker
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

package org.graphwalker.core.io;

import org.graphwalker.core.graph.Graph;

import java.io.PrintStream;

/**
 * <p>Abstract AbstractModelHandler class.</p>
 */
public abstract class AbstractModelHandler {

    protected Graph graph;

    /**
     * <p>load.</p>
     *
     * @param fileName a {@link java.lang.String} object.
     */
    public abstract void load(String fileName);

    /**
     * <p>save.</p>
     *
     * @param ps a {@link java.io.PrintStream} object.
     * @param printIndex a boolean.
     */
    public abstract void save(PrintStream ps, boolean printIndex);

    /**
     * <p>getModel.</p>
     *
     * @return a {@link org.graphwalker.core.graph.Graph} object.
     */
    public Graph getModel() {
        return graph;
    }

    /**
     * <p>setModel.</p>
     *
     * @param graph a {@link org.graphwalker.core.graph.Graph} object.
     */
    public void setModel(Graph graph) {
        this.graph = graph;
    }
}
