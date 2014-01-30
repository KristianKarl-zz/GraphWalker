package org.graphwalker.webrenderer.geometry;

/**
 * @author Nils Olsson
 */
public class Label {

    private int x;
    private int y;
    private String text;

    public Label(String text, int x, int y) {
        this.x = x;
        this.y = y;
        this.text = text;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getText() {
        return text;
    }
}
