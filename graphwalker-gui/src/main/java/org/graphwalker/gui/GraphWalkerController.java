/*
 * #%L
 * GraphWalker GUI
 * %%
 * Copyright (C) 2011 - 2012 GraphWalker
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
package org.graphwalker.gui;

import org.graphwalker.core.GraphWalker;
import org.graphwalker.core.GraphWalkerImpl;
import org.graphwalker.core.configuration.ConfigurationFactory;
import org.graphwalker.gui.events.ControllerEvent;
import org.graphwalker.gui.events.ControllerListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GraphWalkerController {

    private final List<ControllerListener> myControllerListeners = new ArrayList<ControllerListener>();
    private final List<GraphWalker> myInstances = new ArrayList<GraphWalker>();

    GraphWalkerController() {
    }

    public void open(File file) {    //TODO: if the same file is opened twice only one model is created (don't recreate already open configurations)
        addModel(new GraphWalkerImpl(ConfigurationFactory.create(file)));
    }

    private void addModel(GraphWalker model) {
        myInstances.add(model);
        fireInstanceAdded(model);
    }

    public synchronized void addControllerListener(ControllerListener controllerListener) {
        myControllerListeners.add(controllerListener);
    }

    public synchronized void removeControllerListener(ControllerListener controllerListener) {
        myControllerListeners.remove(controllerListener);
    }

    protected void fireInstanceAdded(GraphWalker model) {
        if (myControllerListeners == null) {
            return;
        }
        ControllerEvent event = new ControllerEvent(model);
        for (ControllerListener controllerListener: myControllerListeners) {
            controllerListener.instanceAdded(event);
        }
    }
}
