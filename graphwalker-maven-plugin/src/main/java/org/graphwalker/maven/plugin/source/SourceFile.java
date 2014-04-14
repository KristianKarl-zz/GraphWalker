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

/**
 * @author Nils Olsson
 */
public final class SourceFile {

    private final Path inputPath;
    private final Path relativePath;
    private final Path outputPath;
    private final String packageName;

    public SourceFile(File file, File baseDirectory, File outputDirectory) {
        this(file.toPath(), baseDirectory.toPath(), outputDirectory.toPath());
    }

    public SourceFile(Path inputPath, Path basePath, Path outputPath) {
        this.inputPath = inputPath;
        this.relativePath = basePath.relativize(inputPath);
        this.packageName = this.relativePath.getParent().toString().replace(File.separator, ".");
        this.outputPath = outputPath.resolve(this.relativePath).resolveSibling(getFileName() + ".java");
    }

    public Path getInputPath() {
        return inputPath;
    }

    public Path getRelativePath() {
        return relativePath;
    }

    public Path getOutputPath() {
        return outputPath;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getFileName() {
        return FileUtils.removeExtension(inputPath.getFileName().toString());
    }
}
