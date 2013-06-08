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

import org.graphwalker.core.annotations.Execute;
import org.graphwalker.core.annotations.GraphWalker;
import org.graphwalker.core.machine.Execution;

import java.util.*;

public final class Manager {

    private final Configuration configuration;
    private final List<Class<?>> testClasses;
    private Map<String,Group> groups;

    public Manager(Configuration configuration, List<Class<?>> testClasses) {
        this.configuration = configuration;
        this.testClasses = testClasses;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public Collection<Group> getGroups() {
        if (null == groups) {
            groups = new HashMap<String,Group>();
            for (Class<?> testClass: testClasses) {
                for (Execute execute: testClass.getAnnotation(GraphWalker.class).value()) {
                    if (configuration.getGroups().contains(execute.group())) {
                        if (!groups.containsKey(execute.group())) {
                            groups.put(execute.group(), new Group(execute.group()));
                        }
                        Execution execution = new Execution(testClass, execute.pathGenerator(), execute.stopCondition(), execute.stopConditionValue(), execute.exceptionStrategy());
                        groups.get(execute.group()).addExecution(execution);
                    }
                }
            }
        }
        return groups.values();
    }
}
