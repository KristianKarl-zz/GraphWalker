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
package org.graphwalker.core.util;

import net.sf.oval.exception.ConstraintsViolatedException;
import org.junit.Test;

public class ResourceTest {

    @Test
    public void testGetFileWithUnixPath() {
        Resource.getFile("/models/brokenModel.graphml");
    }

    @Test
    public void testGetFileWithUnixPathAndMissingLeadingPathSeparator() {
        Resource.getFile("models/brokenModel.graphml");
    }

    @Test
    public void testGetFileWithWindowsPath() {
        Resource.getFile("\\models\\brokenModel.graphml");
    }

    @Test
    public void testGetFileWithWindowsPathAndMissingLeadingPathSeparator() {
        Resource.getFile("models\\brokenModel.graphml");
    }

    @Test(expected = ConstraintsViolatedException.class)
    public void testGetFileWithMissingFilename() {
        Resource.getFile(null);
    }

    @Test(expected = ConstraintsViolatedException.class)
    public void testGetFileWithEmptyFilename() {
        Resource.getFile("");
    }
}
