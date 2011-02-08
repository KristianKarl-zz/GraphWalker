package org.graphwalker.multiple;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.graphwalker.ModelBasedTesting;
import org.graphwalker.MultipleModels;
import org.graphwalker.Util;
import org.jdom.JDOMException;
import org.junit.Test;
import static org.junit.Assert.*;

import org.graphwalker.ModelHandler;
import org.graphwalker.MultipleModelsTest.ModelAPI;
import org.graphwalker.exceptions.GeneratorException;
import org.graphwalker.exceptions.StopConditionException;

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
