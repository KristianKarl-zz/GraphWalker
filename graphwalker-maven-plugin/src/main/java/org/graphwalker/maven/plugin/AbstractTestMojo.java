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
package org.graphwalker.maven.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.graphwalker.io.common.ResourceUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author Nils Olsson
 */
public abstract class AbstractTestMojo extends AbstractDefaultMojo {

    /**
     * @since 3.0
     */
    @Parameter(property = "project.testClasspathElements")
    private List<String> classpathElements;

    /**
     * @since 3.0
     */
    @Parameter(defaultValue="${project.build.testOutputDirectory}")
    private File testClassesDirectory;

    /**
     * @since 3.0
     */
    @Parameter(defaultValue="${project.build.outputDirectory}")
    private File classesDirectory;

    /**
     * @since 3.0
     */
    @Parameter(defaultValue = "${project.build.directory}/graphwalker-reports")
    private File reportsDirectory;

    /**
     * @since 3.0
     */
    @Parameter(property = "maven.test.skip", defaultValue="false")
    private boolean mavenTestSkip;

    /**
     * @since 3.0
     */
    @Parameter(property = "skipTests", defaultValue = "false")
    private boolean skipTests;

    /**
     * @since 3.0
     */
    @Parameter(property = "graphwalker.test.skip", defaultValue = "false")
    private boolean graphwalkerTestSkip;

    /**
     * @since 3.0
     */
    @Parameter(property = "includes")
    private Set<String> includes;

    /**
     * @since 3.0
     */
    @Parameter(property = "excludes")
    private Set<String> excludes;

    /**
     * @since 3.0
     */
    @Parameter(property = "test", defaultValue = "*")
    private String test;

    /**
     * @since 3.0
     */
    @Parameter(property = "groups", defaultValue = "*")
    private String groups;

    /**
     * <p>Getter for the field <code>classpathElements</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    protected List<String> getClasspathElements() {
        return classpathElements;
    }

    /**
     * <p>Getter for the field <code>testClassesDirectory</code>.</p>
     *
     * @return a {@link java.io.File} object.
     */
    protected File getTestClassesDirectory() {
        return testClassesDirectory;
    }

    /**
     * <p>Getter for the field <code>classesDirectory</code>.</p>
     *
     * @return a {@link java.io.File} object.
     */
    protected File getClassesDirectory() {
        return classesDirectory;
    }

    /**
     * <p>Getter for the field <code>reportsDirectory</code>.</p>
     *
     * @return a {@link java.io.File} object.
     */
    protected File getReportsDirectory() {
        return reportsDirectory;
    }

    /**
     * <p>Getter for the field <code>skipTests</code>.</p>
     *
     * @return a boolean.
     */
    protected boolean getSkipTests() {
        return mavenTestSkip || graphwalkerTestSkip || skipTests;
    }

    /**
     * <p>Getter for the field <code>includes</code>.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    protected Set<String> getIncludes() {
        return includes;
    }

    /**
     * <p>Getter for the field <code>excludes</code>.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    protected Set<String> getExcludes() {
        return excludes;
    }

    /**
     * <p>Getter for the field <code>test</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    protected String getTest() {
        if (System.getProperties().containsKey("test")) {
            return System.getProperty("test");
        }
        return test;
    }

    /**
     * <p>Getter for the field <code>groups</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    protected String getGroups() {
        if (System.getProperties().containsKey("groups")) {
            return System.getProperty("groups");
        }
        return groups;
    }

    /**
     * <p>createClassLoader.</p>
     *
     * @return a {@link java.lang.ClassLoader} object.
     * @throws org.apache.maven.plugin.MojoExecutionException if any.
     */
    protected ClassLoader createClassLoader() throws MojoExecutionException {
        try {
            return new URLClassLoader(convertToURL(getClasspathElements()), getClass().getClassLoader());
        } catch (MalformedURLException e) {
            throw new MojoExecutionException(ResourceUtils.getText(Bundle.NAME, "exception.create.classloader"));
        }
    }

    private URL[] convertToURL(List<String> elements) throws MalformedURLException {
        List<URL> urlList = new ArrayList<>();
        for (String element : elements) {
            urlList.add(new File(element).toURI().toURL());
        }
        return urlList.toArray(new URL[urlList.size()]);
    }

    /**
     * <p>switchClassLoader.</p>
     *
     * @param newClassLoader a {@link java.lang.ClassLoader} object.
     * @return a {@link java.lang.ClassLoader} object.
     */
    protected ClassLoader switchClassLoader(ClassLoader newClassLoader) {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(newClassLoader);
        return oldClassLoader;
    }

    /**
     * <p>createProperties.</p>
     *
     * @return a {@link java.util.Properties} object.
     */
    protected Properties createProperties() {
        Properties properties = (Properties) System.getProperties().clone();
        properties.putAll((Properties) getMavenProject().getProperties().clone());
        properties.put("groups", groups);
        properties.put("test", test);
        properties.putAll((Properties) getSession().getUserProperties().clone());
        return properties;
    }

    /**
     * <p>switchProperties.</p>
     *
     * @param properties a {@link java.util.Properties} object.
     * @return a {@link java.util.Properties} object.
     */
    protected Properties switchProperties(Properties properties) {
        Properties oldProperties = (Properties) System.getProperties().clone();
        System.setProperties(properties);
        return oldProperties;
    }
}
