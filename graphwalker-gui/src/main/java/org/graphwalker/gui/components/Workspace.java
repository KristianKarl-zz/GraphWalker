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

import com.jidesoft.swing.JideSplitPane;

import javax.swing.*;
import java.awt.*;

public class Workspace extends JPanel {

    private final MasterView myMasterView = new MasterView();
    private final DetailView myDetailView = new DetailView();
    private final ConsoleView myConsoleView = new ConsoleView();

    public Workspace() {
        super(new GridLayout(1,1));
        setBorder(BorderFactory.createEmptyBorder(0, 2, 2, 2));
        JideSplitPane hSplitPane = createSplitPane(JideSplitPane.HORIZONTAL_SPLIT, myMasterView, myDetailView);
        JideSplitPane vSplitPane = createSplitPane(JideSplitPane.VERTICAL_SPLIT, hSplitPane, myConsoleView);
        add(vSplitPane);
    }

    private JideSplitPane createSplitPane(int orientation, Component firstComponent, Component secondComponent) {
        JideSplitPane splitPane = new JideSplitPane(orientation);
        splitPane.setOneTouchExpandable(true);
        splitPane.setShowGripper(true);
        splitPane.add(firstComponent);
        splitPane.add(secondComponent);
        return splitPane;
    }    
    
}
