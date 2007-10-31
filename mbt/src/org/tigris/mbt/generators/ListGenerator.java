package org.tigris.mbt.generators;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

import org.tigris.mbt.FiniteStateMachine;
import org.tigris.mbt.Keywords;
import org.tigris.mbt.ModelBasedTesting;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;

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
		String[] retur = {(String)list.pop(), ""};
		return retur;
	}
	
	private void generateList()
	{
		SortedSet set = new TreeSet();
		
		Object[] vertices = machine.getAllStates().toArray();
		for (int i = 0; i < vertices.length; i++) 
		{
			DirectedSparseVertex vertex = (DirectedSparseVertex)vertices[ i ];
			String element = (String) vertex.getUserDatum( Keywords.LABEL_KEY );
			if ( element != null )
			{
				if ( !element.equals( "Start" ) )
					set.add( element );
			}
		}
		
		Object[] edges    = machine.getAllEdges().toArray();
		for (int i = 0; i < edges.length; i++) 
		{
			DirectedSparseEdge edge = (DirectedSparseEdge)edges[ i ];
			String element = (String) edge.getUserDatum( Keywords.LABEL_KEY );
			if ( element != null )
			{
				set.add( element );
			}
		}
		
		for ( Iterator iterator = set.iterator(); iterator.hasNext(); ) 
		{
			list.add( iterator.next() );			
		}
	}
}
