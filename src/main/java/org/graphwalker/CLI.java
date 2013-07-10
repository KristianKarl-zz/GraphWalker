// This file is part of the GraphWalker java package
// The MIT License
//
// Copyright (c) 2010 graphwalker.org
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

package org.graphwalker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.xml.ws.Endpoint;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.log4j.Logger;
import org.graphwalker.Keywords.Generator;
import org.graphwalker.Keywords.StopCondition;
import org.graphwalker.analyze.Analyze;
import org.graphwalker.conditions.AlternativeCondition;
import org.graphwalker.exceptions.GeneratorException;
import org.graphwalker.exceptions.StopConditionException;
import org.graphwalker.io.PrintHTMLTestSequence;
import org.jdom2.JDOMException;

/**
 * Command Line Interface object, to the org.graphwalker package. The object provides a way of
 * working with MBT using a Windows DOS, or a console window in *nix system. The CLI works like the
 * cvs or the subversion command svn. The syntax is:<br>
 * 
 * <pre>
 * java -jar graphwalker.jar COMMAND <options>
 * </pre>
 * 
 * Where graphwalker.jar is the whole package org.graphwalker using org.graphwalker.CLI as the main
 * class<br>
 * <br>
 * <strong>Example:</strong> Print help for graphwalker.jar<br>
 * 
 * <pre>
 * java -jar graphwalker.jar help
 * </pre>
 * 
 * <br>
 * <strong>Example:</strong> Merge graphml files and save the merged result.<br>
 * 
 * <pre>
 * java -jar graphwalker.jar merge -f folder
 * </pre>
 * 
 * <br>
 * <strong>Example:</strong> Generate offline test sequence, using random walk<br>
 * 
 * <pre>
 * java -jar graphwalker.jar offline -f folder -g RANDOM -s EDGE_COVERAGE:30
 * </pre>
 * 
 * <br>
 * <strong>Example:</strong> Generate online test sequence, using shortest walk<br>
 * 
 * <pre>
 * java -jar graphwalker.jar online -f folder -g A_STAR -s EDGE_COVERAGE:100
 * </pre>
 * 
 * <br>
 * <strong>Example:</strong> Print all names of edges and vertices (Sorted, and unique)<br>
 * 
 * <pre>
 * java -jar graphwalker.jar methods -f folder
 * </pre>
 * 
 * <br>
 * <strong>Example:</strong> When you need to define more complex abstract test cases working with
 * the CLI can sometimes be a burden. For this reason we have added a easier way to structure the
 * abstract test cases, using XML.<br>
 * 
 * <pre>
 * java -jar graphwalker.jar xml -f testcase.xml
 * </pre>
 * 
 * <br>
 * 
 */
public class CLI {

  public static class VerboseStatisticsLogger extends Thread {
    private ModelBasedTesting mbt = null;

    private VerboseStatisticsLogger(ModelBasedTesting modelBasedTesting) {
      this.mbt = modelBasedTesting;
    }

    @Override
    public void run() {
      logger.info(mbt.getStatisticsVerbose());
    }
  }

  private static Logger logger = Util.setupLogger(CLI.class);
  private ModelBasedTesting mbt = null;
  private Timer timer = null;
  private Options opt = new Options();
  private static Endpoint endpoint = null;

  public CLI() {
    mbt = new ModelBasedTesting();
  }

  private ModelBasedTesting getMbt() {
    return mbt;
  }

  /**
   * @param mbt the ModelBasedTesting to set
   */
  private void setMbt(ModelBasedTesting mbt) {
    this.mbt = mbt;
  }

  public static void main(String[] args) {
    CLI cli = new CLI();
    try {
      cli.run(args);
      if ( cli.GetEndpoint() != null ) {
        cli.GetEndpoint().stop();
      }
    } catch (Exception e) {
      Util.logStackTraceToError(e);
    }
  }

  /**
   * Prints statistics when graphwalker has ended it's execution
   * 
   * @param cli
   */
  private static void setStatisticsLogger(CLI cli) {
    Thread shutDownThread = new CLI.VerboseStatisticsLogger(cli.getMbt());
    try {
      Runtime.getRuntime().addShutdownHook(shutDownThread);
    } catch (IllegalArgumentException e) {
      logger.debug("Could not register ShutdownHook for statistics logger, it has already been registered");
    }
  }

  private void run(String[] args) {
    if (args.length < 1) {
      System.err.println("Type 'java -jar graphwalker.jar help' for usage.");
      return;
    } else {
      if (args[0].equals("help")) {
        if (args.length == 1) {
          printGeneralHelpText();
        } else {
          printHelpText(args[1]);
        }
        return;
      } else if (args[0].equalsIgnoreCase("requirements")) {
        buildRequirementsCLI();
      } else if (args[0].equalsIgnoreCase("online")) {
        buildOnlineCLI();
      } else if (args[0].equalsIgnoreCase("offline")) {
        buildOfflineCLI();
      } else if (args[0].equalsIgnoreCase("methods")) {
        buildMethodsCLI();
      } else if (args[0].equalsIgnoreCase("merge")) {
        buildMergeCLI();
      } else if (args[0].equalsIgnoreCase("source")) {
        buildSourceCLI();
      } else if (args[0].equalsIgnoreCase("manual")) {
        buildManualCLI();
      } else if (args[0].equalsIgnoreCase("xml")) {
        buildXmlCLI();
      } else if (args[0].equalsIgnoreCase("soap")) {
        buildSoapCLI();
      } else if (args[0].equalsIgnoreCase("analyze")) {
        buildAnalyzeCLI();
      } else if (args[0].equals("-v") || args[0].equals("--version")) {
        printVersionInformation();
        return;
      } else if (args[0].equals("-h") || args[0].equals("--help")) {
        printGeneralHelpText();
        return;
      } else {
        System.err.println("Unkown command: " + args[0]);
        System.err.println("Type 'java -jar graphwalker.jar help' for usage.");
        return;
      }
    }

    try {
      timer = new Timer();
      CommandLineParser parser = new PosixParser();
      CommandLine cl = parser.parse(opt, args);

      /**
       * Command: requirements
       */
      if (args[0].equalsIgnoreCase("requirements")) {
        RunCommandRequirements(cl);
      }
      /**
       * Command: online
       */
      if (args[0].equalsIgnoreCase("online")) {
        setStatisticsLogger(this);
        RunCommandOnline(cl);
      }
      /**
       * Command: offline
       */
      else if (args[0].equalsIgnoreCase("offline")) {
        setStatisticsLogger(this);
        RunCommandOffline(cl);
      }
      /**
       * Command: methods
       */
      else if (args[0].equalsIgnoreCase("methods")) {
        RunCommandMethods(cl);
      }
      /**
       * Command: merge
       */
      else if (args[0].equalsIgnoreCase("merge")) {
        RunCommandMerge(cl);
      }
      /**
       * Command: source
       */
      else if (args[0].equalsIgnoreCase("source")) {
        RunCommandSource(cl);
      }
      /**
       * Command: xml
       */
      else if (args[0].equalsIgnoreCase("xml")) {
        setStatisticsLogger(this);
        RunCommandXml(cl);
      }
      /**
       * Command: soap
       */
      else if (args[0].equalsIgnoreCase("soap")) {
        setStatisticsLogger(this);
        RunCommandSoap(cl);
      }
      /**
       * Command: analyze
       */
      else if (args[0].equalsIgnoreCase("analyze")) {
        RunCommandAnalyze(cl);
      }
      /**
       * Command: manual
       */
      else if (args[0].equalsIgnoreCase("manual")) {
        setStatisticsLogger(this);
        RunCommandManual(cl);
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      logger.warn(e.getMessage());
      System.err.println("The arguments for either the generator, or the stop-condition, is incorrect.");
      System.err.println("Example: java -jar graphwalker.jar offline -f ../demo/model/UC01.graphml -s EDGE_COVERAGE:100 -g A_STAR");
      System.err.println("Type 'java -jar graphwalker.jar help " + args[0] + "' for help.");
    } catch (MissingOptionException e) {
      logger.warn(e.getMessage());
      System.err.println("Mandatory option(s) are missing.");
      System.err.println(e.getMessage());
      System.err.println("Type 'java -jar graphwalker.jar help " + args[0] + "' for help.");
    } catch (MissingArgumentException e) {
      logger.warn(e.getMessage());
      System.err.println("Argument is required to the option.");
      System.err.println(e.getMessage());
      System.err.println("Type 'java -jar graphwalker.jar help " + args[0] + "' for help.");
    } catch (UnrecognizedOptionException e) {
      logger.warn(e.getMessage());
      System.err.println(e.getMessage());
      System.err.println("Type 'java -jar graphwalker.jar help " + args[0] + "' for help.");
    } catch (StopConditionException e) {
      logger.warn(e.getMessage());
      System.err.println(e.getMessage());
      System.err.println("Type 'java -jar graphwalker.jar help " + args[0] + "' for help.");
    } catch (GeneratorException e) {
      logger.warn(e.getMessage());
      System.err.println(e.getMessage());
      System.err.println("Type 'java -jar graphwalker.jar help " + args[0] + "' for help.");
    } catch (JDOMException e) {
      Util.logStackTraceToError(e);
      System.err.println("Can not access file: " + e.getMessage());
      System.err.println("Type 'java -jar graphwalker.jar help " + args[0] + "' for help.");
    } catch (FileNotFoundException e) {
      Util.logStackTraceToError(e);
      System.err.println("Can not access file: " + e.getMessage());
      System.err.println("Type 'java -jar graphwalker.jar help " + args[0] + "' for help.");
    } catch (IOException e) {
      Util.logStackTraceToError(e);
      System.err.println(e.getMessage());
      System.err.println("Type 'java -jar graphwalker.jar help " + args[0] + "' for help.");
    } catch (Exception e) {
      Util.logStackTraceToError(e);
      System.err.println(e.getMessage());
      System.err.println("Type 'java -jar graphwalker.jar help " + args[0] + "' for help.");
    } finally {
      timer.cancel();
    }
  }

  private void printGeneralHelpText() {
    System.out.println("usage: 'java -jar graphwalker.jar <COMMAND> [OPTION] [ARGUMENT]'\n");
    System.out.println("Type 'java -jar graphwalker.jar help <COMMAND>' to get specific help about a command.");
    System.out.println("Valid commands are:");
    System.out.println("    analyze");
    System.out.println("    help");
    System.out.println("    manual");
    System.out.println("    merge");
    System.out.println("    methods");
    System.out.println("    offline");
    System.out.println("    online");
    System.out.println("    requirements");
    System.out.println("    soap");
    System.out.println("    source");
    System.out.println("    xml\n");
    System.out.println("Type 'java -jar graphwalker.jar -v (--version)' for version information.");
  }

  private void printHelpText(String helpSection) {
    String header = "";
    if (helpSection.equalsIgnoreCase("online")) {
      buildOnlineCLI();
      header =
          "Run the test online.\n" + "MBT will return a test sequence, one line at a time to standard output, "
              + "it will wait until a line is fed back via standard input. The data fed back can be:\n"
              + "  '0' which means, continue the test as normal\n" + "  '2' will end the test normally\n"
              + "anything else will abort the execution.\n";
    } else if (helpSection.equalsIgnoreCase("requirements")) {
      buildRequirementsCLI();
      header = "Print a list of requiremnts found in the model.\n";
    } else if (helpSection.equalsIgnoreCase("offline")) {
      buildOfflineCLI();
      header = "Generate a test sequence offline. The sequence is printed to the standard output\n";
    } else if (helpSection.equalsIgnoreCase("manual")) {
      buildManualCLI();
      header = "Generate a test sequence (offline). The output will be a HTML formatted test case document\n";
    } else if (helpSection.equalsIgnoreCase("methods")) {
      buildMethodsCLI();
      header =
          "Generate all methods, or tests existing in the model.\n" + "MBT will parse the graph(s), and return all methods (or tests) that"
              + " exists in the graph(s). The list will onyl print out a unique name once." + " The list is printed to stdout.\n";
    } else if (helpSection.equalsIgnoreCase("merge")) {
      buildMergeCLI();
      header = "Merge several graphml files into one single graphml file.\n" + "The files to be merged, shall all exist in a single folder.\n";
    } else if (helpSection.equalsIgnoreCase("source")) {
      buildSourceCLI();
      header =
          "Generate code from a template.\n" + "Will generate code using a template. The code generated will contain all lables/names "
              + "defined by the vertices and edges. This enables the user to write templates for a "
              + "multitude of scripting or programming languages. " + "The result will be printed to stdout. "
              + "There is 1 variable in the template, that will be replaced: {LABEL} ->Will be replace "
              + "by the actual name of the edge or vertex.";
    } else if (helpSection.equalsIgnoreCase("xml")) {
      buildXmlCLI();
      header = "Setup mbt engine from xml.\n" + "Will setup and execute an engine based on xml specified criterias.";
    } else if (helpSection.equalsIgnoreCase("soap")) {
      buildSoapCLI();
      header = "Run MBT as a Web Services (SOAP) server.\n" + "To see the services, see the WSDL file at: http://localhost:8080/mbt-services?WSDL";
    } else if (helpSection.equalsIgnoreCase("analyze")) {
      buildAnalyzeCLI();
      header =
          "This will start an analyzing session of an graphml file.\n" + "Graphwalker will try to find any potentials pitfalls, and report them"
              + " This is usefull when the user whishes to debug a graph.";
    } else {
      System.err.println("Type 'java -jar graphwalker.jar help' for usage.");
      return;
    }

    HelpFormatter f = new HelpFormatter();
    f.printHelp(100, "java -jar graphwalker.jar " + helpSection.toLowerCase(), header, opt, "", true);
  }

  @SuppressWarnings("static-access")
  private void buildRequirementsCLI() {
    opt.addOption(OptionBuilder.isRequired().withArgName("file|folder").withDescription("The file (or folder) containing graphml formatted files.")
        .hasArg().withLongOpt("input_graphml").create("f"));
  }

  private String generateListOfValidStopConditions() {
    StringBuilder stringBuilder = new StringBuilder();
    for (StopCondition sc : Keywords.getStopConditions()) {
      stringBuilder.append(sc.getDescription());
      stringBuilder.append(System.getProperty("line.separator"));
    }
    return stringBuilder.toString();
  }

  private String generateListOfValidGenerators() {
    StringBuilder stringBuilder = new StringBuilder();
    for (Generator g : Keywords.getGenerators()) {
      if (g.isPublished()) {
        stringBuilder.append(g.getDescription());
        stringBuilder.append(System.getProperty("line.separator"));
      }
    }
    return stringBuilder.toString();
  }

  @SuppressWarnings("static-access")
  private void buildOnlineCLI() {
    opt.addOption("a", "statistics", false, "Prints the statistics of the test, at the end of the run.");
    opt.addOption("x", "extended", false, "Use an extended finite state machine to handle the model.");
    opt.addOption("j", false, "Enable JavaScript engine");
    opt.addOption(OptionBuilder
        .isRequired()
        .withArgName("stop-condition")
        .withDescription(
            "Defines the stop condition(s).\nHalts the generation after the specified stop-conditon(s) has been met. "
                + "At least 1 condition must be given. If more than 1 is given, the condition that meets "
                + "it's stop-condition first, will cause the generation to halt. "
                + "To separate multiple conditions, the separator pipe-character | is used. "
                + "A list of valid stop-conditions are:\n -------------------\n" + generateListOfValidStopConditions()
                + " -------------------\nFor more extensive examples, " + "see http://mbt.tigris.org/wiki/All_about_stop_conditions").hasArg()
        .create("s"));
    opt.addOption(OptionBuilder
        .isRequired()
        .withArgName("generator")
        .withDescription(
            "The generator to be used when traversing the model. At least 1 generator must be given. "
                + "To separate multiple generators, the separator pipe-character | is used. "
                + "A list of valid generators are:\n -------------------\n" + generateListOfValidGenerators()
                + " -------------------\nFor more extensive examples, " + "see http://mbt.tigris.org/wiki/All_about_generators").hasArg().create("g"));
    opt.addOption(OptionBuilder.isRequired().withArgName("file|folder").withDescription("The file (or folder) containing graphml formatted files.")
        .hasArg().withLongOpt("input_graphml").create("f"));
    opt.addOption(OptionBuilder
        .withArgName("seconds")
        .withDescription(
            "Prints the test coverage of the graph during execution every <n second>. The printout goes to the log file defined in "
                + "mbt.properties, and only, if at least INFO level is set in " + "that same file.").hasArg().withLongOpt("log-coverage").create("o"));
    opt.addOption("c", "class_name", true, "Optional class name to use for test execution.");
    opt.addOption("t", "report-template", true, "Optional report template to use. (Also requires option -r) (To be better documented)");
    opt.addOption("r", "report-output", true, "Optional report filename to save report to. (Also requires option -t)  (To be better documented)");
    opt.addOption("w", "weighted", false, "Use weighted values if they exist in the model, and the generator is RANDOM.");
    opt.addOption("d", "dry-run", false, "Will execute a dry-run of the model. Dialog will pop up for every edge and vertex.");
  }

  /**
   * Build the command offline command line parser
   */
  @SuppressWarnings("static-access")
  private void buildOfflineCLI() {
    opt.addOption("a", false, "Prints the statistics of the test, at the end of the run.");
    opt.addOption("x", false, "Use an extended finite state machine to handle the model.");
    opt.addOption("j", false, "Enable JavaScript engine");
    opt.addOption(OptionBuilder
        .isRequired()
        .withArgName("stop-condition")
        .withDescription(
            "Defines the stop condition(s).\nHalts the generation after the specified stop-conditon(s) has been met. "
                + "At least 1 condition must be given. If more than 1 is given, the condition that meets "
                + "it's stop-condition first, will cause the generation to halt. "
                + "To separate multiple conditions, the separator pipe-character | is used. "
                + "A list of valid stop-conditions are:\n -------------------\n" + generateListOfValidStopConditions()
                + " -------------------\nFor more extensive examples, " + "see http://mbt.tigris.org/wiki/All_about_stop_conditions").hasArg()
        .create("s"));
    opt.addOption(OptionBuilder
        .isRequired()
        .withArgName("generator")
        .withDescription(
            "The generator to be used when traversing the model. At least 1 generator must be given. "
                + "To separate multiple generators, the separator pipe-character | is used. "
                + "A list of valid generators are:\n -------------------\n" + generateListOfValidGenerators()
                + " -------------------\nFor more extensive examples, " + "see http://mbt.tigris.org/wiki/All_about_generators").hasArg().create("g"));
    opt.addOption(OptionBuilder.isRequired().withArgName("file|folder").withDescription("The file (or folder) containing graphml formatted files.")
        .hasArg().create("f"));
    opt.addOption(OptionBuilder
        .withArgName("seconds")
        .withDescription(
            "Prints the test coverage of the graph during execution every <n second>. The printout goes to the log file defined in "
                + "mbt.properties, and only, if at least INFO level is set in " + "that same file.").hasArg().create("o"));
    opt.addOption("t", "report-template", true, "Optional report template to use. (Also requires option -r) (To be better documented)");
    opt.addOption("r", "report-output", true, "Optional report filename to save report to. (Also requires option -t)  (To be better documented)");
    opt.addOption("w", "weighted", false, "Use weighted values if they exist in the model, and the generator is RANDOM.");
  }

  /**
   * Build the command manual command line parser
   */
  @SuppressWarnings("static-access")
  private void buildManualCLI() {
    opt.addOption("x", false, "Use an extended finite state machine to handle the model.");
    opt.addOption("j", false, "Enable JavaScript engine");
    opt.addOption(OptionBuilder
        .isRequired()
        .withArgName("stop-condition")
        .withDescription(
            "Defines the stop condition(s).\nHalts the generation after the specified stop-conditon(s) has been met. "
                + "At least 1 condition must be given. If more than 1 is given, the condition that meets "
                + "it's stop-condition first, will cause the generation to halt. "
                + "To separate multiple conditions, the separator pipe-character | is used. "
                + "A list of valid stop-conditions are:\n -------------------\n" + generateListOfValidStopConditions()
                + " -------------------\nFor more extensive examples, " + "see http://mbt.tigris.org/wiki/All_about_stop_conditions").hasArg()
        .create("s"));
    opt.addOption(OptionBuilder
        .isRequired()
        .withArgName("generator")
        .withDescription(
            "The generator to be used when traversing the model. At least 1 generator must be given. "
                + "To separate multiple generators, the separator pipe-character | is used. "
                + "A list of valid generators are:\n -------------------\n" + generateListOfValidGenerators()
                + " -------------------\nFor more extensive examples, " + "see http://mbt.tigris.org/wiki/All_about_generators").hasArg().create("g"));
    opt.addOption(OptionBuilder.isRequired().withArgName("file|folder").withDescription("The file (or folder) containing graphml formatted files.")
        .hasArg().create("f"));
    opt.addOption("w", "weighted", false, "Use weighted values if they exist in the model, and the generator is RANDOM.");
  }

  @SuppressWarnings("static-access")
  private void buildMethodsCLI() {
    opt.addOption(OptionBuilder.isRequired().withArgName("file|folder").withDescription("The file (or folder) containing graphml formatted files.")
        .hasArg().withLongOpt("input_graphml").create("f"));
  }

  /**
   * Build the command merge command line parser
   */
  @SuppressWarnings("static-access")
  private void buildMergeCLI() {
    opt.addOption(OptionBuilder.isRequired().withArgName("file|folder").withDescription("The file (or folder) containing graphml formatted files.")
        .hasArg().withLongOpt("input_graphml").create("f"));
    opt.addOption("i", "index", true, "Print out the INDEX value when merging models. Default is true");
  }

  /**
   * Build the command source command line parser
   */
  @SuppressWarnings("static-access")
  private void buildSourceCLI() {
    opt.addOption(OptionBuilder.isRequired().withArgName("file|folder").withDescription("The file (or folder) containing graphml formatted files.")
        .hasArg().withLongOpt("input_graphml").create("f"));
    opt.addOption(OptionBuilder.isRequired().withArgName("file").withDescription("The template file").hasArg().withLongOpt("template").create("t"));
  }

  /**
   * Build the command xml command line parser
   */
  @SuppressWarnings("static-access")
  private void buildXmlCLI() {
    opt.addOption("a", false, "Prints the statistics of the test, at the end of the run.");
    opt.addOption(OptionBuilder.isRequired().withArgName("file").withDescription("The xml file containing the mbt settings.").hasArg().create("f"));
    opt.addOption(OptionBuilder
        .withArgName("seconds")
        .withDescription(
            "Prints the test coverage of the graph during execution every <n second>. The printout goes to the log file defined in "
                + "mbt.properties, and only, if at least INFO level is set in " + "that same file.").hasArg().create("o"));
    opt.addOption("d", "dry-run", false, "Will execute a dry-run of the model. Dialog will pop up for every edge and vertex.");
  }

  /**
   * Build the command soap command line parser
   */
  @SuppressWarnings("static-access")
  private void buildSoapCLI() {
    opt.addOption(OptionBuilder.withArgName("file").withDescription("The xml file containing the mbt model and settings.").hasArg().create("f"));
    opt.addOption(OptionBuilder.withArgName("interface")
        .withDescription("The network interface to which mbt should bind to. " + "If not given, the default is 0.0.0.0").hasArg().create("i"));
    opt.addOption(OptionBuilder.withArgName("port").withDescription("The port to which mbt should listen to. " + "If not given, the default is 9090")
        .hasArg().create("p"));
  }

  /**
   * Build the command analyze command line parser
   */
  @SuppressWarnings("static-access")
  private void buildAnalyzeCLI() {
    opt.addOption(OptionBuilder.isRequired().withArgName("file|folder").withDescription("The file (or folder) containing graphml formatted files.")
        .hasArg().withLongOpt("input_graphml").create("f"));
  }

  /**
   * Print version information
   */
  private void printVersionInformation() {
    System.out.println("org.graphwalker version " + mbt.getVersionString() + "\n");
    System.out.println("org.graphwalker is open source software licensed under MIT licens");
    System.out.println("The software (and it's source) can be downloaded from http://graphwalker.org");
    System.out
        .println("For a complete list of this package software dependencies, see http://graphwalker.org:8080/job/graphwalker/site/dependencies.html");
  }

  /**
   * Run the offline command
   * 
   * @param cl
   * @throws StopConditionException
   * @throws GeneratorException
   * @throws InterruptedException
   * @throws FileNotFoundException
   */
  private void RunCommandOffline(CommandLine cl) throws StopConditionException, GeneratorException, InterruptedException, FileNotFoundException {
    /**
     * Get the model from the graphml file (or folder)
     */
    if (helpNeeded("offline", !cl.hasOption("f"), "Missing the input graphml file (folder), See -f (--input_graphml)")
        || helpNeeded("offline", !cl.hasOption("s"), "A stop condition must be supplied, See option -s")
        || helpNeeded("offline", cl.hasOption("t") && !cl.hasOption("r"), "A report output file must be set, See -t, when using a report template")
        || helpNeeded("offline", !cl.hasOption("t") && cl.hasOption("r"), "A report template must be set, See -r, when using a report output file")
        || helpNeeded("offline", !cl.hasOption("g"), "Missing the generator, See option -g")) return;

    getMbt().readGraph(cl.getOptionValue("f"));
    getMbt().enableJsScriptEngine(cl.hasOption("j"));
    getMbt().enableExtended(cl.hasOption("x"));
    getMbt().setWeighted(cl.hasOption("w"));

    /*
     * Set the stop-conditions(s)
     */
    AlternativeCondition alternativeCondition = new AlternativeCondition();
    String[] stopConditions = cl.getOptionValue("s").split("\\|");
    for (String stopCondition : stopConditions) {
      String[] sc = stopCondition.trim().split(":");
      alternativeCondition.add(Util.getCondition(null, Keywords.getStopCondition(sc[0].trim()) // Stop
                                                                                               // condition
          , (sc.length == 1 ? "" : sc[1].trim()))); // Optional condition
                                                    // parameter
    }

    /*
     * Set the generators(s)
     */
    String[] generators = cl.getOptionValue("g").split("\\|");
    for (String genrator : generators) {
      getMbt().setGenerator(Keywords.getGenerator(genrator.trim()));
    }
    getMbt().getGenerator().setStopCondition(alternativeCondition);

    if (cl.hasOption("o")) {
      long seconds = Integer.valueOf(cl.getOptionValue("o")).longValue();

      logger.info("Append coverage to log every: " + seconds + " seconds");

      TimerTask logTask;
      if (cl.hasOption("t") && cl.hasOption("r")) {
        getMbt().setUseStatisticsManager(true);
        getMbt().getStatisticsManager().setReportTemplate(new FileInputStream(new File(cl.getOptionValue('t'))));
        final String reportName = cl.getOptionValue('r');
        logTask = new TimerTask() {
          @Override
          public void run() {
            try {
              getMbt().getStatisticsManager().writeFullReport(new PrintStream(reportName));
            } catch (FileNotFoundException e) {
              throw new RuntimeException("Could not open or write report file '" + reportName + "'", e);
            }
          }
        };
      } else {
        logTask = new TimerTask() {
          @Override
          public void run() {
            logger.info(getMbt().getStatisticsCompact());
          }
        };
      }
      timer = new Timer();
      timer.schedule(logTask, 500, seconds * 1000);
    }

    getMbt().writePath();

    if (cl.hasOption("a")) {
      writeStatisticsVerbose(System.out);
    }
    if (cl.hasOption("o")) {
      logger.info(mbt.getStatisticsCompact());
    }
    if (cl.hasOption("t") && cl.hasOption("r")) {
      getMbt().getStatisticsManager().setReportTemplate(new FileInputStream(new File(cl.getOptionValue('t'))));
      getMbt().getStatisticsManager().writeFullReport(cl.getOptionValue('r'));
    }

  }

  /**
   * Run the manual command
   * 
   * @param cl
   * @throws StopConditionException
   * @throws GeneratorException
   * @throws InterruptedException
   */
  private void RunCommandManual(CommandLine cl) throws StopConditionException, GeneratorException, InterruptedException {
    /**
     * Get the model from the graphml file (or folder)
     */
    if (helpNeeded("manual", !cl.hasOption("f"), "Missing the input graphml file (folder), See -f (--input_graphml)")
        || helpNeeded("manual", !cl.hasOption("s"), "A stop condition must be supplied, See option -s")
        || helpNeeded("manual", !cl.hasOption("g"), "Missing the generator, See option -g")) return;

    getMbt().setManualTestSequence(true);
    getMbt().readGraph(cl.getOptionValue("f"));
    getMbt().enableJsScriptEngine(cl.hasOption("j"));
    getMbt().enableExtended(cl.hasOption("x"));
    getMbt().setWeighted(cl.hasOption("w"));

    /*
     * Set the stop-conditions(s)
     */
    AlternativeCondition alternativeCondition = new AlternativeCondition();
    String[] stopConditions = cl.getOptionValue("s").split("\\|");
    for (String stopCondition : stopConditions) {
      String[] sc = stopCondition.trim().split(":");
      alternativeCondition.add(Util.getCondition(null, Keywords.getStopCondition(sc[0].trim()) // Stop
                                                                                               // condition
          , (sc.length == 1 ? "" : sc[1].trim()))); // Optional condition
                                                    // parameter
    }

    /*
     * Set the generators(s)
     */
    String[] generators = cl.getOptionValue("g").split("\\|");
    for (String genrator : generators) {
      getMbt().setGenerator(Keywords.getGenerator(genrator.trim()));
    }
    getMbt().getGenerator().setStopCondition(alternativeCondition);

    Vector<String[]> testSequence = new Vector<String[]>();
    getMbt().writePath(testSequence);

    new PrintHTMLTestSequence(testSequence, System.out);
  }

  /**
   * Run the online command
   * 
   * @param cl
   * @throws StopConditionException
   * @throws GeneratorException
   * @throws InterruptedException
   * @throws FileNotFoundException
   */
  private void RunCommandOnline(CommandLine cl) throws StopConditionException, GeneratorException, InterruptedException, FileNotFoundException {
    /**
     * Get the model from the graphml file (or folder)
     */
    if (helpNeeded("online", !cl.hasOption("f"), "Missing the input graphml file (folder), See -f (--input_graphml)")
        || helpNeeded("online", !cl.hasOption("s"), "A stop condition must be supplied, See option -s")
        || helpNeeded("online", cl.hasOption("t") && !cl.hasOption("r"), "A report output file must be set, See -t, when using a report template")
        || helpNeeded("online", !cl.hasOption("t") && cl.hasOption("r"), "A report template must be set, See -r, when using a report output file")
        || helpNeeded("online", !cl.hasOption("g"), "Missing the generator, See option -g")) return;

    getMbt().readGraph(cl.getOptionValue("f"));
    getMbt().enableJsScriptEngine(cl.hasOption("j"));
    getMbt().enableExtended(cl.hasOption("x"));
    getMbt().setWeighted(cl.hasOption("w"));

    /*
     * Set the stop-conditions(s)
     */
    AlternativeCondition alternativeCondition = new AlternativeCondition();
    String[] stopConditions = cl.getOptionValue("s").split("\\|");
    for (String stopCondition : stopConditions) {
      String[] sc = stopCondition.trim().split(":");
      alternativeCondition.add(Util.getCondition(null, Keywords.getStopCondition(sc[0].trim()) // Stop
                                                                                               // condition
          , (sc.length == 1 ? "" : sc[1].trim()))); // Optional condition
                                                    // parameter
    }

    /*
     * Set the generators(s)
     */
    String[] genrators = cl.getOptionValue("g").split("\\|");
    for (String genrator : genrators) {
      getMbt().setGenerator(Keywords.getGenerator(genrator.trim()));
    }
    getMbt().getGenerator().setStopCondition(alternativeCondition);

    /**
     * Set dry-run
     */
    getMbt().setDryRun(cl.hasOption("d"));

    if (cl.hasOption("o")) {
      long seconds = Integer.valueOf(cl.getOptionValue("o")).longValue();

      logger.info("Append coverage to log every: " + seconds + " seconds");

      TimerTask logTask;
      if (cl.hasOption("t") && cl.hasOption("r")) {
        getMbt().setUseStatisticsManager(true);
        getMbt().getStatisticsManager().setReportTemplate(new FileInputStream(new File(cl.getOptionValue('t'))));
        final String reportName = cl.getOptionValue('r');
        logTask = new TimerTask() {
          @Override
          public void run() {
            try {
              getMbt().getStatisticsManager().writeFullReport(new PrintStream(reportName));
            } catch (FileNotFoundException e) {
              throw new RuntimeException("Could not open or write report file '" + reportName + "'", e);
            }
          }
        };
      } else {
        logTask = new TimerTask() {
          @Override
          public void run() {
            logger.info(getMbt().getStatisticsCompact());
          }
        };
      }
      timer = new Timer();
      timer.schedule(logTask, 500, seconds * 1000);
    }

    if (cl.hasOption("c")) {
      getMbt().executePath(cl.getOptionValue("c"));
    } else if (cl.hasOption("d")) {
      getMbt().executePath((String) null);
    } else {
      getMbt().interractivePath();
    }

    if (cl.hasOption("a")) {
      writeStatisticsVerbose(System.out);
    }
    if (cl.hasOption("o")) {
      logger.info(mbt.getStatisticsCompact());
    }
    if (cl.hasOption("t") && cl.hasOption("r")) {
      getMbt().getStatisticsManager().setReportTemplate(new FileInputStream(new File(cl.getOptionValue('t'))));
      getMbt().getStatisticsManager().writeFullReport(cl.getOptionValue('r'));
    }

  }

  private void writeStatisticsVerbose(PrintStream out) {
    out.println(getMbt().getStatisticsVerbose());
  }

  /**
   * Run the source command
   * 
   * @param cl
   * @throws GeneratorException
   * @throws IOException
   * @throws InterruptedException
   */
  private void RunCommandSource(CommandLine cl) throws GeneratorException, IOException, InterruptedException {
    if (helpNeeded("source", !cl.hasOption("f"), "Missing the input graphml file (folder), See -f (--input_graphml)")
        || helpNeeded("source", !cl.hasOption("t"), "Missing the template file. See -t (--template)")) return;

    getMbt().readGraph(cl.getOptionValue("f"));
    getMbt().setTemplate(cl.getOptionValue("t"));
    getMbt().setGenerator(Keywords.GENERATOR_STUB);
    getMbt().writePath(System.out);
  }

  /**
   * Run the requirements command This method prints out all the Requirements found in the graph.
   * 
   * @throws GeneratorException
   * @throws InterruptedException
   */
  private void RunCommandRequirements(CommandLine cl) throws GeneratorException, InterruptedException {
    if (helpNeeded("requirements", !cl.hasOption("f"), "Missing the input graphml file (folder), See -f (--input_graphml)")) return;

    getMbt().readGraph(cl.getOptionValue("f"));
    getMbt().getMachine().populateReqHashMap();

    Iterator<Entry<String, Boolean>> it = getMbt().getMachine().getReqs().entrySet().iterator();
    while (it.hasNext()) {
      Entry<String, Boolean> pairs = it.next();
      System.out.println(pairs.getKey());
    }
  }

  /**
   * Run the methods command
   * 
   * @param cl
   * @throws GeneratorException
   * @throws InterruptedException
   */
  private void RunCommandMethods(CommandLine cl) throws GeneratorException, InterruptedException {
    if (helpNeeded("methods", !cl.hasOption("f"), "Missing the input graphml file (folder), See -f (--input_graphml)")) return;

    getMbt().readGraph(cl.getOptionValue("f"));
    getMbt().setTemplate(new String[] {"", "{LABEL}", ""});
    getMbt().setGenerator(Keywords.GENERATOR_STUB);
    getMbt().writePath(System.out);
  }

  /**
   * Run the merge command
   * 
   * @param cl
   */
  private void RunCommandMerge(CommandLine cl) {
    if (helpNeeded("merge", !cl.hasOption("f"), "Missing the input graphml file (folder), See -f (--input_graphml)")) return;

    getMbt().readGraph(cl.getOptionValue("f"));
    getMbt().writeModel(System.out, !cl.hasOption("i"));
  }

  /**
   * Run the xml command
   * 
   * @param cl
   * @throws StopConditionException
   * @throws GeneratorException
   * @throws IOException
   * @throws JDOMException
   * @throws InterruptedException
   */
  private void RunCommandXml(CommandLine cl) throws StopConditionException, GeneratorException, IOException, JDOMException, InterruptedException {
    if (helpNeeded("xml", !cl.hasOption("f"), "Missing the input xml file, See  option -f")) return;

    setMbt(Util.loadMbtFromXml(Util.getFile(cl.getOptionValue("f")), cl.hasOption("d")));

    if (cl.hasOption("a")) {
      logger.info(getMbt().getStatisticsVerbose());
      writeStatisticsVerbose(System.out);
    }
  }

  /**
   * Run the soap command
   * 
   * @param cl
   * @throws StopConditionException
   * @throws GeneratorException
   * @throws IOException
   * @throws JDOMException
   * @throws InterruptedException
   */
  private void RunCommandSoap(CommandLine cl) throws StopConditionException, GeneratorException, IOException, JDOMException, InterruptedException {
    String port = null;
    String nicAddr = null;
    if (cl.hasOption("p")) {
      port = cl.getOptionValue("p");
    } else {
      port = Util.readWSPort();
    }

    if (cl.hasOption("i")) {
      nicAddr = Util.getInternetAddr(cl.getOptionValue("i")).getCanonicalHostName();
    } else {
      nicAddr = "0.0.0.0";
    }

    String file = cl.getOptionValue("f");
    if (file == null || file.isEmpty()) {
      setMbt(new ModelBasedTesting());
    } else {
      setMbt(Util.loadMbtAsWSFromXml(Util.getFile(file)));
    }

    String wsURL = "http://" + nicAddr + ":" + port + "/mbt-services";
    SoapServices soapService = new SoapServices(getMbt());
    soapService.xmlFile = file;

    endpoint = Endpoint.publish(wsURL, soapService);

    if (nicAddr.equals("0.0.0.0")) {
      wsURL = wsURL.replace("0.0.0.0", InetAddress.getLocalHost().getHostName());
    }

    System.out.println("Now running as a SOAP server. For the WSDL file, see: " + wsURL + "?WSDL");
    System.out.println("Press Ctrl+C to quit");
    System.out.println("");
  }

  /**
   * Run the analyze command
   * 
   * @param cl
   * @throws IOException
   */
  private void RunCommandAnalyze(CommandLine cl) throws IOException {
    if (helpNeeded("log", !cl.hasOption("f"), "Missing the input graphml file (folder), See -f (--input_graphml)")) return;

    getMbt().readGraph(cl.getOptionValue("f"));
    System.out.println(Analyze.unreachableVertices(getMbt()));
  }

  private boolean helpNeeded(String module, boolean condition, String message) {
    if (condition) {
      System.out.println(message);
      System.out.println("Type 'java -jar graphwalker.jar help " + module + "' for help.");
    }
    return condition;
  }

  private Endpoint GetEndpoint() {
    return endpoint;
  }
}
