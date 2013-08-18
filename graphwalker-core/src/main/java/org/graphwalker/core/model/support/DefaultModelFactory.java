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
package org.graphwalker.core.model.support;

import org.graphwalker.core.model.Model;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>DefaultModelFactory class.</p>
 */
public final class DefaultModelFactory extends AbstractModelFactory {

    private final GraphMLModelFactory graphMLModelFactory = new GraphMLModelFactory();
    private final Set<String> supportedTypes = new HashSet<String>();

    /**
     * <p>Constructor for DefaultModelFactory.</p>
     */
    public DefaultModelFactory() {
        supportedTypes.addAll(graphMLModelFactory.getSupportedFileTypes());
    }

    /** {@inheritDoc} */
    public boolean accept(String file) {
        //TODO: we need to handle this better, when/if we add more factories
        return graphMLModelFactory.accept(file);
    }

    /** {@inheritDoc} */
    public Model create(String file) {
        //TODO: we need to handle this better, when/if we add more factories
        return graphMLModelFactory.create(file);
    }

    /**
     * <p>getSupportedFileTypes.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<String> getSupportedFileTypes() {
        return supportedTypes;
    }
}
