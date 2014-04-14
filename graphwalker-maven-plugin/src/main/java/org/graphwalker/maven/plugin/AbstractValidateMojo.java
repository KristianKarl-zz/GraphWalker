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
import org.graphwalker.maven.plugin.model.GraphMLModelFactory;
import org.graphwalker.maven.plugin.model.ModelFactory;

import java.io.File;
import java.util.List;

/**
 * @author Nils Olsson
 */
public abstract class AbstractValidateMojo extends AbstractDefaultMojo {

    /**
     * <p>validate.</p>
     *
     * @param resources a {@link java.util.List} object.
     */
    protected void validate(List<Resource> resources) {
        for (Resource resource: resources) {
            validate(resource);
        }
        // TODO: if any execution of the sub method returns false this method should throw a new exception
    }

    /**
     * <p>validate.</p>
     *
     * @param resource a {@link org.apache.maven.model.Resource} object.
     */
    protected void validate(Resource resource) {
        ModelFactory factory = new GraphMLModelFactory();
        for (File file: findFiles(factory.getSupportedFileTypes(), null, new File(resource.getDirectory()))) {
            validate(file);
        }
        // TODO: if any execution of the sub method returns false this method should return false
    }

    private void validate(File file) {
        if (getLog().isInfoEnabled()) {
            getLog().info("Validate: " + file.getAbsolutePath());
        }
        ModelFactory factory = new GraphMLModelFactory();
        factory.validate(file.toPath());
        //TODO: this should log the validation errors and then return false
    }

}
