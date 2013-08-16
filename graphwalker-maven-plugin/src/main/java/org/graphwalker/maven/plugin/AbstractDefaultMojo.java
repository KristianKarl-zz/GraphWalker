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
import org.graphwalker.core.model.ModelFactory;
import org.graphwalker.core.model.support.DefaultModelFactory;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractDefaultMojo extends AbstractMojo {

    @Component
    private MavenSession session;

    @Component
    private MavenProject mavenProject;

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
        executeMojo();
    }

    protected abstract void executeMojo() throws MojoExecutionException, MojoFailureException;

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
}
