//This file is part of the GraphWalker java package
//The MIT License
//
//Copyright (c) 2010 graphwalker.org
//
//Permission is hereby granted, free of charge, to any person obtaining a copy
//of this software and associated documentation files (the "Software"), to deal
//in the Software without restriction, including without limitation the rights
//to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//copies of the Software, and to permit persons to whom the Software is
//furnished to do so, subject to the following conditions:
//
//The above copyright notice and this permission notice shall be included in
//all copies or substantial portions of the Software.
//
//THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//THE SOFTWARE.

package org.graphwalker.machines;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.graphwalker.Keywords;
import org.graphwalker.Util;
import org.graphwalker.exceptions.FoundNoEdgeException;
import org.graphwalker.graph.AbstractElement;
import org.graphwalker.graph.Edge;
import org.graphwalker.graph.Graph;
import org.graphwalker.graph.Vertex;

/**
 * @author Johan Tejle
 * 
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

	public int getNumOfCoveredEdges() {
		return numOfCoveredEdges;
	}

	public int getNumOfCoveredVertices() {
		return numOfCoveredVertices;
	}

	public void setVertex(String vertexName) {
		logger.debug("Setting vertex to: '" + vertexName + "'");
		Vertex e = model.findVertex(vertexName);
		Util.AbortIf(e == null, "Vertex not Found: '" + vertexName + "'");

		currentVertex = e;
		setAsVisited(e);
	}

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

	public boolean hasVertex(String vertexName) {
		if (model.findVertex(vertexName) != null) {
			return true;
		}
		return false;
	}

	public Edge findEdge(String edgeName) {
		for (Edge edge : model.getEdges()) {
			if ((edge.getLabelKey()).equals(edgeName)) {
				return edge;
			}
		}
		return null;
	}

	public FiniteStateMachine() {
		logger.debug("Initializing");
		edgeStack = new Stack<Edge>();
		start_time = System.currentTimeMillis();
	}

	public void setModel(Graph model) {
		reset();
		this.model = model;
		setVertex(Keywords.START_NODE);
	}

	public Vertex getCurrentVertex() {
		return currentVertex;
	}

	public Vertex getStartVertex() {
		for (Vertex vertex : model.getVertices()) {
			if (vertex.getLabelKey().equals(Keywords.START_NODE)) {
				return vertex;
			}
		}
		return null;
	}

	public String getLastEdgeName() {
		return lastEdge.getLabelKey();
	}

	public String getCurrentVertexName() {
		return currentVertex.getLabelKey();
	}

	public Collection<Vertex> getAllVertices() {
		return model.getVertices();
	}

	public Collection<Edge> getAllEdges() {
		return model.getEdges();
	}

	public Collection<Edge> getAllEdgesExceptStartEdge() {
		Vector<Edge> list = new Vector<Edge>(model.getEdges());
		Edge e = (Edge) model.getOutEdges(getStartVertex()).toArray()[0];
		list.remove(e);
		return list;
	}

	public Set<Edge> getCurrentOutEdges() throws FoundNoEdgeException {
		Set<Edge> retur = new HashSet<Edge>(model.getOutEdges(currentVertex));
		if (retur.size() == 0) {
			throw new FoundNoEdgeException("Cul-De-Sac, dead end found in '" + getCurrentVertex() + "'");
		}
		return retur;
	}

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

		if (e.getReqTagKey().isEmpty() == false) {
			Hashtable<String, Integer> reqs = getAllRequirements();
			String[] tags = e.getReqTagKey().split(",");
			for (int j = 0; j < tags.length; j++) {
				reqs.put(tags[j], new Integer((reqs.get(tags[j])).intValue() + 1));
			}
		}
	}

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
			for (int j = 0; j < tags.length; j++) {
				reqs.put(tags[j], new Integer((reqs.get(tags[j])).intValue() - 1));
			}
		}
	}

	public void walkPath(Stack<Edge> path) {
		for (Edge edge : path) {
			walkEdge(edge);
		}
	}

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
		} else
			logger.error(edge + ", is not the source of: " + currentVertex);
		return false;
	}

	public Edge getLastEdge() {
		return lastEdge;
	}

	public void setLastEdge(Edge e) {
		lastEdge = e;
	}

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
		if (e > 0 && v > 0)
			str = "Coverage Edges: " + ec + "/" + e + " => " + (100 * ec) / e + "%\n" + "Coverage Vertices: " + vc + "/" + v + " => "
			    + (100 * vc) / v + "%\n" + "Unvisited Edges:  " + (e - ec) + "\n" + "Unvisited Vertices: " + (v - vc) + "\n"
			    + "Test sequence length:  " + len;
		else if (req > 0)
			str = "Coverage Requirements: " + reqc + "/" + req + " => " + (100 * reqc) / req + "%\n";
		else
			str = "No statistics available. Probably no run made?";

		return str;
	}

	public int[] getStatistics() {
		Collection<Edge> e = model.getEdges();
		Collection<Vertex> v = model.getVertices();

		int[] retur = { e.size(), getEdgeCoverage(e), v.size(), getVertexCoverage(v), numberOfEdgesTravesed, getAllRequirements().size(),
		    getCoveredRequirements().size() };
		return retur;
	}

	public Boolean getValueFromReqs(String key){
		return reqs.get(key);
	}
	
	public void setValueForReq(String key, Boolean b){
		reqs.put(key, b);
	}
	
	/**
	 * This method returns all requirements and its values as a hashmap
	 * @return reqs hashmap containing requirement name as the key, and requirement status as the value
	 */
	public HashMap<String, Boolean> getReqs(){
		return reqs;
	}
	
	/**
	 * This method initiates and populates the hashmap reqs, with all requirements in the graph.
	 * There are two types of requirements: Ordinary and Variable. All of them are fetched by
	 * calling getReqTagKey for all the edges and vertexes in the graph
	 * The Ordinary requirements are directly put into the reqs hashmap.
	 * The Variable requirements are used as a lookup in the list of all the variable values returned by getAllVariableValues and
	 * the value matching the the Variable requirement are splitted with colon and put as keys into the reqs hashmap.
	 * The value for each entity in the reqs hashmap will be set to null, since no requirement are tested yet.
	 * Its never needed to call this method more than once.
	 */
	public void populateReqHashMap(){
		reqs = new HashMap<String, Boolean>();
		Hashtable<String, String> reqsVariables = getAllVariableValues();

		for (Edge edge : model.getEdges()) {
			String reqTag = edge.getReqTagKey();
			if(reqTag.length() ==0)
				continue;
			String[] tmp = reqTag.split(",");
			for(int i =0;i<tmp.length;i++){
				if(tmp[i].matches("[$][{].*[}]")){
					String[] reqNames = reqsVariables.get(tmp[i].substring(2, tmp[i].length()-1)).split(":");
					for(String reqVar : reqNames)
						this.reqs.put(reqVar, null);
				}else
					this.reqs.put(tmp[i], null);
			}	
		}
		for (Vertex vertex : model.getVertices()) {
			String reqTag = vertex.getReqTagKey();
			if(reqTag.length() ==0)
				continue;
			String[] tmp = reqTag.split(",");
			for(int i =0;i<tmp.length;i++){
				if(tmp[i].matches("[$][{].*[}]")){
					String savedReq = reqsVariables.get(tmp[i].substring(2, tmp[i].length()-1));
					if(savedReq==null)
						continue;
					String[] reqNames = savedReq.split(":");
					for(String reqVar : reqNames)
						this.reqs.put(reqVar,null);
				}else
					this.reqs.put(tmp[i],null);
			}
		}		
	}
	
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
		while(it.hasNext()){
			Entry<String, Boolean> pairs = it.next();
			 
			if(pairs.getValue()== null){
				retur += "Requirement: " + pairs.getKey() + " is not tested." + newLine;
				continue;
			}
			if(((Boolean)pairs.getValue()).booleanValue()== true){
				retur += "Requirement: " + pairs.getKey() + " has passed." + newLine;
				continue;
			}
			if(((Boolean)pairs.getValue()).booleanValue()== false){
				retur += "Requirement: " + pairs.getKey() + " has failed." + newLine;
				continue;
			}
		}
		
		retur += getStatisticsString() + newLine;
		retur += "Execution time: " + ((System.currentTimeMillis() - start_time) / 1000) + " seconds";
		return retur;
	}
	
	/**
	 * This method finds and returns all the strings found in the graph used to give a variable its value.  
	 * Each found string will be splitted with comma and the results will be treated as unique values. 
	 * For example
	 * In graph:
	 * reqtag1 = "REQ1" + ",REQ2,"
	 * reqtag1 = reqtag1 + "REQ3"
	 * reqtag2 = "REQ3,REQ4"
	 * reqtag3 = "RE" + "Q5,"
	 * reqtag4 = getValueFromExternalFunction("REQ6");
	 * 
	 * @return Hashmap with the variable name as key, and a string containing 1 to many colon separated values, as the value
	 *  For example
	 *  In HashMap ({key,value}
	 *  {
	 *  reqtag1,"REQ1:REQ2:REQ3"
	 *  reqtag2,"REQ3:REQ4"
	 *  reqtag3,"RE:Q5"
	 *  reqtag4,"REQ6"
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
					if(!tags[j].contains("="))
						continue;
					String[] variableAndValue = tags[j].split("=");
					variableAndValue[0] = variableAndValue[0].replaceAll("[ ]*", "");
					while(variableAndValue[1].contains("\"")){
						
						String[]splittedValue = variableAndValue[1].split("\"", 3);
						String[]reqs = splittedValue[1].split(",");
						for(String s : reqs){
							
							if(s.length()==0)
								continue;
							//fetching previously stored values for this variable
							String tmpVal = varVal.get(variableAndValue[0]);
							String newValue;
							if(tmpVal==null){
								newValue=s;
							}else	
								newValue= tmpVal +":"+ s;
			
							varVal.put(variableAndValue[0], newValue);
						}
						variableAndValue[1] = splittedValue[2];
					}
				}
			}
		}
		return varVal;
	}

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
					for (int j = 0; j < tags.length; j++) {
						associatedRequirements.put(tags[j], new Integer(0));
					}
				}
			}
		}
		return associatedRequirements;
	}

	@SuppressWarnings("unchecked")
	public Set<String> getCoveredRequirements() {
		Vector<Integer> notCoveredValues = new Vector<Integer>();
		notCoveredValues.add(new Integer(0));
		Hashtable<String, Integer> allRequirements = (Hashtable<String, Integer>) getAllRequirements().clone();
		allRequirements.values().removeAll(notCoveredValues);
		return allRequirements.keySet();
	}

	public String getEdgeName(Edge edge) {
		if (edge.getParameterKey().isEmpty()) {
			return edge.getLabelKey();
		}

		return edge.getLabelKey() + " " + edge.getParameterKey();
	}

	public void storeVertex() {
		if (this.vertexStore == null)
			this.vertexStore = new Stack<Integer>();
		this.vertexStore.push(new Integer(edgeStack.size()));
	}

	public void restoreVertex() {
		if (this.vertexStore == null || this.vertexStore.size() == 0)
			throw new RuntimeException("Nothing to restore");
		int prevVertex = (this.vertexStore.pop()).intValue();
		if (prevVertex > edgeStack.size())
			throw new RuntimeException("Cannot restore vertex from backtrack");
		while (prevVertex < edgeStack.size()) {
			popVertex();
		}
	}

	protected void track() {
		edgeStack.push(getLastEdge());
	}

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
	 * @param weighted
	 *          if edge weights are to be considered
	 */
	public void setWeighted(boolean weighted) {
		this.weighted = weighted;
	}

	/**
	 * @return true if the edge weights is considered
	 */
	public boolean isWeighted() {
		return weighted;
	}

	/**
	 * @return the number of edges traversed
	 */
	public int getNumberOfEdgesTravesed() {
		return numberOfEdgesTravesed;
	}

	public boolean isBacktrackPossible() {
		return isCalculatingPath();
	}

	public boolean isCalculatingPath() {
		return calculatingPath;
	}

	public void setCalculatingPath(boolean calculatingPath) {
		this.calculatingPath = calculatingPath;
	}

	/**
	 * This functions returns a list of edges, which has not yet been covered
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
	 */
	public Vector<Edge> getCoveredEdges() {
		Vector<Edge> retur = new Vector<Edge>(getAllEdges());
		retur.removeAll(getUncoveredEdges());
		return retur;
	}

	public Vector<Vertex> getUncoveredVertices() {
		Vector<Vertex> retur = new Vector<Vertex>();
		for (Vertex vertex : getAllVertices()) {
			if (vertex.getVisitedKey() <= 0) {
				retur.add(vertex);
			}
		}
		return retur;
	}

	public Vector<Vertex> getCoveredVertices() {
		Vector<Vertex> retur = new Vector<Vertex>(getAllVertices());
		retur.removeAll(getUncoveredVertices());
		return retur;
	}

	public Vector<AbstractElement> getUncoveredElements() {
		Vector<AbstractElement> retur = new Vector<AbstractElement>(getUncoveredEdges());
		retur.addAll(getUncoveredVertices());
		return retur;
	}

	public Vector<AbstractElement> getCoveredElements() {
		Vector<AbstractElement> retur = new Vector<AbstractElement>(getCoveredEdges());
		retur.addAll(getCoveredVertices());
		return retur;
	}

	public Graph getModel() {
		return model;
	}

	public String getCurrentDataString() {
		return "";
	}

	public boolean hasInternalVariables() {
		return false;
	}

	private void reset() {
		numberOfEdgesTravesed = 0;
		calculatingPath = false;
		numOfCoveredEdges = 0;
		numOfCoveredVertices = 0;
	}

	public void setVertex(Vertex vertex) {
		currentVertex = vertex;
	}

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
