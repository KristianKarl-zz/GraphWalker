package org.graphwalker.core.generator;

import org.graphwalker.core.PathGenerator;
import org.graphwalker.core.StopCondition;
import org.graphwalker.core.machine.ExecutionContext;
import org.graphwalker.core.model.Element;

import java.util.Vector;


public class CombinedPathGenerator implements PathGenerator {
  private Vector<PathGenerator> generatorList = new Vector<PathGenerator>();
  StopCondition stopCondition = null;
  private int currentGenerator = 0;

  public void addPathGenerator(PathGenerator generator) {
    generatorList.add(generator);
  }

  private PathGenerator getActivePathGenerator() {
    return generatorList.get(currentGenerator);
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
