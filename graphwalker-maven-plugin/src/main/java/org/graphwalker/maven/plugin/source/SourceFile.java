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
package org.graphwalker.maven.plugin.source;

import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Nils Olsson
 */
public final class SourceFile extends File {

    private final String extension;
    private final String baseName;
    private final File inputFile;
    private final String packageName;
    private final File outputFile;

    public SourceFile(File file, File baseDirectory, File outputDirectory) {
        this(file.toPath(), baseDirectory.toPath(), outputDirectory.toPath());
    }

    public SourceFile(Path filePath, Path basePath, Path outputPath) {
        super(filePath.toFile().getAbsolutePath());
        this.extension = FileUtils.extension(getName());
        this.baseName = FileUtils.removeExtension(getName());
        this.inputFile = basePath.relativize(filePath).toFile();
        this.packageName = FileUtils.getPath(inputFile.getPath()).replace(File.separator, ".");
        this.outputFile = new File(FileUtils.removeExtension(outputPath.resolve(basePath.relativize(filePath)).toFile().getAbsolutePath()).concat(".java"));
    }

    public String getPackageName() {
        return packageName;
    }

    public Path getOutputPath() {
        return outputFile.toPath();
    }

    public File getOutputFile() {
        return outputFile;
    }

    public File getInputFile() {
        return inputFile;
    }

    public String getInputPath() {
        return inputFile.getPath().replace(File.separator, "/");
    }

    public String getExtension() {
        return extension;
    }

    public String getBaseName() {
        return baseName;
    }
}
