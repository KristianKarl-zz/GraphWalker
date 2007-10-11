package org.tigris.mbt;

import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;

public abstract class AbstractModelHandler {

	private DirectedSparseGraph _graph;
	
	public abstract void load(String fileName);
	
	public abstract void save(String fileName);
	
	public DirectedSparseGraph getModel()
	{
		return _graph;
	}
	
	public void setModel(DirectedSparseGraph graph)
	{
		_graph=graph;
	}
}
