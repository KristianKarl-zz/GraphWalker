package org.tigris.mbt;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;

/**
 * Command Line Interface object, to the org.tigris.mbt package.
 * The object provides a way of working with MBT using a Windows DOS,
 * or a console window in *nix system. The CLI works like the cvs or the subversion
 * command svn. The syntax is:<br><strong>java -jar mbt.jar COMMAND <options></strong><br>
 * Where mbt.jar is the whole package org.tigris.mbt built using the tool Fat Jar
 * from http://fjep.sourceforge.net/ using org.tigris.mbt.CLI as the main class<br><br>
 * <strong>Example: Print help for mbt.jar</strong><br>
 * java -jar mbt.jar help<br><br>
 * <strong>Example: Merge graphml files and save the merged result.</strong><br>
 * java -jar mbt.jar merge -g folder -l result.graphml<br><br>
 * <strong>Example: Generate offline test sequence, using random walk</strong><br>
 * java -jar mbt.jar offline -r -g folder<br><br>
 * <strong>Example: Generate online test sequence, using random walk</strong><br>
 * java -jar mbt.jar online -r -g folder<br><br>
 * <strong>Example: Print all names of edges and vertices (Sorted, and unique)</strong><br>
 * java -jar mbt.jar methods -g folder<br><br>
 *
 */
public class CLI 
{
	static private String graphmlFile;
	static private String outputFile;
	static private String templateFile;
	static private boolean random;
	static private long seconds;
	static private long print_coverage;
	static private long length;
	static private boolean statistics;
	static Logger logger = Logger.getLogger(CLI.class);
	static private Thread shutDownThread = new Thread() {
		public void run() {
			logger.info( mbt.getStatisticsVerbose() );
		}
	};
	static private ModelBasedTesting mbt = new ModelBasedTesting();

	static Options opt = new Options();

	public CLI()
	{
		Runtime.getRuntime().addShutdownHook( shutDownThread );
	}
	
	public static void main(String[] args)
	{
		try
		{
			CLI cli = new CLI();
			cli.run( args );
		}
		catch( RuntimeException e )
		{
			e.printStackTrace();
			logger.fatal(e.getMessage());
		}
		Runtime.getRuntime().removeShutdownHook( shutDownThread );
	}

	private void run(String[] args)
	{
		if ( args.length < 1 )
		{
			System.out.println( "Type 'java -jar mbt.jar help' for usage." );
			return;
		}
		else
		{
			if ( args[ 0 ].equals( "help" ) )
			{
				if ( args.length == 1 )
				{
					printGeneralHelpText();
					return;
				}
				else if ( args[ 1 ].equals( "online" ) )
				{
					buildOnlineCLI();
					
					System.out.println( "Run the test online.\n" +			
							"MBT will return a test sequence, one line at a time to standard output, " +
							"it will wait until a line is fed back via standard input. The data fed back can be:\n" +
							"  '0' which means, continue the test as normal\n" +
							"  '1' which means go back to previous vertex (backtracking)\n" +
							"  '2' will end the test normally\n" +
							"anything else will abort the execution.\n" );					

					HelpFormatter f = new HelpFormatter();
	                f.printHelp( "'java -jar mbt.jar online [OPTION] [ARGUMENT]'", opt );
	                
					return;
				}
				else if ( args[ 1 ].equals( "offline" ) )
				{
					buildOfflineCLI();
					
					System.out.println( "Generate a test sequence offline. The sequence is printed to the standard output\n" );					

					HelpFormatter f = new HelpFormatter();
	                f.printHelp( "'java -jar mbt.jar offline [OPTION] [ARGUMENT]'", opt );
	                
					return;
				}
				else if ( args[ 1 ].equals( "methods" ) )
				{
					buildMethodsCLI();
					
					System.out.println( "Generate all methods, or tests existing in the model.\n" +			
										"MBT will parse the graph(s), and return all methods (or tests) that" +
										" exists in the graph(s). The list will onyl print out a unique name once." +
										" The list is printed to stdout.\n" );					

					HelpFormatter f = new HelpFormatter();
	                f.printHelp( "'java -jar mbt.jar methods [OPTION] [ARGUMENT]'", opt );
	                
					return;
				}
				else if ( args[ 1 ].equals( "merge" ) )
				{
					buildMergeCLI();
					
					System.out.println( "Merge several graphml files into one single graphml file.\n" +			
										"The files to be merged, shall all exist in a single folder.\n" );					

					HelpFormatter f = new HelpFormatter();
	                f.printHelp( "'java -jar mbt.jar merge [OPTION] [ARGUMENT]'", opt );
	                
					return;
				}
				else if ( args[ 1 ].equals( "source" ) )
				{
					buildSourceCLI();
					
					System.out.println( "Generate code from a template.\n" +			
										"Will generate code using a template. The code generated will contain all lables/names " +
										"defined by the vertices and edges. This enables the user to write templates for a " +
										"multitude of scripting or programming languages. " +
										"The result will be printed to stdout. " +
										"There is 1 variable in the template, that will be replaced: {LABEL} ->Will be replace " +
										"by the actual name of the edge or vertex." );
					HelpFormatter f = new HelpFormatter();
	                f.printHelp( "'java -jar mbt.jar source [OPTION] [ARGUMENT]'", opt );
	                
					return;
				}
				else
				{
					System.out.println( "Type 'java -jar mbt.jar help' for usage." );
					return;
				}
			}
			else if ( args[ 0 ].equals( "online" ) )
			{
				buildOnlineCLI();
			}
			else if ( args[ 0 ].equals( "offline" ) )
			{
				buildOfflineCLI();
			}
			else if ( args[ 0 ].equals( "methods" ) )
			{
				buildMethodsCLI();
			}
			else if ( args[ 0 ].equals( "merge" ) )
			{
				buildMergeCLI();
			}
			else if ( args[ 0 ].equals( "source" ) )
			{
				buildSourceCLI();
			}
			else if ( args[ 0 ].equals( "-v" ) || args[ 0 ].equals( "--version" ) )
			{
				printVersionInformation();
				return;
			}
			else
			{
				System.out.println( "Unkown command: " + args[ 0 ] );
				System.out.println( "Type 'java -jar mbt.jar help' for usage." );
				return;				
			}
		}
			
		try 
		{			
			CommandLineParser parser = new PosixParser();
	        CommandLine cl = parser.parse( opt, args );
	        
			if ( args[ 0 ].equals( "online" ) )
			{
	            if ( cl.hasOption( "r" ) && cl.hasOption( "o" ) )
	            {
	            	System.out.println( "Can not set -r (--random) and -o (--optimize) at the same time." );
	            	System.out.println( "Type 'java -jar mbt.jar help online' for help." );
	                return;
	            }

	            if ( !cl.hasOption( "r" ) && !cl.hasOption( "o" ) )
	            {
	            	System.out.println( "Either -r (--random) or -o (--optimize) must bet set." );
	            	System.out.println( "Type 'java -jar mbt.jar help online' for help." );
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
	            		seconds = 0;
	            	}
	            }
	            
            	if ( cl.hasOption( "p" ) )
            	{
            		print_coverage = Integer.valueOf( cl.getOptionValue( "p" ) ).longValue();
            		print_coverage *= 1000;
            	}
            	else
            	{
            		print_coverage = 0;
            	}
	            	            
	            if ( cl.hasOption( "o" ) ) 
	            {
	            	random = false;
	            }

	            if ( !cl.hasOption( "g" ) )
	            {
	            	System.out.println( "Missing the input graphml file (folder), See -f (--input_graphml)" );
	            	System.out.println( "Type 'java -jar mbt.jar help online' for help." );
	                return;	            	
	            }

	            if ( cl.hasOption( "s" ) ) 
	            {
	            	statistics = true;
	            }
	            else
	            {
	            	statistics = false;
	            }
	            
            	graphmlFile = cl.getOptionValue( "g" );
            	
            	CLI cli = new CLI();
    			cli.runInteractively();
			}
			/**
			 *  OFFLINE
			 */
			else if ( args[ 0 ].equals( "offline" ) )
			{
				RunOffline( cl );
			}
			else if ( args[ 0 ].equals( "methods" ) )
			{
	            if ( !cl.hasOption( "g" ) )
	            {
	            	System.out.println( "Missing the input graphml file (folder), See -g (--intput_graphml)" );
	            	System.out.println( "Type 'java -jar mbt.jar help methods' for help." );
	                return;	            	
	            }
            	graphmlFile = cl.getOptionValue( "g" );
            	CLI cli = new CLI();
    			cli.generateTestMethods();	            
			}
			else if ( args[ 0 ].equals( "merge" ) )
			{
	            if ( !cl.hasOption( "g" ) )
	            {
	            	System.out.println( "Missing the input graphml file (folder), See -g (--intput_graphml)" );
	            	System.out.println( "Type 'java -jar mbt.jar help merge' for help." );
	                return;	            	
	            }
	            if ( !cl.hasOption( "l" ) )
	            {
	            	System.out.println( "Missing the output graphml file, See -l (--output_graphml)" );
	            	System.out.println( "Type 'java -jar mbt.jar help merge' for help." );
	                return;	            	
	            }

            	graphmlFile = cl.getOptionValue( "g" );
            	outputFile  = cl.getOptionValue( "l" );            	

    			ModelBasedTesting mbt = new ModelBasedTesting();
            	try
	    		{
	    			mbt.readGraph( graphmlFile );
	    			Util.writeGraphML( mbt.getGraph(), outputFile );
	    		}
	    		catch ( Exception e )
	    		{
	    			StringWriter sw = new StringWriter();
	    		    PrintWriter pw = new PrintWriter( sw );
	    		    e.printStackTrace( pw );
	    		    pw.close();	    		    
	    			logger.error( sw.toString() );
	    			System.err.println( e.getMessage() );
	    		}            	
			}
			else if ( args[ 0 ].equals( "source" ) )
			{
	            if ( !cl.hasOption( "g" ) )
	            {
	            	System.out.println( "Missing the input graphml file (folder), See -g (--intput_graphml)" );
	            	System.out.println( "Type 'java -jar mbt.jar help source' for help." );
	                return;	            	
	            }
	            if ( !cl.hasOption( "t" ) )
	            {
	            	System.out.println( "Missing the template file, See -t (--template)" );
	            	System.out.println( "Type 'java -jar mbt.jar help source' for help." );
	                return;	            	
	            }
	            
            	graphmlFile = cl.getOptionValue( "g" );
            	templateFile = cl.getOptionValue( "t" );            	

    			ModelBasedTesting mbt = new ModelBasedTesting();
            	try
	    		{
	    			mbt.readGraph( graphmlFile );
					Util.generateCodeByTemplate( mbt.getGraph(), templateFile );
	    		}
	    		catch ( Exception e )
	    		{
	    			StringWriter sw = new StringWriter();
	    		    PrintWriter pw = new PrintWriter( sw );
	    		    e.printStackTrace( pw );
	    		    pw.close();	    		    
	    			logger.error( sw.toString() );
	        		System.err.println( e.getMessage() );
	    		}
			}
        }
		catch ( MissingArgumentException e )
        {
			System.err.println( e.getMessage() );
        	System.err.println( "Type 'java -jar mbt.jar help " + args[ 0 ] + "' for help." );
        }
        catch ( ParseException e ) 
        {
    		System.err.println( e.getMessage() );
        }
	}


	private void checkInput( int input ) throws GoBackToPreviousVertexException
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
	
	
	private String readFromStdin()
	{
		//InputStreamReader reader = new InputStreamReader (System.in);
		//BufferedReader buf_in = new BufferedReader (reader);
		
		String str = "";
    	int	inChar	= 0;
    	boolean toggle = false;
    	while ( inChar != -1 )
	    {
    	    try
    		{
    	    	inChar = System.in.read();
    		}
    	    catch (Exception e)
    		{
    	    	break;
    		}
    	    if ( !Character.isWhitespace( (char)inChar ) )
    		{
        	    str += Character.toString((char) inChar);
        	    // Ok, so we got our single non-white space char from stdin, so we're done here. 
        	    toggle = true;
    		}
    	    if ( toggle )
    		{
    	    	break;
    		}
    	}
	    return str;
	}
	
	private void runInteractively()
	{
/*		ModelBasedTesting mbt = new ModelBasedTesting();
		Logger logger = mbt.getLogger();
		
		long startTime = System.currentTimeMillis();

    	try
		{
			mbt.initialize( graphmlFile, random, seconds );
			mbt.reset();
			
			// The unique index of the previous vertex.
			Integer previousVertexIndex = null;

			// If an edge contains the keyword BACKTARCK
			boolean backtrackingEngaged = false;
			
			// Only accept edges that has the same label AND contains the keyword BACKTARCK
			boolean acceptOnlyBacktracking = false;
			String  label = "";
			
			// Skip printing the label of the edge to stdout
			boolean skipEdgeLabel = false;
			
			 
			
			while ( true )
			{
				if ( print_coverage > 0 )
				{
					if ( System.currentTimeMillis() - startTime > print_coverage )
					{						
						logger.info( "Test coverage: " + mbt.getTestCoverage4Vertices() + "% for vertices, and: " + mbt.getTestCoverage4Edges() + "% for edges." );
						startTime = System.currentTimeMillis(); 
					}
				}
				
				DirectedSparseEdge edge = null;
				
				if ( acceptOnlyBacktracking )
				{
					if ( backtrackingEngaged == false )
					{
						throw new RuntimeException( "Internal program problem: If acceptOnlyBacktracking is true also backtrackingEngaged mut be true." );						
					}
					
					while ( true )
					{
						edge = mbt.getEdge();
						if ( edge.containsUserDatumKey( Keywords.BACKTRACK ) == false )
						{
							mbt.SetCurrentVertex( previousVertexIndex );
							continue;
						}

						if ( label == null )
						{
							label = "";
						}
						String label2Comp = (String)edge.getUserDatum( Keywords.LABEL_KEY );
						if ( label2Comp == null )
						{
							label2Comp = "";
						}

						logger.debug( "Label to match: '" + label + "', label to compare: '" + label2Comp + "'" );
						if ( label.equals( label2Comp ) == false )
						{
							mbt.SetCurrentVertex( previousVertexIndex );
							continue;
						}

						acceptOnlyBacktracking = false;
						skipEdgeLabel = true;
						break;
					}
				}
				else
				{
					edge = mbt.getEdge();
				}
	
				// getEdgeAndVertex caught an exception, and returned null
				if ( edge == null )
				{
					break;
				}
				
				if ( skipEdgeLabel == false )
				{
					logger.info( "Edge: " + edge.getUserDatum( Keywords.LABEL_KEY ) + ", index=" + 
							     edge.getUserDatum( Keywords.INDEX_KEY ) );				
					if ( edge.containsUserDatumKey( Keywords.LABEL_KEY ) )
					{
						System.out.print( edge.getUserDatum( Keywords.LABEL_KEY ) );
					}
					else
					{
						System.out.print( "" );
					}
					
					if ( edge.containsUserDatumKey( Keywords.BACKTRACK ) )
					{
						backtrackingEngaged = true;
						System.out.println( " BACKTRACK" );
						logger.info( "BACKTRACK enabled" );
					}
					else
					{
						backtrackingEngaged = false;
						System.out.println( "" );
					}
					
					try
					{
						checkInput( new Integer( readFromStdin() ).intValue() );
					}
					catch ( GoBackToPreviousVertexException e )
					{
						if ( backtrackingEngaged == false )
						{
							throw new RuntimeException( "Test ended with a fault. Backtracking was asked for, where the model did not allow it.\n" +
									"Please check the model at: '" + edge.getUserDatum( Keywords.LABEL_KEY ) + "', INDEX=" + edge.getUserDatum( Keywords.INDEX_KEY ) +
									" coming from: '" + edge.getSource().getUserDatum( Keywords.LABEL_KEY ) + "', INDEX=" + edge.getSource().getUserDatum( Keywords.INDEX_KEY ) );					
						}
						
						if ( previousVertexIndex != null )
						{
							logger.info("== BACKTRACKING FROM EDGE ==" );
							mbt.SetCurrentVertex( previousVertexIndex );						
							continue;
						}					
					}
				}
				skipEdgeLabel = false;
					
				logger.info( "Vertex: " + edge.getDest().getUserDatum( Keywords.LABEL_KEY ) + ", index=" + edge.getDest().getUserDatum( Keywords.INDEX_KEY ) );
				System.out.print( edge.getDest().getUserDatum( Keywords.LABEL_KEY ) );

				if ( edge.containsUserDatumKey( Keywords.BACKTRACK ) )
				{
					backtrackingEngaged = true;
					System.out.println( " BACKTRACK" );
					logger.info( "BACKTRACK enabled" );
				}
				else
				{
					backtrackingEngaged = false;
					System.out.println( "" );
				}

				try
				{
					checkInput( new Integer( readFromStdin() ).intValue() );
				}
				catch ( GoBackToPreviousVertexException e )
				{
					if ( backtrackingEngaged == false )
					{
						throw new RuntimeException( "Test ended with a fault. Backtracking was asked for, where the model did not allow it.\n" +
								"Please check the model at: '" + edge.getUserDatum( Keywords.LABEL_KEY ) + "', INDEX=" + edge.getUserDatum( Keywords.INDEX_KEY ) +
								" coming from: '" + edge.getSource().getUserDatum( Keywords.LABEL_KEY ) + "', INDEX=" + edge.getSource().getUserDatum( Keywords.INDEX_KEY ) );					
					}
					
					if ( previousVertexIndex != null )
					{
						acceptOnlyBacktracking = true;
						label = (String)edge.getUserDatum( Keywords.LABEL_KEY );
						logger.info("== BACKTRACKING FROM VERTEX ==" );
						mbt.SetCurrentVertex( previousVertexIndex );
						continue;
					}
				}
				previousVertexIndex = (Integer)edge.getDest().getUserDatum( Keywords.INDEX_KEY );
			}
		}
		catch ( NumberFormatException e )
		{
			System.err.println( "Incorrect indata. Only 0, 1 or 2 is allowed." );
		}
		catch ( ExecutionTimeException e )
		{
			System.out.println( "End of test. Execution time has ended." );
		}
		catch ( Exception e )
		{
			if ( e.getMessage() != "Test ended normally" )
			{
    			StringWriter sw = new StringWriter();
    		    PrintWriter pw = new PrintWriter( sw );
    		    e.printStackTrace( pw );
    		    pw.close();	    		    
    			logger.error( sw.toString() );
				System.err.println( e.getMessage() );
			}
		}
		
		if ( statistics )
		{
			logger.info( mbt.getStatistics() );
			System.out.println( mbt.getStatistics() );
		}
		*/
	}

	private void generateTestMethods()
	{
		ModelBasedTesting mbt = new ModelBasedTesting();
    	try
		{
			mbt.readGraph( graphmlFile );
			SortedSet set = new TreeSet();

			
			Object[] vertices = mbt.getGraph().getVertices().toArray();
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
			
			Object[] edges    = mbt.getGraph().getEdges().toArray();
			for (int i = 0; i < edges.length; i++) 
			{
				DirectedSparseEdge edge = (DirectedSparseEdge)edges[ i ];
				String element = (String) edge.getUserDatum( Keywords.LABEL_KEY );
				if ( element != null )
				{
					set.add( element );
				}
			}

			StringBuffer strBuff = new StringBuffer();
		    Iterator setIterator = set.iterator();
		    while ( setIterator.hasNext() ) 
		    {
				String element = (String) setIterator.next();
				strBuff.append( element + "\n" );
		    }
			System.out.print( strBuff.toString() );
		}		
		catch ( Exception e )
		{
			StringWriter sw = new StringWriter();
		    PrintWriter pw = new PrintWriter( sw );
		    e.printStackTrace( pw );
		    pw.close();	    		    
			logger.error( sw.toString() );
    		System.err.println( e.getMessage() );
		}
	}
	
	
	private void generateTests()
	{/*
		ModelBasedTesting mbt = new ModelBasedTesting();
		Logger logger = mbt.getLogger();
    	try
		{
			mbt.readGraph( graphmlFile );
			mbt.generateTests( random, length);
		}
		catch ( Exception e )
		{
			StringWriter sw = new StringWriter();
		    PrintWriter pw = new PrintWriter( sw );
		    e.printStackTrace( pw );
		    pw.close();	    		    
			logger.error( sw.toString() );
    		System.err.println( e.getMessage() );
		}*/
	}
	
	private void printGeneralHelpText()
	{
		System.out.println( "usage: 'java -jar mbt.jar <COMMAND> [OPTION] [ARGUMENT]'\n" );
		System.out.println( "Type 'java -jar mbt.jar help <COMMAND>' to get specific help about a command." );
		System.out.println( "Valid commands are:" );
		System.out.println( "    help" );
		System.out.println( "    online" );
		System.out.println( "    offline" );
		System.out.println( "    methods" );
		System.out.println( "    merge" );
		System.out.println( "    source\n" );
		System.out.println( "Type 'java -jar mbt.jar -v (--version)' for version information." );
	}
	
	private void buildOnlineCLI()
	{
		opt.addOption( "r", "random", false, "Run the test with a random walk. Can not be combined with --optimize." );
		opt.addOption( "o", "optimize", false, "Run the test optimized. Can not be combined with --random." );
		opt.addOption( "s", "statistics", false, "Prints the statistics of the test, at the end of the run." );
		opt.addOption( OptionBuilder.withArgName( "file" )
                .withDescription( "The file (or folder) containing graphml formatted files." )
                .hasArg()
                .withLongOpt( "input_graphml" )
                .create( "g" ) );
		opt.addOption( OptionBuilder.withLongOpt( "time" )
				.withArgName( "=seconds" )
                .withDescription( "The time in seconds for the random walk to run. If 0, the test runs forever." )
                .withValueSeparator( '=' )
                .hasArg() 
                .create( "t" ) );
		opt.addOption( OptionBuilder.withLongOpt( "print-coverage" )
				.withArgName( "=seconds" )
                .withDescription( "Prints the test coverage of the graph during execution every <=seconds>. " +
						 "The printout goes to the log file defined in mbt.properties, " + 
						 "and only, if at least INFO level is set in that same file." )
                .withValueSeparator( '=' )
                .hasArg()
                .create( "p" ) );
	}

	private void buildOfflineCLI()
	{
		opt.addOption( "a", "statistics", false, "Prints the statistics of the test, at the end of the run." );
		opt.addOption( "x", "extended",   false, "Use an extended finite state machine to handle the model." );
		opt.addOption( OptionBuilder.withArgName( "generator" )
                .withDescription( "The generator to be used when traversing the model. The generator can be " +
                		          "either 'random' which will generate a sequence using a randomized path algorithm. " +
                		          "or 'dijkstra' which will generate a sequence using Dijkstras shortest path algorithm." )
                .hasArg()
                .withLongOpt( "generator" )
                .create( "g" ) );
		opt.addOption( OptionBuilder.withArgName( "file" )
                .withDescription( "The file (or folder) containing graphml formatted files." )
                .hasArg()
                .withLongOpt( "input_graphml" )
                .create( "f" ) );
		opt.addOption( OptionBuilder.withArgName( "length" )
                .withDescription( "Stop condition. Halts generation after the specified length of the test sequence is reached. " +
                		          "Supply an integer, which tells MBT how many vertices and edges a test sequence shall contain." )
                .hasArg()
                .withLongOpt( "length" )
                .create( "l" ) );
		opt.addOption( OptionBuilder.withArgName( "edge-coverage" )
                .withDescription( "Stop condition. Halts generation after the specified edge-coverage has been met. " +
		                          "The given value shall be a real number between 0 and 100" )
                .hasArg()
                .withLongOpt( "edge-coverage" )
                .create( "e" ) );
		opt.addOption( OptionBuilder.withArgName( "state-coverage" )
                .withDescription( "Stop condition. Halts generation after the specified state-coverage has been met. " +
		                          "The given value shall be a real number between 0 and 100" )
                .hasArg()
                .withLongOpt( "state-coverage" )
                .create( "s" ) );
		opt.addOption( OptionBuilder.withArgName( "end-edge" )
                .withDescription( "Stop condition. Halts generation after the specified edge has been traversed." )
                .hasArg()
                .withLongOpt( "end-edge" )
                .create( "d" ) );
		opt.addOption( OptionBuilder.withArgName( "end-state" )
                .withDescription( "Stop condition. Halts generation after the specified state has been reached." )
                .hasArg()
                .withLongOpt( "end-state" )
                .create( "t" ) );
		opt.addOption( OptionBuilder.withArgName( "log-coverage" )
				.withDescription( "Prints the test coverage of the graph during execution every " +
			              "<n>. The printout goes to the log file defined in " +
			              "mbt.properties, and only, if at least INFO level is set in " +
			              "that same file." )
                .hasArg()
                .withLongOpt( "log-coverage" )
                .create( "o" ) );
	}
	
	private void buildMethodsCLI()
	{
		opt.addOption( OptionBuilder.withArgName( "file" )
                .withDescription( "The file (or folder) containing graphml formatted files." )
                .hasArg()
                .withLongOpt( "input_graphml" )
                .create( "g" ) );
	}
	
	private void buildMergeCLI()
	{
		opt.addOption( OptionBuilder.withArgName( "file" )
				.withDescription( "The folder containing graphml formatted files." )
				.hasArg()
				.withLongOpt( "input_graphml" )
				.create( "g" ) );
			opt.addOption( OptionBuilder.withArgName( "file" )
				.withDescription( "The ouput file is where the merged graphml file is written to." )
				.hasArg()
				.withLongOpt( "output_graphml" )
				.create( "l" ) );
		
	}
	
	private void buildSourceCLI()
	{
		opt.addOption( OptionBuilder.withArgName( "file" )
			.withDescription( "The folder containing graphml formatted files." )
			.hasArg()
			.withLongOpt( "input_graphml" )
			.create( "g" ) );
		opt.addOption( OptionBuilder.withArgName( "file" )
            .withDescription( "The template file" )
            .hasArg()
            .withLongOpt( "template" )
            .create( "t" ) );
	}
	
	private void printVersionInformation()
	{
		System.out.println( "org.tigris.mbt version 2.0 (revision 313)\n" );
		System.out.println( "org.tigris.mbt is open source software licensed under GPL" );
		System.out.println( "The software (and it's source) can be downloaded from http://mbt.tigris.org/\n" );
		System.out.println( "This package contains following software packages:" );
		System.out.println( "  crimson-1.1.3.jar            http://xml.apache.org/crimson/" );
		System.out.println( "  commons-collections-3.1.jar  http://jakarta.apache.org/commons/collections/" );
		System.out.println( "  jdom-1.0.jar                 http://www.jdom.org/" );
		System.out.println( "  log4j-1.2.8.jar              http://logging.apache.org/log4j/" );
		System.out.println( "  commons-cli-1.1.jar          http://commons.apache.org/cli/" );
		System.out.println( "  colt-1.2.jar                 http://dsd.lbl.gov/~hoschek/colt/" );
		System.out.println( "  jung-1.7.6.jar               http://jung.sourceforge.net/" );
		System.out.println( "  bsh-core-2.0b4.jar           http://www.beanshell.org/" );
	}
	
	private void RunOffline( CommandLine cl )
	{
		/**
		 * Get the model from the graphml file (or folder)
		 */
        if ( !cl.hasOption( "f" ) )
        {
        	System.err.println( "Missing the input graphml file (or folder), See -f (--intput_graphml)" );
        	System.err.println( "Type 'java -jar mbt.jar help offline' for help." );
            return;	            	
        }
        mbt.readGraph( cl.getOptionValue( "f" ) );
        
        /**
		 * Set EFSM
		 */
        mbt.enableExtended( cl.hasOption( "x" ) );

		/**
		 * Set stop condition
		 */
        if ( !cl.hasOption( "d" ) &&
           	 !cl.hasOption( "t" ) &&
        	 !cl.hasOption( "e" ) &&
        	 !cl.hasOption( "s" ) &&
        	 !cl.hasOption( "l" ) )
        {
        	System.err.println( "A stop condition must be supplied, See options: -d -t -e -s -l" );
        	System.err.println( "Type 'java -jar mbt.jar help offline' for help." );
            return;	            	
        }
        if( cl.hasOption( "d" ) )
		{
			mbt.addCondition( Keywords.CONDITION_REACHED_EDGE, cl.getOptionValue( "g" ) ); 
		}
		if( cl.hasOption( "t" ) )
		{
			mbt.addCondition( Keywords.CONDITION_REACHED_STATE, cl.getOptionValue( "t" ) ); 
		}
		if( cl.hasOption( "e" ) )
		{
			mbt.addCondition( Keywords.CONDITION_EDGE_COVERAGE, cl.getOptionValue( "e" ) ); 
		}
		if( cl.hasOption( "s" ) )
		{
			mbt.addCondition( Keywords.CONDITION_STATE_COVERAGE, cl.getOptionValue( "s" ) ); 
		}
		if( cl.hasOption( "l" ) )
		{
			mbt.addCondition( Keywords.CONDITION_TEST_LENGTH, cl.getOptionValue( "l" ) ); 
		}

		/**
		 * Get the generator
		 */
        if ( !cl.hasOption( "g" ) )
        {
        	System.err.println( "Missing the generator, See -g (--generator)" );
        	System.err.println( "Type 'java -jar mbt.jar help offline' for help." );
            return;	            	
        }
        else if ( cl.getOptionValue( "g" ).equals( "random" ) )
		{
			mbt.setGenerator( Keywords.GENERATOR_RANDOM );
		}
        else if ( cl.getOptionValue( "g" ).equals( "dijkstra" ) )
		{
			mbt.setGenerator( Keywords.GENERATOR_SHORTEST );
		}
        else
        {
        	System.err.println( "Incorrect generator supplied, See -g (--generator)" );
        	System.err.println( "Type 'java -jar mbt.jar help offline' for help." );
            return;	            	
        }

		Timer t = new Timer();
		if( cl.hasOption( "o" ) )
		{
			logger.info( "Append coverage to log every: " + Integer.valueOf( cl.getOptionValue( "o" ) ).longValue() + " seconds" );
			
			t.schedule(	new TimerTask()	
			{
				public void run() 
				{
					logger.info( mbt.getStatisticsCompact() );
				}
			}, 500, Integer.valueOf( cl.getOptionValue( "o" ) ).longValue() * 1000 );
		}
		
		while( mbt.hasNextStep() )
		{
			String[] stepPair = mbt.getNextStep();
			System.out.println(stepPair[0]);
			logger.debug( "Execute: " + stepPair[0] );
			System.out.println(stepPair[1]);
			logger.debug( "Verify: " + stepPair[1] );
		}
		
		if( cl.hasOption( "a" ) )
		{
			System.out.println( mbt.getStatisticsString() );
		}
		
		t.cancel();
	}
}