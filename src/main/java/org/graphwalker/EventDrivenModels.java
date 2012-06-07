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

package org.graphwalker;

import java.util.Stack;
import java.util.Vector;

import org.apache.log4j.Logger;

public class EventDrivenModels {
  private static Logger logger = Util.setupLogger(EventDrivenModels.class);
  Vector<ModelBasedTesting> models = new Vector<ModelBasedTesting>();
  Stack<ThreadWrapper> pausedModels = new Stack<ThreadWrapper>();
  private Object executionClass = null;
  private ThreadWrapper executingModel = null;
  private static final Object lockbox = new Object();

  public EventDrivenModels(Object executionClass) {
    this.executionClass = executionClass;
  }

  public EventDrivenModels() {}

  public Vector<ModelBasedTesting> getModels() {
    return models;
  }

  public void addModel(ModelBasedTesting model) {
    models.add(model);
  }

  public void runModel(String modelName) {
    stopAndSwitchModel(modelName);
  }

  public void stopAndSwitchModel(String modelName) {
    logger.debug("Will switch to model: " + modelName);
    if (executingModel != null) {
      logger.debug("Stopping model thread: " + executingModel);
      executingModel.getModel().stop();
      executingModel = null;
    }
    for (ModelBasedTesting m : models) {
      if (modelName.equals(m.getGraph().getLabelKey())) {
        logger.debug("Found the model, will now start it: " + m.getGraph().getLabelKey());
        executingModel = new ThreadWrapper(m);
        executingModel.start();
        return;
      }
    }
    throw new RuntimeException("Did not find a model: " + modelName);
  }

  public void pauseAndSwitchModel(String modelName) {
    logger.debug("Will switch to model: " + modelName);
    if (executingModel != null) {
      logger.debug("Suspending model thread: " + executingModel);
      executingModel.getModel().suspend();
      pausedModels.push(executingModel);
      executingModel = null;
    }
    for (ModelBasedTesting m : models) {
      if (modelName.equals(m.getGraph().getLabelKey())) {
        logger.debug("Found the model, will now start it: " + m.getGraph().getLabelKey());
        executingModel = new ThreadWrapper(m);
        executingModel.start();
        return;
      }
    }
    throw new RuntimeException("Did not find a model: " + modelName);
  }

  public void waitToFinish() {
    try {
      while (true) {
        synchronized (lockbox) {
          if (executingModel == null) {
            if (pausedModels.isEmpty()) {
              logger.debug("All threads terminated");
              return;
            }
            logger.debug("Found suspended model: " + pausedModels.peek());
            executingModel = pausedModels.pop();
            executingModel.getModel().resume();
          } else {
            if (executingModel.getState() == Thread.State.TERMINATED) {
              logger.debug("Terminated: " + executingModel);
              executingModel = null;
            }
          }
        }
        Thread.sleep(100);
      }
    } catch (InterruptedException e) {
      Util.logStackTraceToError(e);
    }
  }

  public class ThreadWrapper extends Thread {
    private ModelBasedTesting model;

    public ModelBasedTesting getModel() {
      return model;
    }

    public void setModel(ModelBasedTesting model) {
      this.model = model;
    }

    public ThreadWrapper(ModelBasedTesting model) {
      this.model = model;
      setName(model.getGraph().getLabelKey());
    }

    @Override
    public void run() {
      try {
        model.setCurrentVertex("Start");
        model.reload();
        model.executePath(executionClass);
      } catch (InterruptedException e) {
        Util.logStackTraceToError(e);
      } catch (RuntimeException e) {
        logger.debug(e.getMessage());
      }
    }
  }
}
