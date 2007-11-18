package test.org.tigris.mbt;

import org.tigris.mbt.Keywords;

import junit.framework.TestCase;

public class KeywordsTest extends TestCase {

	public void testKeywords() 
	{
		assertEquals( true, Keywords.isKeyWord( "BACKTRACK" ) );
		assertEquals( true, Keywords.isKeyWord( "BLOCKED" ) );
		assertEquals( true, Keywords.isKeyWord( "MERGE" ) );
		assertEquals( true, Keywords.isKeyWord( "NO_MERGE" ) );		
	}

	public void testNonKeywords() 
	{
		assertEquals( false, Keywords.isKeyWord( "BACKTRACKING" ) );
		assertEquals( false, Keywords.isKeyWord( "BLOCK" ) );
		assertEquals( false, Keywords.isKeyWord( "MERGED" ) );
		assertEquals( false, Keywords.isKeyWord( "NO_MERGED" ) );		
		assertEquals( false, Keywords.isKeyWord( "backtrack" ) );
		assertEquals( false, Keywords.isKeyWord( "blocked" ) );
		assertEquals( false, Keywords.isKeyWord( "merge" ) );
		assertEquals( false, Keywords.isKeyWord( "no_merge" ) );		
		assertEquals( false, Keywords.isKeyWord( "REQTAG" ) );		
	}

	public void testEmpty() 
	{
		assertEquals( false, Keywords.isKeyWord( "" ) );		
	}

	public void testNull() 
	{
		assertEquals( false, Keywords.isKeyWord( null ) );		
	}
}
