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

import com.jidesoft.swing.JideButton;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>ToolBar class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class ToolBar extends JToolBar {

    private List<List<Action>> myActionGroups = new ArrayList<List<Action>>();
    
    /**
     * <p>Constructor for ToolBar.</p>
     */
    public ToolBar() {
        setBorder(null);
        setFloatable(false);
    }

    /**
     * <p>addActionGroup.</p>
     *
     * @param actionGroup a {@link java.util.List} object.
     */
    public void addActionGroup(List<Action> actionGroup) {
        if (0 < myActionGroups.size()) {
            addSeparator();    
        }
        for (Action action: actionGroup) {
            add(createButton(action));
        }
        myActionGroups.add(actionGroup);
    }

    private JButton createButton(Action action) {
        final JideButton button = new JideButton(action);
        if (null != button.getIcon()) {
            button.setText("");
        }
        button.setHorizontalAlignment(SwingConstants.LEADING);
        button.setFocusable(false);
        return button;
    }    

}
