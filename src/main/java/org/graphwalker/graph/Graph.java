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

import org.apache.log4j.Logger;
import org.graphwalker.Util;

import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class Graph extends SparseMultigraph<Vertex, Edge> {

  private static final long serialVersionUID = 4744840850614032582L;
  private static Logger logger = Util.setupLogger(Graph.class);

  private String fileKey = "";
  private String labelKey = "";
  private String descriptionKey = "";

  public String getDescriptionKey() {
    return descriptionKey;
  }

  public void setDescriptionKey(String descriptionKey) {
    this.descriptionKey = descriptionKey;
  }

  public String getLabelKey() {
    return labelKey;
  }

  public void setLabelKey(String labelKey) {
    this.labelKey = labelKey;
  }

  public String getFileKey() {
    return fileKey;
  }

  public void setFileKey(String fileKey) {
    this.fileKey = fileKey;
  }

  @Override
  public String toString() {
    String str = "";
    if (!getFileKey().isEmpty()) str += "File: " + getFileKey() + ", ";
    if (!getLabelKey().isEmpty()) str += "Label: " + getLabelKey() + ", ";
    str += "Num of vertices: " + getVertexCount() + ", ";
    str += "Num of edges: " + getEdgeCount();
    return str;
  }

  @Override
  public boolean addEdge(Edge e, Vertex source, Vertex dest) {
    return super.addEdge(e, source, dest, EdgeType.DIRECTED);
  }

  public Vertex findVertex(String vertexName) {
    logger.debug("Looking for vertex: " + vertexName + ", in model: " + this.toString());
    for (Vertex vertex : getVertices()) {
      logger.debug("  " + vertex.getLabelKey());
      if ((vertex.getLabelKey()).equals(vertexName)) {
        logger.debug("    Found it: " + vertex);
        return vertex;
      }
    }
    return null;
  }

  public Edge findEdge(String edgeName) {
    logger.debug("Looking for edge: " + edgeName + ", in model: " + this.toString());
    for (Edge edge : getEdges()) {
      logger.debug("  " + edge.getLabelKey());
      if ((edge.getLabelKey()).equals(edgeName)) {
        logger.debug("    Found it: " + edge);
        return edge;
      }
    }
    return null;
  }
}
