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

import org.graphwalker.core.util.Resource;
import org.graphwalker.gui.GraphWalkerGUI;

import javax.swing.*;

public class ToolBar extends JToolBar {

    public ToolBar() {
        createToolBar();
    }

    private void createToolBar() {
        add(createButton("open"));
        add(createButton("reload"));
        add(createButton("first"));
        add(createButton("back"));
        add(createButton("pause"));
        add(createButton("run"));
        add(createButton("next"));
        add(createCheckbox("soap"));
        add(createCheckbox("center"));
    }

    private JButton createButton(String id) {
        JButton button = new JButton();
        button.setIcon(Resource.getIcon(GraphWalkerGUI.BUNDLE, "toolbar.button."+id+".icon"));
        return button;
    }

    private JCheckBox createCheckbox(String id) {
        JCheckBox button = new JCheckBox();

        return button;
    }
}
