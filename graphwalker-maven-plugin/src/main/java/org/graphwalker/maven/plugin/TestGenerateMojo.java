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

import org.apache.maven.model.Resource;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.util.ArrayList;
import java.util.List;

@Mojo(name = "test-generate"
        , defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES
        , requiresDependencyResolution = ResolutionScope.TEST)
public final class TestGenerateMojo extends AbstractGenerateMojo {

    @Parameter(defaultValue = "${project.testResources}", required = true, readonly = true)
    private List<Resource> resources;

    @Override
    protected List<String> getClasspathElements() {
        List<String> classpathElements = new ArrayList<String>();
        for (Resource resource : resources) {
            classpathElements.add(resource.getDirectory());
        }
        classpathElements.addAll(super.getClasspathElements());
        return classpathElements;
    }

    @Override
    public void executeMojo() {
        generate(getIncludes(), getExcludes(), resources);
    }
}
