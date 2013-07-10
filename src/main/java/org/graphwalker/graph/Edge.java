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

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.graphwalker.Keywords;

public class Edge extends AbstractElement {

  private String guardKey = "";
  private Float weightKey = 0f;

  // Label
  private Point2D labelLocation = new Point2D.Float();
  private Float labelWidth = 0f;
  private Float labelHeight = 0f;

  // Path
  private Point2D pathSourceLocation = new Point2D.Float(); // Source node offset position
  private Point2D pathTargetLocation = new Point2D.Float(); // Target node offset position

  private ArrayList<Point2D> pathPoints = new ArrayList<Point2D>(); // Array of additional points on
                                                                    // the edge path

  public Edge() {
    super();
  }

  public Edge(Edge edge) {
    super(edge);
    this.guardKey = edge.guardKey;
    this.weightKey = edge.weightKey;
    this.labelLocation = edge.labelLocation;
    this.labelWidth = edge.labelWidth;
    this.labelHeight = edge.labelHeight;
    this.pathSourceLocation = edge.pathSourceLocation;
    this.pathTargetLocation = edge.pathTargetLocation;
    this.pathPoints = edge.pathPoints;
  }

  public Edge(Edge A, Edge B) {
    super(A, B);
    if (A.getFullLabelKey().length() > B.getFullLabelKey().length()) {
      this.guardKey = A.guardKey;
      this.weightKey = A.weightKey;
      this.labelLocation = A.labelLocation;
      this.labelWidth = A.labelWidth;
      this.labelHeight = A.labelHeight;
      this.pathSourceLocation = A.pathSourceLocation;
      this.pathTargetLocation = A.pathTargetLocation;
      this.pathPoints = A.pathPoints;
    } else {
      this.guardKey = B.guardKey;
      this.weightKey = B.weightKey;
      this.labelLocation = B.labelLocation;
      this.labelWidth = B.labelWidth;
      this.labelHeight = B.labelHeight;
      this.pathSourceLocation = B.pathSourceLocation;
      this.pathTargetLocation = B.pathTargetLocation;
      this.pathPoints = B.pathPoints;
    }
  }

  public float getWeightKey() {
    return weightKey;
  }

  public void setWeightKey(float weightKey) {
    if (weightKey < 0 || weightKey > 1) throw new RuntimeException("The value of weight, must be between 0 <= weight <= 1");
    this.weightKey = weightKey;
  }

  public String getGuardKey() {
    return guardKey;
  }

  public void setGuardKey(String guardKey) {
    this.guardKey = guardKey;
  }

  public void setLabelLocation(Point2D labelLocation) {
    this.labelLocation = labelLocation;
  }

  public Point2D getLabelLocation() {
    return labelLocation;
  }

  public void setPathSourceLocation(Point2D location) {
    this.pathSourceLocation = location;
  }

  public Point2D getPathSourceLocation() {
    return pathSourceLocation;
  }

  public void setPathTargetLocation(Point2D location) {
    this.pathTargetLocation = location;
  }

  public Point2D getPathTargetLocation() {
    return pathTargetLocation;
  }

  public Float getLabelWidth() {
    return labelWidth;
  }

  public void setLabelWidth(Float labelWidth) {
    this.labelWidth = labelWidth;
  }

  public Float getLabelHeight() {
    return labelHeight;
  }

  public void setLabelHeight(Float labelHeight) {
    this.labelHeight = labelHeight;
  }

  public ArrayList<Point2D> getPathPoints() {
    return pathPoints;
  }

  public void setPathPoints(Point2D points) {
    pathPoints.add(points);
  }

  /**
   * The label of an edge has the following format: Label Parameter [Guard] /
   * Action1;Action2;ActionN; Keyword Where the Label, Parameter. Guard, Actions and Keyword are
   * optional.
   * 
   * @param str
   * @return
   */
  static public String[] getGuardAndActions(String str) {
    Pattern p = Pattern.compile("(.*)", Pattern.MULTILINE);
    Matcher m = p.matcher(str);
    String label = null;
    String[] guardAndAction = {"", ""};
    if (m.find()) {
      label = m.group(1);

      // Look for a Guard
      Pattern firstLinePattern = Pattern.compile("\\[(.*)\\]\\s*/|\\[(.*)\\]\\s*$", Pattern.MULTILINE);
      Matcher firstLineMatcher = firstLinePattern.matcher(label);
      if (firstLineMatcher.find()) {
        // Since we have 2 groups in the pattern, we have to check which
        // one is valid.
        String guard = firstLineMatcher.group(1);
        if (guard == null) {
          guard = firstLineMatcher.group(2);
        }
        guardAndAction[0] = guard;
      }

      // Look for Actions
      // To simplify this we wash the string by removing the guard
      // from a temporary string and make the search.
      String washedLabel = label.replace(guardAndAction[0], "");
      Pattern actionPattern = Pattern.compile("/\\s*(.*)\\s*$", Pattern.MULTILINE);
      Matcher actionMatcher = actionPattern.matcher(washedLabel);
      if (actionMatcher.find()) {
        guardAndAction[1] = actionMatcher.group(1);
      }
    }
    return guardAndAction;
  }

  /**
   * The label of an edge has the following format: Label Parameter [Guard] /
   * Action1;Action2;ActionN; Keyword Where the Label, Parameter. Guard, Actions and Keyword are
   * optional.
   * 
   * @param str
   * @return
   */
  static public String[] getLabelAndParameter(String str) {
    Pattern p = Pattern.compile("(.*)", Pattern.MULTILINE);
    Matcher m = p.matcher(str);
    String label = null;
    String[] labelAndParameter = {"", ""};
    if (m.find()) {
      label = m.group(1);

      // Look for the Label and Parameter
      Pattern firstLinePattern = Pattern.compile("^([\\w\\.]+)\\s?([^/^\\[]+)?", Pattern.MULTILINE);
      Matcher firstLineMatcher = firstLinePattern.matcher(label);
      if (firstLineMatcher.find()) {
        String label_key = firstLineMatcher.group(1);
        if (Keywords.isKeyWord(label_key)) {
          throw new RuntimeException("Edge has a label '" + label + "', which is a reserved keyword");
        }
        labelAndParameter[0] = label_key;

        String parameter = firstLineMatcher.group(2);
        if (parameter != null) {
          parameter = parameter.trim();
          labelAndParameter[1] = parameter;
        }
      }
    } else {
      throw new RuntimeException("Label for edge must be defined");
    }
    return labelAndParameter;
  }

  /**
   * If weight is defined, find it... weight must be associated with a value, which depicts the
   * probability for the edge to be executed. A value of 0.05 is the same as 5% chance of going down
   * this road.
   * 
   * @param str
   * @return
   */
  static public float getWeight(String str) {
    Pattern p = Pattern.compile("\\n(weight\\s*=\\s*(.*))", Pattern.MULTILINE);
    Matcher m = p.matcher(str);
    Float weight = 0f;
    if (m.find()) {
      String value = m.group(2);
      try {
        weight = Float.valueOf(value.trim());
      } catch (NumberFormatException error) {
        throw new RuntimeException("For label: " + str + ", weight is not a correct float value: " + error.toString());
      }
    }
    return weight;
  }
}
