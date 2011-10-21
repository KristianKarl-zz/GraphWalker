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

package org.graphwalker.core.machines;

import org.apache.log4j.Logger;
import org.graphwalker.core.Keywords;
import org.graphwalker.core.Util;
import org.graphwalker.core.exceptions.FoundNoEdgeException;
import org.graphwalker.core.graph.AbstractElement;
import org.graphwalker.core.graph.Edge;
import org.graphwalker.core.graph.Graph;
import org.graphwalker.core.graph.Vertex;

import java.util.*;
import java.util.Map.Entry;

/**
 * <p>FiniteStateMachine class.</p>
 *
 * @author Johan Tejle
 * @version $Id: $
 */
public class FiniteStateMachine {

    private static Logger logger = Util.setupLogger(FiniteStateMachine.class);

    private Graph model = null;
    private Vertex currentVertex = null;
    private boolean weighted = false;
    private Edge lastEdge = null;
    private Stack<Edge> edgeStack;
    private Stack<Integer> vertexStore;
    private int numberOfEdgesTravesed = 0;
    private HashMap<String, Boolean> reqs = new HashMap<String, Boolean>();
    private boolean calculatingPath = false;
    private int numOfCoveredEdges = 0;
    private int numOfCoveredVertices = 0;

    private long start_time;

    private Hashtable<String, Integer> associatedRequirements;

    /**
     * <p>Getter for the field <code>numOfCoveredEdges</code>.</p>
     *
     * @return a int.
     */
    public int getNumOfCoveredEdges() {
        return numOfCoveredEdges;
    }

    /**
     * <p>Getter for the field <code>numOfCoveredVertices</code>.</p>
     *
     * @return a int.
     */
    public int getNumOfCoveredVertices() {
        return numOfCoveredVertices;
    }

    /**
     * <p>setVertex.</p>
     *
     * @param vertexName a {@link java.lang.String} object.
     */
    public void setVertex(String vertexName) {
        logger.debug("Setting vertex to: '" + vertexName + "'");
        Vertex e = model.findVertex(vertexName);
        Util.AbortIf(e == null, "Vertex not Found: '" + vertexName + "'");

        currentVertex = e;
        setAsVisited(e);
    }

    /**
     * <p>findElement.</p>
     *
     * @param index a {@link java.lang.Integer} object.
     * @return a {@link org.graphwalker.core.graph.AbstractElement} object.
     */
    public AbstractElement findElement(Integer index) {
        for (Vertex vertex : model.getVertices()) {
            if (vertex.getIndexKey().equals(index)) {
                return vertex;
            }
        }
        for (Edge edge : model.getEdges()) {
            if (edge.getIndexKey().equals(index)) {
                return edge;
            }
        }
        return null;
    }

    /**
     * <p>hasVertex.</p>
     *
     * @param vertexName a {@link java.lang.String} object.
     * @return a boolean.
     */
    public boolean hasVertex(String vertexName) {
        return model.findVertex(vertexName) != null;
    }

    /**
     * <p>findEdge.</p>
     *
     * @param edgeName a {@link java.lang.String} object.
     * @return a {@link org.graphwalker.core.graph.Edge} object.
     */
    public Edge findEdge(String edgeName) {
        for (Edge edge : model.getEdges()) {
            if ((edge.getLabelKey()).equals(edgeName)) {
                return edge;
            }
        }
        return null;
    }

    /**
     * <p>Constructor for FiniteStateMachine.</p>
     */
    public FiniteStateMachine() {
        logger.debug("Initializing");
        edgeStack = new Stack<Edge>();
        start_time = System.currentTimeMillis();
    }

    /**
     * <p>Setter for the field <code>model</code>.</p>
     *
     * @param model a {@link org.graphwalker.core.graph.Graph} object.
     */
    public void setModel(Graph model) {
        reset();
        this.model = model;
        setVertex(Keywords.START_NODE);
    }

    /**
     * <p>Getter for the field <code>currentVertex</code>.</p>
     *
     * @return a {@link org.graphwalker.core.graph.Vertex} object.
     */
    public Vertex getCurrentVertex() {
        return currentVertex;
    }

    /**
     * <p>getStartVertex.</p>
     *
     * @return a {@link org.graphwalker.core.graph.Vertex} object.
     */
    public Vertex getStartVertex() {
        for (Vertex vertex : model.getVertices()) {
            if (vertex.getLabelKey().equals(Keywords.START_NODE)) {
                return vertex;
            }
        }
        return null;
    }

    /**
     * <p>getLastEdgeName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getLastEdgeName() {
        return lastEdge.getLabelKey();
    }

    /**
     * <p>getCurrentVertexName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getCurrentVertexName() {
        return currentVertex.getLabelKey();
    }

    /**
     * <p>getAllVertices.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<Vertex> getAllVertices() {
        return model.getVertices();
    }

    /**
     * <p>getAllEdges.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<Edge> getAllEdges() {
        return model.getEdges();
    }

    /**
     * <p>getAllEdgesExceptStartEdge.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<Edge> getAllEdgesExceptStartEdge() {
        Vector<Edge> list = new Vector<Edge>(model.getEdges());
        Edge e = (Edge) model.getOutEdges(getStartVertex()).toArray()[0];
        list.remove(e);
        return list;
    }

    /**
     * <p>getCurrentOutEdges.</p>
     *
     * @return a {@link java.util.Set} object.
     * @throws org.graphwalker.core.exceptions.FoundNoEdgeException if any.
     */
    public Set<Edge> getCurrentOutEdges() throws FoundNoEdgeException {
        Set<Edge> retur = new HashSet<Edge>(model.getOutEdges(currentVertex));
        if (retur.size() == 0) {
            throw new FoundNoEdgeException("Cul-De-Sac, dead end found in '" + getCurrentVertex() + "'");
        }
        return retur;
    }

    /**
     * <p>setAsVisited.</p>
     *
     * @param e a {@link org.graphwalker.core.graph.AbstractElement} object.
     */
    public void setAsVisited(AbstractElement e) {
        if (e instanceof Edge) {
            if (e.getVisitedKey() < 1) {
                numOfCoveredEdges++;
            }
        } else if (e instanceof Vertex) {
            if (e.getVisitedKey() < 1) {
                numOfCoveredVertices++;
            }
        }

        e.setVisitedKey(e.getVisitedKey() + 1);

        if (!e.getReqTagKey().isEmpty()) {
            Hashtable<String, Integer> reqs = getAllRequirements();
            String[] tags = e.getReqTagKey().split(",");
            for (String tag : tags) {
                reqs.put(tag, reqs.get(tag) + 1);
            }
        }
    }

    /**
     * <p>setAsUnvisited.</p>
     *
     * @param e a {@link org.graphwalker.core.graph.AbstractElement} object.
     */
    public void setAsUnvisited(AbstractElement e) {
        Integer visits = e.getVisitedKey();
        e.setVisitedKey(e.getVisitedKey() - 1);
        if (e instanceof Edge) {
            if (e.getVisitedKey() < 1) {
                numOfCoveredEdges--;
            }
        } else if (e instanceof Vertex) {
            if (e.getVisitedKey() < 1) {
                numOfCoveredVertices--;
            }
        }

        if (visits <= 0)
            logger.error(e + ", has a negative number in VISITED_KEY");

        if (!e.getReqTagKey().isEmpty()) {
            Hashtable<String, Integer> reqs = getAllRequirements();
            String[] tags = e.getReqTagKey().split(",");
            for (String tag : tags) {
                reqs.put(tag, reqs.get(tag) - 1);
            }
        }
    }

    /**
     * <p>walkPath.</p>
     *
     * @param path a {@link java.util.Stack} object.
     */
    public void walkPath(Stack<Edge> path) {
        for (Edge edge : path) {
            walkEdge(edge);
        }
    }

    /**
     * <p>walkEdge.</p>
     *
     * @param edge a {@link org.graphwalker.core.graph.Edge} object.
     * @return a boolean.
     */
    public boolean walkEdge(Edge edge) {
        if (model.isSource(currentVertex, edge)) {
            lastEdge = edge;
            if (isBacktrackPossible()) {
                track();
            }

            currentVertex = model.getDest(edge);
            setAsVisited(lastEdge);
            setAsVisited(currentVertex);
            numberOfEdgesTravesed++;
            logger.debug("No. of walked edges: " + numberOfEdgesTravesed);
            return true;
        } else {
            logger.error(edge + ", is not the source of: " + currentVertex);
        }
        return false;
    }

    /**
     * <p>Getter for the field <code>lastEdge</code>.</p>
     *
     * @return a {@link org.graphwalker.core.graph.Edge} object.
     */
    public Edge getLastEdge() {
        return lastEdge;
    }

    /**
     * <p>Setter for the field <code>lastEdge</code>.</p>
     *
     * @param e a {@link org.graphwalker.core.graph.Edge} object.
     */
    public void setLastEdge(Edge e) {
        lastEdge = e;
    }

    /**
     * <p>getStatisticsStringCompact.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getStatisticsStringCompact() {
        int stats[] = getStatistics();
        int e = stats[0];
        int ec = stats[1];
        int v = stats[2];
        int vc = stats[3];
        int len = stats[4];
        int req = stats[5];
        int reqc = stats[6];

        return (req > 0 ? "RC: " + reqc + "/" + req + " => " + (100 * reqc) / req + "% " : "") + "EC: " + ec + "/" + e + " => " + (100 * ec)
                / e + "% " + "SC: " + vc + "/" + v + " => " + (100 * vc) / v + "% " + "L: " + len;
    }

    /**
     * <p>getStatisticsString.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getStatisticsString() {
        int stats[] = getStatistics();
        int e = stats[0];
        int ec = stats[1];
        int v = stats[2];
        int vc = stats[3];
        int len = stats[4];
        int req = stats[5];
        int reqc = stats[6];

        String str = "";
        if (e > 0 && v > 0) {
            str = "Coverage Edges: " + ec + "/" + e + " => " + (100 * ec) / e + "%\n" + "Coverage Vertices: " + vc + "/" + v + " => "
                    + (100 * vc) / v + "%\n" + "Unvisited Edges:  " + (e - ec) + "\n" + "Unvisited Vertices: " + (v - vc) + "\n"
                    + "Test sequence length:  " + len;
        } else if (req > 0) {
            str = "Coverage Requirements: " + reqc + "/" + req + " => " + (100 * reqc) / req + "%\n";
        } else {
            str = "No statistics available. Probably no run made?";
        }
        return str;
    }

    /**
     * <p>getStatistics.</p>
     *
     * @return an array of int.
     */
    public int[] getStatistics() {
        Collection<Edge> e = model.getEdges();
        Collection<Vertex> v = model.getVertices();

        int[] retur = {e.size(), getEdgeCoverage(e), v.size(), getVertexCoverage(v), numberOfEdgesTravesed, getAllRequirements().size(),
                getCoveredRequirements().size()};
        return retur;
    }

    /**
     * <p>getValueFromReqs.</p>
     *
     * @param key a {@link java.lang.String} object.
     * @return a {@link java.lang.Boolean} object.
     */
    public Boolean getValueFromReqs(String key) {
        return reqs.get(key);
    }

    /**
     * <p>setValueForReq.</p>
     *
     * @param key a {@link java.lang.String} object.
     * @param b a {@link java.lang.Boolean} object.
     */
    public void setValueForReq(String key, Boolean b) {
        reqs.put(key, b);
    }

    /**
     * This method returns all requirements and its values as a hashmap
     *
     * @return reqs hashmap containing requirement name as the key, and
     *         requirement status as the value
     */
    public HashMap<String, Boolean> getReqs() {
        return reqs;
    }

    /**
     * This method initiates and populates the hashmap reqs, with all requirements
     * in the graph. There are two types of requirements: Ordinary and Variable.
     * All of them are fetched by calling getReqTagKey for all the edges and
     * vertexes in the graph The Ordinary requirements are directly put into the
     * reqs hashmap. The Variable requirements are used as a lookup in the list of
     * all the variable values returned by getAllVariableValues and the value
     * matching the the Variable requirement are splitted with colon and put as
     * keys into the reqs hashmap. The value for each entity in the reqs hashmap
     * will be set to null, since no requirement are tested yet. Its never needed
     * to call this method more than once.
     */
    public void populateReqHashMap() {
        reqs = new HashMap<String, Boolean>();
        Hashtable<String, String> reqsVariables = getAllVariableValues();

        for (Edge edge : model.getEdges()) {
            String reqTag = edge.getReqTagKey();
            if (reqTag.length() == 0)
                continue;
            String[] tmp = reqTag.split(",");
            for (int i = 0; i < tmp.length; i++) {
                if (tmp[i].matches("[$][{].*[}]")) {
                    String[] reqNames = reqsVariables.get(tmp[i].substring(2, tmp[i].length() - 1)).split(":");
                    for (String reqVar : reqNames)
                        this.reqs.put(reqVar, null);
                } else
                    this.reqs.put(tmp[i], null);
            }
        }
        for (Vertex vertex : model.getVertices()) {
            String reqTag = vertex.getReqTagKey();
            if (reqTag.length() == 0)
                continue;
            String[] tmp = reqTag.split(",");
            for (int i = 0; i < tmp.length; i++) {
                if (tmp[i].matches("[$][{].*[}]")) {
                    String savedReq = reqsVariables.get(tmp[i].substring(2, tmp[i].length() - 1));
                    if (savedReq == null)
                        continue;
                    String[] reqNames = savedReq.split(":");
                    for (String reqVar : reqNames)
                        this.reqs.put(reqVar, null);
                } else
                    this.reqs.put(tmp[i], null);
            }
        }
    }

    /**
     * <p>getStatisticsVerbose.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getStatisticsVerbose() {
        String retur = "";
        String newLine = "\n";
        Vector<String> notCovered = new Vector<String>();

        for (Edge edge : model.getEdges()) {
            if (edge.getVisitedKey() <= 0) {
                notCovered.add("Edge not reached: " + edge + newLine);
            }
        }
        for (Vertex vertex : model.getVertices()) {
            if (vertex.getVisitedKey() <= 0) {
                notCovered.add("Vertex not reached: " + vertex + newLine);
            }
        }
        if (notCovered.size() > 0) {
            Collections.sort(notCovered);
            for (String string : notCovered) {
                retur += string;
            }
        }

        Iterator<Entry<String, Boolean>> it = reqs.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, Boolean> pairs = it.next();

            if (pairs.getValue() == null) {
                retur += "Requirement: " + pairs.getKey() + " is not tested." + newLine;
                continue;
            }
            if (pairs.getValue().booleanValue() == true) {
                retur += "Requirement: " + pairs.getKey() + " has passed." + newLine;
                continue;
            }
            if (pairs.getValue().booleanValue() == false) {
                retur += "Requirement: " + pairs.getKey() + " has failed." + newLine;
                continue;
            }
        }

        retur += getStatisticsString() + newLine;
        retur += "Execution time: " + ((System.currentTimeMillis() - start_time) / 1000) + " seconds";
        return retur;
    }

    /**
     * This method finds and returns all the strings found in the graph used to
     * give a variable its value. Each found string will be splitted with comma
     * and the results will be treated as unique values. For example In graph:
     * reqtag1 = "REQ1" + ",REQ2," reqtag1 = reqtag1 + "REQ3" reqtag2 =
     * "REQ3,REQ4" reqtag3 = "RE" + "Q5," reqtag4 =
     * getValueFromExternalFunction("REQ6");
     *
     * @return Hashmap with the variable name as key, and a string containing 1 to
     *         many colon separated values, as the value For example In HashMap
     *         ({key,value} { reqtag1,"REQ1:REQ2:REQ3" reqtag2,"REQ3:REQ4"
     *         reqtag3,"RE:Q5" reqtag4,"REQ6"
     */
    public Hashtable<String, String> getAllVariableValues() {

        Hashtable<String, String> varVal = new Hashtable<String, String>();

        Vector<AbstractElement> abstractElements = new Vector<AbstractElement>();
        abstractElements.addAll(getAllVertices());
        abstractElements.addAll(getAllEdges());

        for (AbstractElement abstractElement : abstractElements) {
            String actionkey = abstractElement.getActionsKey();
            if (!actionkey.isEmpty()) {
                String[] tags = actionkey.split(";");
                for (int j = 0; j < tags.length; j++) {
                    if (!tags[j].contains("="))
                        continue;
                    String[] variableAndValue = tags[j].split("=");
                    variableAndValue[0] = variableAndValue[0].replaceAll("[ ]*", "");
                    while (variableAndValue[1].contains("\"")) {

                        String[] splittedValue = variableAndValue[1].split("\"", 3);
                        String[] reqs = splittedValue[1].split(",");
                        for (String s : reqs) {

                            if (s.length() == 0)
                                continue;
                            // fetching previously stored values for this variable
                            String tmpVal = varVal.get(variableAndValue[0]);
                            String newValue;
                            if (tmpVal == null) {
                                newValue = s;
                            } else
                                newValue = tmpVal + ":" + s;

                            varVal.put(variableAndValue[0], newValue);
                        }
                        variableAndValue[1] = splittedValue[2];
                    }
                }
            }
        }
        return varVal;
    }

    /**
     * <p>isCurrentVertex.</p>
     *
     * @param vertex a {@link org.graphwalker.core.graph.Vertex} object.
     * @return a boolean.
     */
    public boolean isCurrentVertex(Vertex vertex) {
        if (getCurrentVertex() != null)
            return getCurrentVertex().equals(vertex);
        return false;
    }

    private int getVertexCoverage(Collection<Vertex> modelItems) {
        int unique = 0;

        for (Vertex vertex : modelItems) {
            if (vertex.getVisitedKey() > 0) {
                unique++;
            }
        }
        return unique;
    }

    private int getEdgeCoverage(Collection<Edge> modelItems) {
        int unique = 0;

        for (Edge edge : modelItems) {
            if (edge.getVisitedKey() > 0) {
                unique++;
            }
        }
        return unique;
    }

    /**
     * <p>getAllRequirements.</p>
     *
     * @return a {@link java.util.Hashtable} object.
     */
    public Hashtable<String, Integer> getAllRequirements() {
        if (associatedRequirements == null) {
            associatedRequirements = new Hashtable<String, Integer>();

            Vector<AbstractElement> abstractElements = new Vector<AbstractElement>();
            abstractElements.addAll(getAllVertices());
            abstractElements.addAll(getAllEdges());

            for (AbstractElement abstractElement : abstractElements) {
                String reqtags = abstractElement.getReqTagKey();
                if (!reqtags.isEmpty()) {
                    String[] tags = reqtags.split(",");
                    for (String tag : tags) {
                        associatedRequirements.put(tag, 0);
                    }
                }
            }
        }
        return associatedRequirements;
    }

    /**
     * <p>getCoveredRequirements.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    @SuppressWarnings("unchecked")
    public Set<String> getCoveredRequirements() {
        Vector<Integer> notCoveredValues = new Vector<Integer>();
        notCoveredValues.add(0);
        Hashtable<String, Integer> allRequirements = (Hashtable<String, Integer>) getAllRequirements().clone();
        allRequirements.values().removeAll(notCoveredValues);
        return allRequirements.keySet();
    }

    /**
     * <p>getEdgeName.</p>
     *
     * @param edge a {@link org.graphwalker.core.graph.Edge} object.
     * @return a {@link java.lang.String} object.
     */
    public String getEdgeName(Edge edge) {
        if (edge.getParameterKey().isEmpty()) {
            return edge.getLabelKey();
        }

        return edge.getLabelKey() + " " + edge.getParameterKey();
    }

    /**
     * <p>storeVertex.</p>
     */
    public void storeVertex() {
        if (this.vertexStore == null)
            this.vertexStore = new Stack<Integer>();
        this.vertexStore.push(edgeStack.size());
    }

    /**
     * <p>restoreVertex.</p>
     */
    public void restoreVertex() {
        if (this.vertexStore == null || this.vertexStore.size() == 0)
            throw new RuntimeException("Nothing to restore");
        int prevVertex = this.vertexStore.pop();
        if (prevVertex > edgeStack.size())
            throw new RuntimeException("Cannot restore vertex from backtrack");
        while (prevVertex < edgeStack.size()) {
            popVertex();
        }
    }

    /**
     * <p>track.</p>
     */
    protected void track() {
        edgeStack.push(getLastEdge());
    }

    /**
     * <p>popVertex.</p>
     */
    protected void popVertex() {
        setAsUnvisited(getLastEdge());
        setAsUnvisited(getCurrentVertex());

        edgeStack.pop();
        if (lastEdge == null) {
            setVertex(Keywords.START_NODE);
        } else {
            currentVertex = model.getSource(lastEdge);
        }
        lastEdge = (edgeStack.size() > 0 ? edgeStack.peek() : null);
        numberOfEdgesTravesed--;
    }

    /**
     * <p>Setter for the field <code>weighted</code>.</p>
     *
     * @param weighted if edge weights are to be considered
     */
    public void setWeighted(boolean weighted) {
        this.weighted = weighted;
    }

    /**
     * <p>isWeighted.</p>
     *
     * @return true if the edge weights is considered
     */
    public boolean isWeighted() {
        return weighted;
    }

    /**
     * <p>Getter for the field <code>numberOfEdgesTravesed</code>.</p>
     *
     * @return the number of edges traversed
     */
    public int getNumberOfEdgesTravesed() {
        return numberOfEdgesTravesed;
    }

    /**
     * <p>isBacktrackPossible.</p>
     *
     * @return a boolean.
     */
    public boolean isBacktrackPossible() {
        return isCalculatingPath();
    }

    /**
     * <p>isCalculatingPath.</p>
     *
     * @return a boolean.
     */
    public boolean isCalculatingPath() {
        return calculatingPath;
    }

    /**
     * <p>Setter for the field <code>calculatingPath</code>.</p>
     *
     * @param calculatingPath a boolean.
     */
    public void setCalculatingPath(boolean calculatingPath) {
        this.calculatingPath = calculatingPath;
    }

    /**
     * This functions returns a list of edges, which has not yet been covered
     *
     * @return a {@link java.util.Vector} object.
     */
    public Vector<Edge> getUncoveredEdges() {
        Vector<Edge> retur = new Vector<Edge>();
        for (Edge edge : getAllEdges()) {
            if (edge.getVisitedKey() <= 0) {
                retur.add(edge);
            }
        }
        return retur;
    }

    /**
     * This functions returns a list of edges, which has been covered
     *
     * @return a {@link java.util.Vector} object.
     */
    public Vector<Edge> getCoveredEdges() {
        Vector<Edge> retur = new Vector<Edge>(getAllEdges());
        retur.removeAll(getUncoveredEdges());
        return retur;
    }

    /**
     * <p>getUncoveredVertices.</p>
     *
     * @return a {@link java.util.Vector} object.
     */
    public Vector<Vertex> getUncoveredVertices() {
        Vector<Vertex> retur = new Vector<Vertex>();
        for (Vertex vertex : getAllVertices()) {
            if (vertex.getVisitedKey() <= 0) {
                retur.add(vertex);
            }
        }
        return retur;
    }

    /**
     * <p>getCoveredVertices.</p>
     *
     * @return a {@link java.util.Vector} object.
     */
    public Vector<Vertex> getCoveredVertices() {
        Vector<Vertex> retur = new Vector<Vertex>(getAllVertices());
        retur.removeAll(getUncoveredVertices());
        return retur;
    }

    /**
     * <p>getUncoveredElements.</p>
     *
     * @return a {@link java.util.Vector} object.
     */
    public Vector<AbstractElement> getUncoveredElements() {
        Vector<AbstractElement> retur = new Vector<AbstractElement>(getUncoveredEdges());
        retur.addAll(getUncoveredVertices());
        return retur;
    }

    /**
     * <p>getCoveredElements.</p>
     *
     * @return a {@link java.util.Vector} object.
     */
    public Vector<AbstractElement> getCoveredElements() {
        Vector<AbstractElement> retur = new Vector<AbstractElement>(getCoveredEdges());
        retur.addAll(getCoveredVertices());
        return retur;
    }

    /**
     * <p>Getter for the field <code>model</code>.</p>
     *
     * @return a {@link org.graphwalker.core.graph.Graph} object.
     */
    public Graph getModel() {
        return model;
    }

    /**
     * <p>getCurrentDataString.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getCurrentDataString() {
        return "";
    }

    /**
     * <p>hasInternalVariables.</p>
     *
     * @return a boolean.
     */
    public boolean hasInternalVariables() {
        return false;
    }

    private void reset() {
        numberOfEdgesTravesed = 0;
        calculatingPath = false;
        numOfCoveredEdges = 0;
        numOfCoveredVertices = 0;
    }

    /**
     * <p>setVertex.</p>
     *
     * @param vertex a {@link org.graphwalker.core.graph.Vertex} object.
     */
    public void setVertex(Vertex vertex) {
        currentVertex = vertex;
    }

    /**
     * <p>setAllUnvisited.</p>
     */
    public void setAllUnvisited() {
        logger.debug("setAllUnvisited");
        reset();
        for (Vertex vertex : model.getVertices()) {
            vertex.setVisitedKey(0);
        }
        for (Edge edge : model.getEdges()) {
            edge.setVisitedKey(0);
        }
    }
}
