package org.graphwalker.analyze;

import static org.junit.Assert.assertTrue;

import org.graphwalker.ModelBasedTesting;
import org.junit.Test;

public class AnalyzeTest {

  @Test
  public void test() {
    ModelBasedTesting mbt = new ModelBasedTesting();
    mbt.readGraph("graphml/analyze/infinite-loop.graphml");
    String str = Analyze.unreachableVertices(mbt);
    assertTrue("We should have found vertices not reachable from one another", str.length() > 0);

    mbt = new ModelBasedTesting();
    mbt.readGraph("graphml/analyze/non-infinite-loop.graphml");
    str = Analyze.unreachableVertices(mbt);
    assertTrue("We should not have found any vertices not reachable from one another", str.length() == 0);
  }

}
