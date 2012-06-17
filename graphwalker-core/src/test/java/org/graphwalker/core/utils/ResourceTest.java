/*
 * #%L
 * GraphWalker Core
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
package org.graphwalker.core.utils;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class ResourceTest {

    @Test
    public void testGetFileWithUnixPath() {
        Resource.getResourceAsFile("/models/brokenModel.graphml");
    }

    @Test
    public void testGetResourceFile() {
        Resource.getResourceAsFile("models/brokenModel.graphml");
    }

    @Test
    public void jarTest() throws MalformedURLException {
        // try to find the resource in the test jar
        InputStream inputStream = null;
        try {
            inputStream = Resource.getResourceAsStream("jar-resources/resource.graphml");
        } catch (Throwable throwable) {
            //
        }
        Assert.assertNull(inputStream);

        // add the test jar to the classLoader
        File file = null;
        if (System.getProperty("user.dir").endsWith("graphwalker-core")) {
            file = new File(System.getProperty("user.dir") + "/target/test-classes/jar-resources.jar");
        } else {
            file = new File(System.getProperty("user.dir") + "/graphwalker-core/target/test-classes/jar-resources.jar");
        }
        URLClassLoader classLoader = new URLClassLoader(new URL[]{file.toURI().toURL()}, Thread.currentThread().getContextClassLoader());
        Thread.currentThread().setContextClassLoader(classLoader);

        // try to find the resource again
        inputStream = Resource.getResourceAsStream("jar-resources/resource.graphml");
        Assert.assertNotNull(inputStream);

        try {
            inputStream.close();
        } catch (IOException e) {
            //
        }
    }

}
