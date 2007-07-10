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
import org.apache.commons.cli.MissingArgumentException;
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
	static private boolean statistics;
	private String  LABEL_KEY = "label";
	private String  INDEX_KEY = "index";
	private String  BACKTRACK = "BACKTRACK";
	private boolean backtrackingEngaged;

	public static void main(String[] args)
	{
		Options opt = new Options();

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
					System.out.println( "usage: 'java -jar mbt.jar <COMMAND> [OPTION] [ARGUMENT]'\n" );
					System.out.println( "Type 'java -jar mbt.jar help <COMMAND>' to get specific help about a command." );
					System.out.println( "Valid commands are:" );
					System.out.println( "    help" );
					System.out.println( "    dynamic" );
					System.out.println( "    static" );
					System.out.println( "    methods" );
					System.out.println( "    merge" );
					System.out.println( "    perl" );
					System.out.println( "    java_output" );
					System.out.println( "    perl_output\n" );
					System.out.println( "Type 'java -jar mbt.jar -v (--version)' to version information." );
					return;
				}
				else if ( args[ 1 ].equals( "dynamic" ) )
				{
					opt.addOption( "r", "random", false, "Run the test with a random walk. Can not be combined with --optimize. " + 
					 									 "This argument also needs the --time to be set." );
					opt.addOption( "o", "optimize", false, "Run the test optimized. Can not be combined with --random." );
					opt.addOption( "s", "statistics", false, "Prints the statistics of the test, at the end of the run." );
					opt.addOption( OptionBuilder.withArgName( "file" )
		                    .withDescription( "The file (or folder) containing graphml formatted files." )
		                    .hasArg()
		                    .withLongOpt( "input_graphml" )
		                    .create( "g" ) );
					opt.addOption( OptionBuilder.withLongOpt( "time" )
							.withArgName( "=seconds" )
		                    .withDescription( "The time in seconds for the random walk to run." )
		                    .withValueSeparator( '=' )
		                    .hasArg()
		                    .create( "t" ) );
					
					System.out.println( "Run the test dynamically.\n" +			
										"MBT will return a test sequence, one line at a time to standard output, " +
										"it will wait until a line is fed back via standard input. The data fed back can be:\n" +
										"  '0' which means, continue the test as normal\n" +
										"  '1' which means go back to previous vertex (backtracking)\n" +
										"  '2' will end the test normally\n" +
										"anything else will abort the execution.\n" );					

					HelpFormatter f = new HelpFormatter();
	                f.printHelp( "'java -jar mbt.jar dynamic [OPTION] [ARGUMENT]'", opt );
	                
					return;
				}
				else if ( args[ 1 ].equals( "static" ) )
				{
					opt.addOption( "r", "random", false, "Run the test with a random walk. Can not be combined with --optimize. " + 
					 									 "This argument also needs the --time to be set." );
					opt.addOption( "o", "optimize", false, "Run the test optimized. Can not be combined with --random." );
					opt.addOption( OptionBuilder.withArgName( "file" )
		                    .withDescription( "The file (or folder) containing graphml formatted files." )
		                    .hasArg()
		                    .withLongOpt( "input_graphml" )
		                    .create( "g" ) );
					opt.addOption( OptionBuilder.withLongOpt( "length" )
							.withArgName( "=length" )
		                    .withDescription( "The length of the test sequence. Supply an integer, which tells MBT how many " + 
		                    		"vertices a test sequence shall contain." )
		                    .hasArg()
		                    .create( "n" ) );
					
					System.out.println( "Generate a test sequence statically.\n" +			
										"MBT will return the whole test sequence in one go.\n" );					

					HelpFormatter f = new HelpFormatter();
	                f.printHelp( "'java -jar mbt.jar static [OPTION] [ARGUMENT]'", opt );
	                
					return;
				}
				else if ( args[ 1 ].equals( "methods" ) )
				{
					opt.addOption( OptionBuilder.withArgName( "file" )
		                    .withDescription( "The file (or folder) containing graphml formatted files." )
		                    .hasArg()
		                    .withLongOpt( "input_graphml" )
		                    .create( "g" ) );
					
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
					
					System.out.println( "Merge several graphml files into one single graphml file.\n" +			
										"The files to be merged, shall all exist in a single folder.\n" );					

					HelpFormatter f = new HelpFormatter();
	                f.printHelp( "'java -jar mbt.jar merge [OPTION] [ARGUMENT]'", opt );
	                
					return;
				}
				else if ( args[ 1 ].equals( "perl" ) )
				{
					opt.addOption( OptionBuilder.withArgName( "file" )
						.withDescription( "The folder containing graphml formatted files." )
						.hasArg()
						.withLongOpt( "input_graphml" )
						.create( "g" ) );
					opt.addOption( "r", "random", false, "Run the test with a random walk. Can not be combined with --optimize. " + 
						"This argument also needs the --time to be set." );
					opt.addOption( "o", "optimize", false, "Run the test optimized. Can not be combined with --random." );			
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
					
					System.out.println( "Run a perl script, implementing the model.\n" +
							"MBT will launch the desgnated perl script, and call the subroutines" +
							" in that script accorning to the model.\n" );					

					HelpFormatter f = new HelpFormatter();
	                f.printHelp( "'java -jar mbt.jar perl [OPTION] [ARGUMENT]'", opt );
	                
					return;
				}
				else if ( args[ 1 ].equals( "java_output" ) )
				{
					opt.addOption( OptionBuilder.withArgName( "file" )
						.withDescription( "The folder containing graphml formatted files." )
						.hasArg()
						.withLongOpt( "input_graphml" )
						.create( "g" ) );
					opt.addOption( OptionBuilder.withArgName( "file" )
	                    .withDescription( "The ouput java source file." )
	                    .hasArg()
	                    .withLongOpt( "source_file" )
	                    .create( "s" ) );
					
					System.out.println( "Generate java code.\n" +			
										"MBT will generate java code of all methods, that are non-existing" +
										" within a desgnated java source file. This utility helps the user" +
										" to automatically add all those methods existing in the graph(s)" +
										" but not in the implementation.\n" );					

					HelpFormatter f = new HelpFormatter();
	                f.printHelp( "'java -jar mbt.jar java_output [OPTION] [ARGUMENT]'", opt );
	                
					return;
				}
				else if ( args[ 1 ].equals( "perl_output" ) )
				{
					opt.addOption( OptionBuilder.withArgName( "file" )
						.withDescription( "The folder containing graphml formatted files." )
						.hasArg()
						.withLongOpt( "input_graphml" )
						.create( "g" ) );
					opt.addOption( OptionBuilder.withArgName( "file" )
	                    .withDescription( "The ouput perl source file." )
	                    .hasArg()
	                    .withLongOpt( "source_file" )
	                    .create( "s" ) );
					
					System.out.println( "Generate perl code.\n" +			
										"MBT will generate perl code of all methods, that are non-existing" +
										" within a desgnated perl source file. This utility helps the user" +
										" to automatically add all those methods existing in the graph(s)" +
										" but not in the implementation.\n" );					

					HelpFormatter f = new HelpFormatter();
	                f.printHelp( "'java -jar mbt.jar perl_output [OPTION] [ARGUMENT]'", opt );
	                
					return;
				}
				else
				{
					System.out.println( "Type 'java -jar mbt.jar help' for usage." );
					return;
				}
			}
			else if ( args[ 0 ].equals( "dynamic" ) )
			{
				opt.addOption( "r", "random", false, "Run the test with a random walk. Can not be combined with --optimize. " + 
				 "This argument also needs the --time to be set." );
				opt.addOption( "o", "optimize", false, "Run the test optimized. Can not be combined with --random." );
				opt.addOption( "s", "statistics", false, "Prints the statistics of the test, at the end of the run." );
				opt.addOption( OptionBuilder.withArgName( "file" )
					.withDescription( "The file (or folder) containing graphml formatted files." )
					.hasArg()
					.withLongOpt( "input_graphml" )
					.create( "g" ) );
				opt.addOption( OptionBuilder.withLongOpt( "time" )
					.withArgName( "=seconds" )
					.withDescription( "The time in seconds for the random walk to run." )
					.withValueSeparator( '=' )
					.hasArg()
					.create( "t" ) );
			}
			else if ( args[ 0 ].equals( "static" ) )
			{
				opt.addOption( "r", "random", false, "Run the test with a random walk. Can not be combined with --optimize. " + 
					"This argument also needs the --time to be set." );
				opt.addOption( "o", "optimize", false, "Run the test optimized. Can not be combined with --random." );
				opt.addOption( OptionBuilder.withArgName( "file" )
					.withDescription( "The file (or folder) containing graphml formatted files." )
					.hasArg()
					.withLongOpt( "input_graphml" )
					.create( "g" ) );
				opt.addOption( OptionBuilder.withLongOpt( "length" )
					.withArgName( "=length" )
					.withDescription( "The length of the test sequence. Supply an integer, which tells MBT how many " + 
					"vertices a test sequence shall contain." )
					.hasArg()
					.create( "n" ) );
			}
			else if ( args[ 0 ].equals( "methods" ) )
			{
				opt.addOption( OptionBuilder.withArgName( "file" )
					.withDescription( "The file (or folder) containing graphml formatted files." )
					.hasArg()
					.withLongOpt( "input_graphml" )
					.create( "g" ) );
			}
			else if ( args[ 0 ].equals( "merge" ) )
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
			else if ( args[ 0 ].equals( "perl" ) )
			{
				opt.addOption( OptionBuilder.withArgName( "file" )
					.withDescription( "The folder containing graphml formatted files." )
					.hasArg()
					.withLongOpt( "input_graphml" )
					.create( "g" ) );
				opt.addOption( "r", "random", false, "Run the test with a random walk. Can not be combined with --optimize. " + 
					"This argument also needs the --time to be set." );
				opt.addOption( "o", "optimize", false, "Run the test optimized. Can not be combined with --random." );			
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
			}
			else if ( args[ 0 ].equals( "java_output" ) )
			{
				opt.addOption( OptionBuilder.withArgName( "file" )
					.withDescription( "The folder containing graphml formatted files." )
					.hasArg()
					.withLongOpt( "input_graphml" )
					.create( "g" ) );
				opt.addOption( OptionBuilder.withArgName( "file" )
                    .withDescription( "The ouput java source file." )
                    .hasArg()
                    .withLongOpt( "source_file" )
                    .create( "s" ) );
			}
			else if ( args[ 0 ].equals( "perl_output" ) )
			{
				opt.addOption( OptionBuilder.withArgName( "file" )
					.withDescription( "The folder containing graphml formatted files." )
					.hasArg()
					.withLongOpt( "input_graphml" )
					.create( "g" ) );
				opt.addOption( OptionBuilder.withArgName( "file" )
                    .withDescription( "The ouput perl source file." )
                    .hasArg()
                    .withLongOpt( "source_file" )
                    .create( "s" ) );
			}
			else if ( args[ 0 ].equals( "-v" ) || args[ 0 ].equals( "--version" ) )
			{
				System.out.println( "org.tigris.mbt version 1.11 (r158)\n" );
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
	        
			if ( args[ 0 ].equals( "dynamic" ) )
			{
	            if ( cl.hasOption( "r" ) && cl.hasOption( "o" ) )
	            {
	            	System.out.println( "Can not set -r (--random) and -o (--optimize) at the same time." );
	            	System.out.println( "Type 'java -jar mbt.jar help dynamic' for help." );
	                return;
	            }

	            if ( !cl.hasOption( "r" ) && !cl.hasOption( "o" ) )
	            {
	            	System.out.println( "Either -r (--random) or -o (--optimize) must bet set." );
	            	System.out.println( "Type 'java -jar mbt.jar help dynamic' for help." );
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
		            	System.out.println( "When running in -r (--random) mode, the -t (--time) must also be set." );
		            	System.out.println( "Type 'java -jar mbt.jar help dynamic' for help." );
		                return;
	            	}
	            }
	            
	            if ( cl.hasOption( "o" ) ) 
	            {
	            	random = false;
	            }

	            if ( !cl.hasOption( "g" ) )
	            {
	            	System.out.println( "Missing the input graphml file (folder), See -g (--intput_graphml)" );
	            	System.out.println( "Type 'java -jar mbt.jar help dynamic' for help." );
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
			else if ( args[ 0 ].equals( "static" ) )
			{
	            if ( cl.hasOption( "r" ) && cl.hasOption( "o" ) )
	            {
	            	System.out.println( "Can not set -r (--random) and -o (--optimize) at the same time." );
	            	System.out.println( "Type 'java -jar mbt.jar help static' for help." );
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
		            	System.out.println( "Type 'java -jar mbt.jar help static' for help." );
		                return;
	            	}
	            }
	            else if ( cl.hasOption( "o" ) ) 
	            {
	            	if ( cl.hasOption( "n" ) )
	            	{
		            	System.out.println( "When running in -o (--optimize) mode, the -n (--length) option can not be used." );
		            	System.out.println( "Type 'java -jar mbt.jar help static' for help." );
		                return;
	            	}
	            	random = false;
	            }
	            else
	            {
	            	System.out.println( "When generating a test sequence, either -o( --optimize) or -r (--random) must be defined." );
	            	System.out.println( "Type 'java -jar mbt.jar help static' for help." );
	                return;	            	
	            }

	            if ( !cl.hasOption( "g" ) )
	            {
	            	System.out.println( "Missing the input graphml file (folder), See -g (--intput_graphml)" );
	            	System.out.println( "Type 'java -jar mbt.jar help static' for help." );
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
			else if ( args[ 0 ].equals( "perl" ) )
			{
	            if ( cl.hasOption( "r" ) && cl.hasOption( "o" ) )
	            {
	            	System.out.println( "Can not set -r (--random) and -o (--optimize) at the same time." );
	            	System.out.println( "Type 'java -jar mbt.jar help perl' for help." );
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
		            	System.out.println( "When running in -r (--random) mode, the -t (--time) must also be set." );
		            	System.out.println( "Type 'java -jar mbt.jar help perl' for help." );
		                return;
	            	}
	            }
	            
	            if ( cl.hasOption( "o" ) ) 
	            {
	            	random = false;
	            }

	            if ( !cl.hasOption( "g" ) )
	            {
	            	System.out.println( "Missing the input graphml file (folder), See -g (--intput_graphml)" );
	            	System.out.println( "Type 'java -jar mbt.jar help perl' for help." );
	                return;	            	
	            }

	            if ( !cl.hasOption( "p" ) )
	            {
	            	System.out.println( "Missing the perl script, See -p (--perl)" );
	            	System.out.println( "Type 'java -jar mbt.jar help perl' for help." );
	                return;	            	
	            }

            	graphmlFile = cl.getOptionValue( "g" );
            	perlScript  = cl.getOptionValue( "p" );            	
            	
            	CLI cli = new CLI();
    			cli.runPerlScript();
			}
			else if ( args[ 0 ].equals( "java_output" ) )
			{
	            if ( !cl.hasOption( "g" ) )
	            {
	            	System.out.println( "Missing the input graphml file (folder), See -g (--intput_graphml)" );
	            	System.out.println( "Type 'java -jar mbt.jar help java_output' for help." );
	                return;	            	
	            }
	            if ( !cl.hasOption( "s" ) )
	            {
	            	System.out.println( "Missing the ouput perl source file, See -s (--source_file)" );
	            	System.out.println( "Type 'java -jar mbt.jar help java_output' for help." );
	                return;	            	
	            }
	            
            	graphmlFile = cl.getOptionValue( "g" );
            	outputFile  = cl.getOptionValue( "s" );            	

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
			else if ( args[ 0 ].equals( "perl_output" ) )
			{
	            if ( !cl.hasOption( "g" ) )
	            {
	            	System.out.println( "Missing the input graphml file (folder), See -g (--intput_graphml)" );
	            	System.out.println( "Type 'java -jar mbt.jar help perl_output' for help." );
	                return;	            	
	            }
	            if ( !cl.hasOption( "s" ) )
	            {
	            	System.out.println( "Missing the ouput perl source file, See -s (--source_file)" );
	            	System.out.println( "Type 'java -jar mbt.jar help perl_output' for help." );
	                return;	            	
	            }
	            
            	graphmlFile = cl.getOptionValue( "g" );
            	outputFile  = cl.getOptionValue( "s" );            	

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
        }
		catch ( MissingArgumentException e )
        {
			System.out.println( e.getMessage() );
        	System.out.println( "Type 'java -jar mbt.jar help " + args[ 0 ] + "' for help." );
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
				DirectedSparseEdge edge = mbt.getEdge( random, seconds );
	
				// getEdgeAndVertex caught an exception, and returned null
				if ( edge == null )
				{
					break;
				}
	
				mbt.getLogger().info( "Edge: " + edge.getUserDatum( LABEL_KEY ) + ", index=" + edge.getUserDatum( INDEX_KEY ) );
				if ( edge.containsUserDatumKey( LABEL_KEY ) )
				{
					System.out.print( edge.getUserDatum( LABEL_KEY ) );
				}
				else
				{
					System.out.print( "" );
				}
				
				if ( edge.containsUserDatumKey( BACKTRACK ) )
				{
					backtrackingEngaged = true;
					System.out.println( " BACKTRACK" );
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
								"Please check the model at: '" + edge.getUserDatum( LABEL_KEY ) + "', INDEX=" + edge.getUserDatum( INDEX_KEY ) +
								" coming from: '" + edge.getSource().getUserDatum( LABEL_KEY ) + "', INDEX=" + edge.getSource().getUserDatum( INDEX_KEY ) );					
					}
					
					if ( previousVertexIndex != null )
					{
						mbt.getLogger().info("== BACKTRACKING ==" );
						mbt.SetCurrentVertex( previousVertexIndex );						
						continue;
					}					
				}
					
				mbt.getLogger().info( "Vertex: " + edge.getDest().getUserDatum( LABEL_KEY ) + ", index=" + edge.getDest().getUserDatum( INDEX_KEY ) );
				System.out.println( edge.getDest().getUserDatum( LABEL_KEY ) );
				try
				{
					checkInput( new Integer( readFromStdin() ).intValue() );
				}
				catch ( GoBackToPreviousVertexException e )
				{
					if ( backtrackingEngaged == false )
					{
						throw new RuntimeException( "Test ended with a fault. Backtracking was asked for, where the model did not allow it.\n" +
								"Please check the model at: '" + edge.getUserDatum( LABEL_KEY ) + "', INDEX=" + edge.getUserDatum( INDEX_KEY ) +
								" coming from: '" + edge.getSource().getUserDatum( LABEL_KEY ) + "', INDEX=" + edge.getSource().getUserDatum( INDEX_KEY ) );					
					}
					
					if ( previousVertexIndex != null )
					{
						mbt.getLogger().info("== BACKTRACKING ==" );
						mbt.SetCurrentVertex( previousVertexIndex );
						continue;
					}
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
				mbt.getLogger().error( e.getStackTrace() );
				System.err.println( e.getMessage() );
			}
		}
		
		if ( statistics )
		{
			mbt.getLogger().info( mbt.getStatistics() );
			System.out.println( mbt.getStatistics() );
		}
	}

	public void runPerlScript()
	{
		ModelBasedTesting mbt = new ModelBasedTesting( graphmlFile );	
		mbt.reset();
		
		while ( true )
		{
			DirectedSparseEdge edge = mbt.getEdge( random, seconds );

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