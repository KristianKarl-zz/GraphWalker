package org.tigris.mbt;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;

import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.HelpCmdLineHandler;
import jcmdline.IntParam;
import jcmdline.Parameter;
import jcmdline.StringParam;
import jcmdline.VersionCmdLineHandler;

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
	private static String cmdLineSyntax = "java -jar mbt.jar";
	private static String version = "2.0";
	
	public static void main(String[] args)
	{
	       // command line arguments
        FileParam modelParam =
            new FileParam("model",
                          "the GraphML model or directory of GraphML files to be processed",
                          ( FileParam.IS_FILE | FileParam.IS_DIR ) & FileParam.IS_READABLE,
                          FileParam.REQUIRED,
                          FileParam.MULTI_VALUED);
        StringParam generatorParam =
        	new StringParam("generator",
        					"Possible generators are:\n"+
        					"  random    Use a randomized path algorithm\n"+
        					"  dijkstra  Use Dijkstras shortest path algorithm\n"+
        					"  list      Generate all unique available Edge and State names\n"+
        					"  stub      Generate code stubs using a template.\n",
        					new String[]{"random", "dijkstra", "list", "stub"},
        					StringParam.REQUIRED);
        
        FileParam mergeOpt =
            new FileParam("output-graphml",
            		"Merge all loaded graphml files into one single graphml file",
                          FileParam.DOESNT_EXIST | FileParam.IS_WRITEABLE,
                          FileParam.OPTIONAL,
                          FileParam.SINGLE_VALUED);
        
        FileParam outputOpt =
            new FileParam("output-file",
            		"Save generated content to a file, insead of to stdout.",
                          FileParam.DOESNT_EXIST | FileParam.IS_WRITEABLE,
                          FileParam.OPTIONAL,
                          FileParam.SINGLE_VALUED);

        // command line options
        FileParam templateOpt =
            new FileParam("template",
            		"Template file to use when generating stub functions.\n" +
            		"The code generated will contain all lables/names defined " +
            		"by the vertices and edges. This enables the user to write " +
            		"templates for a multitude of scripting or programming languages. " +
            		"Any occurance of {LABEL} in the template will be replaced " +
            		"by the actual name of the edge or vertex.",
                          FileParam.EXISTS & FileParam.IS_FILE & FileParam.IS_READABLE,
                          FileParam.OPTIONAL,
                          FileParam.SINGLE_VALUED);

        
        BooleanParam statisticsOpt = 
            new BooleanParam("statistics", "Display coverage statistics at the end of the run.");
        BooleanParam culdesacOpt = 
            new BooleanParam("cul-de-sac", "Accepts graphs that has cu-de-sac and continue the test," +
            		" without this flag, the execution of the test will be stopped.");
        BooleanParam backtrackOpt = 
            new BooleanParam("backtrack", "Enable backtracking in the model.");
        BooleanParam extendedOpt = 
            new BooleanParam("extended", "Use an extended finite state machine to handle the model.");
        BooleanParam onlineOpt = 
            new BooleanParam("online", "Run the test interactively. The test sequence will be generated " + 
            		"one line at a time and then wait until a response is fed back via standard input. " +
            		"The data fed back can be:\n" +
					"  '0' which means, continue the test as normal\n" +
					"  '1' which means go back to previous vertex (backtracking)\n" +
					"  '2' will end the test normally\n" +
					"anything else will abort the execution.");
        
        StringParam reachedEdgeConditionOpt =
        	new StringParam("end-edge", "Halts generation after the specified edge has been traversed.");
        StringParam reachedStateConditionOpt =
        	new StringParam("end-state", "Halts generation after the specified state has been reached.");
        IntParam edgeCoverageConditionOpt =
        	new IntParam("end-edge-coverage", "Halts generation after the specified edge-coverage has been met.");
        IntParam stateCoverageConditionOpt =
        	new IntParam("end-state-coverage", "Halts generation after the specified state-coverage has been met.");
        IntParam testLengthConditionOpt =
        	new IntParam("end-length", "Halts generation after the specified script length has been reached.");
        IntParam testDurationConditionOpt =
        	new IntParam("end-duration", "Halts generation after the specified duration in seconds has been reached.");
        IntParam logCoverageOpt =
        	new IntParam("log-coverage", "Prints the test coverage of the graph during execution every <n>. " +
        			"The printout goes to the log file defined in mbt.properties, " + 
        			"and only, if at least INFO level is set in that same file." );

        CmdLineHandler commandLine =
            new VersionCmdLineHandler(version,
            new HelpCmdLineHandler(
            	"This program prints to stdout an abstract script generated " + 
  				"from an abstract finite state model model.\n\n" +

  				// TODO Insert more CLI usage text and add examples.
  				"<INSERT MORE USAGE TEXT AND EXAMPLES HERE>\n" +
  				"<INSERT MORE USAGE TEXT AND EXAMPLES HERE>\n" +
  				"<INSERT MORE USAGE TEXT AND EXAMPLES HERE>\n" +

  				"org.tigris.mbt is open source software licensed under GPL\n" +
  				"The software (and it's source) can be downloaded at http://mbt.tigris.org/\n\n" +
  				"This package contains following software packages:\n" +
  				"  crimson-1.1.3.jar            http://xml.apache.org/crimson/\n" +
  				"  commons-collections-3.1.jar  http://jakarta.apache.org/commons/collections/\n" +
  				"  jdom-1.0.jar                 http://www.jdom.org/\n" +
  				"  log4j-1.2.8.jar              http://logging.apache.org/log4j/\n" +
  				"  commons-cli-1.0.jar          http://jakarta.apache.org/commons/cli/\n" +
  				"  colt-1.2.jar                 http://dsd.lbl.gov/~hoschek/colt/\n" +
  				"  jung-1.7.6.jar               http://jung.sourceforge.net/\n" +
  				"  bsh-core-2.0b4.jar           http://www.beanshell.org/\n",
                cmdLineSyntax,
                "Generate test sequences from an abstract model.",
                new Parameter[] { 
            		reachedEdgeConditionOpt, 
            		reachedStateConditionOpt, 
            		edgeCoverageConditionOpt, 
            		stateCoverageConditionOpt,  
            		testLengthConditionOpt, 
            		testDurationConditionOpt, 
            		logCoverageOpt, statisticsOpt, 
            		culdesacOpt, templateOpt, outputOpt,  
            		extendedOpt, onlineOpt, backtrackOpt, mergeOpt},
                new Parameter[] { generatorParam, modelParam } ));
        
        commandLine.parse(args);
        
        ModelBasedTesting mbt = new ModelBasedTesting();
        for(Iterator i = modelParam.getValues().iterator();i.hasNext();)
        {
        	mbt.readGraph((String) i.next());
        }

        mbt.enableExtended(extendedOpt.isTrue());
       
		if(reachedEdgeConditionOpt.isSet())
		{
			mbt.addCondition(Keywords.CONDITION_REACHED_EDGE, reachedEdgeConditionOpt.getValue()); 
		}
		if(reachedStateConditionOpt.isSet())
		{
			mbt.addCondition(Keywords.CONDITION_REACHED_STATE, reachedStateConditionOpt.getValue()); 
		}
		if(edgeCoverageConditionOpt.isSet())
		{
			mbt.addCondition(Keywords.CONDITION_EDGE_COVERAGE, edgeCoverageConditionOpt.getValue()); 
		}
		if(stateCoverageConditionOpt.isSet())
		{
			mbt.addCondition(Keywords.CONDITION_STATE_COVERAGE, stateCoverageConditionOpt.getValue()); 
		}
		if(testLengthConditionOpt.isSet())
		{
			mbt.addCondition(Keywords.CONDITION_TEST_LENGTH, testLengthConditionOpt.getValue()); 
		}
		if(testDurationConditionOpt.isSet())
		{
			mbt.addCondition(Keywords.CONDITION_TEST_DURATION, testDurationConditionOpt.getValue()); 
		}
		
		String generator = generatorParam.getValue();
		if(generator.equals("list"))
		{
			mbt.setGenerator(Keywords.GENERATOR_LIST);
		}
		else if(generator.equals("random")) 
		{
			mbt.setGenerator(Keywords.GENERATOR_RANDOM);
		}
		else if(generator.equals("dijkstra")) 
		{
			mbt.setGenerator(Keywords.GENERATOR_SHORTEST);
		}
		else if(generator.equals("stub")) 
		{
			mbt.setGenerator(Keywords.GENERATOR_STUB);
		}
		
		mbt.ignoreDeadEnds(culdesacOpt.isTrue());
		if(templateOpt.isSet())
		{
			mbt.setTemplate(templateOpt.getValue());
		}

		PrintStream oldSystemOut = null;
		if(outputOpt.isSet())
		{
			oldSystemOut = System.out;
			try {
				System.setOut( new PrintStream(new FileOutputStream(outputOpt.getValue())));
			} catch (FileNotFoundException e) {
				Util.AbortIf(true, "File output error: " + e.getMessage() );
			}
		}
		
		mbt.enableBacktrack(backtrackOpt.isTrue());
		
		if(logCoverageOpt.isSet())
		{
			mbt.setLogCoverageInterval(logCoverageOpt.getValue());
		}
		if(mergeOpt.isSet())
		{
			mbt.writeModel(mergeOpt.getValue());
		}
		
		if(onlineOpt.isTrue())
		{
			while(mbt.hasNextStep())
			{
				char input = getInput(); 
				if(input == '2')
				{
					break;
				}
				if(input == '1')
				{
					mbt.backtrack();
				}
				else if(input != '0')
				{
					Util.AbortIf(true, "Input not supported: '"+ input +"'");
				}
				String[] stepPair = mbt.getNextStep();
				System.out.println(stepPair[0]);
				System.out.println(stepPair[1]);
			}
		}
		else
		{
			while(mbt.hasNextStep())
			{
				String[] stepPair = mbt.getNextStep();
				System.out.println(stepPair[0]);
				System.out.println(stepPair[1]);
			}
		}
		
		if(oldSystemOut != null)
		{
			System.setOut(oldSystemOut);
		}

		if(statisticsOpt.isTrue())
		{
			System.out.println(mbt.getStatistics());
		}
	}

	private static char getInput() 
	{
		char c = 0; 
		try {
			 int tmp = System.in.read ();
			   c = (char) tmp;
			 }
			 catch (IOException e) {}
		return c;
	}
}