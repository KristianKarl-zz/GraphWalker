package org.graphwalker.multiple;

import org.apache.log4j.Logger;
import org.graphwalker.Keywords;
import org.graphwalker.ModelBasedTesting;
import org.graphwalker.Util;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

import org.graphwalker.ModelHandler;
import org.graphwalker.conditions.EdgeCoverage;
import org.graphwalker.conditions.ReachedVertex;
import org.graphwalker.generators.PathGenerator;
import org.graphwalker.generators.RandomPathGenerator;

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
    modelhandler.add("A", Util.loadMbtFromXml(Util.getFile("xml/switch/A.xml")), null);
    assertTrue(modelhandler.getModels().size() == 1);
    modelhandler.add("B", Util.loadMbtFromXml(Util.getFile("xml/switch/B.xml")), null);
    assertTrue(modelhandler.getModels().size() == 2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void addDuplicateNameModels() throws Exception {
    ModelHandler modelhandler = new ModelHandler();
    modelhandler.add("A", Util.loadMbtFromXml(Util.getFile("xml/switch/A.xml")), null);
    modelhandler.add("A", Util.loadMbtFromXml(Util.getFile("xml/switch/B.xml")), null);
  }

  @Test
  public void removeModel() throws Exception {
    ModelHandler modelhandler = new ModelHandler();
    ModelBasedTesting mbt = Util.loadMbtFromXml(Util.getFile("xml/switch/A.xml"));
    modelhandler.add("A", mbt, null);
    modelhandler.remove(0);
    assertTrue(modelhandler.getModels().isEmpty());
  }

  @Test(expected = IllegalArgumentException.class)
  public void executeIncorrectName() throws Exception {
    ModelHandler modelhandler = new ModelHandler();
    ModelBasedTesting mbt = Util.loadMbtFromXml(Util.getFile("xml/switch/A.xml"));
    modelhandler.add("A", mbt, new Model_A_API());
    modelhandler.execute("a");
  }

  @Test
  public void executeSingleModel() throws Exception {
    ModelHandler modelhandler = new ModelHandler();
    ModelBasedTesting mbt = Util.loadMbtFromXml(Util.getFile("xml/switch/A.xml"));
    modelhandler.add("A", mbt, new Model_A_API());
    modelhandler.execute("A");
    assertTrue(modelhandler.isAllModelsDone());
  }

  @Test
  public void executeTwoModel() throws Exception {
    ModelHandler modelhandler = new ModelHandler();
    ModelBasedTesting a = Util.getNewMbtFromXml(Util.getFile("xml/switch/A.xml"));
    ModelBasedTesting b = Util.getNewMbtFromXml(Util.getFile("xml/switch/B.xml"));
    modelhandler.add("A", a, new Model_A_API());
    modelhandler.add("B", b, new Model_B_API());
    modelhandler.execute("A");
    assertTrue(modelhandler.isAllModelsDone());
  }

  @Test
  public void executeTwoModelsCulDeSac() throws Exception {
    ModelHandler modelhandler = new ModelHandler();
    
    ModelBasedTesting login = new ModelBasedTesting();
    login.readGraph("graphml/multiple/switch/A1.graphml");
    login.enableExtended(true);
    PathGenerator generator = new RandomPathGenerator();
    generator.setStopCondition(new EdgeCoverage(1.0));
    login.setGenerator(generator);
    modelhandler.add("Login", login, new Model_A1_API());

    ModelBasedTesting exitClient = new ModelBasedTesting();
    exitClient.readGraph("graphml/multiple/switch/B1.graphml");
    exitClient.enableExtended(true);
    generator = new RandomPathGenerator();
    generator.setStopCondition(new EdgeCoverage(1.0));
    exitClient.setGenerator(generator);
    modelhandler.add("ExitClient", exitClient, new Model_B1_API());
    
    
    modelhandler.execute("Login");
    assertTrue(modelhandler.isAllModelsDone());
  }

  @Ignore
  public void executeModelThatStops() throws Exception {
    ModelHandler modelhandler = new ModelHandler();
    
    ModelBasedTesting a = new ModelBasedTesting();
    a.readGraph("graphml/multiple/switch/A.graphml");
    a.setGenerator(Keywords.GENERATOR_RANDOM);
    a.setCondition(new ReachedVertex("v_WhatsNew"));
    modelhandler.add("A", a, new Model_A_API());
   
    ModelBasedTesting b = new ModelBasedTesting();
    b.readGraph("graphml/multiple/switch/B.graphml");
    b.setGenerator(Keywords.GENERATOR_RANDOM);
    b.setCondition(new EdgeCoverage(1.0));
    modelhandler.add("B", b, new Model_B_API());
            
    modelhandler.execute("A");
    assertTrue(modelhandler.isAllModelsDone());
  }

  @Test
  public void executeThreeModel() throws Exception {
    ModelHandler modelhandler = new ModelHandler();
    ModelBasedTesting a = Util.getNewMbtFromXml(Util.getFile("xml/switch/A.xml"));
    ModelBasedTesting b = Util.getNewMbtFromXml(Util.getFile("xml/switch/B.xml"));
    ModelBasedTesting c = Util.getNewMbtFromXml(Util.getFile("xml/switch/C.xml"));
    modelhandler.add("A", a, new Model_A_API());
    modelhandler.add("B", b, new Model_B_API());
    modelhandler.add("C", c, new Model_C_API());
    modelhandler.execute("A");
    assertTrue(modelhandler.isAllModelsDone());
  }

  @Test
  public void getStatistics() throws Exception {
    ModelHandler modelhandler = new ModelHandler();
    ModelBasedTesting mbt = Util.loadMbtFromXml(Util.getFile("xml/switch/A.xml"));
    modelhandler.add("A", mbt, new Model_A_API());
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
    ModelBasedTesting a = Util.getNewMbtFromXml(Util.getFile("xml/switch/A.xml"));
    ModelBasedTesting b = Util.getNewMbtFromXml(Util.getFile("xml/switch/B.xml"));
    ModelBasedTesting c = Util.getNewMbtFromXml(Util.getFile("xml/switch/C.xml"));
    modelhandler.add("A", a, new Model_A_API());
    modelhandler.add("B", b, new Model_B_API());
    modelhandler.add("C", c, new Model_C_API());
    modelhandler.execute("A");
    assertTrue(modelhandler.isAllModelsDone());

    String actualResult = modelhandler.getStatistics();
    assertTrue(actualResult, actualResult.contains("Statistics for A:"));
    assertTrue(actualResult, actualResult.contains("Statistics for B:"));
    assertTrue(actualResult, actualResult.contains("Statistics for C:"));
  }
}
