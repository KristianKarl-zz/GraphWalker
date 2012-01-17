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
package org.graphwalker.core.model;

import org.graphwalker.core.model.factory.GraphMLModelFactory;
import org.graphwalker.core.util.ResourceException;
import org.junit.Assert;
import org.junit.Test;

public class ModelFactoryTest {

    @Test
    public void singleModelTest() {
        ModelFactory modelFactory = new GraphMLModelFactory();
        Model model = modelFactory.create("m1", "/models/singleModel.graphml");
        Assert.assertNotNull(model);
    }

    @Test
    public void multiModelATest() {
        ModelFactory modelFactory = new GraphMLModelFactory();
        Model model = modelFactory.create("m1", "/models/multiModelA.graphml");
        Assert.assertNotNull(model);
    }

    @Test
    public void edgeFilterModelATest() {
        ModelFactory modelFactory = new GraphMLModelFactory();
        Model model = modelFactory.create("m1", "/models/edgeFilterModelA.graphml");
        Assert.assertNotNull(model);
    }

    @Test(expected = ResourceException.class)
    public void fileNotFoundTest() {
        ModelFactory modelFactory = new GraphMLModelFactory();
        modelFactory.create("m1", "FileNotFound");
    }

    @Test(expected = ModelException.class)
    public void brokenModelTest() {
        ModelFactory modelFactory = new GraphMLModelFactory();
        modelFactory.create("m1", "/models/brokenModel.graphml");
    }

    @Test
    public void readModelWithRequirementsTest() {
        ModelFactory modelFactory = new GraphMLModelFactory();
        Model model = modelFactory.create("m1", "/models/requirementModel.graphml");
        Assert.assertNotNull(model);
    }

}
