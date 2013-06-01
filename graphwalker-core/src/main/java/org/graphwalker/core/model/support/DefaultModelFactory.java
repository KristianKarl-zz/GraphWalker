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
import org.graphwalker.core.model.ModelFactory;

import java.util.ArrayList;
import java.util.List;

public class DefaultModelFactory implements ModelFactory {

    private final GraphMLModelFactory graphMLModelFactory = new GraphMLModelFactory();
    private final List<String> supportedTypes = new ArrayList<String>();

    public DefaultModelFactory() {
        supportedTypes.addAll(graphMLModelFactory.getSupportedFileTypes());
    }

    public boolean accept(String type) {
        return supportedTypes.contains(type);
    }

    public Model create(String id, String filename, String type) {
        //TODO: we need to handle this better, when/if we add more factories
        return graphMLModelFactory.create(id, filename, type);
    }

    public List<String> getSupportedFileTypes() {
        return supportedTypes;
    }
}
