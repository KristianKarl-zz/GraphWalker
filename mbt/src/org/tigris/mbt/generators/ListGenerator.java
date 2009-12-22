package org.tigris.mbt.generators;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;

import org.tigris.mbt.Keywords;
import org.tigris.mbt.graph.AbstractElement;
import org.tigris.mbt.graph.Edge;

public class ListGenerator extends PathGenerator {

	private Stack<String[]> list = null;

	public boolean hasNext() {
		if (list == null)
			generateList();
		return !list.isEmpty();
	}

	public String[] getNext() {
		if (list == null)
			generateList();
		return (String[]) list.pop();
	}

	private void generateList() {
		list = new Stack<String[]>();
		TreeSet<String[]> tempList = new TreeSet<String[]>(new Comparator<String[]>() {
			public int compare(String[] arg0, String[] arg1) {
				return ((String[]) arg1)[0].compareTo(((String[]) arg0)[0]);
			}
		});

		Vector<AbstractElement> abstractElements = new Vector<AbstractElement>();
		abstractElements.addAll(getMachine().getAllVertices());
		abstractElements.addAll(getMachine().getAllEdges());

		for (Iterator<AbstractElement> i = abstractElements.iterator(); i.hasNext();) {
			AbstractElement ae = i.next();
			if (!ae.getLabelKey().equals(Keywords.START_NODE)) {
				String[] value = { (String) ae.getLabelKey(), (ae instanceof Edge ? "Edge" : "Vertex") };
				tempList.add(value);
			}
		}
		list.addAll(tempList);
	}

	public String toString() {
		return "LIST";
	}
}
