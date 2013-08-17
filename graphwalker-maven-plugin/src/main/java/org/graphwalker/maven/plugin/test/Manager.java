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

import org.codehaus.plexus.util.SelectorUtils;
import org.graphwalker.core.annotations.Execute;
import org.graphwalker.core.annotations.GraphWalker;
import org.graphwalker.core.machine.Execution;

import java.util.*;

public final class Manager {

    private final Configuration configuration;
    private final Collection<Group> executionGroups;

    public Manager(Configuration configuration, Collection<Class<?>> testClasses) {
        this.configuration = configuration;
        this.executionGroups = createExecutionGroups(filterTestClasses(testClasses));
    }


    private Collection<Class<?>> filterTestClasses(Collection<Class<?>> testClasses) {
        Set<Class<?>> filteredClasses = new HashSet<Class<?>>(testClasses.size());
        for (Class<?> testClass: testClasses) {
            if (isIncluded(testClass)) {
                filteredClasses.add(testClass);
            }
        }
        return filteredClasses;
    }

    private boolean isIncluded(Class<?> testClass) {
        String name = testClass.getName();
        for (String excluded: configuration.getExcludes()) {
            if (SelectorUtils.match(excluded, name, true)) {
                return false;
            }
        }
        for (String included: configuration.getIncludes()) {
            if (SelectorUtils.match(included, name, true)) {
                return true;
            }
        }
        return false;
    }

    private Collection<Group> createExecutionGroups(Collection<Class<?>> testClasses) {
        Map<String, Group> groups = new HashMap<String, Group>();
        for (Class<?> testClass: testClasses) {
            for (Execute execute: testClass.getAnnotation(GraphWalker.class).value()) {
                if (isExecutionGroup(execute.group())) {
                    if (!groups.containsKey(execute.group())) {
                        groups.put(execute.group(), new Group(execute.group()));
                    }
                    Execution execution = new Execution(testClass, execute.pathGenerator(), execute.stopCondition(), execute.stopConditionValue(), execute.exceptionStrategy());
                    groups.get(execute.group()).addExecution(execution);
                }
            }
        }
        return groups.values();
    }

    private boolean isExecutionGroup(String name) {
        for (String group: configuration.getGroups()) {
            if (SelectorUtils.match(group, name, true)) {
                return true;
            }
        }
        return false;
    }

    public Collection<Group> getExecutionGroups() {
        return executionGroups;
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
