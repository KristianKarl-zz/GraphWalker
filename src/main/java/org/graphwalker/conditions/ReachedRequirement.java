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

package org.graphwalker.conditions;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class ReachedRequirement extends StopCondition {

  private Collection<String> requirements;

  public ReachedRequirement(String requirements) {
    String[] list = requirements.split(",");
    for (int i = 0; i < list.length; i++) {
      list[i] = list[i].trim();
    }
    this.requirements = new HashSet<String>(Arrays.asList(list));
  }

  @Override
  public boolean isFulfilled() {
    return machine.getCoveredRequirements().containsAll(requirements);
  }

  @Override
  public double getFulfilment() {
    Collection<String> covered = machine.getCoveredRequirements();
    covered.retainAll(requirements);
    return covered.size() / (double) requirements.size();
  }

  @Override
  public String toString() {
    return "RC=" + Arrays.deepToString(requirements.toArray());
  }

  public Collection<String> getRequirements() {
    return requirements;
  }

}
