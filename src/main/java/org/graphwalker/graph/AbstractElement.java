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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.graphwalker.Keywords;
import org.graphwalker.Util;

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

  public AbstractElement() {}

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

  public boolean isMergedMbtKey() {
    return mergedMbtKey;
  }

  public void setMergedMbtKey(boolean mergedMbtKey) {
    this.mergedMbtKey = mergedMbtKey;
  }

  public String getImageKey() {
    return imageKey;
  }

  public void setImageKey(String imageKey) {
    this.imageKey = imageKey;
  }

  public boolean isBlockedKey() {
    return blockedKey;
  }

  public void setBlockedKey(boolean blockedKey) {
    this.blockedKey = blockedKey;
  }

  public boolean isNoMergeKey() {
    return noMergeKey;
  }

  public void setNoMergeKey(boolean noMergeKey) {
    this.noMergeKey = noMergeKey;
  }

  public boolean isMergeKey() {
    return mergeKey;
  }

  public void setMergeKey(boolean mergeKey) {
    this.mergeKey = mergeKey;
  }

  public String getFileKey() {
    return fileKey;
  }

  public void setFileKey(String fileKey) {
    this.fileKey = fileKey;
  }

  public String getIdKey() {
    return idKey;
  }

  public void setIdKey(String idKey) {
    this.idKey = idKey;
  }

  public Integer getIndexKey() {
    return indexKey;
  }

  public void setIndexKey(Integer indexKey) {
    this.indexKey = indexKey;
  }

  public String getActionsKey() {
    return actionsKey;
  }

  public void setActionsKey(String actionsKey) {
    this.actionsKey = actionsKey;
  }

  public Integer getVisitedKey() {
    return visitedKey;
  }

  public void setVisitedKey(Integer visitedKey) {
    this.visitedKey = visitedKey;
  }

  public String getParameterKey() {
    return parameterKey;
  }

  public void setParameterKey(String parameterKey) {
    this.parameterKey = parameterKey;
  }

  public String getReqTagKey() {
    return reqTagKey;
  }

  /**
   * Associates a requirement to the edge/vertex.
   * 
   * @param reqTagKey The requirement. It can be one or many, if many that are comma separated.
   */
  public void setReqTagKey(String reqTagKey) {
    this.reqTagKey = reqTagKey;
  }

  /**
   * @return 0 is the requirement is untested.<br>
   *         1 if requirement tested ok<br>
   *         2 if the requirement has failed the test.
   */
  public Integer getReqTagResult() {
    return reqTagResult;
  }

  public void setReqTagResult(Integer reqTagResult) {
    this.reqTagResult = reqTagResult;
  }

  public String getFullLabelKey() {
    return fullLabelKey;
  }

  public void setFullLabelKey(String fullLabelKey) {
    this.fullLabelKey = fullLabelKey;
  }

  public String getLabelKey() {
    return labelKey;
  }

  public void setLabelKey(String labelKey) {
    this.labelKey = labelKey;
  }

  @Override
  public String toString() {
    return Util.getCompleteName(this);
  }

  public String getManualInstructions() {
    return manualInstructions;
  }

  public void setManualInstructions(String attributeValue) {
    this.manualInstructions = attributeValue;
  }

  /**
   * Parses for the MERGE keyword. If merge is defined, find it... If defined, it means that the
   * node will be merged with all other nodes wit the same name, but not replaced by any subgraphs
   * 
   * @param str
   * @return
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
   * If no merge is defined, find it... If defined, it means that when merging graphs, this specific
   * vertex will not be merged or replaced by any subgraphs
   * 
   * @param str
   * @return
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
   * If BLOCKED is defined, find it... If defined, it means that this vertex will not be added to
   * the graph. Sometimes it can be useful during testing to mark vertices as BLOCKED due to bugs in
   * the system you test. When the bug is removed, the BLOCKED tag can be removed.
   * 
   * @param str
   * @return
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
   * If INDEX is defined, find it... If defined, it means that this vertex has already a unique id
   * generated by mbt before, so we use this instead..
   * 
   * @param str
   * @return
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
   * @param str
   * @return
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

  public void setDesctiptionKey(String str) {
    this.descriptionKey = str;
  }

  public String getDescriptionKey() {
    return this.descriptionKey;
  }

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
