package org.graphwalker.intellij.plugin.visualization;

import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class GraphWalkerScrollPane extends JBScrollPane {

    public GraphWalkerScrollPane() {
        super(new GraphWalkerView());
        addResizeHandler();
    }

    public GraphWalkerView getView() {
        return (GraphWalkerView)getViewport().getView();
    }

    private void addResizeHandler() {
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent event) {
                getView().updateSize(getViewport().getViewRect());
                centerScrollBars();
            }
        });
    }

    private void centerScrollBars() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                centerScrollBar(getHorizontalScrollBar());
                centerScrollBar(getVerticalScrollBar());
            }
        });
    }

    private void centerScrollBar(JScrollBar scrollBar) {
        if (null != scrollBar) {
            BoundedRangeModel model = scrollBar.getModel();
            model.setValue(Math.round((model.getMaximum()-model.getExtent())/2));
        }
    }
}
