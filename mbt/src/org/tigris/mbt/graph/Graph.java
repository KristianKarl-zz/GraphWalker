package org.tigris.mbt.graph;

import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class Graph extends SparseMultigraph<Vertex, Edge> {

	private static final long serialVersionUID = 4744840850614032582L;

	private String fileKey = new String();
	private String labelKey = new String();

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

	public void removeAllEdges() {
		Object[] list = getEdges().toArray();
		for (int i = 0; i < list.length; i++) {
			Edge e = (Edge) list[i];
			removeEdge(e);
		}
	}

	public void removeAllVertices() {
		Object[] list = getVertices().toArray();
		for (int i = 0; i < list.length; i++) {
			Vertex v = (Vertex) list[i];
			removeVertex(v);
		}
	}

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

	public boolean addEdge(Edge e, Vertex source, Vertex dest) {
		return super.addEdge(e, source, dest, EdgeType.DIRECTED);
	}

}
