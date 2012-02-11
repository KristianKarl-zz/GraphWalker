/*
 * #%L
 * Maven GraphWalker Plugin
 * %%
 * Copyright (C) 2012 GraphWalker
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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.graphwalker.core.GraphWalker;
import org.graphwalker.core.GraphWalkerExecutor;
import org.graphwalker.core.GraphWalkerFactory;
import org.graphwalker.core.configuration.Configuration;
import org.graphwalker.core.configuration.ConfigurationFactory;
import org.graphwalker.core.model.Model;
import org.graphwalker.core.utils.Resource;
import org.graphwalker.maven.plugin.reports.XMLReportGenerator;
import org.graphwalker.maven.plugin.utils.TestUtil;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * <p>ExecuteMojo class.</p>
 *
 * @author nilols
 * @version $Id: $
 * @goal test
 * @phase test
 * @execute phase="test-compile"
 * @requiresDependencyResolution test
 */
public class TestMojo extends AbstractMojo {

    /**
     * @parameter expression="${project}"
     */
    private MavenProject mavenProject;

    /**
     * @parameter expression="${project.testClasspathElements}"
     */
    private List<String> classpathElements;

    /**
     * @parameter expression="${skipTests}" default-value="false"
     */
    private boolean skipTests;

    /**
     * @parameter expression="${graphwalker.test.skip}" default-value="false"
     */
    private boolean skipTestsProperty;

    /**
     * @parameter expression="${maven.test.skip}" default-value="false"
     */
    private boolean skipAllTests;

    /**
     * @parameter expression="${test}"
     */
    private String test;

    /**
     * @parameter default-value="${project.build.testOutputDirectory}"
     */
    private File testClassesDirectory;

    /**
     * @parameter default-value="${project.build.directory}/graphwalker-reports"
     */
    private File reportsDirectory;

    /**
     * @parameter property="includes"
     */
    private String[] includes;

    /**
     * @parameter property="excludes"
     */
    private String[] excludes;    
    
    
    private List<GraphWalker> myGraphWalkers = new ArrayList<GraphWalker>();

    /**
     * <p>execute.</p>
     *
     * @throws org.apache.maven.plugin.MojoExecutionException if any.
     * @throws org.apache.maven.plugin.MojoFailureException if any.
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (!skipTests()) {
            ClassLoader classLoader = switchClassLoader(createClassLoader());
            setSystemProperties();
            createInstances();
            executeInstances();
            reportExecution();
            switchClassLoader(classLoader);
        }
    }

    private ClassLoader createClassLoader() throws MojoExecutionException {
        try {
            return new URLClassLoader(convertToURL(classpathElements), getClass().getClassLoader());
        } catch (MalformedURLException e) {
            throw new MojoExecutionException(Resource.getText(Bundle.NAME, "exception.create.classloader"));
        }
    }

    private ClassLoader switchClassLoader(ClassLoader newClassLoader) {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(newClassLoader);
        return oldClassLoader;
    }
    
    private void setSystemProperties() {
        System.getProperties().putAll(mavenProject.getProperties());
    }

    private void createInstances() throws MojoExecutionException {
        try {
            Map<String, List<Class<?>>> testGroups = new HashMap<String, List<Class<?>>>();
            List<Class<?>> tests = TestUtil.findTests(testClassesDirectory, getIncludes(), excludes);
            for (Class<?> test: tests) {
                String group = TestUtil.getGroup(test);
                if (!testGroups.containsKey(group)) {
                    testGroups.put(group, new ArrayList<Class<?>>());
                }
                testGroups.get(group).add(test);
            }

            for (String group: testGroups.keySet()) {
                Configuration configuration = ConfigurationFactory.create(testGroups.get(group));
                myGraphWalkers.add(GraphWalkerFactory.create(configuration));
            }
        } catch (ClassNotFoundException e) {
            throw new MojoExecutionException(Resource.getText(Bundle.NAME, "exception.create.test"));
        }
    }

    private void executeInstances() throws MojoExecutionException {
        try {
            ExecutorService executorService = Executors.newFixedThreadPool(myGraphWalkers.size());
            for (GraphWalker graphWalker: myGraphWalkers) {
                executorService.execute(new GraphWalkerExecutor(graphWalker));
            }
            executorService.shutdown();
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            throw new MojoExecutionException(Resource.getText(Bundle.NAME, "exception.execution.interrupted"));
        }
    }

    private void reportExecution() throws MojoExecutionException {
        boolean hasExceptions = false;
        for (GraphWalker graphWalker: myGraphWalkers) {
            for (Model model: graphWalker.getConfiguration().getModels()) {
                hasExceptions |= graphWalker.hasExceptions(model);
                XMLReportGenerator.writeReport(reportsDirectory, model, graphWalker.getExceptions(model));
            }
        }
        if (hasExceptions) {
            throw new MojoExecutionException(Resource.getText(Bundle.NAME, "exception.execution.failed", reportsDirectory.getAbsolutePath()));
        }
    }

    private String[] getIncludes() {
        if (null == includes) {
            return new String[] {"**/*.class"};
        }
        return includes;
    } 
    
    private URL[] convertToURL(List<String> elements) throws MalformedURLException {
        List<URL> urlList = new ArrayList<URL>();
        for (String element: elements) {
            urlList.add(new File(element).toURI().toURL());
        }
        return urlList.toArray(new URL[urlList.size()]);
    }

    private boolean skipTests() {
        return skipAllTests || skipTestsProperty || skipTests;
    }

}
