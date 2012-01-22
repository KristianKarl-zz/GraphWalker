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
import org.graphwalker.core.GraphWalkerImpl;
import org.graphwalker.core.configuration.ConfigurationFactory;
import org.graphwalker.core.util.Resource;
import org.graphwalker.maven.plugin.report.ReportGenerator;
import org.graphwalker.maven.plugin.report.XMLReportGenerator;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

// TODO: fork execution of config files
// TODO: create html reports

/**
 * <p>ExecuteMojo class.</p>
 *
 * @author nilols
 * @version $Id: $
 * @goal test
 * @phase test
 * @requiresDependencyResolution test
 */
public class ExecuteMojo extends AbstractMojo {

    /**
     * @parameter expression="${project}"
     * @required
     */
    private MavenProject mavenProject;

    /**
     * Classpath.
     *
     * @parameter expression="${project.testClasspathElements}"
     * @required
     */
    private List<String> classpathElements;

    /**
     * @parameter default-value="false" expression="${skipTests}"
     */
    private boolean skipTests;

    /**
     * @parameter default-value="false" expression="${graphwalker.test.skip}"
     */
    private boolean skipTestsProperty;

    /**
     * @parameter default-value="false" expression="${maven.test.skip}"
     */
    private boolean skipAllTests;

    /**
     * @parameter property="configPath" default-value="${project.build.testOutputDirectory}"
     * @required
     */
    private String configPath;
    
    /**
     * @parameter property="configFiles"
     * @required
     */
    private List<String> configFiles;

    /**
     * @parameter default-value="${project.build.directory}/graphwalker-reports"
     */
    private File reportsDirectory;


    private List<GraphWalker> myGraphWalkers = new ArrayList<GraphWalker>();

    /**
     * <p>execute.</p>
     *
     * @throws org.apache.maven.plugin.MojoExecutionException if any.
     * @throws org.apache.maven.plugin.MojoFailureException if any.
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (!skipTests()) {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            try {
                URLClassLoader classLoader = new URLClassLoader(convertToURL(classpathElements), getClass().getClassLoader());

                System.getProperties().putAll(mavenProject.getProperties());

                Thread.currentThread().setContextClassLoader(classLoader);
                File parent = new File(configPath);
                for (String configFile: configFiles) {
                    File file = new File(parent, configFile);
                    myGraphWalkers.add(new GraphWalkerImpl(ConfigurationFactory.create(file)));
                }
                for (GraphWalker graphWalker: myGraphWalkers) {
                    graphWalker.executePath();
                }
                for (GraphWalker graphWalker: myGraphWalkers) {
                    new XMLReportGenerator(graphWalker, reportsDirectory).writeReport();
                }
            } catch (MalformedURLException e) {
                throw new MojoExecutionException(Resource.getText(Bundle.NAME, "exception.classloader"));
            } finally {
                Thread.currentThread().setContextClassLoader(contextClassLoader);
            }
        }
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
