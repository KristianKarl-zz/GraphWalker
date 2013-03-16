/*
 * #%L
 * GraphWalker Server
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
package org.graphwalker.server;

import org.apache.thrift.TException;
import org.graphwalker.api.GraphWalker;
import org.graphwalker.api.Model;
import org.graphwalker.api.PathGenerator;
import org.graphwalker.api.StopCondition;

import java.util.Set;

/**
 * <p>GraphWalkerImpl class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class GraphWalkerImpl implements GraphWalker.Iface {

    /** {@inheritDoc} */
    public String ping(String message) {
        return message;
    }

    public Set<Model> getModels() throws TException {
        return null;  // TODO: Fix me (Auto generated)
    }

    public Model getModel(String name) throws TException {
        return null;  // TODO: Fix me (Auto generated)
    }

    public String createModel(String name, String content) throws TException {
        return null;  // TODO: Fix me (Auto generated)
    }

    public void updateModel(String name, String content) throws TException {
        // TODO: Fix me (Auto generated)
    }

    public void deleteModel(String name) throws TException {
        // TODO: Fix me (Auto generated)
    }

    public Set<StopCondition> getStopConditions() throws TException {
        return null;  // TODO: Fix me (Auto generated)
    }

    public Set<PathGenerator> getPathGenerator() throws TException {
        return null;  // TODO: Fix me (Auto generated)
    }

    public void execute(String name, PathGenerator generator, StopCondition condition) throws TException {
        // TODO: Fix me (Auto generated)
    }

    public boolean hasMoreSteps() throws TException {
        return false;  // TODO: Fix me (Auto generated)
    }

    public String getCurrentStep() throws TException {
        return null;  // TODO: Fix me (Auto generated)
    }

    public String getNextStep() throws TException {
        return null;  // TODO: Fix me (Auto generated)
    }

    public void fail() throws TException {
        // TODO: Fix me (Auto generated)
    }
}
