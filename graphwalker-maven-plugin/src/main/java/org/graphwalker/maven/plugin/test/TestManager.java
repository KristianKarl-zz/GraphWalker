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
import org.graphwalker.core.conditions.StopCondition;
import org.graphwalker.core.generator.PathGenerator;
import org.graphwalker.core.machine.Execution;

import java.util.*;

/**
 * @author Nils Olsson
 */
public final class TestManager {

    private final Configuration configuration;
    private final Collection<TestGroup> executionGroups;

    public TestManager(Configuration configuration, Collection<Class<?>> testClasses) {
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

    private Collection<TestGroup> createExecutionGroups(Collection<Class<?>> testClasses) {
        Map<String, TestGroup> groups = new HashMap<String, TestGroup>();
        for (Class<?> testClass: testClasses) {
            Execute[] executions = testClass.getAnnotation(GraphWalker.class).value();
            if (0 == executions.length) {
                try {
                    // if no Execute parameter is supplied the we create a default
                    String groupName = (String)Execute.class.getMethod("group").getDefaultValue();
                    Class<? extends PathGenerator> pathGenerator = (Class<? extends PathGenerator>)Execute.class.getMethod("pathGenerator").getDefaultValue();
                    Class<? extends StopCondition> stopCondition = (Class<? extends StopCondition>)Execute.class.getMethod("stopCondition").getDefaultValue();
                    String stopConditionValue = (String)Execute.class.getMethod("stopConditionValue").getDefaultValue();
                    Execution execution = new Execution(testClass, pathGenerator, stopCondition, stopConditionValue);
                    groups.put(groupName, new TestGroup(groupName));
                    groups.get(groupName).addExecution(execution);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            } else {
                for (Execute execute: executions) {
                    if (isExecutionGroup(execute.group())) {
                        if (!groups.containsKey(execute.group())) {
                            groups.put(execute.group(), new TestGroup(execute.group()));
                        }
                        Execution execution = new Execution(testClass, execute.pathGenerator(), execute.stopCondition(), execute.stopConditionValue());
                        groups.get(execute.group()).addExecution(execution);
                    }
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

    public Collection<TestGroup> getExecutionGroups() {
        return executionGroups;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public int getGroupCount() {
        return getExecutionGroups().size();
    }

    public int getTestCount() {
        int count = 0;
        for (TestGroup group: getExecutionGroups()) {
            count += group.getExecutions().size();
        }
        return count;
    }

}
