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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>AbstractElement class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class AbstractElement {

    static Logger logger = Util.setupLogger(AbstractElement.class);
    private String labelKey = "";
    private String fullLabelKey = "";
    private String reqTagKey = "";
    private Integer reqTagResult = 0;
    private String parameterKey = "";
    private Integer visitedKey = 0;
    private String actionsKey = "";
    private Integer indexKey = 0;
    private String idKey = "";
    private String fileKey = "";
    private boolean mergeKey = false;
    private boolean noMergeKey = false;
    private boolean blockedKey = false;
    private String imageKey = "";
    private boolean mergedMbtKey = false;
    private String manualInstructions = "";
    private String descriptionKey = "";

    /**
     * <p>Constructor for AbstractElement.</p>
     */
    public AbstractElement() {
    }

    /**
     * <p>Constructor for AbstractElement.</p>
     *
     * @param ae a {@link org.graphwalker.core.graph.AbstractElement} object.
     */
    protected AbstractElement(AbstractElement ae) {
        this.labelKey = ae.labelKey;
        this.fullLabelKey = ae.fullLabelKey;
        this.reqTagKey = ae.reqTagKey;
        this.reqTagResult = ae.reqTagResult;
        this.parameterKey = ae.parameterKey;
        this.visitedKey = ae.visitedKey;
        this.actionsKey = ae.actionsKey;
        this.indexKey = ae.indexKey;
        this.idKey = ae.idKey;
        this.fileKey = ae.fileKey;
        this.mergeKey = ae.mergeKey;
        this.noMergeKey = ae.noMergeKey;
        this.blockedKey = ae.blockedKey;
        this.imageKey = ae.imageKey;
        this.mergedMbtKey = ae.mergedMbtKey;
        this.manualInstructions = ae.manualInstructions;
        this.descriptionKey = ae.descriptionKey;
    }

    /**
     * <p>Constructor for AbstractElement.</p>
     *
     * @param A a {@link org.graphwalker.core.graph.AbstractElement} object.
     * @param B a {@link org.graphwalker.core.graph.AbstractElement} object.
     */
    protected AbstractElement(AbstractElement A, AbstractElement B) {
        if (A.fullLabelKey.length() > B.fullLabelKey.length()) {
            this.labelKey = A.labelKey;
            this.fullLabelKey = A.fullLabelKey;
            this.reqTagKey = A.reqTagKey;
            this.reqTagResult = A.reqTagResult;
            this.parameterKey = A.parameterKey;
            this.visitedKey = A.visitedKey;
            this.actionsKey = A.actionsKey;
            this.indexKey = A.indexKey;
            this.idKey = A.idKey;
            this.fileKey = A.fileKey;
            this.mergeKey = A.mergeKey;
            this.noMergeKey = A.noMergeKey;
            this.blockedKey = A.blockedKey;
            this.imageKey = A.imageKey;
            this.mergedMbtKey = A.mergedMbtKey;
            this.manualInstructions = A.manualInstructions;
            this.descriptionKey = A.descriptionKey;
        } else {
            this.labelKey = B.labelKey;
            this.fullLabelKey = B.fullLabelKey;
            this.reqTagKey = B.reqTagKey;
            this.reqTagResult = B.reqTagResult;
            this.parameterKey = B.parameterKey;
            this.visitedKey = B.visitedKey;
            this.actionsKey = B.actionsKey;
            this.indexKey = B.indexKey;
            this.idKey = B.idKey;
            this.fileKey = B.fileKey;
            this.mergeKey = B.mergeKey;
            this.noMergeKey = B.noMergeKey;
            this.blockedKey = B.blockedKey;
            this.imageKey = B.imageKey;
            this.mergedMbtKey = B.mergedMbtKey;
            this.manualInstructions = B.manualInstructions;
            this.descriptionKey = B.descriptionKey;
        }
    }

    /**
     * <p>isMergedMbtKey.</p>
     *
     * @return a boolean.
     */
    public boolean isMergedMbtKey() {
        return mergedMbtKey;
    }

    /**
     * <p>Setter for the field <code>mergedMbtKey</code>.</p>
     *
     * @param mergedMbtKey a boolean.
     */
    public void setMergedMbtKey(boolean mergedMbtKey) {
        this.mergedMbtKey = mergedMbtKey;
    }

    /**
     * <p>Getter for the field <code>imageKey</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getImageKey() {
        return imageKey;
    }

    /**
     * <p>Setter for the field <code>imageKey</code>.</p>
     *
     * @param imageKey a {@link java.lang.String} object.
     */
    public void setImageKey(String imageKey) {
        this.imageKey = imageKey;
    }

    /**
     * <p>isBlockedKey.</p>
     *
     * @return a boolean.
     */
    public boolean isBlockedKey() {
        return blockedKey;
    }

    /**
     * <p>Setter for the field <code>blockedKey</code>.</p>
     *
     * @param blockedKey a boolean.
     */
    public void setBlockedKey(boolean blockedKey) {
        this.blockedKey = blockedKey;
    }

    /**
     * <p>isNoMergeKey.</p>
     *
     * @return a boolean.
     */
    public boolean isNoMergeKey() {
        return noMergeKey;
    }

    /**
     * <p>Setter for the field <code>noMergeKey</code>.</p>
     *
     * @param noMergeKey a boolean.
     */
    public void setNoMergeKey(boolean noMergeKey) {
        this.noMergeKey = noMergeKey;
    }

    /**
     * <p>isMergeKey.</p>
     *
     * @return a boolean.
     */
    public boolean isMergeKey() {
        return mergeKey;
    }

    /**
     * <p>Setter for the field <code>mergeKey</code>.</p>
     *
     * @param mergeKey a boolean.
     */
    public void setMergeKey(boolean mergeKey) {
        this.mergeKey = mergeKey;
    }

    /**
     * <p>Getter for the field <code>fileKey</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getFileKey() {
        return fileKey;
    }

    /**
     * <p>Setter for the field <code>fileKey</code>.</p>
     *
     * @param fileKey a {@link java.lang.String} object.
     */
    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    /**
     * <p>Getter for the field <code>idKey</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getIdKey() {
        return idKey;
    }

    /**
     * <p>Setter for the field <code>idKey</code>.</p>
     *
     * @param idKey a {@link java.lang.String} object.
     */
    public void setIdKey(String idKey) {
        this.idKey = idKey;
    }

    /**
     * <p>Getter for the field <code>indexKey</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getIndexKey() {
        return indexKey;
    }

    /**
     * <p>Setter for the field <code>indexKey</code>.</p>
     *
     * @param indexKey a {@link java.lang.Integer} object.
     */
    public void setIndexKey(Integer indexKey) {
        this.indexKey = indexKey;
    }

    /**
     * <p>Getter for the field <code>actionsKey</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getActionsKey() {
        return actionsKey;
    }

    /**
     * <p>Setter for the field <code>actionsKey</code>.</p>
     *
     * @param actionsKey a {@link java.lang.String} object.
     */
    public void setActionsKey(String actionsKey) {
        this.actionsKey = actionsKey;
    }

    /**
     * <p>Getter for the field <code>visitedKey</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getVisitedKey() {
        return visitedKey;
    }

    /**
     * <p>Setter for the field <code>visitedKey</code>.</p>
     *
     * @param visitedKey a {@link java.lang.Integer} object.
     */
    public void setVisitedKey(Integer visitedKey) {
        this.visitedKey = visitedKey;
    }

    /**
     * <p>Getter for the field <code>parameterKey</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getParameterKey() {
        return parameterKey;
    }

    /**
     * <p>Setter for the field <code>parameterKey</code>.</p>
     *
     * @param parameterKey a {@link java.lang.String} object.
     */
    public void setParameterKey(String parameterKey) {
        this.parameterKey = parameterKey;
    }

    /**
     * <p>Getter for the field <code>reqTagKey</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getReqTagKey() {
        return reqTagKey;
    }

    /**
     * Associates a requirement to the edge/vertex.
     *
     * @param reqTagKey The requirement. It can be one or many, if many that are comma
     *                  separated.
     */
    public void setReqTagKey(String reqTagKey) {
        this.reqTagKey = reqTagKey;
    }

    /**
     * <p>Getter for the field <code>reqTagResult</code>.</p>
     *
     * @return 0 is the requirement is untested.<br>
     *         1 if requirement tested ok<br>
     *         2 if the requirement has failed the test.
     */
    public Integer getReqTagResult() {
        return reqTagResult;
    }

    /**
     * <p>Setter for the field <code>reqTagResult</code>.</p>
     *
     * @param reqTagResult a {@link java.lang.Integer} object.
     */
    public void setReqTagResult(Integer reqTagResult) {
        this.reqTagResult = reqTagResult;
    }

    /**
     * <p>Getter for the field <code>fullLabelKey</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getFullLabelKey() {
        return fullLabelKey;
    }

    /**
     * <p>Setter for the field <code>fullLabelKey</code>.</p>
     *
     * @param fullLabelKey a {@link java.lang.String} object.
     */
    public void setFullLabelKey(String fullLabelKey) {
        this.fullLabelKey = fullLabelKey;
    }

    /**
     * <p>Getter for the field <code>labelKey</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getLabelKey() {
        return labelKey;
    }

    /**
     * <p>Setter for the field <code>labelKey</code>.</p>
     *
     * @param labelKey a {@link java.lang.String} object.
     */
    public void setLabelKey(String labelKey) {
        this.labelKey = labelKey;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return Util.getCompleteName(this);
    }

    /**
     * <p>Getter for the field <code>manualInstructions</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getManualInstructions() {
        return manualInstructions;
    }

    /**
     * <p>Setter for the field <code>manualInstructions</code>.</p>
     *
     * @param attributeValue a {@link java.lang.String} object.
     */
    public void setManualInstructions(String attributeValue) {
        this.manualInstructions = attributeValue;
    }

    /**
     * Parses for the MERGE keyword. If merge is defined, find it... If defined,
     * it means that the node will be merged with all other nodes wit the same
     * name, but not replaced by any subgraphs
     *
     * @param str a {@link java.lang.String} object.
     * @return a {@link java.lang.Boolean} object.
     */
    static public Boolean isMerged(String str) {
        Pattern p = Pattern.compile("\\n(MERGE)", Pattern.MULTILINE);
        Matcher m = p.matcher(str);
        if (m.find()) {
            logger.debug("Found keyword MERGE");
            return true;
        }
        return false;
    }

    /**
     * If no merge is defined, find it... If defined, it means that when merging
     * graphs, this specific vertex will not be merged or replaced by any
     * subgraphs
     *
     * @param str a {@link java.lang.String} object.
     * @return a {@link java.lang.Boolean} object.
     */
    static public Boolean isNoMerge(String str) {
        Pattern p = Pattern.compile("\\n(NO_MERGE)", Pattern.MULTILINE);
        Matcher m = p.matcher(str);
        if (m.find()) {
            logger.debug("Found keyword NO_MERGE");
            return true;
        }
        return false;
    }

    /**
     * If BLOCKED is defined, find it... If defined, it means that this vertex
     * will not be added to the graph. Sometimes it can be useful during testing
     * to mark vertices as BLOCKED due to bugs in the system you test. When the
     * bug is removed, the BLOCKED tag can be removed.
     *
     * @param str a {@link java.lang.String} object.
     * @return a {@link java.lang.Boolean} object.
     */
    static public Boolean isBlocked(String str) {
        Pattern p = Pattern.compile("\\n(BLOCKED)", Pattern.MULTILINE);
        Matcher m = p.matcher(str);
        if (m.find()) {
            logger.debug("Found keyword BLOCKED");
            return true;
        }
        return false;
    }

    /**
     * If INDEX is defined, find it... If defined, it means that this vertex has
     * already a unique id generated by mbt before, so we use this instead..
     *
     * @param str a {@link java.lang.String} object.
     * @return a {@link java.lang.Integer} object.
     */
    static public Integer getIndex(String str) {
        Pattern p = Pattern.compile("\\n(INDEX=(.*))", Pattern.MULTILINE);
        Matcher m = p.matcher(str);
        if (m.find()) {
            String index_key = m.group(2);
            logger.debug("Found INDEX: " + index_key);
            return Integer.valueOf(index_key);
        }
        return 0;
    }

    /**
     * If the REQTAG is defined, find it...
     *
     * @param str a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    static public String getReqTags(String str) {
        Pattern p = Pattern.compile("\\n(REQTAG=(.*))", Pattern.MULTILINE);
        Matcher m = p.matcher(str);
        if (m.find()) {
            String value = m.group(2);
            p = Pattern.compile("([^,]+)", Pattern.MULTILINE);
            m = p.matcher(value);
            String reqtags = "";
            while (m.find()) {
                String reqtag = m.group(1);
                reqtag = reqtag.trim();
                if (reqtags.length() == 0) {
                    reqtags = reqtag;
                } else {
                    reqtags += "," + reqtag;
                }
            }
            logger.debug("Found REQTAG: " + reqtags);
            return reqtags;
        }
        return "";
    }

    /**
     * <p>setDesctiptionKey.</p>
     *
     * @param str a {@link java.lang.String} object.
     */
    public void setDesctiptionKey(String str) {
        this.descriptionKey = str;
    }

    /**
     * <p>Getter for the field <code>descriptionKey</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getDescriptionKey() {
        return this.descriptionKey;
    }

    /**
     * <p>getDescription.</p>
     *
     * @param str a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public String getDescription(String str) {
        Pattern p;
        Matcher m;
        String label = "";
        p = Pattern.compile("\\<\\!\\[CDATA\\[.*\\]\\]\\>\\</data\\>", Pattern.MULTILINE);
        {
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
    }
}
