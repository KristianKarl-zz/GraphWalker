/*
 * #%L
 * GraphWalker Core
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
package org.graphwalker.maven.plugin.model;

import org.graphwalker.core.Model;

import java.nio.file.Path;
import java.util.Set;

/**
 * @author Nils Olsson
 */
public interface ModelFactory {

    /**
     * <p>accept.</p>
     *
     * @param path a {@link java.nio.file.Path} object.
     * @return a boolean.
     */
    boolean accept(Path path);

    /**
     * <p>validate.</p>
     *
     * @param path a {@link java.nio.file.Path} object.
     * @return a boolean.
     */
    boolean validate(Path path);

    /**
     * <p>create.</p>
     *
     * @param path a {@link java.nio.file.Path} object.
     * @return a {@link org.graphwalker.core.Model} object.
     */
    Model create(Path path);

    /**
     * <p>getSupportedFileTypes.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    Set<String> getSupportedFileTypes();
}
