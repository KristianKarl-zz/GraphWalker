package org.graphwalker.core.condition;

import org.graphwalker.core.StopCondition;
import org.graphwalker.core.machine.ExecutionContext;

import java.util.Iterator;
import java.util.Vector;

public class CombinationalCondition implements StopCondition{

  private Vector<StopCondition> conditions;

  public void add(StopCondition condition) {
    this.conditions.add(condition);
  }

  public boolean isFulfilled(ExecutionContext context) {
    for (StopCondition condition : conditions) {
      if (!condition.isFulfilled(context)) {
        return false;
      }
    }
    return true;
  }

  public double getFulfilment(ExecutionContext context) {
    double fulfilment = 0;
    for (StopCondition condition : conditions) {
      fulfilment += condition.getFulfilment(context);
    }
    return fulfilment / conditions.size();
  }

  public String getValue() {
    StringBuilder stringBuilder = new StringBuilder("(");
    for (Iterator<StopCondition> i = conditions.iterator(); i.hasNext();) {
      stringBuilder.append(i.next().toString());
      if (i.hasNext()) {
        stringBuilder.append(" AND ");
      }
    }
    stringBuilder.append(")");
    return stringBuilder.toString();
  }
}
