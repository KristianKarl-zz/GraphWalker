package org.graphwalker.intellij.plugin;

import com.intellij.ui.ScrollPaneFactory;

import javax.swing.*;
import java.awt.*;

public class GraphWalkerFileEditorComponent {

    private JScrollPane myScrollPane;
    private JComponent component;

    public GraphWalkerFileEditorComponent() {
        component = new JPanel();
        component.setBackground(Color.ORANGE);
        myScrollPane = ScrollPaneFactory.createScrollPane(component);
    }

    public JComponent getComponent() {
        return myScrollPane;
    }

    public JComponent getPreferredFocusedComponent() {
        return myScrollPane; // tmp
    }

    public boolean isModified() {
        return false;
    }
}
