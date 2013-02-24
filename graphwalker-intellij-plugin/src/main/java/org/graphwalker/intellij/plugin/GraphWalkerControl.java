package org.graphwalker.intellij.plugin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GraphWalkerControl extends JComponent implements Scrollable,MouseWheelListener, MouseListener, MouseMotionListener {

    private final GraphWalkerGraph myGraph;
    private final RenderingHints myRenderingHints;

    //private int myGridSize = 10;

    private Point myStartPoint;
    private Rectangle myViewRect;
    private double myTranslateX = 0;
    private double myTranslateY = 0;
    private double myDeltaX = 0;
    private double myDeltaY = 0;
    private int myOffsetX = -1;
    private int myOffsetY = -1;
    private double myScale = 1.4;

    public GraphWalkerControl(GraphWalkerGraph graph) {
        myGraph = graph;
        myRenderingHints = new RenderingHints(null);
        myRenderingHints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        myRenderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        myRenderingHints.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        addMouseWheelListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);
        setOpaque(true);
        setDoubleBuffered(true);

        setBackground(Color.ORANGE);
    }

    public Rectangle getViewRect() {
        return myViewRect;
    }

    public void setViewRect(Rectangle viewRect) {
        myViewRect = viewRect;
    }

    public GraphWalkerGraph getGraph() {
        return myGraph;
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D)graphics;
        graphics2D.setRenderingHints(myRenderingHints);
        Rectangle bounds = graphics2D.getClipBounds();
        if (bounds == null) {
            bounds = getBounds();
        }
        paintBackground(graphics2D, bounds);
        paintGrid(graphics2D, bounds);
    }

    private void paintBackground(Graphics2D graphics2D, Rectangle bounds) {
        graphics2D.setColor(getBackground());
        if (null != bounds) {
            graphics2D.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        } else {
            graphics2D.fillRect(getX(), getY(), getWidth(), getHeight());
        }
    }

    private void paintGrid(Graphics2D graphics2D, Rectangle bounds) {
         /*
        double tx = myTranslateX * myScale;
        double ty = myTranslateY * myScale;
        double step = 10 * myScale;

        double xs = Math.floor((bounds.getX() - tx) / step) * step + tx;
        double xe = Math.ceil(bounds.getX() + bounds.getWidth() / step) * step;
        double ys = Math.floor((bounds.getY() - ty) / step) * step + ty;
        double ye = Math.ceil(bounds.getY() + bounds.getHeight() / step) * step;

        xe += (int) Math.ceil(step);
        ye += (int) Math.ceil(step);

        int ixs = (int) Math.round(xs);
        int ixe = (int) Math.round(xe);
        int iys = (int) Math.round(ys);
        int iye = (int) Math.round(ye);

        graphics2D.setColor(getForeground());

        for (double x = xs; x <= xe; x += step) {
            // FIXME: Workaround for rounding errors when adding
            // step to
            // xs or ys multiple times (leads to double grid lines when
            // zoom
            // is set to eg. 121%)
            x = Math.round((x - tx) / step) * step + tx;

            int ix = (int) Math.round(x);
            graphics2D.drawLine(ix, iys, ix, iye);
        }

        for (double y = ys; y <= ye; y += step) {

            // FIXME: Workaround for rounding errors when adding
            // step to
            // xs or ys multiple times (leads to double grid lines when
            // zoom
            // is set to eg. 121%)
            y = Math.round((y - ty) / step) * step + ty;

            int iy = (int) Math.round(y);
            graphics2D.drawLine(ixs, iy, ixe, iy);
        } */

        /*
        Font font = new Font("Arial", Font.PLAIN, 10);
        graphics2D.setFont(font);
        graphics2D.setPaint(Color.BLACK);
        graphics2D.drawString("Scale  : " + myScale, -getX() + 100, -getY() + 100);
        graphics2D.drawString("X      : " + myTranslateX, -getX() + 100, -getY() + 112);
        graphics2D.drawString("Y      : " + myTranslateY, -getX() + 100, -getY() + 124);
        graphics2D.drawString("DeltaX : "+myDeltaX, -getX()+100, -getY()+136);
        graphics2D.drawString("DeltaY : "+myDeltaY, -getX()+100, -getY()+148);
        graphics2D.drawString("OffsetX: "+myOffsetX, -getX()+100, -getY()+160);
        graphics2D.drawString("OffsetY: "+myOffsetY, -getX()+100, -getY()+172);
        */

        graphics2D.setColor(Color.cyan);
        graphics2D.fillOval(100,100,100,100);
    }

    public void updateSize(Rectangle viewRect) {
        Rectangle bounds = getGraph().getBounds();
        Dimension dimension = new Dimension(
                (int)Math.round(bounds.getX()+bounds.getWidth()),
                (int)Math.round(bounds.getY()+bounds.getHeight()));

        if (!dimension.equals(getPreferredSize())) {
            setViewRect(viewRect);
            setPreferredSize(dimension);
            setMinimumSize(dimension);
            revalidate();
        }
    }

    public void mouseWheelMoved(MouseWheelEvent event) {
        /*
        if (MouseWheelEvent.WHEEL_UNIT_SCROLL == event.getScrollType()) {
            myScale += (.1 * event.getWheelRotation());
            myScale = Math.max(0.00001, myScale);
            updateSize();
            repaint();
        }
        */
    }

    public void mouseClicked(MouseEvent event) {
    }

    public void mousePressed(MouseEvent event) {
        if (!event.isConsumed()) {
            myOffsetX = event.getX();
            myOffsetY = event.getY();
        }
    }

    public void mouseReleased(MouseEvent event) {
        if (!event.isConsumed()) {
            myOffsetX = -1;
            myOffsetY = -1;
            event.consume();
        }
    }

    public void mouseEntered(MouseEvent event) {
    }

    public void mouseExited(MouseEvent event) {
    }

    public void mouseDragged(MouseEvent event) {
        if (!event.isConsumed() && -1 != myOffsetX && -1 != myOffsetY) {
            int dx = (int)Math.round(0.7*(myOffsetX-event.getX()));
            int dy = (int)Math.round(0.7*(myOffsetY-event.getY()));

            Rectangle visibleRect = getVisibleRect();
            visibleRect.translate(dx, dy);

            scrollRectToVisible(visibleRect);

            myOffsetX += dx;
            myOffsetY += dy;

            event.consume();
        }
    }

    public void mouseMoved(MouseEvent event) {
    }

    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 1;
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 1;
    }

    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}
