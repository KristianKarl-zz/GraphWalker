// This file is part of the GraphWalker java package
// The MIT License
//
// Copyright (c) 2010 graphwalker.org
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

package org.graphwalker.graph;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.graphwalker.Keywords;
import org.graphwalker.Util;

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

  public float getWidth() {
    return width;
  }

  public void setWidth(float width) {
    this.width = width;
  }

  public float getHeight() {
    return height;
  }

  public void setHeight(float height) {
    this.height = height;
  }

  public void setLocation(Point2D location) {
    this.location = location;
  }

  public Point2D getLocation() {
    return location;
  }

  public Color getFillColor() {
    return fillColor;
  }

  public void setFillColor(Color fillColor) {
    this.fillColor = fillColor;
  }

  public Vertex() {
    super();
  }

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
   * If SWITCH_MODEL is defined, find it... If defined, it means that the vertex can be a point for
   * switching to another model using the same label, see org.graphwalker.graph.Graph.getLabelKey().
   * 
   * @param str
   * @return
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

  public String getSubGraphStartVertexKey() {
    return subGraphStartVertexKey;
  }

  public void setSubGraphStartVertexKey(String subGraphStartVertexKey) {
    this.subGraphStartVertexKey = subGraphStartVertexKey;
  }

  public String getMotherStartVertexKey() {
    return motherStartVertexKey;
  }

  public void setMotherStartVertexKey(String motherStartVertexKey) {
    this.motherStartVertexKey = motherStartVertexKey;
  }

  /**
   * @param str
   * @return
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

  public boolean isSwitchModelKey() {
    return switchModelKey;
  }

  public void setSwitchModelKey(Boolean switchModel) {
    this.switchModelKey = switchModel;
  }

  public boolean isGraphVertex() {
    return graphVertex;
  }

  public void setGraphVertex(boolean graphVertex) {
    this.graphVertex = graphVertex;
  }
}
