package org.tigris.mbt;

public class GeneratePerlCode 
{
	private static ModelBasedTesting _mtb;


	public static void main(String[] args)
	{
		if ( args.length < 2 )
		{
			System.out.println( "Too few arguments" );
			displayHelpMessage();
			return;
		}

		try
		{
			_mtb = new ModelBasedTesting( args[ 0 ] );

			_mtb.generatePerlCode( args[ 1 ] );
		}
		catch ( RuntimeException e )
		{
			e.printStackTrace();
		}
	}

	private static void displayHelpMessage()
	{
		System.out.println( "GeneratePerlCode <input file> <output file>" );
		System.out.println( "   input file is a graphml file." );
		System.out.println( "   ouput file is where the output is written to." );
	}
}
