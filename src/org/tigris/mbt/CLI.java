package org.tigris.mbt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;

public class CLI 
{
	static private String graphmlFile;
	static private String outputFile;
	static private String perlScript;
	static private boolean random;
	static private long seconds;
	static private long length;
	private String  LABEL_KEY = "label";
	private String  INDEX_KEY = "index";

	public static void main(String[] args)
	{
		try 
		{
			Options opt = new Options();
			opt.addOption( "h", "help", false, "Print help for this application." );
			opt.addOption( "i", "interactively", false, "Run the test interactively. " +			
													    "MBT will return a test sequence, one line at a time to standard output, " +
													    "it will wait until a line is fed back via standard input. The data fed back can be: " +
													    "'0' which means, continue the test as normal, " +
													    "'1' which means go back to previous vertex (backtracking), " +
													    "'2' will end the test normally, " +
													    "anything else will abort the execution. ");
			opt.addOption( "r", "random", false, "Run the test with a random walk. Can not be combined with --optimize. " + 
												 "This argument also needs the --time to be set." );
			opt.addOption( "o", "optimize", false, "Run the test optimized. Can not be combined with --random." );			
			opt.addOption( "q", "test_methods", false, "Will generate an output of the unique tests in the graph." );			
			opt.addOption( "s", "test_sequence", false, "Will generate a sequence of tests." );			
			opt.addOption( OptionBuilder.withLongOpt( "length" )
					.withArgName( "=length" )
                    .withDescription( "The length of the test sequence. Supply an integer, which tells MBT how many " + 
                    		"vertices a test sequence shall contain." )
                    .hasArg()
                    .create( "n" ) );
			opt.addOption( OptionBuilder.withArgName( "file" )
                    .withDescription( "The file (or folder) containing graphml (yEd) formatted files." )
                    .hasArg()
                    .withLongOpt( "input_graphml" )
                    .create( "g" ) );
			opt.addOption( OptionBuilder.withArgName( "file" )
                    .withDescription( "The ouput file is where the merged graphml file is written to." )
                    .hasArg()
                    .withLongOpt( "output_graphml" )
                    .create( "l" ) );
			opt.addOption( OptionBuilder.withArgName( "file" )
                    .withDescription( "The ouput perl source file." )
                    .hasArg()
                    .withLongOpt( "perl_source" )
                    .create( "e" ) );
			opt.addOption( OptionBuilder.withArgName( "file" )
                    .withDescription( "The ouput java source file." )
                    .hasArg()
                    .withLongOpt( "java_source" )
                    .create( "j" ) );
			opt.addOption( OptionBuilder.withArgName( "file" )
                    .withDescription( "The perl script implementing the model." )
                    .hasArg()
                    .withLongOpt( "perl" )
                    .create( "p" ) );
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
                f.printHelp( "Model-based testing", opt );
            }
            else if ( cl.hasOption( "s" ) )
            {
	            if ( cl.hasOption( "r" ) && cl.hasOption( "o" ) )
	            {
	            	System.out.println( "Can not set --random and --optimize at the same time." );
	                HelpFormatter f = new HelpFormatter();
	                f.printHelp( "Model-based testing", opt );
	                return;
	            }

	            if ( cl.hasOption( "r" ) ) 
	            {
	            	random = true;
	            	if ( cl.hasOption( "n" ) )
	            	{
	            		length = Integer.valueOf( cl.getOptionValue( "n" ) ).longValue();
	            	}
	            	else
	            	{
		            	System.out.println( "When running in --random mode, the --length must also be set." );
		                HelpFormatter f = new HelpFormatter();
		                f.printHelp( "Model-based testing", opt );
		                return;
	            	}
	            }
	            else if ( cl.hasOption( "o" ) ) 
	            {
	            	if ( cl.hasOption( "n" ) )
	            	{
		            	System.out.println( "When running in --optimize mode, the --length option can not be used." );
		                HelpFormatter f = new HelpFormatter();
		                f.printHelp( "Model-based testing", opt );
		                return;
	            	}
	            	random = false;
	            }
	            else
	            {
	            	System.out.println( "When generating a test sequence, --test_sequence, either --optimize or --random must be defined." );
	                HelpFormatter f = new HelpFormatter();
	                f.printHelp( "Model-based testing", opt );
	                return;	            	
	            }

	            if ( !cl.hasOption( "g" ) )
	            {
	            	System.out.println( "Missing the graphml file, See --graphml or -g" );
	                HelpFormatter f = new HelpFormatter();
	                f.printHelp( "Model-based testing", opt );
	                return;	            	
	            }

            	graphmlFile = cl.getOptionValue( "g" );
            	
            	CLI cli = new CLI();
    			cli.generateTests();
            }
            else if ( cl.hasOption( "q" ) )
            {
	            if ( !cl.hasOption( "g" ) )
	            {
	            	System.out.println( "Missing the graphml file, See --graphml or -g" );
	                HelpFormatter f = new HelpFormatter();
	                f.printHelp( "Model-based testing", opt );
	                return;	            	
	            }
            	graphmlFile = cl.getOptionValue( "g" );
            	CLI cli = new CLI();
    			cli.generateTestMethods();	            
            }
            else if ( cl.hasOption( "e" ) )
            {
	            if ( !cl.hasOption( "g" ) )
	            {
	            	System.out.println( "Missing the graphml file, See --graphml or -g" );
	                HelpFormatter f = new HelpFormatter();
	                f.printHelp( "Model-based testing", opt );
	                return;	            	
	            }
	            
            	graphmlFile = cl.getOptionValue( "g" );
            	outputFile  = cl.getOptionValue( "e" );            	

            	try
	    		{
	    			ModelBasedTesting mtb = new ModelBasedTesting( graphmlFile );
					mtb.generatePerlCode( outputFile );
	    		}
	    		catch ( RuntimeException e )
	    		{
	    			e.printStackTrace();
	    			System.out.println( e.getMessage() );
	    		}            	
            }
            else if ( cl.hasOption( "j" ) )
            {
	            if ( !cl.hasOption( "g" ) )
	            {
	            	System.out.println( "Missing the graphml file, See --graphml or -g" );
	                HelpFormatter f = new HelpFormatter();
	                f.printHelp( "Model-based testing", opt );
	                return;	            	
	            }
	            
            	graphmlFile = cl.getOptionValue( "g" );
            	outputFile  = cl.getOptionValue( "j" );            	

            	try
	    		{
	    			ModelBasedTesting mtb = new ModelBasedTesting( graphmlFile );
					mtb.generateJavaCode( outputFile );
	    		}
	    		catch ( RuntimeException e )
	    		{
	    			e.printStackTrace();
	    			System.out.println( e.getMessage() );
	    		}            	
            }
            else if ( cl.hasOption( "l" ) )
            {
	            if ( !cl.hasOption( "g" ) )
	            {
	            	System.out.println( "Missing the graphml file, See --graphml or -g" );
	                HelpFormatter f = new HelpFormatter();
	                f.printHelp( "Model-based testing", opt );
	                return;	            	
	            }
	            
            	graphmlFile = cl.getOptionValue( "g" );
            	outputFile  = cl.getOptionValue( "l" );            	

            	try
	    		{
	    			ModelBasedTesting mbt = new ModelBasedTesting( graphmlFile );
	    			mbt.writeGraph( mbt.getGraph(), outputFile );
	    		}
	    		catch ( RuntimeException e )
	    		{
	    			e.printStackTrace();
	    			System.out.println( e.getMessage() );
	    		}            	
            }
            else if ( cl.hasOption( "p" ) )
            {
	            if ( cl.hasOption( "r" ) && cl.hasOption( "o" ) )
	            {
	            	System.out.println( "Can not set --random and --optimize at the same time." );
	                HelpFormatter f = new HelpFormatter();
	                f.printHelp( "Model-based testing", opt );
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
		                f.printHelp( "Model-based testing", opt );
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
	                f.printHelp( "Model-based testing", opt );
	                return;	            	
	            }

	            if ( !cl.hasOption( "p" ) )
	            {
	            	System.out.println( "Missing the perl script, See --perl or -p" );
	                HelpFormatter f = new HelpFormatter();
	                f.printHelp( "Model-based testing", opt );
	                return;	            	
	            }

            	graphmlFile = cl.getOptionValue( "g" );
            	perlScript  = cl.getOptionValue( "p" );            	
            	
            	CLI cli = new CLI();
    			cli.runPerlScript();
            }
            else if ( cl.hasOption( "i" ) )
            {	            
	            if ( cl.hasOption( "r" ) && cl.hasOption( "o" ) )
	            {
	            	System.out.println( "Can not set --random and --optimize at the same time." );
	                HelpFormatter f = new HelpFormatter();
	                f.printHelp( "Model-based testing", opt );
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
		                f.printHelp( "Model-based testing", opt );
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
	                f.printHelp( "Model-based testing", opt );
	                return;	            	
	            }

            	graphmlFile = cl.getOptionValue( "g" );
            	
            	CLI cli = new CLI();
    			cli.runInteractively();
			}
            else 
            {
                HelpFormatter f = new HelpFormatter();
                f.printHelp( "Model-based testing", opt );
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
	
	public void runInteractively()
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
		catch ( NumberFormatException e )
		{
			System.out.println( "Incorrect indata. Only 0, 1 or 2 is allowed." );
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

	public void runPerlScript()
	{
		ModelBasedTesting mbt = new ModelBasedTesting( graphmlFile );	
		mbt.reset();
		
		while ( true )
		{
			DirectedSparseEdge edge = mbt.getEdgeAndVertex( random, seconds );

			// getEdgeAndVertex caught an exception, and returned null
			if ( edge == null )
			{
				break;
			}

			if ( run_Perl_Subrotine( "perl " + perlScript + " " + edge.getUserDatum( LABEL_KEY ) ) != 0 )
			{
				break;
			}
			if ( run_Perl_Subrotine( "perl " + perlScript + " " + edge.getDest().getUserDatum( LABEL_KEY ) ) != 0 )
			{
				break;
			}
		}
		
		System.out.println( mbt.getStatistics() );
	}

	public int run_Perl_Subrotine( String command )
	{
		int result = 1;
		// prepare buffers for process output and error streams
		StringBuffer err=new StringBuffer();
		StringBuffer out=new StringBuffer();    
		try
		{
			Process proc=Runtime.getRuntime().exec(command);
			//create thread for reading inputStream (process' stdout)
			StreamReaderThread outThread=new StreamReaderThread(proc.getInputStream(),out);
			//create thread for reading errorStream (process' stderr)
			StreamReaderThread errThread=new StreamReaderThread(proc.getErrorStream(),err);
			//start both threads
			outThread.start();
			errThread.start();
			//wait for process to end
			result=proc.waitFor();
			//finish reading whatever's left in the buffers
			outThread.join();
			errThread.join();
			
			System.out.print(out.toString());
			
		}
		catch (Exception e)
		{
			System.out.println("Error executing "+command);
			e.printStackTrace();
		}
		return result;
	}
	
	
	public void generateTestMethods()
	{
		try
		{
			ModelBasedTesting mbt = new ModelBasedTesting( graphmlFile );	
			Vector testSequence = mbt.generateTests( false, 0);
			SortedSet set = new TreeSet();
			Iterator iterTestSequence = testSequence.iterator();
		    while ( iterTestSequence.hasNext() ) 
		    {
				String element = (String) iterTestSequence.next();
				set.add( element );
		    }

			StringBuffer strBuff = new StringBuffer();
		    Iterator setIterator = set.iterator();
		    while ( setIterator.hasNext() ) 
		    {
				String element = (String) setIterator.next();
				strBuff.append( element + "\n" );
		    }
			System.out.println( strBuff.toString() );
		}		
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	
	public void generateTests()
	{
		try
		{
			ModelBasedTesting mbt = new ModelBasedTesting( graphmlFile );	
			Vector testSequence = mbt.generateTests( random, length);
			StringBuffer strBuff = new StringBuffer();
			for (Iterator iter = testSequence.iterator(); iter.hasNext();)
			{
				String element = (String) iter.next();
				strBuff.append( element + "\n" );
			}
			System.out.println( strBuff.toString() );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}
}