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
package org.graphwalker.core.generators;

import org.graphwalker.core.Bundle;
import org.graphwalker.core.generators.support.RandomLeastVisitedPath;
import org.graphwalker.core.generators.support.RandomPath;
import org.graphwalker.core.generators.support.RandomUnvisitedFirstPath;
import org.graphwalker.core.utils.Resource;

/**
 * <p>PathGeneratorFactory class.</p>
 */
public final class PathGeneratorFactory {

    private PathGeneratorFactory() {
    }

    /**
     * <p>create.</p>
     *
     * @param type a {@link java.lang.String} object.
     * @return a {@link org.graphwalker.core.generators.PathGenerator} object.
     */
    public static PathGenerator create(String type) {
        if ("Random".equalsIgnoreCase(type)) {
            return new RandomPath();
        } else if ("RandomUnvisitedFirst".equalsIgnoreCase(type)) {
            return new RandomUnvisitedFirstPath();
        } else if ("RandomLeastVisited".equalsIgnoreCase(type)) {
            return new RandomLeastVisitedPath();
        }
        throw new PathGeneratorException(Resource.getText(Bundle.NAME, "exception.generator.unknown"));
    }
}
