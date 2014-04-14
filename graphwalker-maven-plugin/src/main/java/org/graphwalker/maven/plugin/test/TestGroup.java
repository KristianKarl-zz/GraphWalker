/*
 * #%L
 * GraphWalker Maven Plugin
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
package org.graphwalker.maven.plugin.test;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Nils Olsson
 */
public final class TestGroup {

    private final String name;
    private final Set<Execution> executions = new HashSet<Execution>();

    /**
     * <p>Constructor for TestGroup.</p>
     *
     * @param name a {@link java.lang.String} object.
     */
    public TestGroup(String name) {
        this.name = name;
    }

    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        return name;
    }

    /**
     * <p>addExecution.</p>
     *
     * @param execution a {@link org.graphwalker.maven.plugin.test.Execution} object.
     */
    public void addExecution(Execution execution) {
        executions.add(execution);
    }

    /**
     * <p>Getter for the field <code>executions</code>.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<Execution> getExecutions() {
        return executions;
    }
}
