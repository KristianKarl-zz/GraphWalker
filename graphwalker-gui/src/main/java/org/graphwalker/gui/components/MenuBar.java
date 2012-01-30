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

import org.graphwalker.core.utils.Resource;
import org.graphwalker.gui.Bundle;
import org.graphwalker.gui.actions.ActionConstants;

import javax.swing.*;
import java.util.*;

/**
 * <p>MenuBar class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class MenuBar extends JMenuBar {

    private Map<String, List<Action>> myActionGroups = new HashMap<String, List<Action>>();

    /**
     * <p>Constructor for MenuBar.</p>
     *
     * @param actions a {@link java.util.List} object.
     */
    public MenuBar(List<Action> actions) {
        createActionGroups(actions);
        createMenuBar();
    }
    
    private void createActionGroups(List<Action> actions) {
        for (Action action: actions) {
            String group = (String)action.getValue(ActionConstants.GROUP);
            if (null != group && !"".equals(group)) {
                if (!myActionGroups.containsKey(group)) {
                    myActionGroups.put(group, new ArrayList<Action>());
                }
                addActionToGroup(myActionGroups.get(group), action);
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

    private void createMenuBar() {
        String groupOrderString = Resource.getText(Bundle.NAME, "menubar.group.order");
        String[] groupOrder = groupOrderString.split(",");
        for (String groupKey : groupOrder) {
            Collections.reverse(myActionGroups.get(groupKey.trim()));
            createMenuGroup(groupKey, myActionGroups.get(groupKey.trim()));
        }
    }

    private void createMenuGroup(String group, List<Action> actions) {
        JMenu menu = new JMenu(group);
        for (Action action: actions) {
            menu.add(new JMenuItem(action));
        }
        add(menu);
    }

}
