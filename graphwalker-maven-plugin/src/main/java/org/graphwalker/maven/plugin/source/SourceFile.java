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

public final class SourceFile extends File {

    private final Path filePath;
    private final Path basePath;
    private final Path outputPath;

    private String packageName;
    private File outputFile;


    public SourceFile(File file, File baseDirectory, File outputDirectory) {
        super(file.getAbsolutePath());
        this.filePath = Paths.get(file.getAbsolutePath());
        this.basePath = Paths.get(baseDirectory.getAbsolutePath());
        this.outputPath = Paths.get(outputDirectory.getAbsolutePath());
    }

    public String getPackageName() {
        if (null == packageName) {
            packageName = FileUtils.getPath(basePath.relativize(filePath).toFile().getPath()).replaceAll(File.separator, ".");
        }
        return packageName;
    }

    public File getOutputFile() {
        if (null == outputFile) {
            File file = outputPath.resolve(basePath.relativize(filePath)).toFile();
            outputFile = new File(file.getParentFile(), FileUtils.removeExtension(file.getName()).concat(".java"));
        }
        return outputFile;
    }

    public String getExtension() {
        return FileUtils.extension(getName());
    }

    public String getBaseName() {
        return FileUtils.removeExtension(getName());
    }

}
