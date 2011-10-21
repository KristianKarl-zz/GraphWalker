/*
 * #%L
 * GraphWalker Core
 * %%
 * Copyright (C) 2011 GraphWalker
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

package org.graphwalker.core.graph;

import org.apache.log4j.Logger;
import org.graphwalker.core.Keywords;
import org.graphwalker.core.Util;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Vertex class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class Vertex extends AbstractElement {

    static Logger logger = Util.setupLogger(Vertex.class);
    private String motherStartVertexKey = "";
    private String subGraphStartVertexKey = "";
    private Color fillColor = new Color(0);
    private Point2D location = new Point2D.Float();
    private float width = 0;
    private float height = 0;
    private boolean switchModelKey = false;
    private boolean graphVertex = false;

    /**
     * <p>Getter for the field <code>width</code>.</p>
     *
     * @return a float.
     */
    public float getWidth() {
        return width;
    }

    /**
     * <p>Setter for the field <code>width</code>.</p>
     *
     * @param width a float.
     */
    public void setWidth(float width) {
        this.width = width;
    }

    /**
     * <p>Getter for the field <code>height</code>.</p>
     *
     * @return a float.
     */
    public float getHeight() {
        return height;
    }

    /**
     * <p>Setter for the field <code>height</code>.</p>
     *
     * @param height a float.
     */
    public void setHeight(float height) {
        this.height = height;
    }

    /**
     * <p>Setter for the field <code>location</code>.</p>
     *
     * @param location a {@link java.awt.geom.Point2D} object.
     */
    public void setLocation(Point2D location) {
        this.location = location;
    }

    /**
     * <p>Getter for the field <code>location</code>.</p>
     *
     * @return a {@link java.awt.geom.Point2D} object.
     */
    public Point2D getLocation() {
        return location;
    }

    /**
     * <p>Getter for the field <code>fillColor</code>.</p>
     *
     * @return a {@link java.awt.Color} object.
     */
    public Color getFillColor() {
        return fillColor;
    }

    /**
     * <p>Setter for the field <code>fillColor</code>.</p>
     *
     * @param fillColor a {@link java.awt.Color} object.
     */
    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    /**
     * <p>Constructor for Vertex.</p>
     */
    public Vertex() {
        super();
    }

    /**
     * <p>Constructor for Vertex.</p>
     *
     * @param vertex a {@link org.graphwalker.core.graph.Vertex} object.
     */
    public Vertex(Vertex vertex) {
        super(vertex);
        this.motherStartVertexKey = vertex.motherStartVertexKey;
        this.subGraphStartVertexKey = vertex.subGraphStartVertexKey;
        this.fillColor = vertex.fillColor;
        this.location = vertex.location;
        this.width = vertex.width;
        this.height = vertex.height;
        this.switchModelKey = vertex.switchModelKey;
        this.graphVertex = vertex.graphVertex;
    }

    /**
     * If SWITCH_MODEL is defined, find it... If defined, it means that the vertex
     * can be a point for switching to another model using the same label, see
     * org.graphwalker.graph.Graph.getLabelKey().
     *
     * @param str a {@link java.lang.String} object.
     * @return a {@link java.lang.Boolean} object.
     */
    static public Boolean isSwitchModel(String str) {
        Pattern p = Pattern.compile("\\n(SWITCH_MODEL)", Pattern.MULTILINE);
        Matcher m = p.matcher(str);
        if (m.find()) {
            logger.debug("Found keyword SWITCH_MODEL");
            return true;
        }
        return false;
    }

    /**
     * <p>Getter for the field <code>subGraphStartVertexKey</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getSubGraphStartVertexKey() {
        return subGraphStartVertexKey;
    }

    /**
     * <p>Setter for the field <code>subGraphStartVertexKey</code>.</p>
     *
     * @param subGraphStartVertexKey a {@link java.lang.String} object.
     */
    public void setSubGraphStartVertexKey(String subGraphStartVertexKey) {
        this.subGraphStartVertexKey = subGraphStartVertexKey;
    }

    /**
     * <p>Getter for the field <code>motherStartVertexKey</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getMotherStartVertexKey() {
        return motherStartVertexKey;
    }

    /**
     * <p>Setter for the field <code>motherStartVertexKey</code>.</p>
     *
     * @param motherStartVertexKey a {@link java.lang.String} object.
     */
    public void setMotherStartVertexKey(String motherStartVertexKey) {
        this.motherStartVertexKey = motherStartVertexKey;
    }

    /**
     * <p>getLabel.</p>
     *
     * @param str a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    static public String getLabel(String str) {
        Pattern p;
        Matcher m;
        String label = "";
        if (str.split("/").length > 1 || str.split("\\[").length > 1) {
            p = Pattern.compile("^([\\w\\.]+)\\s?([^/^\\[]+)?", Pattern.MULTILINE);
        } else {
            p = Pattern.compile("(.*)", Pattern.MULTILINE);
        }
        m = p.matcher(str);
        if (m.find()) {
            label = m.group(1);
            if (label.length() <= 0) {
                throw new RuntimeException("Vertex is missing mandatory label");
            }
            if (label.matches(".*[\\s].*")) {
                throw new RuntimeException("Label of vertex: '" + label + "', containing whitespaces");
            }
            if (Keywords.isKeyWord(label)) {
                throw new RuntimeException("The label of vertex: '" + label + "', is a reserved keyword");
            }
        } else {
            throw new RuntimeException("Label must be defined for vertex");
        }
        return label;
    }

    /**
     * <p>isSwitchModelKey.</p>
     *
     * @return a boolean.
     */
    public boolean isSwitchModelKey() {
        return switchModelKey;
    }

    /**
     * <p>Setter for the field <code>switchModelKey</code>.</p>
     *
     * @param switchModel a {@link java.lang.Boolean} object.
     */
    public void setSwitchModelKey(Boolean switchModel) {
        this.switchModelKey = switchModel;
    }

    /**
     * <p>isGraphVertex.</p>
     *
     * @return a boolean.
     */
    public boolean isGraphVertex() {
        return graphVertex;
    }

    /**
     * <p>Setter for the field <code>graphVertex</code>.</p>
     *
     * @param graphVertex a boolean.
     */
    public void setGraphVertex(boolean graphVertex) {
        this.graphVertex = graphVertex;
    }
}
