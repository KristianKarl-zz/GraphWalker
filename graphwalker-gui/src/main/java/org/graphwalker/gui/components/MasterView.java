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
package org.graphwalker.gui.components;

import org.graphwalker.core.configuration.Configuration;
import org.graphwalker.core.model.Model;
import org.graphwalker.gui.GraphWalkerController;
import org.graphwalker.gui.events.ControllerEvent;
import org.graphwalker.gui.events.ControllerListener;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.awt.*;

/**
 * <p>MasterView class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class MasterView extends JTree implements ControllerListener {

    private static final Dimension PREFERRED_SIZE = new Dimension(400, 200);

    private final DefaultMutableTreeNode myRootNode;
    private final DefaultTreeModel myTreeModel;
    private final GraphWalkerController myController;

    /**
     * <p>Constructor for MasterView.</p>
     */
    public MasterView(GraphWalkerController controller) {
        myController = controller;
        myController.addControllerListener(this);
        myRootNode = new DefaultMutableTreeNode("Models"); //TODO: Get root node name from properties
        myTreeModel = new DefaultTreeModel(myRootNode);
        setBorder(BorderFactory.createLineBorder(UIManager.getColor("controlShadow")));
        setPreferredSize(PREFERRED_SIZE);
        setRootVisible(false);
        setModel(myTreeModel);
    }

    @Override
    public void instanceAdded(ControllerEvent event) {
        Configuration configuration = event.getInstance().getConfiguration();
        DefaultMutableTreeNode modelNode = new DefaultMutableTreeNode(configuration.getConfigurationFile().getName());
        for (Model model: configuration.getModels()) {
            modelNode.add(new DefaultMutableTreeNode(model.getId()));
        }
        myTreeModel.insertNodeInto(modelNode, myRootNode, myRootNode.getChildCount());

        updateUI();
    }
}
