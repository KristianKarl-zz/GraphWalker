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
import org.graphwalker.core.GraphWalker;
import org.graphwalker.core.GraphWalkerImpl;
import org.graphwalker.core.configuration.ConfigurationFactory;
import org.graphwalker.core.util.Resource;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

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
     * Classpath.
     *
     * @parameter expression="${project.testClasspathElements}"
     * @required
     */
    private List<String> classpathElements;

    /**
     * @parameter property="configFiles"
     * @required
     */
    private List<String> configFiles;

    private List<GraphWalker> myGraphWalkers = new ArrayList<GraphWalker>();

    /**
     * <p>execute.</p>
     *
     * @throws org.apache.maven.plugin.MojoExecutionException if any.
     * @throws org.apache.maven.plugin.MojoFailureException if any.
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            URLClassLoader classLoader = new URLClassLoader(convertToURL(classpathElements), getClass().getClassLoader());
            Thread.currentThread().setContextClassLoader(classLoader);
            for (String configFile: configFiles) {
                myGraphWalkers.add(new GraphWalkerImpl(ConfigurationFactory.create(configFile)));
            }
            for (GraphWalker graphWalker: myGraphWalkers) {
                graphWalker.executePath();
            }
        } catch (MalformedURLException e) {
            throw new MojoExecutionException(Resource.getText(Bundle.NAME, "exception.classloader"));
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
    }

    private URL[] convertToURL(List<String> elements) throws MalformedURLException {
        List<URL> urlList = new ArrayList<URL>();
        for (String element: elements) {
            urlList.add(new File(element).toURI().toURL());
        }
        return urlList.toArray(new URL[urlList.size()]);
    }

}
