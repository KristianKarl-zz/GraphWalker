package org.tigris.mbt.generators;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;

import org.tigris.mbt.FiniteStateMachine;
import org.tigris.mbt.Keywords;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.impl.AbstractElement;

public class ListGenerator extends PathGenerator {

	private Stack list = new Stack();

	public ListGenerator( FiniteStateMachine machine ) {
		super( machine );
		generateList();
	}

    public boolean hasNext()
    {
    	return !list.isEmpty();
    }

    public String[] getNext() 
    {
		return (String[])list.pop();
	}
    
	private void generateList()
	{
		TreeSet tempList = new TreeSet(new Comparator(){

			public int compare(Object arg0, Object arg1) {
				return ((String[])arg1)[0].compareTo(((String[])arg0)[0]);
			}});
		
		
		Vector abstractElements = new Vector();
		abstractElements.addAll(machine.getAllStates());
		abstractElements.addAll(machine.getAllEdges());
		
		for(Iterator i = abstractElements.iterator();i.hasNext();)
		{
			AbstractElement ae = (AbstractElement) i.next();
			Object label = ae.getUserDatum(Keywords.LABEL_KEY);
			if(label != null && !label.equals(Keywords.START_NODE))
			{
				String[] value = { 
						(String) label, 
						(ae instanceof Edge ? "Edge" : "Vertex")}; 
				tempList.add(value);
			}
		}
		list.addAll(tempList);
	}
}
