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
import org.graphwalker.core.util.Resource;
import org.graphwalker.gui.GraphWalker;
import org.graphwalker.gui.actions.ActionConstants;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>ToolBar class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class ToolBar extends JToolBar {

    private Map<String, List<Action>> myActionGroups = new HashMap<String, List<Action>>();

    /**
     * <p>Constructor for ToolBar.</p>
     *
     * @param actions a {@link java.util.List} object.
     */
    public ToolBar(List<Action> actions) {
        createActionGroups(actions);
        createToolBar();
    }

    private void createActionGroups(List<Action> actions) {
        for (Action action: actions) {
            String groupKey = (String)action.getValue(ActionConstants.GROUP);
            if (null != groupKey && !"".equals(groupKey)) {
                if (!myActionGroups.containsKey(groupKey)) {
                    myActionGroups.put(groupKey, new ArrayList<Action>());
                }
                addActionToGroup(myActionGroups.get(groupKey), action);
            }
        }
    }
    
    private void addActionToGroup(List<Action> group, Action action) {
        if (group.isEmpty()) {
            group.add(action);                
        } else {
            Integer actionIndex = (Integer)action.getValue(ActionConstants.INDEX);
            Integer lastActionIndex = (Integer)group.get(group.size()-1).getValue(ActionConstants.INDEX);
            if (actionIndex >= lastActionIndex) {
                group.add(action);
            } else {
                for (int i=0; i<group.size(); i++) {
                    Integer actualActionIndex = (Integer)group.get(i).getValue(ActionConstants.INDEX);
                    if (actionIndex <= actualActionIndex) {
                        group.add(i, action);
                        break;
                    }
                }
            }
        }   
    }
    
    private void createToolBar() {
        String groupOrderString = Resource.getText(GraphWalker.BUNDLE, "toolbar.group.order");
        String[] groupOrder = groupOrderString.split(",");        
        for (int i=0; i<groupOrder.length; i++) {
            createButtonGroup(myActionGroups.get(groupOrder[i].trim()));
            if (i<groupOrder.length-1) {
                addSeparator();
            }
        }
    }

    private void createButtonGroup(List<Action> actions) {
        for (Action action: actions) {
            add(createButton(action));
        }
    }

    private JideButton createButton(Action action) {
        final JideButton button = new JideButton(action);
        if (null != button.getIcon()) {
            button.setText("");
        }
        button.setHorizontalAlignment(SwingConstants.LEADING);
        button.setFocusable(false);
        return button;
    }    

}
