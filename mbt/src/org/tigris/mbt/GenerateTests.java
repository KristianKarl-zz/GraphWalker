package org.tigris.mbt;

public class GenerateTests {
	
	private static ModelBasedTesting _mtb;


	public static void main(String[] args)
	{
		if ( args.length < 1 )
		{
			System.out.println( "Too few arguments" );
			displayHelpMessage();
			return;
		}

		try
		{
			_mtb = new ModelBasedTesting( args[ 0 ] );
			_mtb.generateTests();
		}
		catch ( RuntimeException e )
		{
			e.printStackTrace();
		}
	}

	private static void displayHelpMessage()
	{
		System.out.println( "GenerateTests <input file>" );
		System.out.println( "   input file is the graphml file or directory of graphml files used to produce the tests." );
	}
}
