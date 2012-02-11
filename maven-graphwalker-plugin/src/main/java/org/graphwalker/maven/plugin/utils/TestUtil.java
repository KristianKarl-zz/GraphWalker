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
package org.graphwalker.maven.plugin.utils;

import org.apache.maven.plugin.PluginExecutionException;
import org.codehaus.plexus.util.DirectoryScanner;
import org.graphwalker.core.annotations.GraphWalker;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>TestUtil class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public final class TestUtil {

    private TestUtil() {
    }

    /**
     * <p>findTests.</p>
     *
     * @param basedir a {@link java.io.File} object.
     * @param includes an array of {@link java.lang.String} objects.
     * @param excludes an array of {@link java.lang.String} objects.
     * @return a {@link java.util.List} object.
     * @throws java.lang.ClassNotFoundException if any.
     */
    public static List<Class<?>> findTests(File basedir, String[] includes, String[] excludes) throws ClassNotFoundException {
        List<Class<?>> tests = new ArrayList<Class<?>>();
        for (String fileName: findFiles(basedir, includes, excludes)) {
            Class<?> clazz = loadClass(getClassName(fileName));
            if (acceptClass(clazz)) {
                tests.add(clazz);
            }
        }
        return tests;
    }

    /**
     * <p>getGroup.</p>
     *
     * @param clazz a {@link java.lang.Class} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getGroup(Class<?> clazz) {
        if (acceptClass(clazz)) {
            GraphWalker metadata = clazz.getAnnotation(GraphWalker.class);
            return metadata.group();
        }
        return null; // TODO: throw exception
    }
    
    /**
     * <p>findMethods.</p>
     *
     * @param clazz a {@link java.lang.Class} object.
     * @param type a {@link java.lang.Class} object.
     * @return a {@link java.util.List} object.
     */
    public static List<Method> findMethods(Class<?> clazz, Class<? extends Annotation> type) {
        List<Method> methods = new ArrayList<Method>();
        for (Method method: clazz.getMethods()) {
            if (method.isAnnotationPresent(type)) {
                methods.add(method);
            }
        }
        return methods;
    }
    
    private static String[] findFiles(File basedir, String[] includes, String[] excludes) {
        DirectoryScanner directoryScanner = new DirectoryScanner();
        directoryScanner.setIncludes(includes);
        directoryScanner.setExcludes(excludes);
        directoryScanner.setBasedir(basedir);
        directoryScanner.setCaseSensitive(true);
        directoryScanner.scan();
        return directoryScanner.getIncludedFiles();        
    } 
    
    private static Class<?> loadClass(String className) throws ClassNotFoundException {
        return Thread.currentThread().getContextClassLoader().loadClass(className);    
    }

    private static boolean acceptClass(Class<?> clazz) {
        return clazz.isAnnotationPresent(GraphWalker.class);
    }

    private static String getClassName(String fileName) {
        return fileName.substring(0, fileName.indexOf(".")).replace(File.separatorChar, '.');
    }
    
}
