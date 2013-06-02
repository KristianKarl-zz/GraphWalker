/*
 * #%L
 * Maven GraphWalker Plugin
 * %%
 * Copyright (C) 2011 - 2012 GraphWalker
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
package org.graphwalker.maven.plugin.test;

import org.codehaus.plexus.util.DirectoryScanner;
import org.graphwalker.core.annotations.GraphWalker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Scanner class.</p>
 *
 *
 */
public final class Scanner {

    private final Configuration configuration;

    public Scanner(Configuration configuration) {
        this.configuration = configuration;
    }

    public List<Class<?>> scan(File... directories) {
        List<Class<?>> tests = new ArrayList<Class<?>>();
        for (File directory: directories) {
            tests.addAll(scan(directory));
        }
        return tests;
    }

    private List<Class<?>> scan(File directory) {
        List<Class<?>> tests = new ArrayList<Class<?>>();
        for (String fileName: findFiles(directory)) {
            try {
                Class<?> clazz = loadClass(getClassName(fileName));
                if (acceptClass(clazz)) {
                    tests.add(clazz);
                }
            } catch (ClassNotFoundException e) {
                // ignore
            }
        }
        return tests;
    }

    private String[] findFiles(File directory) {
        if (directory.exists()) {
            DirectoryScanner directoryScanner = new DirectoryScanner();
            directoryScanner.setIncludes(configuration.getIncludes().toArray(new String[configuration.getIncludes().size()]));
            directoryScanner.setExcludes(configuration.getExcludes().toArray(new String[configuration.getExcludes().size()]));
            directoryScanner.setBasedir(directory);
            directoryScanner.setCaseSensitive(true);
            directoryScanner.scan();
            return directoryScanner.getIncludedFiles();
        } else {
            return new String[]{};
        }
    }

    private boolean acceptClass(Class<?> clazz) {
        return clazz.isAnnotationPresent(GraphWalker.class);
    }

    private Class<?> loadClass(String className) throws ClassNotFoundException {
        return Thread.currentThread().getContextClassLoader().loadClass(className);
    }

    private String getClassName(String fileName) {
        return fileName.substring(0, fileName.indexOf(".")).replace(File.separatorChar, '.');
    }

}
