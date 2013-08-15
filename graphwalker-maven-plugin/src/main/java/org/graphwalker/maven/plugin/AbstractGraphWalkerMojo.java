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
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.graphwalker.core.common.ResourceUtils;
import org.graphwalker.core.model.ModelFactory;
import org.graphwalker.core.model.support.DefaultModelFactory;
import org.graphwalker.maven.plugin.source.SourceFile;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

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
    private boolean graphwalkerTestSkip;

    @Parameter(property = "maven.test.skip", defaultValue="false")
    private boolean mavenTestSkip;

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
    private Set<String> includes;

    @Parameter(property = "excludes")
    private Set<String> excludes;

    private final ModelFactory modelFactory = new DefaultModelFactory();

    protected MavenSession getSession() {
        return session;
    }

    protected MavenProject getMavenProject() {
        return mavenProject;
    }

    protected List<String> getClasspathElements() {
        return new ArrayList<String>(classpathElements);
    }

    protected boolean getSkipTests() {
        return mavenTestSkip || graphwalkerTestSkip || skipTests;
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

    protected Set<String> getIncludes() {
        if (0 == includes.size()) {
            includes.addAll(getModelFactory().getSupportedFileTypes());
        }
        return includes;
    }

    protected Set<String> getExcludes() {
        return excludes;
    }

    protected ModelFactory getModelFactory() {
        return modelFactory;
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        ClassLoader classLoader = switchClassLoader(createClassLoader());
        Properties properties = switchProperties(createProperties());
        executeMojo();
        switchProperties(properties);
        switchClassLoader(classLoader);
    }

    protected abstract void executeMojo();

    private String toString(Set<String> set) {
        return StringUtils.join(set.toArray(new String[set.size()]), ",");
    }

    protected Set<File> findFiles(Set<String> includes, Set<String> excludes, File... directories) {
        return findFiles(toString(includes), toString(excludes), directories);
    }

    protected Set<File> findFiles(String includes, String excludes, File... directories) {
        Set<File> files = new HashSet<File>();
        for (File directory : directories) {
            if (directory.exists()) {
                try {
                    for (Object filename: FileUtils.getFileNames(directory, includes, excludes, true, true)) {
                        files.add(new File((String)filename));
                    }
                } catch (Throwable t) {
                    getLog().debug(t);
                }
            }
        }
        return files;
    }

    private ClassLoader createClassLoader() throws MojoExecutionException {
        try {
            return new URLClassLoader(convertToURL(getClasspathElements()), getClass().getClassLoader());
        } catch (MalformedURLException e) {
            throw new MojoExecutionException(ResourceUtils.getText(Bundle.NAME, "exception.create.classloader"));
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

}
