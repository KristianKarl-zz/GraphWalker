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
package org.graphwalker.maven.plugin.io;

import org.codehaus.plexus.util.FileUtils;

import java.io.File;

public class FileInfo {

    private File parent;
    private String filename;

    public FileInfo(File parent, String filename) {
        this.parent = parent;
        String parentPath = parent.getAbsolutePath()+File.separator;
        this.filename = filename.substring(parentPath.length());
    }

    public File getOutputFile() {
        File outputParent = new File(parent.getParentFile(), "java");
        return new File(outputParent, FileUtils.removeExtension(filename)+".java");
    }

    public String getFilename() {
        return filename;
    }

    public String getPath() {
        return FileUtils.getPath(filename);
    }

    public String getExtension() {
        return FileUtils.extension(filename);
    }

    public String getBaseName() {
        return FileUtils.basename(FileUtils.removeExtension(filename));
    }

    public boolean exists() {
        return new File(parent, filename).exists();
    }
}
