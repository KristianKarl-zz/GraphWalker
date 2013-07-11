/*
 * The MIT License
 * 
 * Copyright 2011 krikar.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.graphwalker.multipleModels;

import java.util.ArrayList;
import java.util.Random;

import org.apache.log4j.Logger;
import org.graphwalker.Keywords;
import org.graphwalker.ModelBasedTesting;
import org.graphwalker.Util;
import org.graphwalker.WebRenderer;
import org.graphwalker.conditions.NeverCondition;
import org.graphwalker.generators.RandomPathGenerator;
import org.graphwalker.graph.Graph;

/**
 * The ModelHandler handles multiple models. The basic workflow using this class is:
 * 
 * <pre>
 * ModelHandler modelhandler = new ModelHandler();
 * ModelBasedTesting a = Util.getNewMbtFromXml(Util.getFile(&quot;A.xml&quot;));
 * ModelBasedTesting b = Util.getNewMbtFromXml(Util.getFile(&quot;B.xml&quot;));
 * ModelBasedTesting c = Util.getNewMbtFromXml(Util.getFile(&quot;C.xml&quot;));
 * modelhandler.add(&quot;A&quot;, a, new Model_A_API());
 * modelhandler.add(&quot;B&quot;, b, new Model_B_API());
 * modelhandler.add(&quot;C&quot;, c, new Model_C_API());
 * modelhandler.execute(&quot;A&quot;);
 * </pre>
 * 
 * New keywords and datum is introduced to handle multiple models. A model can now switch execution
 * into another model using the keyword {@link Keywords#SWITCH_MODEL SWITCH_MODEL}. This means that
 * the current model is paused, and any other loaded model(s) with the same
 * {@link Keywords#GRAPH_VERTEX GRAH_VERTEX} is a candidate to switch into. Also the current paused
 * model can be resumed.
 * 
 * The executions ends when all models has reached their stop criteria.
 * 
 * @see SWITCH_MODEL
 * @see GRAPH_VERTEX
 * 
 * @author Kristian Karl
 */
public class ModelHandler {

  static Logger logger = Util.setupLogger(ModelHandler.class);
  ArrayList<ModelRunnable> models = new ArrayList<ModelRunnable>();
  static private Random random = new Random();
  private String currentVertex;
  private WebRenderer webdriver = null;

  private static class ModelRunnable implements Runnable {

    private String name;
    private ModelBasedTesting mbt;
    private Object modelAPI;
    private boolean executionRestarted = false;
    private Exception thrownException = null;

    public ModelRunnable(String name, ModelAPI modelAPI) {
      this.name = name;
      this.mbt = modelAPI.getMbt();
      this.modelAPI = modelAPI;
    }

    @Override
    public void run() {
      try {
        logger.debug("Will start executing the model: " + this.mbt.getGraph());
        mbt.executePath(modelAPI);
      } catch (Exception e) {
        thrownException = e;
        Util.logStackTraceToError(e);
      }
    }

    public String getName() {
      return name;
    }

    public ModelBasedTesting getMbt() {
      return mbt;
    }

    public boolean isCrashed() {
      return thrownException != null;
    }

    public Exception crashException() {
      return thrownException;
    }

    public boolean isExecutionRestarted() {
      return executionRestarted;
    }

    public void setExecutionRestarted(boolean executionRestarted) {
      logger.debug("Will change executionRestarted from: " + this.executionRestarted + ", to: " + executionRestarted);
      this.executionRestarted = executionRestarted;
    }
  }

  /**
   * @return All models currently loaded.
   */
  public ArrayList<ModelRunnable> getModels() {
    return models;
  }

  /**
   * Adds a model to the handler.
   * 
   * @param name The name of the model. This is not the same as the name of the
   *        {@link Graph#getLabelKey() graph}. It's a logical name of the model, and it may not
   *        already be used by the handler.
   * @param modelAPI
   */
  public synchronized void add(String name, ModelAPI modelAPI) {
    if (WebRenderer.readRunProperty()) {
      if (webdriver == null) {
        webdriver = new WebRenderer(this, name, modelAPI);
      }
      else {
        webdriver.addModel(name, modelAPI);
      }
    }
    if (hasModel(name)) {
      throw new IllegalArgumentException("The model name " + name + " has already been used.");
    }
    logger.debug("Adding the model: " + Integer.toHexString(System.identityHashCode(modelAPI.getMbt())) + ", " + modelAPI.getMbt().getGraph());
    modelAPI.getMbt().setMultiModelHandler(this);
    models.add(new ModelRunnable(name, modelAPI));
  }

  /**
   * Removed a model from the handler.
   * 
   * @param model Removes the specified model from the handler.
   */
  public void remove(ModelRunnable model) {
    models.remove(model);
  }

  /**
   * Removed a model from the handler.
   * 
   * @param index Removes the specified model by index from the handler.
   */
  public void remove(long index) {
    models.remove((int) index);
  }

  /**
   * Starts executing the models. The execution is finished when all the models stop criteria are
   * reached.
   * 
   * @param name The logical name of the model which will start the execution.
   * @throws InterruptedException
   */
  public void execute(String name) throws InterruptedException {
    if (!hasModel(name)) {
      throw new IllegalArgumentException("The model name " + name + " does not exist in the model handler. Have you forgotten to add it?");
    }

    // All models are set and we are ready to start the websocket.
    if (webdriver != null) {
      webdriver.startup();
    }

    ModelRunnable model = getModel(name);

    // Start running the first model
    Thread t = new Thread(model);
    t.start();

    // Enter main loop
    while (true) {
      waitForModelToDoSomething(model);

      // As long as we have any running model, let them run until finished or
      // paused.
      while (isAnyModelRunning()) {
        Thread.sleep(10);
      }
      logger.debug("No model is running.");

      // If all models are finished, then exit
      if (isAllModelsDone()) {
        break;
      }
      logger.debug("Not all models has reached their desired stop conditions.");

      // Now, find models with matching vertex name.
      // If the model is paused, the current vertex is matched.
      // If the model is not started, the graph name is matched.
      ArrayList<ModelRunnable> pausedAndNotStartedModels = getModelMatchingCurrentVertex();
      if (pausedAndNotStartedModels.isEmpty()) {
        logger.debug("Did not find any models, matching the current vertex: " + currentVertex);
        logger.debug(getStatistics());
        break;
      }

      // Run the model, but check for it's state. The model is either
      // suspended or not started.
      int selectModel = random.nextInt(pausedAndNotStartedModels.size());
      model = pausedAndNotStartedModels.get(selectModel);
      logger.debug("Number of models to select from: " + pausedAndNotStartedModels.size());
      logger.debug("Selecting model(" + selectModel + ") " + model.getName());
      if (model.getMbt().isSuspended()) {
        model.getMbt().resume();
      } else {
        t = new Thread(model);
        t.start();
      }
    }
  }

  /**
   * Wait for the model to enter a running, paused or stopped state
   * 
   * @param model
   * @throws InterruptedException
   */
  private void waitForModelToDoSomething(ModelRunnable model) throws InterruptedException {
    while (true) {
      check4Crash(model);
      if (model.getMbt().isRunning()) {
        break;
      } else if (model.getMbt().isSuspended()) {
        break;
      } else if (!model.getMbt().hasNextStep()) {
        break;
      }
      Thread.sleep(10);
    }
  }

  /**
   * Searches for any model with matching vertex name. If the model is paused, the current vertex is
   * matched. If the model is not started, the graph name is matched.
   * 
   * @param v The vertex to match
   * @return an array of models that matches
   */
  private ArrayList<ModelRunnable> getModelMatchingCurrentVertex() {
    logger.debug("Looking for paused or not started model matching current vertex: " + currentVertex);
    ArrayList<ModelRunnable> array = new ArrayList<ModelRunnable>();
    for (ModelRunnable model : models) {
      logger.debug("Examining model: " + model.getName());
      logger.debug("  Current vertex of graph: " + model.getMbt().getCurrentVertex());

      check4Crash(model);

      if (model.getMbt().getGraph().getLabelKey().equals(currentVertex) || model.getMbt().getCurrentVertex().getLabelKey().equals(currentVertex)) {
        logger.debug("  " + model.getName() + ", has matching graph or current vertex label");
        if (model.getMbt().hasNotStartedExecution()) {
          logger.debug("  Adding not started model: " + model.getName());
          array.add(model);
        } else if (model.getMbt().isSuspended()) {
          if (model.getMbt().isCulDeSac()) {
            logger.debug("  Model has ended up in a Cul-de-Sac");
            if (!model.getMbt().hasNextStep()) {
              logger.debug("  Model has reached it's stop condition, so restarting model and resetting current vertex to Start: " + model.getName());
              model.setExecutionRestarted(true);
              model.getMbt().setGenerator(new RandomPathGenerator(new NeverCondition()));
              model.getMbt().setCurrentVertex(Keywords.START_NODE);
            } else {
              logger.debug("  Model has not reached it's stop condition, so restarting model and resetting current vertex to Start: "
                  + model.getName());
              model.setExecutionRestarted(true);
              model.getMbt().setCurrentVertex(Keywords.START_NODE);
            }
          } else {
            logger.debug("  Adding paused model, " + model.getName());
            array.add(model);
          }
        } else if (!model.getMbt().hasNextStep()) {
          logger.debug("  Restarting model: " + model.getName());
          model.setExecutionRestarted(true);
          model.getMbt().setGenerator(new RandomPathGenerator(new NeverCondition()));
          array.add(model);
        } else if (model.isExecutionRestarted()) {
          logger.debug("  Adding recently restarted model: " + model.getName());
          array.add(model);
        }
      }
    }
    return array;
  }

  /**
   * Gets a model by it's logical name.
   * 
   * @param name The logical name of a model
   * @return The model that matches the logical name.
   */
  private ModelRunnable getModel(String name) {
    for (ModelRunnable model : models) {
      check4Crash(model);
      if (model.getName().equals(name)) {
        return model;
      }
    }
    return null;
  }

  /**
   * Searches the handler for a model in a running state.
   * 
   * @return True if any model is in a running state, else false will be returned.
   */
  private boolean isAnyModelRunning() {
    for (ModelRunnable model : models) {
      check4Crash(model);
      if (model.getMbt().isRunning()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks whether the models of the handler has executed.
   * 
   * @return True if all models has reached the stop criteria, else false is returned.
   */
  /**
   * @return
   */
  public boolean isAllModelsDone() {
    for (ModelRunnable model : models) {
      logger.debug("Examining model: " + model.getMbt().getGraph());
      check4Crash(model);
      if (model.getMbt().getGenerator().getStopCondition() instanceof NeverCondition) {
        logger.debug("  Model: " + model.getName() + ", has a NeverCondition, thus by definition finished.");
      } else if (!model.getMbt().hasNextStep()) {
        logger.debug("  Model: " + model.getName() + ", has reached it's stop condition");
      } else {
        logger.debug("  Model: " + model.getName() + ", is not done: " + model.getMbt().getStatisticsString());
        return false;
      }
    }
    logger.debug("All models has reached their desired stop conditions.");
    return true;
  }

  /**
   * Checks if the handler has a model with the same logical name already loaded.
   * 
   * @param name The logical name of the model
   * @return True if the handler already has a model with matching logical name.
   */
  private boolean hasModel(String name) {
    for (ModelRunnable model : models) {
      check4Crash(model);
      if (model.getName().equals(name)) {
        return true;
      }
    }
    return false;
  }

  private void check4Crash(ModelRunnable model) {
    if (model.isCrashed()) {
      logger.error("Model has crashed: " + model.getName());
      throw new RuntimeException("Model has crashed", model.crashException());
    }
  }

  /**
   * Returns the statistics from all models.
   * 
   * @return The aggregated statistics for all models
   * @throws InterruptedException
   */
  public String getStatistics() throws InterruptedException {
    StringBuilder statistics = new StringBuilder("Statistics for multiple models");
    for (ModelRunnable model : models) {
      statistics.append("\n\nStatistics for ").append(model.getName()).append(":\n");
      statistics.append(model.mbt.getStatisticsString());
    }
    return statistics.toString();
  }

  public synchronized String getCurrentVertex() {
    return currentVertex;
  }

  public synchronized void setCurrentVertex(String currentVertex) {
    logger.debug("Changing current vertex from: " + this.currentVertex + ", to: " + currentVertex);
    this.currentVertex = currentVertex;
  }

  public String getCurrentRunningModel() {
    for (ModelRunnable model : models) {
      check4Crash(model);
      if (model.getMbt().isRunning()) {
        return model.getName();
      }
    }
    return null;
  }
}
