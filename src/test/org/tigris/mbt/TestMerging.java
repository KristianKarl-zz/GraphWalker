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
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting( "graphml/test01" );
	    	mbt.writeGraph( "graphml/merged/test01.graphml" );
	    	assertTrue( mbt.getGraph().getEdges().size() == 9 );
	    	assertTrue( mbt.getGraph().getVertices().size() == 7 );
    	}
    	catch ( RuntimeException e)
    	{
    		fail( e.getMessage() );
    	}
    }
    
    // Test merging of 2 simple graphs, with  nodes containing key word NO_MERGE
    public void test02()
    {
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting( "graphml/test02" );
	    	mbt.writeGraph( "graphml/merged/test02.graphml" );
	    	assertTrue( mbt.getGraph().getEdges().size() == 11 );
	    	assertTrue( mbt.getGraph().getVertices().size() == 8 );
    	}
    	catch ( RuntimeException e)
    	{
    		fail( e.getMessage() );
    	}
    }
    
    // Test merging a folder consisting 162 graphs
    public void test03()
    {
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting( "graphml/test03" );
	    	mbt.writeGraph( "graphml/merged/test03.graphml" );
	    	assertTrue( mbt.getGraph().getEdges().size() == 1578 );
	    	assertTrue( mbt.getGraph().getVertices().size() == 799 );
    	}
    	catch ( RuntimeException e)
    	{
    		fail( e.getMessage() );
    	}
    }
    
    // Test Generating Tests
    public void test04()
    {
    	try
    	{
        	ModelBasedTesting mbt = new ModelBasedTesting( "graphml/merged/test01.graphml" );
    		mbt.generateTests();
    	}
    	catch ( Exception e )
    	{
    		System.out.println(e.getMessage());
    		assertTrue( e.getMessage().matches("There is no way to reach|Found a cul-de-sac"));
    	}
    }
    
    // Test Generating Tests
    public void test05()
    {
    	try
    	{
        	ModelBasedTesting mbt = new ModelBasedTesting( "graphml/merged/test03.graphml" );
	    	mbt.generateTests();
		}
		catch ( Exception e)
		{
			fail( e.getMessage() );
		}
    }
    
    // Test Generate Java Code
    public void test06()
    {
    	java.io.File file = new java.io.File( "graphml/java/test02.java" );
    	file.delete();
    	file = null;
    	
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting( "graphml/test02" );
	    	mbt.generateJavaCode( "graphml/java/test02.java" );	
	    	mbt.generateJavaCode( "graphml/java/test02.java" );	
		}
		catch ( RuntimeException e)
		{
			fail( e.getMessage() );
		}
    }
}
