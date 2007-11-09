package test.org.tigris.mbt;

import org.tigris.mbt.Keywords;
import org.tigris.mbt.ModelBasedTesting;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SparseGraph;

import junit.framework.TestCase;

public class UtilTest extends TestCase {

    public void test_mergeSubgraphs_01()
    {
		System.out.println( "TEST: test_mergeSubgraphs_01" );
		System.out.println( "=======================================================================" );
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting();
	    	mbt.readGraph( "graphml/mergeSubgraphs_01" );
	    	mbt.writeModel( System.out );
	    	assertTrue( mbt.getGraph().getVertices().size() == 6 );
	    	assertTrue( mbt.getGraph().getEdges().size() == 16 );
	    	verifyIds( mbt.getGraph() );
    	}
    	catch ( Exception e)
    	{
    		System.out.println(e.getMessage());
    		fail( e.getMessage() );
    	}
		System.out.println( "" );
    }

    
    
    public void test_mergeSubgraphs_02()
    {
		System.out.println( "TEST: test_mergeSubgraphs_02" );
		System.out.println( "=======================================================================" );
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting();
	    	mbt.readGraph( "graphml/mergeSubgraphs_02" );
	    	mbt.writeModel( System.out );
	    	assertTrue( mbt.getGraph().getVertices().size() == 16 );
	    	assertTrue( mbt.getGraph().getEdges().size() == 55 );
	    	verifyIds( mbt.getGraph() );
    	}
    	catch ( Exception e)
    	{
    		System.out.println(e.getMessage());
    		fail( e.getMessage() );
    	}
		System.out.println( "" );
    }

    
    
    // Test merging of 2 simple graphs
    public void test01()
    {
		System.out.println( "TEST: test01" );
		System.out.println( "=======================================================================" );
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting();
	    	mbt.readGraph( "graphml/test01" );
	    	mbt.writeModel( System.out );
	    	assertTrue( mbt.getGraph().getEdges().size() == 9 );
	    	assertTrue( mbt.getGraph().getVertices().size() == 7 );
	    	verifyIds( mbt.getGraph() );
    	}
    	catch ( Exception e)
    	{
    		System.out.println(e.getMessage());
    		fail( e.getMessage() );
    	}
		System.out.println( "" );
    }
    
    // Test merging of 2 simple graphs, with  nodes containing key word NO_MERGE
    public void test02()
    {
		System.out.println( "TEST: test02" );
		System.out.println( "=======================================================================" );
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting();
	    	mbt.readGraph( "graphml/test02" );
	    	mbt.writeModel( System.out );
	    	assertTrue( mbt.getGraph().getEdges().size() == 11 );
	    	assertTrue( mbt.getGraph().getVertices().size() == 8 );
	    	verifyIds( mbt.getGraph() );
    	}
    	catch ( Exception e)
    	{
    		System.out.println(e.getMessage());
    		fail( e.getMessage() );
    	}
		System.out.println( "" );
    }
    
    // Test merging a folder consisting 162 graphs
    public void test03()
    {
		System.out.println( "TEST: test03" );
		System.out.println( "=======================================================================" );
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting();
	    	mbt.readGraph( "graphml/test03" );
	    	mbt.writeModel( System.out );
	    	assertTrue( mbt.getGraph().getEdges().size() == 1550 );
	    	assertTrue( mbt.getGraph().getVertices().size() == 788 );
	    	verifyIds( mbt.getGraph() );
    	}
    	catch ( Exception e)
    	{
    		System.out.println(e.getMessage());
    		fail( e.getMessage() );
    	}
		System.out.println( "" );
    }

    // Verify that a graph containing a Stop vertex is correctly merged.
    public void test22()
    {
		System.out.println( "TEST: test22" );
		System.out.println( "=======================================================================" );
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting();
	    	mbt.readGraph( "graphml/test22" );
	    	mbt.writeModel( System.out );
	    	assertTrue( mbt.getGraph().getEdges().size() == 8 );
	    	assertTrue( mbt.getGraph().getVertices().size() == 8 );
	    	verifyIds( mbt.getGraph() );
    	}
    	catch ( Exception e)
    	{
    		System.out.println(e.getMessage());
	    	fail( e.getMessage() );
    	}
		System.out.println( "" );
    }

    // Merging with subgraphs containing Stop vertices 
    public void test23()
    {
		System.out.println( "TEST: test23" );
		System.out.println( "=======================================================================" );
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting();
	    	mbt.readGraph( "graphml/test23" );
	    	mbt.writeModel( System.out );
	    	assertTrue( mbt.getGraph().getEdges().size() == 14 );
	    	assertTrue( mbt.getGraph().getVertices().size() == 9 );
	    	verifyIds( mbt.getGraph() );
    	}
    	catch ( Exception e)
    	{
    		System.out.println(e.getMessage());
	    	fail( e.getMessage() );
    	}
		System.out.println( "" );
    }
    // Verify that all vertices and edges has indexes, and that no duplicates exists.
    private void verifyIds( SparseGraph g )
	{
		Object[] vertices1 = g.getVertices().toArray();
		for ( int i = 0; i < vertices1.length; i++ )
		{
			DirectedSparseVertex v1 = (DirectedSparseVertex)vertices1[ i ];
			int hits = 0;
			Integer index1 = (Integer)v1.getUserDatum( Keywords.INDEX_KEY );
	    	Object[] vertices2 = g.getVertices().toArray();
			for ( int j = 0; j < vertices1.length; j++ )
			{
				DirectedSparseVertex v2 = (DirectedSparseVertex)vertices2[ j ];
				Integer index2 = (Integer)v2.getUserDatum( Keywords.INDEX_KEY );
				if ( index1.intValue() == index2.intValue() )
				{
					hits++;
		    	}
	    	}
	    	assertTrue( hits == 1 );

	    	Object[] edges = g.getEdges().toArray();
			for ( int j = 0; j < edges.length; j++ )
			{
				DirectedSparseEdge e = (DirectedSparseEdge)edges[ j ];
				Integer index2 = (Integer)e.getUserDatum( Keywords.INDEX_KEY );
				if ( index1.intValue() == index2.intValue() )
				{
					hits++;
		    	}
	    	}
	    	assertTrue( hits == 1 );
		}					

		Object[] edges1 = g.getEdges().toArray();
		for ( int i = 0; i < edges1.length; i++ )
		{
			DirectedSparseEdge e1 = (DirectedSparseEdge)edges1[ i ];
			int hits = 0;
			Integer index1 = (Integer)e1.getUserDatum( Keywords.INDEX_KEY );
	    	Object[] edges2 = g.getEdges().toArray();
			for ( int j = 0; j < edges2.length; j++ )
			{
				DirectedSparseEdge e2 = (DirectedSparseEdge)edges2[ j ];
				Integer index2 = (Integer)e2.getUserDatum( Keywords.INDEX_KEY );
				if ( index1.intValue() == index2.intValue() )
				{
					hits++;
		    	}
	    	}
	    	assertTrue( hits == 1 );

	    	Object[] vertices2 = g.getVertices().toArray();
			for ( int j = 0; j < vertices1.length; j++ )
			{
				DirectedSparseVertex v2 = (DirectedSparseVertex)vertices2[ j ];
				Integer index2 = (Integer)v2.getUserDatum( Keywords.INDEX_KEY );
				if ( index1.intValue() == index2.intValue() )
				{
					hits++;
		    	}
	    	}
	    	assertTrue( hits == 1 );
		}					
	}
}
