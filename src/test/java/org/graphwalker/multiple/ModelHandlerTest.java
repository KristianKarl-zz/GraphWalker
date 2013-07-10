package org.graphwalker.multiple;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.graphwalker.Util;
import org.graphwalker.conditions.EdgeCoverage;
import org.graphwalker.conditions.NeverCondition;
import org.graphwalker.conditions.ReachedVertex;
import org.graphwalker.generators.RandomPathGenerator;
import org.graphwalker.multipleModels.ModelAPI;
import org.graphwalker.multipleModels.ModelHandler;
import org.junit.Ignore;
import org.junit.Test;

public class ModelHandlerTest {

  static Logger logger = Util.setupLogger(ModelHandlerTest.class);

  @Test
  public void contructor() throws Exception {
    ModelHandler modelhandler = new ModelHandler();
    assertNotNull(modelhandler);
    assertNotNull(modelhandler.getModels());
  }

  @Test
  public void addModels() throws Exception {
    ModelHandler modelhandler = new ModelHandler();
    modelhandler.add("A", new ModelAPI("graphml/multiple/switch/A.graphml", true, new RandomPathGenerator(new NeverCondition()), false));
    assertTrue(modelhandler.getModels().size() == 1);
    modelhandler.add("B", new ModelAPI("graphml/multiple/switch/B.graphml", true, new RandomPathGenerator(new NeverCondition()), false));
    assertTrue(modelhandler.getModels().size() == 2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void addDuplicateNameModels() throws Exception {
    ModelHandler modelhandler = new ModelHandler();
    modelhandler.add("A", new ModelAPI("graphml/multiple/switch/A.graphml", true, new RandomPathGenerator(new NeverCondition()), false));
    modelhandler.add("A", new ModelAPI("graphml/multiple/switch/B.graphml", true, new RandomPathGenerator(new NeverCondition()), false));
  }

  @Test
  public void removeModel() throws Exception {
    ModelHandler modelhandler = new ModelHandler();
    modelhandler.add("A", new ModelAPI("graphml/multiple/switch/A.graphml", true, new RandomPathGenerator(new NeverCondition()), false));
    modelhandler.remove(0);
    assertTrue(modelhandler.getModels().isEmpty());
  }

  @Test(expected = IllegalArgumentException.class)
  public void executeIncorrectName() throws Exception {
    ModelHandler modelhandler = new ModelHandler();
    modelhandler.add("A", new ModelAPI("graphml/multiple/switch/A.graphml", true, new RandomPathGenerator(new NeverCondition()), false));
    modelhandler.execute("a");
  }

  @Test
  public void executeSingleModel() throws Exception {
    ModelHandler modelhandler = new ModelHandler();
    modelhandler.add("A", new Model_A_API("graphml/multiple/switch/A.graphml", true, new RandomPathGenerator(new EdgeCoverage(1.0))));
    modelhandler.execute("A");
    assertTrue(modelhandler.isAllModelsDone());
  }

  @Test
  public void executeTwoModel() throws Exception {
    ModelHandler modelhandler = new ModelHandler();
    modelhandler.add("A", new Model_A_API("graphml/multiple/switch/A.graphml", true, new RandomPathGenerator(new EdgeCoverage(1.0))));
    modelhandler.add("B", new Model_B_API("graphml/multiple/switch/B.graphml", true, new RandomPathGenerator(new EdgeCoverage(1.0))));
    modelhandler.execute("A");
    assertTrue(modelhandler.isAllModelsDone());
  }

  @Test
  public void executeTwoModelsCulDeSac() throws Exception {
    ModelHandler modelhandler = new ModelHandler();

    modelhandler.add("Login", new Model_A1_API("graphml/multiple/switch/A1.graphml", true, new RandomPathGenerator(new EdgeCoverage(1.0))));
    modelhandler.add("ExitClient", new Model_B1_API("graphml/multiple/switch/B1.graphml", true, new RandomPathGenerator(new EdgeCoverage(1.0))));

    modelhandler.execute("Login");
    assertTrue(modelhandler.isAllModelsDone());
  }

  @Ignore
  public void executeModelThatStops() throws Exception {
    ModelHandler modelhandler = new ModelHandler();

    modelhandler.add("A", new Model_A_API("graphml/multiple/switch/A.graphml", true, new RandomPathGenerator(new ReachedVertex("v_WhatsNew"))));
    modelhandler.add("B", new Model_B_API("graphml/multiple/switch/B.graphml", true, new RandomPathGenerator(new EdgeCoverage(1.0))));

    modelhandler.execute("A");
    assertTrue(modelhandler.isAllModelsDone());
  }

  @Test
  public void executeThreeModel() throws Exception {
    ModelHandler modelhandler = new ModelHandler();
    modelhandler.add("A", new Model_A_API("graphml/multiple/switch/A.graphml", true, new RandomPathGenerator(new ReachedVertex("v_WhatsNew"))));
    modelhandler.add("B", new Model_B_API("graphml/multiple/switch/B.graphml", true, new RandomPathGenerator(new EdgeCoverage(1.0))));
    modelhandler.add("C", new Model_C_API("graphml/multiple/switch/C.graphml", true, new RandomPathGenerator(new EdgeCoverage(1.0))));
    modelhandler.execute("A");
    assertTrue(modelhandler.isAllModelsDone());
  }

  @Test
  public void getStatistics() throws Exception {
    ModelHandler modelhandler = new ModelHandler();
    modelhandler.add("A", new Model_A_API("graphml/multiple/switch/A.graphml", true, new RandomPathGenerator(new ReachedVertex("v_WhatsNew"))));
    modelhandler.execute("A");
    assertTrue(modelhandler.isAllModelsDone());

    String actualResult = modelhandler.getStatistics();
    assertTrue(actualResult, actualResult.contains("Statistics for A:"));
    assertFalse(actualResult, actualResult.contains("Statistics for B:"));
    assertFalse(actualResult, actualResult.contains("Statistics for C:"));
  }

  @Test
  public void getStatisticsMultipleModels() throws Exception {
    ModelHandler modelhandler = new ModelHandler();
    modelhandler.add("A", new Model_A_API("graphml/multiple/switch/A.graphml", true, new RandomPathGenerator(new ReachedVertex("v_WhatsNew"))));
    modelhandler.add("B", new Model_B_API("graphml/multiple/switch/B.graphml", true, new RandomPathGenerator(new EdgeCoverage(1.0))));
    modelhandler.add("C", new Model_C_API("graphml/multiple/switch/C.graphml", true, new RandomPathGenerator(new EdgeCoverage(1.0))));
    modelhandler.execute("A");
    assertTrue(modelhandler.isAllModelsDone());

    String actualResult = modelhandler.getStatistics();
    assertTrue(actualResult, actualResult.contains("Statistics for A:"));
    assertTrue(actualResult, actualResult.contains("Statistics for B:"));
    assertTrue(actualResult, actualResult.contains("Statistics for C:"));
  }
}
