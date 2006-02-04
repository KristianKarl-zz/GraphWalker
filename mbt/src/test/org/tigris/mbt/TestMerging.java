package test.org.tigris.mbt;

import org.tigris.mbt.ModelBasedTesting;

import junit.framework.TestCase;

public class TestMerging extends TestCase
{
    public TestMerging( String testName )
    {
        super(testName);
    }

    public static void main( String args[] )
    {
        String[] testCaseName = { TestMerging.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }

    // Test merging of 2 simple graphs
    public void test01()
    {
    	ModelBasedTesting mbt = new ModelBasedTesting( "graphml/test01" );
    	mbt.writeGraph( "graphml/merged/test01.graphml" );
    	assertTrue( mbt.getGraph().getEdges().size() == 9 );
    	assertTrue( mbt.getGraph().getVertices().size() == 7 );
    }
    
    // Test merging of 2 simple graphs, with  nodes containing key word NO_MERGE
    public void test02()
    {
    	ModelBasedTesting mbt = new ModelBasedTesting( "graphml/test02" );
    	mbt.writeGraph( "graphml/merged/test02.graphml" );
    	assertTrue( mbt.getGraph().getEdges().size() == 11 );
    	assertTrue( mbt.getGraph().getVertices().size() == 8 );
    }
    
    // Test merging a folder consisting 162 graphs
    public void test03()
    {
    	ModelBasedTesting mbt = new ModelBasedTesting( "graphml/test03" );
    	mbt.writeGraph( "graphml/merged/test03.graphml" );
    	assertTrue( mbt.getGraph().getEdges().size() == 1587 );
    	assertTrue( mbt.getGraph().getVertices().size() == 799l );
    }
    
}
