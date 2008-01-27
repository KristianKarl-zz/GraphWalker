package org.tigris.mbt;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;

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
	public static class VerboseStatisticsLogger extends Thread
	{
		private ModelBasedTesting modelBasedTesting;
		
		VerboseStatisticsLogger( ModelBasedTesting modelBasedTesting )
		{
			this.modelBasedTesting = modelBasedTesting;
		}
		
		public void run() {
			logger.info( modelBasedTesting.getStatisticsVerbose() );
		}
	}
	static Logger logger = Logger.getLogger(CLI.class);
	
	private ModelBasedTesting mbt;
	private Timer timer;
	private Options opt = new Options();

	public CLI()
	{
		mbt = new ModelBasedTesting();
	}
	
	public ModelBasedTesting getMbt() 
	{
		return mbt;
	}
	
	public static void main(String[] args)
	{
		CLI cli = new CLI();
		Thread shutDownThread = new CLI.VerboseStatisticsLogger(cli.getMbt());
		try 
		{
			Runtime.getRuntime().addShutdownHook( shutDownThread );
		}
		catch ( IllegalArgumentException e )
		{
			logger.debug( "Hook previously registered" );
		}
		try
		{
			cli.run( args );
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
				else if ( args[ 1 ].equals( "requirements" ) )
				{
					buildRequirementsCLI();
					
					System.out.println( "Print a list of requiremnts found in the model.\n" );					

					HelpFormatter f = new HelpFormatter();
	                f.printHelp( "'java -jar mbt.jar online [OPTION] [ARGUMENT]'", opt );
	                
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
			else if ( args[ 0 ].equals( "requirements" ) )
			{
				buildRequirementsCLI();
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
			timer = new Timer();
			CommandLineParser parser = new PosixParser();
	        CommandLine cl = parser.parse( opt, args );
	        
			/**
			 *  Command: requirements
			 */
			if ( args[ 0 ].equals( "requirements" ) )
			{
				RunCommandRequirements( cl );
			}
			/**
			 *  Command: online
			 */
			if ( args[ 0 ].equals( "online" ) )
			{
				RunCommandOnline( cl );
			}
			/**
			 *  Command: offline
			 */
			else if ( args[ 0 ].equals( "offline" ) )
			{
				RunCommandOffline( cl );
			}
			/**
			 *  Command: methods
			 */
			else if ( args[ 0 ].equals( "methods" ) )
			{
				RunCommandMethods( cl );
			}
			/**
			 *  Command: merge
			 */
			else if ( args[ 0 ].equals( "merge" ) )
			{
				RunCommandMerge( cl );
			}
			/**
			 *  Command: source
			 */
			else if ( args[ 0 ].equals( "source" ) )
			{
				RunCommandSource( cl );
			}
        }		
		catch ( ArrayIndexOutOfBoundsException e )
        {
			System.err.println( "The arguments for either the generator, or the stop-condition, is incorrect." );
			System.err.println( "Example: java -jar mbt.jar offline -f ../demo/model/UC01.graphml -s EDGE_COVERAGE:100 -g SHORTEST" );
        	System.err.println( "Type 'java -jar mbt.jar help " + args[ 0 ] + "' for help." );
        }
		catch ( Exception e )
        {
			System.err.println( e.getMessage() );
        	System.err.println( "Type 'java -jar mbt.jar help " + args[ 0 ] + "' for help." );
        }
        finally
        {
    		timer.cancel();
        }
	}

	private char getInput() 
	{
		char c = 0; 
		try 
		{
			while(c != '0' && c != '1' && c != '2')
			{
				int tmp = System.in.read ();
				c = (char) tmp;
			}
		}
		catch ( IOException e ) {}
		return c;
	}
	
	private void printGeneralHelpText()
	{
		System.out.println( "usage: 'java -jar mbt.jar <COMMAND> [OPTION] [ARGUMENT]'\n" );
		System.out.println( "Type 'java -jar mbt.jar help <COMMAND>' to get specific help about a command." );
		System.out.println( "Valid commands are:" );
		System.out.println( "    help" );
		System.out.println( "    online" );
		System.out.println( "    offline" );
		System.out.println( "    requirements" );
		System.out.println( "    methods" );
		System.out.println( "    merge" );
		System.out.println( "    source\n" );
		System.out.println( "Type 'java -jar mbt.jar -v (--version)' for version information." );
	}
	
	private void buildRequirementsCLI()
	{
		opt.addOption( OptionBuilder.withArgName( "file" )
                .withDescription( "The file (or folder) containing graphml formatted files." )
                .hasArg()
                .withLongOpt( "input_graphml" )
                .create( "f" ) );
	}

	private String generateListOfValidStopConditions()
	{
		String list = "";
		Vector stopConditions = Keywords.getStopConditions();
		for (int i = 0; i < stopConditions.size(); i++) {
			list += (String)stopConditions.get( i ) + "\n";
		}
		return list;
	}
	
	private String generateListOfValidGenerators()
	{
		String list = "";
		Vector generators = Keywords.getGenerators();
		for (int i = 0; i < generators.size(); i++) {
			list += (String)generators.get( i ) + "\n";
		}
		return list;
	}
	
	private void buildOnlineCLI()
	{
		opt.addOption( "a", "statistics", false, "Prints the statistics of the test, at the end of the run." );
		opt.addOption( "x", "extended",   false, "Use an extended finite state machine to handle the model." );
		opt.addOption( "b", "backtrack",  false, "Enable backtracking in the model." );
		opt.addOption( OptionBuilder.withArgName( "stop-condition" )
                .withDescription( "Stop condition. Halts generation after the specified stop-conditon(s) has been met. " +
		                          "At least 1 condition can be given. If more are given, the condition that meets" +
		                          "it's stop-condition first, will cause the generation to halt.\n" +
		                  		"Each stop condition must be followed with a : and then an integer between 1-100, representing the" + 
		                		" percentage that the stop-condition should reach. Every stop-condition is seperated with a |. " +
		                		"A list of valid stop-conditions are:\n" + generateListOfValidStopConditions() )
                .hasArg()
                .create( "s" ) );
		opt.addOption( OptionBuilder.withArgName( "generator" )
                .withDescription( "The generator to be used when traversing the model. At least 1 generator must be given. " +
                		"Every generator is seperated with a |.\nA list of valid generators are:\n" + generateListOfValidGenerators() )
                .hasArg()
                .create( "g" ) );
		opt.addOption( OptionBuilder.withArgName( "file" )
                .withDescription( "The file (or folder) containing graphml formatted files." )
                .hasArg()
                .withLongOpt( "input_graphml" )
                .create( "f" ) );
		opt.addOption( OptionBuilder.withArgName( "log-coverage" )
				.withDescription( "Prints the test coverage of the graph during execution every " +
			              "<n>. The printout goes to the log file defined in " +
			              "mbt.properties, and only, if at least INFO level is set in " +
			              "that same file." )
                .hasArg()
                .withLongOpt( "log-coverage" )
                .create( "o" ) );
		opt.addOption( "c", "class_name", true, "Optional class name to use for test execution." );
	}

	/**
	 * Build the command offline command line parser
	 */
	private void buildOfflineCLI()
	{
		opt.addOption( "a", false, "Prints the statistics of the test, at the end of the run." );
		opt.addOption( "x", false, "Use an extended finite state machine to handle the model." );
		opt.addOption( OptionBuilder.withArgName( "stop-condition" )
                .withDescription( "Stop condition. Halts generation after the specified stop-conditon(s) has been met. " +
		                          "At least 1 condition can be given. If more are given, the condition that meets" +
		                          "it's stop-condition first, will cause the generation to halt.\n" +
		                  		"Each stop condition must be followed with a : and then an integer between 1-100, representing the" + 
		                		" percentage that the stop-condition should reach. Every stop-condition is seperated with a |. " +
		                		"A list of valid stop-conditions are:\n" + generateListOfValidStopConditions() )
                .hasArg()
                .create( "s" ) );
		opt.addOption( OptionBuilder.withArgName( "generator" )
                .withDescription( "The generator to be used when traversing the model. At least 1 generator must be given. " +
                		"Every generator is seperated with a |.\nA list of valid generators are:\n" + generateListOfValidGenerators() )
                .hasArg()
                .create( "g" ) );
		opt.addOption( OptionBuilder.withArgName( "file" )
                .withDescription( "The file (or folder) containing graphml formatted files." )
                .hasArg()
                .create( "f" ) );
		opt.addOption( OptionBuilder.withArgName( "log-coverage" )
				.withDescription( "Prints the test coverage of the graph during execution every " +
			              "<n>. The printout goes to the log file defined in " +
			              "mbt.properties, and only, if at least INFO level is set in " +
			              "that same file." )
                .hasArg()
                .create( "o" ) );
	}
	
	private void buildMethodsCLI()
	{
		opt.addOption( OptionBuilder.withArgName( "file" )
                .withDescription( "The file (or folder) containing graphml formatted files." )
                .hasArg()
                .withLongOpt( "input_graphml" )
                .create( "f" ) );
	}
	
	/**
	 * Build the command merge command line parser
	 */
	private void buildMergeCLI()
	{
		opt.addOption( OptionBuilder.withArgName( "file" )
                .withDescription( "The file (or folder) containing graphml formatted files." )
                .hasArg()
                .withLongOpt( "input_graphml" )
                .create( "f" ) );
	}
	
	/**
	 * Build the command source command line parser
	 */
	private void buildSourceCLI()
	{
		opt.addOption( OptionBuilder.withArgName( "file" )
                .withDescription( "The file (or folder) containing graphml formatted files." )
                .hasArg()
                .withLongOpt( "input_graphml" )
                .create( "f" ) );
		opt.addOption( OptionBuilder.withArgName( "file" )
                .withDescription( "The template file" )
                .hasArg()
                .withLongOpt( "template" )
                .create( "t" ) );
	}
	
	/**
	 * Print version information
	 */
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
		System.out.println( "  bsh-2.0b4.jar                http://www.beanshell.org/" );
	}
	
	/**
	 * Run the offline command
	 */
	private void RunCommandOffline( CommandLine cl )
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
        getMbt().readGraph( cl.getOptionValue( "f" ) );
        
        /**
		 * Set EFSM
		 */
        getMbt().enableExtended( cl.hasOption( "x" ) );

		/**
		 * Check that a stop-condition exists
		 */
        if ( !cl.hasOption( "s" ) )
        {
        	System.err.println( "A stop condition must be supplied, See option -s" );
        	System.err.println( "Type 'java -jar mbt.jar help offline' for help." );
            return;	            	
        }
		/**
		 * Check that a generator exists
		 */
        if ( !cl.hasOption( "g" ) )
        {
        	System.err.println( "Missing the generator, See option -g" );
        	System.err.println( "Type 'java -jar mbt.jar help offline' for help." );
            return;	            	
        }

        /*
         * Set the stop-conditions(s)
         */
		if( cl.hasOption( "s" ) )
		{
			String argument = cl.getOptionValue( "s" );
			String[] stopConditions = argument.split( "\\|" );
			for (int i = 0; i < stopConditions.length; i++) {
				String[] stopCondition = stopConditions[ i ].trim().split( ":" );
				getMbt().addAlternativeCondition( Keywords.getKeyword( stopCondition[ 0 ].trim() ), stopCondition[ 1 ].trim() );
			}
		}

        /*
         * Set the generators(s)
         */
        if ( cl.hasOption( "g" ) )
		{
			String argument = cl.getOptionValue( "g" );
			String[] genrators = argument.split( "\\|" );
			for (int i = 0; i < genrators.length; i++) {
				getMbt().setGenerator( Keywords.getKeyword( genrators[ 0 ].trim() ) );
			}
		}

		if( cl.hasOption( "o" ) )
		{
			logger.info( "Append coverage to log every: " + Integer.valueOf( cl.getOptionValue( "o" ) ).longValue() + " seconds" );
			
			timer.schedule(	new TimerTask()	
			{
				public void run() 
				{
					logger.info( getMbt().getStatisticsCompact() );
				}
			}, 500, Integer.valueOf( cl.getOptionValue( "o" ) ).longValue() * 1000 );
		}
		
		while( getMbt().hasNextStep() )
		{
			String[] stepPair = getMbt().getNextStep();
			System.out.println(stepPair[0]);
			logger.debug( "Execute: " + stepPair[0] );
			System.out.println(stepPair[1]);
			logger.debug( "Verify: " + stepPair[1] );
		}
		
		if( cl.hasOption( "a" ) )
		{
			System.out.println( getMbt().getStatisticsString() );
		}
	}

	/**
	 * Run the online command
	 */
	private void RunCommandOnline( CommandLine cl )
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
        getMbt().readGraph( cl.getOptionValue( "f" ) );
        
        /**
		 * Set EFSM
		 */
        getMbt().enableExtended( cl.hasOption( "x" ) );

		/**
		 * Check that a stop-condition exists
		 */
        if ( !cl.hasOption( "s" ) )
        {
        	System.err.println( "A stop condition must be supplied, See options: -d -t -e -s -l" );
        	System.err.println( "Type 'java -jar mbt.jar help offline' for help." );
            return;	            	
        }
		/**
		 * Check that a generator exists
		 */
        if ( !cl.hasOption( "g" ) )
        {
        	System.err.println( "Missing the generator, See -g (--generator)" );
        	System.err.println( "Type 'java -jar mbt.jar help offline' for help." );
            return;	            	
        }

        /*
         * Set the stop-conditions(s)
         */
		if( cl.hasOption( "s" ) )
		{
			String argument = cl.getOptionValue( "s" );
			String[] stopConditions = argument.split( "\\|" );
			for (int i = 0; i < stopConditions.length; i++) {
				String[] stopCondition = stopConditions[ i ].trim().split( ":" );
				getMbt().addAlternativeCondition( Keywords.getKeyword( stopCondition[ 0 ].trim() ), stopCondition[ 1 ].trim() );
			}
		}

        /*
         * Set the generators(s)
         */
        if ( cl.hasOption( "g" ) )
		{
			String argument = cl.getOptionValue( "g" );
			String[] genrators = argument.split( "\\|" );
			for (int i = 0; i < genrators.length; i++) {
				getMbt().setGenerator( Keywords.getKeyword( genrators[ 0 ].trim() ) );
			}
		}
        
        /**
		 * Set backtracking
		 */
        getMbt().enableBacktrack( cl.hasOption( "b" ) );

		Timer t = new Timer();
		if( cl.hasOption( "o" ) )
		{
			logger.info( "Append coverage to log every: " + Integer.valueOf( cl.getOptionValue( "o" ) ).longValue() + " seconds" );
			
			t.schedule(	new TimerTask()	
			{
				public void run() 
				{
					logger.info( getMbt().getStatisticsCompact() );
				}
			}, 500, Integer.valueOf( cl.getOptionValue( "o" ) ).longValue() * 1000 );
		}
	
		if( cl.hasOption( "c" ) )
		{
			getMbt().executePath( cl.getOptionValue( "c" ) );
		}
		else
		{
			boolean firstLine = true;
			char input = 0;
			String[] stepPair = null;
			while(getMbt().hasNextStep())
			{
				if ( firstLine == false )
				{
					input = getInput();
					logger.debug("Recieved: '"+ input+"'");
					if(input == '2')
					{
						break;
					}
					if(input == '1')
					{
						getMbt().backtrack();
						continue;
					}
				}
				else
				{
					firstLine = false;
				}
				stepPair = getMbt().getNextStep();
				logger.debug("Execute: " + stepPair[0]);
				System.out.println(stepPair[0]);
	
				input = getInput(); 
				logger.debug("Recieved: '"+ input+"'");
				if(input == '2')
				{
					break;
				}
				if(input == '1')
				{
					getMbt().backtrack();
					continue;
				}
				logger.debug("Verify: " + stepPair[1]);
				System.out.println(stepPair[1]);
			}
		}
		if( cl.hasOption( "a" ) )
		{
			System.out.println( getMbt().getStatisticsString() );
		}
		
		t.cancel();
	}

	/**
	 * Run the source command
	 */
	private void RunCommandSource( CommandLine cl )
	{
		if ( !cl.hasOption( "f" ) )
	    {
	    	System.out.println( "Missing the input graphml file (folder). See -f (--intput_graphml)" );
	    	System.out.println( "Type 'java -jar mbt.jar help source' for help." );
	        return;	            	
	    }
		if ( !cl.hasOption( "t" ) )
	    {
	    	System.out.println( "Missing the template file. See -t (--template)" );
	    	System.out.println( "Type 'java -jar mbt.jar help source' for help." );
	        return;	            	
	    }
		getMbt().readGraph( cl.getOptionValue( "f" ) );
		getMbt().setTemplate( cl.getOptionValue( "t" ) );
		getMbt().setGenerator( Keywords.GENERATOR_STUB );
	
		while( getMbt().hasNextStep() )
		{
			String[] stepPair = getMbt().getNextStep();
			System.out.println(stepPair[0]);
		}
	}

	/**
	 * Run the requirements command
	 */
	private void RunCommandRequirements( CommandLine cl )
	{
		if ( !cl.hasOption( "f" ) )
	    {
	    	System.out.println( "Missing the input graphml file (folder), See -f (--intput_graphml)" );
	    	System.out.println( "Type 'java -jar mbt.jar help requirements' for help." );
	        return;	            	
	    }
		getMbt().readGraph( cl.getOptionValue( "f" ) );
		getMbt().setGenerator( Keywords.GENERATOR_REQUIREMENTS );
		
		while( getMbt().hasNextStep() )
		{
			String[] stepPair = getMbt().getNextStep();
			System.out.println(stepPair[0]);
		}
	}

	/**
	 * Run the methods command
	 */
	private void RunCommandMethods( CommandLine cl )
	{
		if ( !cl.hasOption( "f" ) )
	    {
	    	System.out.println( "Missing the input graphml file (folder), See -f (--intput_graphml)" );
	    	System.out.println( "Type 'java -jar mbt.jar help methods' for help." );
	        return;	            	
	    }
		getMbt().readGraph( cl.getOptionValue( "f" ) );
		getMbt().setGenerator( Keywords.GENERATOR_LIST );
		
		while( getMbt().hasNextStep() )
		{
			String[] stepPair = getMbt().getNextStep();
			System.out.println(stepPair[0]);
		}
	}

	/**
	 * Run the merge command
	 */
	private void RunCommandMerge( CommandLine cl )
	{
		if ( !cl.hasOption( "f" ) )
	    {
	    	System.out.println( "Missing the input graphml file (folder), See -f (--intput_graphml)" );
	    	System.out.println( "Type 'java -jar mbt.jar help merge' for help." );
	        return;	            	
	    }
		getMbt().readGraph( cl.getOptionValue( "f" ) );
		getMbt().writeModel( System.out );
	}
}
	