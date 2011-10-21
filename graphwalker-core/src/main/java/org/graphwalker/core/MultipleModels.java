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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * A class that handles multiple instances of ModelBasedTesting models and can
 * execute them in parallel. This is only usable for running online since
 * multiple models will be executed at the same time. The method is to add a
 * model and then execute it when appropriate.
 *
 * @author Ola Sundin
 * @see ModelBasedTesting
 * @version $Id: $
 */
public class MultipleModels {
    /**
     * The maximum number of names of the same base.
     */
    private static final int MAX_NAMES = 1000;

    private static MultipleModels instance;
    private HashMap<String, ModelBasedTesting> models;
    private HashMap<String, ModelBasedTesting> executingModels;
    private HashMap<String, ModelBasedTesting> finishedModels;
    private LinkedHashSet<String> reservedNames;

    /**
     * The constructor is private so only one instance exists of the object.
     */
    private MultipleModels() {
        models = new HashMap<String, ModelBasedTesting>();
        executingModels = new HashMap<String, ModelBasedTesting>();
        finishedModels = new HashMap<String, ModelBasedTesting>();
        reservedNames = new LinkedHashSet<String>();
    }

    /**
     * A static method of getting the only instance of the class. If the instance
     * is not initialized a new instance is created.
     *
     * @return the only instance of the class
     */
    public static synchronized MultipleModels getInstance() {
        if (instance == null) {
            instance = new MultipleModels();
        }
        return instance;
    }

    /**
     * Creating new lists for the models. If an instance of this class is to be
     * 're-used', it may need to clear the lists calling this method.
     */
    public void reset() {
        models = new HashMap<String, ModelBasedTesting>();
        executingModels = new HashMap<String, ModelBasedTesting>();
        finishedModels = new HashMap<String, ModelBasedTesting>();
        reservedNames = new LinkedHashSet<String>();
    }

    /**
     * Returns the ModelBasedTesting object for the submitted key. If not such key
     * was found null is returned.
     *
     * @param modelName the key which corresponds to the wanted model
     * @return the ModelBasedTesting object for that key or null if key was not
     *         found
     */
    public ModelBasedTesting getModel(String modelName) {
        return models.get(modelName);
    }

    /**
     * Adds a new model to the set of models. If the submitted name already exists
     * in the set an exception is thrown. In order to guarantee uniqueness of the
     * model name use the helper function {@link #getUniqueName(String)}.
     *
     * @param modelName the name of the model
     * @param modelName the name of the model
     * @param modelName the name of the model
     * @param modelName the name of the model
     * @param modelName the name of the model
     * @param modelName the name of the model
     * @param model     the model to be added
     * @throws java.lang.IllegalArgumentException if the given name has all ready been used.
     */
    public synchronized void addModel(String modelName, ModelBasedTesting model) throws IllegalArgumentException {
        if (models.containsKey(modelName)) {
            throw new IllegalArgumentException("The model name " + modelName + " has already been used.");
        }
        if (!reservedNames.contains(modelName)) {
            reservedNames.add(modelName);
        }
        models.put(modelName, model);
    }

    /**
     * Checks whether a model is executing or not.
     *
     * @param modelName the name of the model
     * @return true if the model is being executed
     */
    public boolean isExecuting(String modelName) {
        return executingModels.containsKey(modelName);
    }

    /**
     * Checks whether a model is finished or not.
     *
     * @param modelName the name of the model
     * @return true if the model is finished
     */
    public boolean isFinished(String modelName) {
        return finishedModels.containsKey(modelName);
    }

    /**
     * Checks if all models are finished.
     *
     * @return true if all models are finished
     */
    public boolean isFinished() {
        return finishedModels.size() > 0 && executingModels.size() == 0;
    }

    /**
     * Returns the statistics from all models. This methods is blocking and will
     * wait until all models are finished before returning
     *
     * @return the aggregated statistics for all models
     * @throws java.lang.InterruptedException if any.
     */
    public String getStatistics() throws InterruptedException {
        StringBuffer statistics = new StringBuffer("Statistics for multiple models");
        while (!isFinished()) {
            Thread.sleep(10);
        }

        Iterator<String> iterator = finishedModels.keySet().iterator();
        while (iterator.hasNext()) {
            String currentModelName = iterator.next();
            statistics.append("\n\nStatistics for " + currentModelName + ":\n");
            statistics.append(finishedModels.get(currentModelName).getStatisticsString());
        }
        return statistics.toString();
    }

    /**
     * Will start the execution of the given model in a separate thread.
     *
     * @param modelName the name of the model to start the execution for
     * @param modelAPI  an instance of the class that the model should call
     * @throws java.lang.IllegalArgumentException if no model with the given name exists
     * @throws java.lang.IllegalStateException    if the given model is already being executed
     */
    public void executeModel(String modelName, Object modelAPI) throws IllegalArgumentException, IllegalStateException {
        if (!models.containsKey(modelName)) {
            throw new IllegalArgumentException("No model found with the name " + modelName);
        }
        if (executingModels.containsKey(modelName)) {
            throw new IllegalStateException("Model is already executing, spawn a new model if another instance of the model should be executed.");
        }
        executingModels.put(modelName, models.get(modelName));
        Thread t = new Thread(new ModelThread(modelName, models.get(modelName), modelAPI));
        t.start();
    }

    /**
     * Marks the model as finished
     *
     * @param modelName the name of the model to finish
     */
    private void finishModel(String modelName) {
        executingModels.remove(modelName);
        finishedModels.put(modelName, models.get(modelName));
    }

    /**
     * Marks a model as interrupted
     *
     * @param modelName the name of the model that was interrupted
     */
    private void interruptModel(String modelName) {
        // TODO Add interruption handling
        executingModels.remove(modelName);
    }

    /**
     * Helper method to create unique names in a Set of keys. The format of the
     * names will be <i>&lt;name&gt;_&lt;index&gt;</i>. The first name will be the
     * name only and the second will get the index 1.
     *
     * @param desiredName  the desired name
     * @return the actual name that is unique, could be the same as the desired
     *         name
     * @throws java.lang.IndexOutOfBoundsException if any.
     */
    public synchronized String getUniqueName(String desiredName) throws IndexOutOfBoundsException {
        String actualName = desiredName;
        for (int i = 1; reservedNames.contains(actualName); i++) {
            if (i >= MAX_NAMES) {
                throw new IndexOutOfBoundsException("To many names based on " + desiredName + " has been reserved.");
            }
            actualName = desiredName + " " + i;
        }
        reservedNames.add(actualName);
        return actualName;
    }

    /**
     * Private class for execution of models in a seperate thread.
     *
     * @author Ola Sundin
     */
    private class ModelThread implements Runnable {
        private String modelName;
        private ModelBasedTesting model;
        private Object modelAPI;

        public ModelThread(String modelName, ModelBasedTesting model, Object modelAPI) {
            this.modelName = modelName;
            this.model = model;
            this.modelAPI = modelAPI;
        }

        @Override
        public void run() {
            try {
                model.executePath(modelAPI);
                finishModel(modelName);
            } catch (InterruptedException e) {
                interruptModel(modelName);
                Thread.currentThread().interrupt();
            }

        }

    }

}
