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
        for (File file: findFiles(getModelFactory().getSupportedFileTypes(), getExcludes(), baseDirectory)) {
            generate(file, baseDirectory, getGeneratedSourcesDirectory());
        }
    }

    protected void generate(File file, File baseDirectory, File outputDirectory) {
        generate(new SourceFile(file, baseDirectory, outputDirectory));
    }

    private void generate(SourceFile sourceFile) {
        try {
            Model model = getModelFactory().create(sourceFile.getAbsolutePath(), sourceFile.getExtension());
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










/*





    protected Set<String> getIncludes() {
        Set<String> includes = super.getIncludes();
        if (includes.isEmpty()) {
            for (String type: modelFactory.getSupportedFileTypes()) {
                includes.add("* * /*."+type);
            }
        }
        return includes;
    }

    protected void generate(Set<String> includes, Set<String> excludes, List<Resource> resources) {
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
            Model model = modelFactory.create(sourceFile.getFilename(), sourceFile.getExtension());
            String source = new CodeGenerator(sourceFile, model).generate();
            if (sourceFile.getOutputFile().exists()) {
                String existingSource = StringUtils.removeDuplicateWhitespace(FileUtils.fileRead(sourceFile.getOutputFile(), sourceEncoding));
                if (existingSource.equals(StringUtils.removeDuplicateWhitespace(new String(source.getBytes(), sourceEncoding)))) {
                    return;
                }
            }
            FileUtils.mkdir(sourceFile.getOutputFile().getParent());
            FileUtils.fileDelete(sourceFile.getOutputFile().getAbsolutePath());
            FileUtils.fileWrite(sourceFile.getOutputFile(), sourceEncoding, source);
        } catch (Throwable t) {
            getLog().info("Error generate interface for " + sourceFile.getFilename());
            getLog().debug("Error generate interface for " + sourceFile.getFilename(), t);
        }
    }

    private Set<SourceFile> findModels(String includes, String excludes, File... directories) {
        Set<SourceFile> models = new HashSet<SourceFile>();
        for (File directory : directories) {
            if (directory.exists()) {
                try {
                    for (Object filename : FileUtils.getFileNames(directory, includes, excludes, true, true)) {
                        models.add(new SourceFile(directory, (String)filename));
                    }
                } catch (Throwable t) {
                    getLog().info("Failed to generate interfaces for " + directory.getAbsolutePath());
                    getLog().debug("Failed to generate interfaces for " + directory.getAbsolutePath(), t);
                }
            }
        }
        return models;
    }
*/
}
