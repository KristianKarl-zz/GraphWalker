/*
 * #%L
 * GraphWalker Core
 * %%
 * Copyright (C) 2011 GraphWalker
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

package org.graphwalker.core;

import org.apache.log4j.Logger;

import java.util.Stack;
import java.util.Vector;

/**
 * <p>EventDrivenModels class.</p>
 */
public class EventDrivenModels {
    private static Logger logger = Util.setupLogger(EventDrivenModels.class);
    Vector<ModelBasedTesting> models = new Vector<ModelBasedTesting>();
    Stack<ThreadWrapper> pausedModels = new Stack<ThreadWrapper>();
    private Object executionClass = null;
    private ThreadWrapper executingModel = null;
    private static final Object lockbox = new Object();

    /**
     * <p>Constructor for EventDrivenModels.</p>
     *
     * @param executionClass a {@link java.lang.Object} object.
     */
    public EventDrivenModels(Object executionClass) {
        this.executionClass = executionClass;
    }

    /**
     * <p>Constructor for EventDrivenModels.</p>
     */
    public EventDrivenModels() {
    }

    /**
     * <p>Getter for the field <code>models</code>.</p>
     *
     * @return a {@link java.util.Vector} object.
     */
    public Vector<ModelBasedTesting> getModels() {
        return models;
    }

    /**
     * <p>addModel.</p>
     *
     * @param model a {@link org.graphwalker.core.ModelBasedTesting} object.
     */
    public void addModel(ModelBasedTesting model) {
        models.add(model);
    }

    /**
     * <p>runModel.</p>
     *
     * @param modelName a {@link java.lang.String} object.
     */
    public void runModel(String modelName) {
        stopAndSwitchModel(modelName);
    }

    /**
     * <p>stopAndSwitchModel.</p>
     *
     * @param modelName a {@link java.lang.String} object.
     */
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

    /**
     * <p>pauseAndSwitchModel.</p>
     *
     * @param modelName a {@link java.lang.String} object.
     */
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

    /**
     * <p>waitToFinish.</p>
     */
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
