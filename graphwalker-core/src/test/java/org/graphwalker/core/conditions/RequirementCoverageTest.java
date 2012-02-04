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
package org.graphwalker.core.conditions;

import org.graphwalker.core.GraphWalker;
import org.graphwalker.core.GraphWalkerExecutor;
import org.graphwalker.core.GraphWalkerImpl;
import org.graphwalker.core.configuration.Configuration;
import org.graphwalker.core.configuration.ConfigurationImpl;
import org.graphwalker.core.generators.PathGenerator;
import org.graphwalker.core.generators.RandomPath;
import org.graphwalker.core.model.*;
import org.graphwalker.core.model.GraphMLModelFactory;
import org.junit.Assert;
import org.junit.Test;

public class RequirementCoverageTest {
    
    @Test
    public void testRequirement() {
        Configuration configuration = new ConfigurationImpl();
        ModelFactory modelFactory = new GraphMLModelFactory();
        Model model = modelFactory.create("m1", "models/requirementModel.graphml");
        PathGenerator pathGenerator = new RandomPath();
        StopCondition stopCondition = new RequirementCoverage(100);
        pathGenerator.setStopCondition(stopCondition);
        model.setPathGenerator(pathGenerator);
        model.setImplementation(this);
        model.afterElementsAdded();
        configuration.addModel(model);
        GraphWalkerExecutor executor = new GraphWalkerExecutor(new GraphWalkerImpl(configuration));
        executor.run();
    }
    /*
    @Test
    public void testFailedRequirement() {
        Configuration configuration = new ConfigurationImpl();
        Model model = new ModelImpl("m1");
        Vertex start = model.addVertex(new Vertex("start"));
        Vertex vertex = model.addVertex(new Vertex("v_fail"));
        Requirement requirement = new Requirement("badReq");
        vertex.addRequirement(requirement);
        model.addEdge(new Edge(), start, vertex);
        PathGenerator pathGenerator = new RandomPath();
        StopCondition stopCondition = new RequirementCoverage(100);
        pathGenerator.setStopCondition(stopCondition);
        model.setPathGenerator(pathGenerator);
        model.setImplementation(this);
        model.afterElementsAdded();
        configuration.addModel(model);
        GraphWalker graphWalker = new GraphWalkerImpl(configuration);
        graphWalker.executePath();
        Assert.assertEquals(RequirementStatus.FAILED, requirement.getStatus());
    }

    @Test
    public void testBacktrackingRequirement() {
        Configuration configuration = new ConfigurationImpl();
        ModelFactory modelFactory = new GraphMLModelFactory();
        Model model = modelFactory.create("m1", "models/requirementBacktrackingModel.graphml");
        PathGenerator pathGenerator = new RandomPath();
        StopCondition stopCondition = new RequirementCoverage(100);
        pathGenerator.setStopCondition(stopCondition);
        model.setPathGenerator(pathGenerator);
        model.setImplementation(this);
        model.afterElementsAdded();
        configuration.addModel(model);
        GraphWalker graphWalker = new GraphWalkerImpl(configuration);
        graphWalker.executePath();
        // TODO: verify requirement status
    }
  */

    public void v_1() {}

    public void v_2() {}

    public void v_3() {}

    public void v_fail() {
        Assert.assertTrue("Assert fails, the result is not as expected!", false);
    }
}
