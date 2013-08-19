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
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.graphwalker.core.model.Model;
import org.graphwalker.maven.plugin.source.CodeGenerator;
import org.graphwalker.maven.plugin.source.SourceFile;

import java.io.File;
import java.util.List;

/**
 * <p>AbstractGenerateMojo class.</p>
 *
 */
public abstract class AbstractGenerateMojo extends AbstractDefaultMojo {

    @Parameter(defaultValue = "${project.build.sourceEncoding}", required = true, readonly = true)
    private String sourceEncoding;

    protected String getSourceEncoding() {
        return sourceEncoding;
    }

    protected abstract File getGeneratedSourcesDirectory();

    protected void generate(List<Resource> resources) {
        for (Resource resource: resources) {
            generate(resource);
        }
    }

    protected void generate(Resource resource) {
        File baseDirectory = new File(resource.getDirectory());
        for (File file: findFiles(getModelFactory().getSupportedFileTypes(), null, baseDirectory)) {
            generate(file, baseDirectory, getGeneratedSourcesDirectory());
        }
    }

    protected void generate(File file, File baseDirectory, File outputDirectory) {
        generate(new SourceFile(file, baseDirectory, outputDirectory));
    }

    private void generate(SourceFile sourceFile) {
        try {
            Model model = getModelFactory().create(sourceFile.getAbsolutePath());
            String source = new CodeGenerator(sourceFile, model).generate();
            if (sourceFile.getOutputFile().exists()) {
                String existingSource = StringUtils.removeDuplicateWhitespace(FileUtils.fileRead(sourceFile.getOutputFile(), getSourceEncoding()));
                if (existingSource.equals(StringUtils.removeDuplicateWhitespace(new String(source.getBytes(), getSourceEncoding())))) {
                    return;
                }
            }
            if (getLog().isInfoEnabled()) {
                getLog().info("Generate interface for " + sourceFile.getAbsolutePath());
            }
            FileUtils.mkdir(sourceFile.getOutputFile().getParent());
            FileUtils.fileDelete(sourceFile.getOutputFile().getAbsolutePath());
            FileUtils.fileWrite(sourceFile.getOutputFile(), getSourceEncoding(), source);
        } catch (Throwable t) {
            if (getLog().isInfoEnabled()) {
                getLog().info("Error generate interface for " + sourceFile.getAbsolutePath());
            }
            if (getLog().isDebugEnabled()) {
                getLog().debug("Error generate interface for " + sourceFile.getAbsolutePath(), t);
            }
        }
    }
}
