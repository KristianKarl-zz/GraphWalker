package org.tigris.mbt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;

public class RunTestInteractively {
	static private String graphmlFile;
	static private boolean random;
	static private long seconds;
	private String  LABEL_KEY = "label";
	private String  INDEX_KEY = "index";


	public static void main(String[] args)
	{
		try 
		{
			Options opt = new Options();
			opt.addOption( "h", "help", false, "Print help for this application. " +
											   "MBT will return a test sequence, one line at a time to standard output, " +
											   "it will wait until a line is fed back via standard input. The data fed back can be: " +
											   "'0' which means, continue the test as normal, " +
											   "'1' which means go back to previous vertex (backtracking), " +
											   "anything else will abort the execution. ");
			opt.addOption( "r", "random", false, "Run the test with a random walk. Can not be combined with --optimize. " + 
												 "This argument also needs the --time to be set." );
			opt.addOption( "o", "optimize", false, "Run the test optimized. Can not be combined with --random." );			
			opt.addOption( OptionBuilder.withArgName( "file" )
                    .withDescription( "The graphml file containing the model of the test" )
                    .hasArg()
                    .withLongOpt( "graphml" )
                    .create( "g" ) );
			opt.addOption( OptionBuilder.withLongOpt( "time" )
					.withArgName( "=seconds" )
                    .withDescription( "The time in seconds for the random walk to run." )
                    .withValueSeparator( '=' )
                    .hasArg()
                    .create( "t" ) );
			
			CommandLineParser parser = new PosixParser();
	        CommandLine cl = parser.parse( opt, args );
	        
            if ( cl.hasOption( "h" ) ) 
            {
                HelpFormatter f = new HelpFormatter();
                f.printHelp( "RunTestInteractively", opt );
            }
            else
            {
	            
	            if ( cl.hasOption( "r" ) && cl.hasOption( "o" ) )
	            {
	            	System.out.println( "Can not set --random and --optimize at the same time." );
	                HelpFormatter f = new HelpFormatter();
	                f.printHelp( "RunTestInteractively", opt );
	                return;
	            }

	            if ( cl.hasOption( "r" ) ) 
	            {
	            	random = true;
	            	if ( cl.hasOption( "t" ) )
	            	{
	            		seconds = Integer.valueOf( cl.getOptionValue( "t" ) ).longValue();
	            	}
	            	else
	            	{
		            	System.out.println( "When running in --random mode, the --time must also be set." );
		                HelpFormatter f = new HelpFormatter();
		                f.printHelp( "RunTestInteractively", opt );
		                return;
	            	}
	            }
	            
	            if ( cl.hasOption( "o" ) ) 
	            {
	            	random = false;
	            }

	            if ( !cl.hasOption( "g" ) )
	            {
	            	System.out.println( "Missing the graphml file, See --graphml or -g" );
	                HelpFormatter f = new HelpFormatter();
	                f.printHelp( "RunTestInteractively", opt );
	                return;	            	
	            }

            	graphmlFile = cl.getOptionValue( "g" );
            	
            	RunTestInteractively test = new RunTestInteractively();
    			test.run();
			}
        }
        catch ( ParseException e ) 
        {
            e.printStackTrace();
        }
	}
	
	void checkInput( int input ) throws GoBackToPreviousVertexException
	{
		// 0 : Continue the test
		switch ( input )
		{
			case 0:
				return;
				
			case 1:
				throw new GoBackToPreviousVertexException();
				
			case 2:
				throw new RuntimeException( "Test ended normally" );					
				
			default:
				throw new RuntimeException( "Unkown input data: '" + input + "', only '0', '1' or '2' is allowed." );					
		}
	}
	
	
	String readFromStdin()
	{
		InputStreamReader reader = new InputStreamReader (System.in);
		BufferedReader buf_in = new BufferedReader (reader);
		
		String str = "";
	    try {
	        str = buf_in.readLine ();	        
	    }
	    catch  (IOException e) {
	    	e.printStackTrace();
	    }
	    return str;
	}

	public void run()
	{
		ModelBasedTesting mbt = new ModelBasedTesting( graphmlFile );	
		try
		{
			mbt.reset();
			
			// The array will conatin the lebel an id of the edge, and the vertex
			Integer previousVertexIndex = null;
			while ( true )
			{
				DirectedSparseEdge edge = mbt.getEdgeAndVertex( random, seconds );

				// getEdgeAndVertex caught an exception, and returned null
				if ( edge == null )
				{
					break;
				}

				mbt.getLogger().info( "Edge: " + edge.getUserDatum( LABEL_KEY ) + ", index=" + edge.getUserDatum( INDEX_KEY ) );
				if ( edge.containsUserDatumKey( LABEL_KEY ) )
				{
					System.out.println( edge.getUserDatum( LABEL_KEY ) );
				}
				else
				{
					System.out.println( "" );
				}
				
				try
				{
					checkInput( new Integer( readFromStdin() ).intValue() );
				}
				catch ( GoBackToPreviousVertexException e )
				{
					mbt.getLogger().info("== BACKTRACKING ==" );
					mbt.SetCurrentVertex( previousVertexIndex );
					continue;
				}
					
				mbt.getLogger().info( "Vertex: " + edge.getDest().getUserDatum( LABEL_KEY ) + ", index=" + edge.getDest().getUserDatum( INDEX_KEY ) );
				System.out.println( edge.getDest().getUserDatum( LABEL_KEY ) );
				try
				{
					checkInput( new Integer( readFromStdin() ).intValue() );
				}
				catch ( GoBackToPreviousVertexException e )
				{
					mbt.getLogger().info("== BACKTRACKING ==" );
					mbt.SetCurrentVertex( previousVertexIndex );
					continue;
				}
				previousVertexIndex = (Integer)edge.getDest().getUserDatum( INDEX_KEY );
			}
		}
		catch ( Exception e )
		{
			if ( e.getMessage() != "Test ended normally" )
			{
			  e.printStackTrace();
			}
		}
		mbt.getLogger().info( mbt.getStatistics() );
		System.out.println( mbt.getStatistics() );
	}
}
