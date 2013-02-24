package org.graphwalker.intellij.plugin;

import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class GraphWalkerComponent extends JBScrollPane {

    private final GraphWalkerControl myControl;

    public GraphWalkerComponent(GraphWalkerGraph graph) {
        myControl = new GraphWalkerControl(graph);
        addResizeHandler();
        setViewportView(getControl());
    }

    public GraphWalkerGraph getGraph() {
        return getControl().getGraph();
    }

    public GraphWalkerControl getControl() {
        return myControl;
    }

    private void addResizeHandler() {
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent event) {
                getControl().updateSize(getViewport().getViewRect());
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
            model.setValue((model.getMaximum()-model.getExtent())/2);
        }
    }

}
