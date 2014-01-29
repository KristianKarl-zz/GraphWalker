/*
 * #%L
 * GraphWalker Core
 * %%
 * Copyright (C) 2011 - 2014 GraphWalker
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
package org.graphwalker.core.generator;

import org.graphwalker.core.PathGenerator;
import org.graphwalker.core.StopCondition;
import org.graphwalker.core.machine.ExecutionContext;
import org.graphwalker.core.model.Element;

import java.util.Vector;


public class CombinedPathGenerator implements PathGenerator {
  private Vector<PathGenerator> generatorList = new Vector<>();
  StopCondition stopCondition = null;
  private int currentGenerator = 0;

  public void addPathGenerator(PathGenerator generator) {
    generatorList.add(generator);
  }

  public PathGenerator getActivePathGenerator() {
    return generatorList.get(currentGenerator);
  }

  public PathGenerator getPathGeneratorAtIndex( int index) {
    return generatorList.elementAt(index);
  }

  private boolean hasPath() {
    return generatorList.size() > currentGenerator;
  }

  private void scrapActivePathGenerator() {
    currentGenerator++;
  }

  public Element getNextStep(ExecutionContext context) {
    boolean nextIsAvailable = false;

    while (hasPath() && !nextIsAvailable) {
      nextIsAvailable = getActivePathGenerator().hasNextStep(context);
      if (!nextIsAvailable) scrapActivePathGenerator();
    }

    if (!nextIsAvailable) {
      return null;
    }

    return getActivePathGenerator().getNextStep(context);
  }

  public Boolean hasNextStep(ExecutionContext context) {
    boolean nextIsAvailable = false;

    while (hasPath() && !nextIsAvailable) {
      nextIsAvailable = getActivePathGenerator().hasNextStep(context);

      if (!nextIsAvailable) {
        scrapActivePathGenerator();
      }
    }

    return nextIsAvailable;
  }

  public StopCondition getStopCondition() {
    return getActivePathGenerator().getStopCondition();
  }

  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    for (PathGenerator aGeneratorList : generatorList) {
      stringBuilder.append(aGeneratorList.toString());
      stringBuilder.append(System.getProperty("line.separator"));
    }
    return stringBuilder.toString().trim();
  }
}
