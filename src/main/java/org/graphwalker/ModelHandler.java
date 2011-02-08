/*
 * The MIT License
 *
 * Copyright 2011 krikar.
 *
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
 */
package org.graphwalker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.apache.log4j.Logger;
import org.graphwalker.graph.Graph;
import org.graphwalker.graph.Vertex;

/**
 * The ModelHandler handles multiple models. The basic workflow using this class
 * is:
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
 * New keywords and datum is introduced to handle multiple models. A model can
 * now switch execution into another model using the keyword
 * {@link Keywords#SWITCH_MODEL SWITCH_MODEL}. This means that the current model
 * is paused, and any other loaded model(s) with the same
 * {@link Keywords#GRAPH_VERTEX GRAH_VERTEX} is a candidate to switch into. Also
 * the current paused model can be resumed.
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

  private class ModelRunnable implements Runnable {

    private String name;
    private ModelBasedTesting mbt;
    private Object modelAPI;

    public ModelRunnable(String name, ModelBasedTesting mbt, Object modelAPI) {
      this.name = name;
      this.mbt = mbt;
      this.modelAPI = modelAPI;
    }

    @Override
    public void run() {
      try {
        logger.debug("Will start executing the model: " + this.mbt.getGraph());
        mbt.executePath(modelAPI);
      } catch (Exception e) {
        Util.logStackTraceToError(e);
      }
    }

    public String getName() {
      return name;
    }

    public ModelBasedTesting getMbt() {
      return mbt;
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
   * @param name
   *          The name of the model. This is not the same as the name of the
   *          {@link Graph#getLabelKey() graph}. It's a logical name of the
   *          model, and it may not already be used by the handler.
   * @param mbt
   *          The model.
   * @param object
   *          The model's java class implementing the API for the model.
   */
  public synchronized void add(String name, ModelBasedTesting mbt, Object object) {
    if (hasModel(name)) {
      throw new IllegalArgumentException("The model name " + name + " has already been used.");
    }
    logger.debug("Adding the model: " + Integer.toHexString(System.identityHashCode(mbt)) + ", " + mbt.getGraph());
    mbt.setOverrideStopCondition(true);
    mbt.setMultiModelRun(true);
    models.add(new ModelRunnable(name, mbt, object));
  }

  /**
   * Removed a model from the handler.
   * 
   * @param model
   *          Removes the specified model from the handler.
   */
  public void remove(ModelRunnable model) {
    models.remove(model);
  }

  /**
   * Removed a model from the handler.
   * 
   * @param index
   *          Removes the specified model by index from the handler.
   */
  public void remove(long index) {
    models.remove((int) index);
  }

  /**
   * Starts executing the models. The execution is finished when all the models
   * stop criteria are reached.
   * 
   * @param name
   *          The logical name of the model which will start the execution.
   * @throws InterruptedException
   */
  public void execute(String name) throws InterruptedException {
    if (!hasModel(name)) {
      throw new IllegalArgumentException("The model name " + name + " has already been used.");
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
        logger.debug("All models has reached their desired stop conditions.");
        break;
      }

      // Do we have any paused model?
      ArrayList<ModelRunnable> pausedModels = getPausedModel();
      if (pausedModels.isEmpty()) {
        logger.error("Not all models are finished, but no model is running, and none is paused. What is happening here?");
        break;
      }

      // Get from random paused model, the vertex at which the model is paused.
      Vertex v = pausedModels.get(random.nextInt(pausedModels.size())).getMbt().getCurrentVertex();
      if (v == null) {
        logger.error("Did not expect the vertex to be null!");
        break;
      }

      // Now, find models with matching vertex name.
      // If the model is paused, the current vertex is matched.
      // If the model is not started, the graph name is matched.
      ArrayList<ModelRunnable> pausedAndNotStartedModels = getModel(v);
      if (pausedAndNotStartedModels.isEmpty()) {
        logger.debug("Did not find any models, matching the vertex: " + v.getLabelKey());
        break;
      }

      // Run the model, but check for it's state. The model is either
      // suspended or not started.
      int selectModel = random.nextInt(pausedAndNotStartedModels.size());
      model = pausedAndNotStartedModels.get(selectModel);
      logger.debug("Number of models to select from: " + pausedAndNotStartedModels.size());
      logger.debug("Selecting model(" + selectModel + ") " + model.getMbt().getGraph());
      if (model.getMbt().isSuspended()) {
        model.getMbt().resume();
      } else if (!model.getMbt().isFinished()) {
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
      if (model.getMbt().isRunning()) {
        break;
      } else if (model.getMbt().isSuspended()) {
        break;
      } else if (model.getMbt().isFinished()) {
        break;
      }
      Thread.sleep(10);
    }
  }

  /**
   * Searches for any model with matching vertex name. If the model is paused,
   * the current vertex is matched. If the model is not started, the graph name
   * is matched.
   * 
   * @param v
   *          The vertex to match
   * @return an array of models that matches
   */
  private ArrayList<ModelRunnable> getModel(Vertex v) {
    logger.debug("Looking for paused or not started model matching vertex: " + v.getLabelKey());
    ArrayList<ModelRunnable> m = new ArrayList<ModelRunnable>();
    Iterator<ModelRunnable> itr = models.iterator();
    while (itr.hasNext()) {
      ModelRunnable model = itr.next();
      if (model.getMbt().hasNotStartedExecution()) {
        if (model.getMbt().getGraph().getLabelKey().equals(v.getLabelKey())) {
          logger.debug("  Adding not started model " + model.getMbt().getGraph());
          m.add(model);
        }
      } else if (model.getMbt().isSuspended()) {
        if (model.getMbt().getCurrentVertex().getLabelKey().equals(v.getLabelKey())) {
          logger.debug("  Adding paused model " + model.getMbt().getGraph());
          m.add(model);
        }
      }
    }
    return m;
  }

  /**
   * Gets a model by it's logical name.
   * 
   * @param name
   *          The logical name of a model
   * @return The model that matches the logical name.
   */
  private ModelRunnable getModel(String name) {
    Iterator<ModelRunnable> itr = models.iterator();
    while (itr.hasNext()) {
      ModelRunnable model = itr.next();
      if (model.getName().equals(name)) {
        return model;
      }
    }
    return null;
  }

  /**
   * Searches for paused models
   * 
   * @return An array of paused models
   */
  private ArrayList<ModelRunnable> getPausedModel() {
    logger.debug("Looking for paused models");
    ArrayList<ModelRunnable> pausedModels = new ArrayList<ModelRunnable>();
    Iterator<ModelRunnable> itr = models.iterator();
    while (itr.hasNext()) {
      ModelRunnable model = itr.next();
      if (model.getMbt().isSuspended()) {
        logger.debug("  Adding paused model " + model.getMbt().getGraph());
        pausedModels.add(model);
      }
    }
    return pausedModels;
  }

  /**
   * Searches the handler for a model in a running state.
   * 
   * @return True if any model is in a running state, else false will be
   *         returned.
   */
  private boolean isAnyModelRunning() {
    Iterator<ModelRunnable> itr = models.iterator();
    while (itr.hasNext()) {
      ModelRunnable model = itr.next();
      if (model.getMbt().isRunning()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks whether the models of the handler has executed.
   * 
   * @return True if all models has reached the stop criteria, else false is
   *         returned.
   */
  /**
   * @return
   */
  public boolean isAllModelsDone() {
    Iterator<ModelRunnable> itr = models.iterator();
    while (itr.hasNext()) {
      ModelRunnable task = itr.next();
      if (task.mbt.hasNext()) {
        return false;
      }
    }
    return true;
  }

  /**
   * Checks if the handler has a model with the same logical name already
   * loaded.
   * 
   * @param name
   *          The logical name of the model
   * @return True if the handler already has a model with matching logical name.
   */
  private boolean hasModel(String name) {
    Iterator<ModelRunnable> itr = models.iterator();
    while (itr.hasNext()) {
      ModelRunnable task = itr.next();
      if (task.getName().equals(name)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the statistics from all models.
   * 
   * @return The aggregated statistics for all models
   */
  public String getStatistics() throws InterruptedException {
    StringBuffer statistics = new StringBuffer("Statistics for multiple models");
    Iterator<ModelRunnable> itr = models.iterator();
    while (itr.hasNext()) {
      ModelRunnable model = itr.next();
      statistics.append("\n\nStatistics for " + model.getName() + ":\n");
      statistics.append(model.mbt.getStatisticsString());
    }
    return statistics.toString();
  }
}
