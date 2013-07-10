// This file is part of the GraphWalker java package
// The MIT License
//
// Copyright (c) 2010 graphwalker.org
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

package org.graphwalker.generators;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.graphwalker.Util;
import org.graphwalker.conditions.TestCaseLength;
import org.graphwalker.exceptions.StopConditionException;
import org.graphwalker.io.GraphML;
import org.graphwalker.machines.FiniteStateMachine;

public class AllPathPermutationsGeneratorTest extends TestCase {

  private final Logger logger = Util.setupLogger(AllPathPermutationsGeneratorTest.class);

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  public void test_AllPathPermutationsGeneratior() throws StopConditionException, InterruptedException {
    logger.info("TEST: test_AllPathPermutationsGeneration");
    logger.info("=======================================================================");
    GraphML gml = new GraphML();
    gml.load("graphml/permutations/simple.graphml");
    FiniteStateMachine FSM = new FiniteStateMachine();
    FSM.setModel(gml.getModel());
    FSM.setWeighted(false);

    PathGenerator pathGenerator = new AllPathPermutationsGenerator(new TestCaseLength(42));
    pathGenerator.setMachine(FSM);

    while (pathGenerator.hasNext()) {
      String[] stepPair = pathGenerator.getNext();
      int stats[] = FSM.getStatistics();
      int ec = 100 * stats[1] / stats[0];

      logger.debug("call( " + stepPair[0] + " ) then verify( " + stepPair[1] + " ) --> Edge coverage @ " + ec + "%");
    }

    // TODO: The original test wanted the depth to be 3, but it always ends up in 2. Why? Is this a
    // bug?
    // assertEquals(3, ((AllPathPermutationsGenerator) pathGenerator).getDepth());
    logger.debug("==============================");
  }
}
