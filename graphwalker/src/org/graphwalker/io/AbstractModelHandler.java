package org.graphwalker.io;

import java.io.PrintStream;

import org.graphwalker.graph.Graph;

public abstract class AbstractModelHandler {

	protected Graph graph;

	public abstract void load(String fileName);

	public abstract void save(PrintStream ps);

	public Graph getModel() {
		return graph;
	}

	public void setModel(Graph graph) {
		this.graph = graph;
	}
}
