package org.graphwalker.generators;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;

import org.graphwalker.graph.AbstractElement;

public class RequirementsGenerator extends PathGenerator {

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
				return arg1[0].compareTo(arg0[0]);
			}
		});

		Vector<AbstractElement> abstractElements = new Vector<AbstractElement>();
		abstractElements.addAll(getMachine().getAllVertices());
		abstractElements.addAll(getMachine().getAllEdges());

		for (Iterator<AbstractElement> i = abstractElements.iterator(); i.hasNext();) {
			AbstractElement ae = i.next();
			String reqtags = ae.getReqTagKey();
			if (!reqtags.isEmpty()) {
				String[] tags = reqtags.split(",");
				for (int j = 0; j < tags.length; j++) {
					String[] value = { tags[j], "" };
					tempList.add(value);
				}
			}
		}
		list.addAll(tempList);
	}

	public String toString() {
		return "REQUIREMENTS";
	}

}
