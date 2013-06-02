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

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.graphwalker.core.utils.Resource;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public abstract class AbstractGraphWalkerMojo extends AbstractMojo {

    @Component
    private MavenSession session;

    @Component
    private MavenProject mavenProject;

    @Parameter(property = "project.testClasspathElements")
    private List<String> classpathElements;

    @Parameter(property = "skipTests", defaultValue = "false")
    private boolean skipTests;

    @Parameter(property = "graphwalker.test.skip", defaultValue = "false")
    private boolean skipTestsProperty;

    @Parameter(property = "maven.test.skip", defaultValue="false")
    private boolean skipAllTests;

    @Parameter(property = "test", defaultValue = "")
    private String test;

    @Parameter(property = "graphwalker.test.groups", defaultValue = "")
    private String groups;

    @Parameter(defaultValue="${project.build.testOutputDirectory}")
    private File testClassesDirectory;

    @Parameter(defaultValue="${project.build.outputDirectory}")
    private File classesDirectory;

    @Parameter(defaultValue = "${project.build.directory}/graphwalker-reports")
    private File reportsDirectory;

    @Parameter(property = "includes")
    private List<String> includes = new ArrayList<String>();

    @Parameter(property = "excludes")
    private List<String> excludes;

    protected MavenSession getSession() {
        return session;
    }

    protected MavenProject getMavenProject() {
        return mavenProject;
    }

    protected List<String> getClasspathElements() {
        return classpathElements;
    }

    protected boolean isSkipTests() {
        return skipTests;
    }

    protected boolean isSkipTestsProperty() {
        return skipTestsProperty;
    }

    protected boolean isSkipAllTests() {
        return skipAllTests;
    }

    protected String getTest() {
        return test;
    }

    protected String getGroups() {
        return groups;
    }

    protected File getTestClassesDirectory() {
        return testClassesDirectory;
    }

    protected File getClassesDirectory() {
        return classesDirectory;
    }

    protected File getReportsDirectory() {
        return reportsDirectory;
    }

    protected List<String> getIncludes() {
        return includes;
    }

    protected List<String> getExcludes() {
        return excludes;
    }

    public AbstractGraphWalkerMojo() {
        updateIncludeFilter(includes, test);
    }

    protected boolean skipTests() {
        return isSkipAllTests() || isSkipTestsProperty() || isSkipTests();
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        ClassLoader classLoader = switchClassLoader(createClassLoader());
        Properties properties = switchProperties(createProperties());
        executeMojo();
        switchProperties(properties);
        switchClassLoader(classLoader);
    }

    public abstract void executeMojo();

    private ClassLoader createClassLoader() throws MojoExecutionException {
        try {
            return new URLClassLoader(convertToURL(getClasspathElements()), getClass().getClassLoader());
        } catch (MalformedURLException e) {
            throw new MojoExecutionException(Resource.getText(Bundle.NAME, "exception.create.classloader"));
        }
    }

    private URL[] convertToURL(List<String> elements) throws MalformedURLException {
        List<URL> urlList = new ArrayList<URL>();
        for (String element : elements) {
            urlList.add(new File(element).toURI().toURL());
        }
        return urlList.toArray(new URL[urlList.size()]);
    }

    private ClassLoader switchClassLoader(ClassLoader newClassLoader) {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(newClassLoader);
        return oldClassLoader;
    }

    private Properties createProperties() {
        Properties properties = (Properties) System.getProperties().clone();
        properties.putAll((Properties) getMavenProject().getProperties().clone());
        properties.putAll((Properties) getSession().getUserProperties().clone());
        return properties;
    }

    private Properties switchProperties(Properties properties) {
        Properties oldProperties = (Properties) System.getProperties().clone();
        System.setProperties(properties);
        return oldProperties;
    }

    private List<String> updateIncludeFilter(List<String> includes, String tests) {
        if (null != tests) {
            includes.clear();
            for (String regex : tests.split(",")) {
                if (regex.endsWith(".java")) {
                    regex = regex.substring(0, regex.length() - 5);
                }
                if (regex.endsWith(".class")) {
                    regex = regex.substring(0, regex.length() - 6);
                }
                regex = regex.replace('.', '/');
                includes.add("**/" + regex + ".class");
            }
        } else if (includes.isEmpty()) {
            includes.add("**/*.class");
        }
        return includes;
    }
}
