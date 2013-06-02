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
import org.apache.maven.plugins.annotations.*;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.graphwalker.core.model.Model;
import org.graphwalker.core.model.ModelFactory;
import org.graphwalker.core.model.support.DefaultModelFactory;
import org.graphwalker.maven.plugin.source.CodeGenerator;
import org.graphwalker.maven.plugin.source.SourceFile;

import java.io.File;
import java.util.*;

/**
 * <p>AbstractGenerateMojo class.</p>
 *
 */

public abstract class AbstractGenerateMojo extends AbstractGraphWalkerMojo {

    @Parameter(defaultValue = "${project.build.sourceEncoding}", required = true, readonly = true)
    private String sourceEncoding;

    private final ModelFactory modelFactory = new DefaultModelFactory();

    protected List<String> getIncludes() {
        List<String> includes = super.getIncludes();
        if (includes.isEmpty()) {
            includes.addAll(modelFactory.getSupportedFileTypes());
        }
        return includes;
    }

    protected void generate(List<String> includes, List<String> excludes, List<Resource> resources) {
        generate(StringUtils.join(includes.toArray(), ","), StringUtils.join(excludes.toArray(), ","), resources);
    }

    private void generate(String includes, String excludes, List<Resource> resources) {
        for (Resource resource : resources) {
            generate(includes, excludes, new File(resource.getDirectory()));
        }
    }

    private void generate(String includes, String excludes, File directory) {
        for (SourceFile fileInfo : findModels(includes, excludes, directory)) {
            generate(fileInfo);
        }
    }

    private void generate(SourceFile sourceFile) {
        try {
            Model model = modelFactory.create(sourceFile.getBaseName(), sourceFile.getFilename(), sourceFile.getExtension());
            String source = new CodeGenerator(sourceFile, model).generate();
            FileUtils.mkdir(sourceFile.getOutputFile().getParent());
            FileUtils.fileDelete(sourceFile.getOutputFile().getAbsolutePath());
            FileUtils.fileWrite(sourceFile.getOutputFile(), sourceEncoding, source);
        } catch (Throwable t) {
            getLog().info("Error generate interface for " + sourceFile.getFilename());
            getLog().debug("Error generate interface for " + sourceFile.getFilename(), t);
        }
    }

    private List<SourceFile> findModels(String includes, String excludes, File... directories) {
        List<SourceFile> models = new ArrayList<SourceFile>();
        for (File directory : directories) {
            try {
                for (Object filename : FileUtils.getFileNames(directory, includes, excludes, true, true)) {
                    models.add(new SourceFile(directory, (String) filename));
                }
            } catch (Throwable t) {
                getLog().info("Failed to generate interfaces for " + directory.getAbsolutePath());
                getLog().debug("Failed to generate interfaces for " + directory.getAbsolutePath(), t);
            }
        }
        return models;
    }
}
