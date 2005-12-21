package mbt;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class ParseGraphs 
{
	private static Logger _logger = Logger.getLogger( GenerateJavaCode.class );
	private static ModelBasedTesting _mtb;

	public static void main(String[] args) 
	{
		BasicConfigurator.configure();
		_logger.setLevel( Level.ALL );
	
		if ( args.length < 2 )
		{
			_logger.warn( "To few arguments" );
			displayHelpMessage();
			return;
		}
	
		try
		{
			_mtb = new ModelBasedTesting( args[ 0 ],
										  _logger );
	
			_mtb.writeGraph( args[ 1 ] );
		}
		catch ( RuntimeException e )
		{
			e.printStackTrace();
			_logger.error( e.getMessage() );
		}
	}

	private static void displayHelpMessage()
	{
		System.out.println( "ParseGraphs <input dir> <output file>" );
		System.out.println( "   input dir is a folder containing graphml (yEd) formatted files." );
		System.out.println( "   ouput file is where the merged graphml file is written to." );
	}
}
