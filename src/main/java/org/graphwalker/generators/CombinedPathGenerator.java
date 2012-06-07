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

import java.util.Vector;

import org.apache.log4j.Logger;
import org.graphwalker.conditions.StopCondition;
import org.graphwalker.machines.FiniteStateMachine;

public class CombinedPathGenerator extends PathGenerator {

  private static Logger logger = Logger.getLogger(CombinedPathGenerator.class);

  private Vector<PathGenerator> generatorList = new Vector<PathGenerator>();
  private int currentGenerator = 0;

  public CombinedPathGenerator() {
    super();
  }

  public CombinedPathGenerator(StopCondition stopCondition) {
    super(stopCondition);
  }

  public void addPathGenerator(PathGenerator generator) {
    logger.debug("Adding PathGenerator: " + generator);
    generatorList.add(generator);
  }

  @Override
  public void setMachine(FiniteStateMachine machine) {
    for (PathGenerator aGeneratorList : generatorList) {
      aGeneratorList.setMachine(machine);
    }
  }

  @Override
  public void setStopCondition(StopCondition stopCondition) {
    for (PathGenerator aGeneratorList : generatorList) {
      aGeneratorList.setStopCondition(stopCondition);
    }
  }

  private PathGenerator getActivePathGenerator() {
    return generatorList.get(currentGenerator);
  }

  private boolean hasPath() {
    return generatorList.size() > currentGenerator;
  }

  private void scrapActivePathGenerator() {
    logger.debug("Removing PathGenerator: " + getActivePathGenerator());
    currentGenerator++;
  }

  @Override
  public boolean hasNext() {
    boolean nextIsAvailable = false;
    while (hasPath() && !nextIsAvailable) {
      nextIsAvailable = getActivePathGenerator().hasNext();
      if (!nextIsAvailable) scrapActivePathGenerator();
    }
    return nextIsAvailable;
  }

  @Override
  public String[] getNext() throws InterruptedException {
    String[] retur = {"", ""};

    boolean nextIsAvailable = false;
    while (hasPath() && !nextIsAvailable) {
      nextIsAvailable = getActivePathGenerator().hasNext();
      if (!nextIsAvailable) scrapActivePathGenerator();
    }
    if (!nextIsAvailable) return retur;
    return getActivePathGenerator().getNext();
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    for (PathGenerator aGeneratorList : generatorList) {
      stringBuilder.append(aGeneratorList.toString());
      stringBuilder.append(System.getProperty("line.separator"));
    }
    return stringBuilder.toString().trim();
  }

}
