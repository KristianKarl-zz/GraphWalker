package org.tigris.mbt;

import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;

public abstract class AbstractModelHandler {

	protected DirectedSparseGraph graph;
	
	public abstract void load(String fileName);
	
	public abstract void save(String fileName);
	
	public DirectedSparseGraph getModel()
	{
		return graph;
	}
	
	public void setModel(DirectedSparseGraph graph)
	{
		this.graph=graph;
	}
}
