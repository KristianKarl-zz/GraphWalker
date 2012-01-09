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

import com.jidesoft.swing.JideScrollPane;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * <p>Abstract AbstractView class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public abstract class AbstractView extends JPanel {

    private final ToolBar myToolBar = new ToolBar();
    private final JScrollPane myScrollPane = new JideScrollPane();
    private JComponent myComponent;

    AbstractView(JComponent component) {
        setBorder(BorderFactory.createLineBorder(UIManager.getColor("controlShadow")));
        setLayout(new BorderLayout());
        add(myToolBar, BorderLayout.NORTH);
        myScrollPane.setBorder(null);
        add(myScrollPane, BorderLayout.CENTER);
        setComponent(component);
    }

    AbstractView(JComponent component, ToolBarPlacement placement) {
        this(component);
        setToolBarPlacement(placement);
    }

    /**
     * <p>setToolBarPlacement.</p>
     *
     * @param placement a {@link org.graphwalker.gui.components.AbstractView.ToolBarPlacement} object.
     */
    public void setToolBarPlacement(ToolBarPlacement placement) {
        remove(myToolBar);
        switch (placement) {
            case NORTH: {
                add(myToolBar, BorderLayout.NORTH);
                myToolBar.setOrientation(ToolBar.HORIZONTAL);
                myToolBar.setVisible(true);
            }
            break;
            case EAST: {
                add(myToolBar, BorderLayout.EAST);
                myToolBar.setOrientation(ToolBar.VERTICAL);
                myToolBar.setVisible(true);
            }
            break;
            case SOUTH: {
                add(myToolBar, BorderLayout.SOUTH);
                myToolBar.setOrientation(ToolBar.HORIZONTAL);
                myToolBar.setVisible(true);
            }
            break;
            case WEST: {
                add(myToolBar, BorderLayout.WEST);
                myToolBar.setOrientation(ToolBar.VERTICAL);
                myToolBar.setVisible(true);
            }
            break;
            case NONE: {
                myToolBar.setVisible(false);
            }
            break;
        }
    }
    
    /**
     * <p>addActionGroup.</p>
     *
     * @param actionGroup a {@link java.util.List} object.
     */
    public void addActionGroup(List<Action> actionGroup) {
        myToolBar.addActionGroup(actionGroup);
    }

    /**
     * <p>getComponent.</p>
     *
     * @return a {@link javax.swing.JComponent} object.
     */
    public JComponent getComponent() {
        return myComponent;
    }

    /**
     * <p>setComponent.</p>
     *
     * @param component a {@link javax.swing.JComponent} object.
     */
    public void setComponent(JComponent component) {
        if (null != myComponent) {
            myScrollPane.getViewport().remove(myComponent);
        }
        myComponent = component;
        if (null != myComponent) {
            myScrollPane.getViewport().add(myComponent);
        }
    }
    
    public enum ToolBarPlacement {
        NORTH, EAST, SOUTH, WEST, NONE
    }   
}
