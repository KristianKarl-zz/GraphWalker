package org.graphwalker.io;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import org.graphwalker.Keywords;
import org.graphwalker.graph.AbstractElement;
import org.graphwalker.graph.Edge;
import org.graphwalker.graph.Graph;
import org.graphwalker.graph.Vertex;

public abstract class AbstractModelHandler {

	/**
	 * List of all models.
	 */
	protected Vector<Graph> models;
	private String activeModelName = null;
	private Graph activeModel = null;

	public abstract void load(String fileName);

	public abstract void save(PrintStream ps);
	
	public Graph activateMainGraph() {
		for (Graph graph : models) {
			if (graph.getMainGraph()) {
				activeModelName = graph.getLabelKey();
				activeModel = graph;
				return activeModel;
			}
		}
		activeModel = null;
		activeModelName= null;
		return activeModel;
	}

	public Graph getActiveModel() {
		return activeModel;
	}

	public String getActiveModelName() {
		return activeModelName;
	}

	public void addModel(Graph g) {
		models.add(g);
	}

	public Graph setActiveModel(String modelName) {
		for (Graph graph : models) {
			if (graph.getLabelKey().equals(modelName)) {
				activeModel = graph;
				activeModelName = modelName;
				return graph;
			}
		}
		activeModel = null;
		activeModelName= null;
		return activeModel;
	}
	
	public Graph setActiveModel(Graph graph) {
		for (Graph g : models) {
			if (g == graph ) {
				activeModel = g;
				activeModelName = g.getLabelKey();
				return activeModel;
			}
		}
		activeModel = null;
		activeModelName= null;
		return activeModel;
	}
	
	public Vertex getDestination(Edge edge) {
		if ( activeModel == null ) {
			return null;
		}
		Vertex v = activeModel.getDest(edge);
		if ( v.isNoMergeKey() ) {
			return v;
		}

		Graph g = getSubgraph(v);
		if ( g == null ) {
			return v;
		}
		setActiveModel(g);
		return getFirstvertex(activeModel);
	}

	/**
	 * Finds and returns the vertex to which the START vertex points to.
	 * @param graph
	 * @return
	 */
	private Vertex getFirstvertex(Graph graph) {
		for (Vertex v : graph.getVertices()) {
	    if ( v.getLabelKey().equalsIgnoreCase(Keywords.START_NODE)) {
	    	for ( Edge e : graph.getOutEdges(v) ) {
	    		return graph.getDest(e);
	    	}
	    }
    }
	  return null;
  }

	private Graph getSubgraph(Vertex v) {
		for (Graph graph : models) {
			if (graph.getLabelKey().equals(v.getLabelKey())) {
				return graph;
			}
		}
	  return null;
  }

	public Vertex findVertex(String vertexName) {
		for (Graph g : models) {
			for (Vertex vertex : g.getVertices()) {
				if (((String) vertex.getLabelKey()).equals(vertexName)) {
					return vertex;
				}
			}
		}
		return null;
	}
	
	public AbstractElement findElement(Integer index) {
		for (Graph g : models) {
			for (Vertex vertex : g.getVertices()) {
				if (vertex.getIndexKey().equals(index)) {
					return vertex;
				}
			}
			for (Edge edge : g.getEdges()) {
				if (edge.getIndexKey().equals(index)) {
					return edge;
				}
			}
		}
		return null;
	}

	public Edge findEdge(String edgeName) {
		for (Graph g : models) {
			for (Edge edge : g.getEdges()) {
				if (((String) edge.getLabelKey()).equals(edgeName)) {
					return edge;
				}
			}
		}
		return null;
	}

	public Collection<Vertex> getVertices() {
		Collection<Vertex> col = new ArrayList<Vertex>();
		for (Graph g : models) {
			col.addAll(g.getVertices());
		}
	  return col;
  }

	public Collection<Edge> getEdges() {
		Collection<Edge> col = new ArrayList<Edge>();
		for (Graph g : models) {
			col.addAll(g.getEdges());
		}
	  return col;
  }
}
