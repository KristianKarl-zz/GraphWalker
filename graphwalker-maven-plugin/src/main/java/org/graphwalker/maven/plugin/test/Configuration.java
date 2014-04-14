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
import java.util.Set;

/**
 * @author Nils Olsson
 */
public final class Configuration {

    private Set<String> includes;
    private Set<String> excludes;
    private File testClassesDirectory;
    private File classesDirectory;
    private File reportsDirectory;
    private Set<String> groups;

    /**
     * <p>Constructor for Configuration.</p>
     */
    public Configuration() {
    }

    /**
     * <p>Getter for the field <code>includes</code>.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<String> getIncludes() {
        return includes;
    }

    /**
     * <p>Setter for the field <code>includes</code>.</p>
     *
     * @param includes a {@link java.util.Set} object.
     */
    public void setIncludes(Set<String> includes) {
        this.includes = includes;
    }

    /**
     * <p>Getter for the field <code>excludes</code>.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<String> getExcludes() {
        return excludes;
    }

    /**
     * <p>Setter for the field <code>excludes</code>.</p>
     *
     * @param excludes a {@link java.util.Set} object.
     */
    public void setExcludes(Set<String> excludes) {
        this.excludes = excludes;
    }

    /**
     * <p>Getter for the field <code>testClassesDirectory</code>.</p>
     *
     * @return a {@link java.io.File} object.
     */
    public File getTestClassesDirectory() {
        return testClassesDirectory;
    }

    /**
     * <p>Setter for the field <code>testClassesDirectory</code>.</p>
     *
     * @param testClassesDirectory a {@link java.io.File} object.
     */
    public void setTestClassesDirectory(File testClassesDirectory) {
        this.testClassesDirectory = testClassesDirectory;
    }

    /**
     * <p>Getter for the field <code>classesDirectory</code>.</p>
     *
     * @return a {@link java.io.File} object.
     */
    public File getClassesDirectory() {
        return classesDirectory;
    }

    /**
     * <p>Setter for the field <code>classesDirectory</code>.</p>
     *
     * @param classesDirectory a {@link java.io.File} object.
     */
    public void setClassesDirectory(File classesDirectory) {
        this.classesDirectory = classesDirectory;
    }

    /**
     * <p>Getter for the field <code>reportsDirectory</code>.</p>
     *
     * @return a {@link java.io.File} object.
     */
    public File getReportsDirectory() {
        return reportsDirectory;
    }

    /**
     * <p>Setter for the field <code>reportsDirectory</code>.</p>
     *
     * @param reportsDirectory a {@link java.io.File} object.
     */
    public void setReportsDirectory(File reportsDirectory) {
        this.reportsDirectory = reportsDirectory;
    }

    /**
     * <p>Getter for the field <code>groups</code>.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<String> getGroups() {
        return groups;
    }

    /**
     * <p>Setter for the field <code>groups</code>.</p>
     *
     * @param groups a {@link java.util.Set} object.
     */
    public void setGroups(Set<String> groups) {
        this.groups = groups;
    }
}
