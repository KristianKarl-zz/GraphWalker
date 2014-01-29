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
package org.graphwalker.core.condition;

import org.graphwalker.core.StopCondition;
import org.graphwalker.core.machine.ExecutionContext;

import java.util.Iterator;
import java.util.Vector;

public class CombinationalCondition extends BaseStopCondition{

  private Vector<StopCondition> conditions;

  public CombinationalCondition() {
    this.conditions = new Vector<StopCondition>();
  }

  public void add(StopCondition condition) {
    this.conditions.add(condition);
  }

  public StopCondition getStopConditionAtIndex( int index) {
    return conditions.elementAt(index);
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
