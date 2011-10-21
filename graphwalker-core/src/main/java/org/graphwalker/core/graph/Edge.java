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

import org.graphwalker.core.Keywords;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Edge class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class Edge extends AbstractElement {

    private String guardKey = "";
    private Float weightKey = 0f;

    /**
     * <p>Constructor for Edge.</p>
     */
    public Edge() {
        super();
    }

    /**
     * <p>Constructor for Edge.</p>
     *
     * @param edge a {@link org.graphwalker.core.graph.Edge} object.
     */
    public Edge(Edge edge) {
        super(edge);
        this.guardKey = edge.guardKey;
        this.weightKey = edge.weightKey;
    }

    /**
     * <p>Constructor for Edge.</p>
     *
     * @param A a {@link org.graphwalker.core.graph.Edge} object.
     * @param B a {@link org.graphwalker.core.graph.Edge} object.
     */
    public Edge(Edge A, Edge B) {
        super(A, B);
        if (A.getFullLabelKey().length() > B.getFullLabelKey().length()) {
            this.guardKey = A.guardKey;
            this.weightKey = A.weightKey;
        } else {
            this.guardKey = B.guardKey;
            this.weightKey = B.weightKey;
        }
    }

    /**
     * <p>Getter for the field <code>weightKey</code>.</p>
     *
     * @return a float.
     */
    public float getWeightKey() {
        return weightKey;
    }

    /**
     * <p>Setter for the field <code>weightKey</code>.</p>
     *
     * @param weightKey a float.
     */
    public void setWeightKey(float weightKey) {
        if (weightKey < 0 || weightKey > 1)
            throw new RuntimeException("The value of weight, must be between 0 <= weight <= 1");
        this.weightKey = weightKey;
    }

    /**
     * <p>Getter for the field <code>guardKey</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getGuardKey() {
        return guardKey;
    }

    /**
     * <p>Setter for the field <code>guardKey</code>.</p>
     *
     * @param guardKey a {@link java.lang.String} object.
     */
    public void setGuardKey(String guardKey) {
        this.guardKey = guardKey;
    }

    /**
     * The label of an edge has the following format: Label Parameter [Guard] /
     * Action1;Action2;ActionN; Keyword Where the Label, Parameter. Guard, Actions
     * and Keyword are optional.
     *
     * @param str a {@link java.lang.String} object.
     * @return an array of {@link java.lang.String} objects.
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
     * Action1;Action2;ActionN; Keyword Where the Label, Parameter. Guard, Actions
     * and Keyword are optional.
     *
     * @param str a {@link java.lang.String} object.
     * @return an array of {@link java.lang.String} objects.
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
     * If weight is defined, find it... weight must be associated with a value,
     * which depicts the probability for the edge to be executed. A value of 0.05
     * is the same as 5% chance of going down this road.
     *
     * @param str a {@link java.lang.String} object.
     * @return a float.
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
