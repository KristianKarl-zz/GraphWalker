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

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Configuration {

    private Set<String> includes;
    private Set<String> excludes;
    private boolean skipTests;
    private String test;
    private File testClassesDirectory;
    private File classesDirectory;
    private File reportsDirectory;
    private Set<String> groups;

    public Configuration() {
    }

    public Set<String> getIncludes() {
        /*
        if (null != test) {
            includes = new ArrayList<String>();
            for (String include: test.split(",")) {
                if (include.endsWith(".java")) {
                    include = include.substring(0, include.length() - 5);
                }
                if (include.endsWith(".class")) {
                    include = include.substring(0, include.length() - 6);
                }
                include = include.replace('.', '/');
                includes.add("* * /" + include + ".class");
            }
        } else if (null == includes) {
            includes = new ArrayList<String>();
            includes.add("* * /*.class");
        }
        */
        return includes;
    }

    public void setIncludes(Set<String> includes) {
        this.includes = includes;
    }

    public Set<String> getExcludes() {
        return excludes;
    }

    public void setExcludes(Set<String> excludes) {
        this.excludes = excludes;
    }

    public boolean getSkipTests() {
        return skipTests;
    }

    public void setSkipTests(boolean skipTests) {
        this.skipTests = skipTests;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public File getTestClassesDirectory() {
        return testClassesDirectory;
    }

    public void setTestClassesDirectory(File testClassesDirectory) {
        this.testClassesDirectory = testClassesDirectory;
    }

    public File getClassesDirectory() {
        return classesDirectory;
    }

    public void setClassesDirectory(File classesDirectory) {
        this.classesDirectory = classesDirectory;
    }

    public File getReportsDirectory() {
        return reportsDirectory;
    }

    public void setReportsDirectory(File reportsDirectory) {
        this.reportsDirectory = reportsDirectory;
    }

    public Set<String> getGroups() {
        return groups;
    }

    public void setGroups(String groups) {
        this.groups = new HashSet<String>();
        if (null != groups) {
            for (String group: groups.trim().split(",")) {
                this.groups.add(group.trim());
            }
        }
    }
}
