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

import net.miginfocom.swing.MigLayout;
import org.graphwalker.core.util.Resource;
import org.graphwalker.gui.actions.*;
import org.graphwalker.gui.components.MenuBar;
import org.graphwalker.gui.components.StatusBar;
import org.graphwalker.gui.components.ToolBar;
import org.graphwalker.gui.components.Workspace;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GraphWalkerView extends JFrame {

    private static final Dimension PREFERRED_SIZE = new Dimension(800, 700);

    private final GraphWalkerController myController;
    private final List<Action> myActions = new ArrayList<Action>();
    private final Workspace myWorkspace;
    private final StatusBar myStatusBar = new StatusBar();

    GraphWalkerView(GraphWalkerController controller) {
        super(Resource.getText(GraphWalker.BUNDLE, "application.label"));
        myController = controller;
        myWorkspace = new Workspace(myController);
        getContentPane().setLayout(new MigLayout("fill"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(PREFERRED_SIZE);
        setLocationRelativeTo(null);
        createActions();
        createComponents();
        pack();
    }

    public GraphWalkerController getController() {
        return myController;
    }

    private void createActions() {
        myActions.add(new BackAction(this));
        myActions.add(new ExitAction(this));
        myActions.add(new FirstAction(this));
        myActions.add(new NextAction(this));
        myActions.add(new OpenAction(this));
        myActions.add(new PauseAction(this));
        myActions.add(new ReloadAction(this));
        myActions.add(new RunAction(this));
    }

    private void createComponents() {
        addMenu();
        addToolBar();
        addWorkspace();
        addStatusBar();
    }

    private void addMenu() {
        setJMenuBar(new MenuBar(myActions));
    }

    private void addToolBar() {
        JToolBar toolBar = new ToolBar(myActions);
        toolBar.setFloatable(false);
        getContentPane().add(toolBar, "north");
    }

    private void addStatusBar() {
        getContentPane().add(myStatusBar, "south");
    }

    private void addWorkspace() {
        getContentPane().add(myWorkspace, "grow");
    }
}
