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

import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import org.apache.log4j.Logger;
import org.graphwalker.core.Util;

/**
 * <p>Graph class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class Graph extends SparseMultigraph<Vertex, Edge> {

    private static final long serialVersionUID = 4744840850614032582L;
    private static Logger logger = Util.setupLogger(Graph.class);

    private String fileKey = "";
    private String labelKey = "";

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

    /** {@inheritDoc} */
    @Override
    public String toString() {
        String str = "";
        if (!getFileKey().isEmpty())
            str += "File: " + getFileKey() + ", ";
        if (!getLabelKey().isEmpty())
            str += "Label: " + getLabelKey() + ", ";
        str += "Num of vertices: " + getVertexCount() + ", ";
        str += "Num of edges: " + getEdgeCount();
        return str;
    }

    /** {@inheritDoc} */
    @Override
    public boolean addEdge(Edge e, Vertex source, Vertex dest) {
        return super.addEdge(e, source, dest, EdgeType.DIRECTED);
    }

    /**
     * <p>findVertex.</p>
     *
     * @param vertexName a {@link java.lang.String} object.
     * @return a {@link org.graphwalker.core.graph.Vertex} object.
     */
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

    /**
     * <p>findEdge.</p>
     *
     * @param edgeName a {@link java.lang.String} object.
     * @return a {@link org.graphwalker.core.graph.Edge} object.
     */
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
