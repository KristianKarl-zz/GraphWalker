package org.graphwalker.intellij.plugin.visualization;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class GraphWalkerView extends JComponent implements Scrollable, MouseListener, MouseMotionListener {

    private Rectangle myViewRect;
    private Point myStartPoint;
    private Point myTranslate = new Point(0, 0);
    private double myScale = 1.4;
    private boolean myGridVisible = true;
    private int myGridSize = 10;

    public GraphWalkerView() {
        setBackground(Color.WHITE);
        setForeground(Color.GRAY);
        setAutoscrolls(true);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public Rectangle getViewRect() {
        return myViewRect;
    }

    public void setViewRect(Rectangle viewRect) {
        myViewRect = viewRect;
    }

    public double getScale() {
        return myScale;
    }

    public void setScale(double scale) {
        myScale = scale;
    }

    public Point getTranslate() {
        return myTranslate;
    }

    public void setTranslate(Point point) {
        myTranslate = point;
    }

    public void updateTranslate(Point point) {
        myTranslate.move(point.x-myTranslate.x, point.y-myTranslate.y);
    }

    public boolean isGridVisible() {
        return myGridVisible;
    }

    public void setGridVisible(boolean gridVisible) {
        myGridVisible = gridVisible;
    }

    public int getGridSize() {
        return myGridSize;
    }

    public void setGridSize(int gridSize) {
        myGridSize = gridSize;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(3000, 3000);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Rectangle bounds = graphics.getClipBounds();
        if (bounds == null) {
            bounds = getBounds();
        }
        paintBackground(graphics, bounds);
        paintGrid(graphics, bounds);
    }

    private void paintBackground(Graphics graphics, Rectangle bounds) {
        graphics.setColor(getBackground());
        if (null != bounds) {
            graphics.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        } else {
            graphics.fillRect(getX(), getY(), getWidth(), getHeight());
        }
    }
    /*
    private void paintGrid(Graphics graphics) {
        Rectangle clipRect = graphics.getClipBounds();
        Rectangle bounds = getBounds();
        double step = 100;
        if (null != clipRect) {
            bounds = bounds.intersection(clipRect);
        }

        int iLeft = (int)Math.round(bounds.getX());
        int iRight = (int)Math.round(bounds.getX()+bounds.getWidth());
        int iTop = (int)Math.round(bounds.getY());
        int iBottom = (int)Math.round(bounds.getY()+bounds.getHeight());

        graphics.setColor(getForeground());
        for (double x = bounds.getX(); x <= iRight; x += step) {
            int ix = (int)Math.round(x);
            graphics.drawLine(ix, iTop, ix, iBottom);
        }
        for (double y = bounds.getY(); y <= iBottom; y += step) {
            int iy = (int)Math.round(y);
            graphics.drawLine(iLeft, iy, iRight, iy);
        }
    } */

    private void paintGrid(Graphics graphics, Rectangle bounds) {

        double tx = getTranslate().getX() * getScale();
        double ty = getTranslate().getY() * getScale();
        double step = getGridSize() * getScale();

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

        graphics.setColor(getForeground());

        for (double x = xs; x <= xe; x += step) {
            // FIXME: Workaround for rounding errors when adding
            // step to
            // xs or ys multiple times (leads to double grid lines when
            // zoom
            // is set to eg. 121%)
            x = Math.round((x - tx) / step) * step + tx;

            int ix = (int) Math.round(x);
            graphics.drawLine(ix, iys, ix, iye);
        }

        for (double y = ys; y <= ye; y += step) {

            // FIXME: Workaround for rounding errors when adding
            // step to
            // xs or ys multiple times (leads to double grid lines when
            // zoom
            // is set to eg. 121%)
            y = Math.round((y - ty) / step) * step + ty;

            int iy = (int) Math.round(y);
            graphics.drawLine(ixs, iy, ixe, iy);
        }
    }

    public void updateSize(Rectangle viewRect) {
        Dimension dimension = new Dimension(
            Math.max((int)Math.round(getWidth()*getScale()), (int)Math.round(viewRect.getWidth())),
            Math.max((int)Math.round(getHeight()*getScale()), (int)Math.round(viewRect.getHeight()))
        );
        if (!dimension.equals(getPreferredSize())) {
            setViewRect(viewRect);
            setPreferredSize(dimension);
            setMinimumSize(dimension);
            revalidate();
        }
    }
    /*
    @Override
    public void paint(Graphics graphics) {
        //graphics.translate(myTranslate.x, myTranslate.y);
        super.paint(graphics);
        //graphics.translate(-myTranslate.x, -myTranslate.y);
    }
    */

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

    public void mouseDragged(MouseEvent event) {
        if (null != myStartPoint) {
            int dx = event.getX()- myStartPoint.x;
            int dy = event.getY()- myStartPoint.y;
            scrollRectToVisible(new Rectangle(getViewRect().x+(dx>0?0:getViewRect().width)-dx, getViewRect().y+(dy>0?0:getViewRect().height)-dy, 1, 1));
            myStartPoint = event.getPoint();
        }
    }

    public void mouseMoved(MouseEvent event) {
    }

    public void mouseClicked(MouseEvent event) {
    }

    public void mousePressed(MouseEvent event) {
        myStartPoint = event.getPoint();
    }

    public void mouseReleased(MouseEvent event) {
        myStartPoint = null;
    }

    public void mouseEntered(MouseEvent event) {
    }

    public void mouseExited(MouseEvent event) {
    }
}
