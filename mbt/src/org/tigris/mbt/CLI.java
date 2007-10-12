package org.tigris.mbt;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.SortedSet;
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
	static private boolean cul_de_sac;
	static private boolean random;
	static private long seconds;
	static private long print_coverage;
	static private long length;
	static private boolean statistics;

	static Options opt = new Options();

	public static void main(String[] args)
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
					
					System.out.println( "Generate a test sequence offline.\n" );					

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
				System.out.println( "Unkown cammand: " + args[ 0 ] );
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

	            if ( cl.hasOption( "c" ) ) 
	            {
	            	cul_de_sac = false;
	            }
	            else
	            {
	            	cul_de_sac = true;
	            }

	            if ( !cl.hasOption( "g" ) )
	            {
	            	System.out.println( "Missing the input graphml file (folder), See -g (--intput_graphml)" );
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
			else if ( args[ 0 ].equals( "offline" ) )
			{
	            if ( cl.hasOption( "r" ) && cl.hasOption( "o" ) )
	            {
	            	System.out.println( "Can not set -r (--random) and -o (--optimize) at the same time." );
	            	System.out.println( "Type 'java -jar mbt.jar help offline' for help." );
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
		            	System.out.println( "When running in -r (--random) mode, the -n (--length) must also be set." );
		            	System.out.println( "Type 'java -jar mbt.jar help offline' for help." );
		                return;
	            	}
	            }
	            else if ( cl.hasOption( "o" ) ) 
	            {
	            	if ( cl.hasOption( "n" ) )
	            	{
		            	System.out.println( "When running in -o (--optimize) mode, the -n (--length) option can not be used." );
		            	System.out.println( "Type 'java -jar mbt.jar help offline' for help." );
		                return;
	            	}
	            	random = false;
	            }
	            else
	            {
	            	System.out.println( "When generating a test sequence, either -o( --optimize) or -r (--random) must be defined." );
	            	System.out.println( "Type 'java -jar mbt.jar help offline' for help." );
	                return;	            	
	            }

	            if ( cl.hasOption( "c" ) ) 
	            {
	            	cul_de_sac = false;
	            }
	            else
	            {
	            	cul_de_sac = true;
	            }

	            if ( cl.hasOption( "s" ) ) 
	            {
	            	statistics = true;
	            }
	            else
	            {
	            	statistics = false;
	            }

	            if ( !cl.hasOption( "g" ) )
	            {
	            	System.out.println( "Missing the input graphml file (folder), See -g (--intput_graphml)" );
	            	System.out.println( "Type 'java -jar mbt.jar help offline' for help." );
	                return;	            	
	            }

            	graphmlFile = cl.getOptionValue( "g" );
            	
            	CLI cli = new CLI();
    			cli.generateTests();
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

	            if ( cl.hasOption( "c" ) ) 
	            {
	            	cul_de_sac = false;
	            }
	            else
	            {
	            	cul_de_sac = true;
	            }
	            
            	graphmlFile = cl.getOptionValue( "g" );
            	outputFile  = cl.getOptionValue( "l" );            	

    			ModelBasedTesting mbt = new ModelBasedTesting();
    			Logger logger = mbt.getLogger();
    			mbt.set_cul_de_sac( cul_de_sac );
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
    			Logger logger = mbt.getLogger();
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
	
	public void runInteractively()
	{
		ModelBasedTesting mbt = new ModelBasedTesting();
		Logger logger = mbt.getLogger();
		mbt.set_cul_de_sac( cul_de_sac );
		
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
	}

	public void generateTestMethods()
	{
		ModelBasedTesting mbt = new ModelBasedTesting();
		Logger logger = mbt.getLogger();
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
	
	
	public void generateTests()
	{
		ModelBasedTesting mbt = new ModelBasedTesting();
		Logger logger = mbt.getLogger();
		mbt.set_cul_de_sac( cul_de_sac );
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
		}
	}
	
	static private void printGeneralHelpText()
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
	
	static private void buildOnlineCLI()
	{
		opt.addOption( "r", "random", false, "Run the test with a random walk. Can not be combined with --optimize." );
		opt.addOption( "o", "optimize", false, "Run the test optimized. Can not be combined with --random." );
		opt.addOption( "s", "statistics", false, "Prints the statistics of the test, at the end of the run." );
		opt.addOption( "c", "cul-de-sac", false, "Accepts graphs that has cu-de-sac and continue the test, without this flag, " +
												  "the execution of the test will be stopped." );
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

	static private void buildOfflineCLI()
	{
		opt.addOption( "r", "random", false,     "Run the test with a random walk. Can not be combined with --optimize. " + 
											     "This argument also needs the --time to be set." );
		opt.addOption( "o", "optimize", false,   "Run the test optimized. Can not be combined with --random." );
		opt.addOption( "s", "statistics", false, "Prints the statistics of the test, at the end of the run." );
		opt.addOption( "c", "cul-de-sac", false, "Accepts graphs that has a cul-de-sac. Without the flag, " +
												  "the generation of the test sequence will be stopped." );
		opt.addOption( OptionBuilder.withArgName( "file" )
				.withDescription( "The file (or folder) containing graphml formatted files." )
				.hasArg()
				.withLongOpt( "input_graphml" )
				.create( "g" ) );
		opt.addOption( OptionBuilder.withLongOpt( "length" )
				.withArgName( "=length" )
				.withDescription( "The length of the test sequence. Supply an integer, which tells MBT how many " + 
								  "vertices and edges a test sequence shall contain." )
				.hasArg()
				.create( "n" ) );
	}
	
	static private void buildMethodsCLI()
	{
		opt.addOption( OptionBuilder.withArgName( "file" )
                .withDescription( "The file (or folder) containing graphml formatted files." )
                .hasArg()
                .withLongOpt( "input_graphml" )
                .create( "g" ) );
	}
	
	static private void buildMergeCLI()
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
			opt.addOption( "c", "cul-de-sac", false, "Accepts graphs that has cu-de-sac, without this flag, " +
			  										 "an error message will be displayed." );
		
	}
	
	static private void buildSourceCLI()
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
	
	static private void printVersionInformation()
	{
		System.out.println( "org.tigris.mbt version 2.0 (r253)\n" );
		System.out.println( "org.tigris.mbt is open source software licensed under GPL" );
		System.out.println( "The software (and it's source) can be downloaded at http://mbt.tigris.org/\n" );
		System.out.println( "This package contains following software packages:" );
		System.out.println( "  crimson-1.1.3.jar            http://xml.apache.org/crimson/" );
		System.out.println( "  commons-collections-3.1.jar  http://jakarta.apache.org/commons/collections/" );
		System.out.println( "  jdom-1.0.jar                 http://www.jdom.org/" );
		System.out.println( "  log4j-1.2.8.jar              http://logging.apache.org/log4j/" );
		System.out.println( "  commons-cli-1.0.jar          http://jakarta.apache.org/commons/cli/" );
		System.out.println( "  colt-1.2.jar                 http://dsd.lbl.gov/~hoschek/colt/" );
		System.out.println( "  jung-1.7.6.jar               http://jung.sourceforge.net/" );
	}
}