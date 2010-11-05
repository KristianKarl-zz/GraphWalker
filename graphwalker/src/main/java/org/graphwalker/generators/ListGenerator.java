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

package org.graphwalker.generators;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;

import org.graphwalker.Keywords;
import org.graphwalker.graph.AbstractElement;
import org.graphwalker.graph.Edge;

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
			if (!ae.getLabelKey().equalsIgnoreCase(Keywords.START_NODE)) {
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
