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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

public class CLITest extends TestCase {

  Pattern pattern;
  Matcher matcher;
  StringBuffer stdOutput;
  StringBuffer errOutput;
  String outMsg;
  String errMsg;
  static Logger logger = Util.setupLogger(CLITest.class);

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    ModelBasedTesting.getInstance().reset();
  }

  private OutputStream redirectOut() {
    return new OutputStream() {
      @Override
      public void write(int b) throws IOException {
        stdOutput.append(Character.toString((char) b));
      }
    };
  }

  private OutputStream redirectErr() {
    return new OutputStream() {
      @Override
      public void write(int b) throws IOException {
        errOutput.append(Character.toString((char) b));
      }
    };
  }

  private InputStream redirectIn() {
    return new InputStream() {
      @Override
      public int read() throws IOException {
        try {
          Thread.sleep(300);
        } catch (InterruptedException e) {
          Util.logStackTraceToError(e);
        }
        return '0';
      }
    };
  }

  private void runCommand(String args[]) {
    stdOutput = new StringBuffer();
    errOutput = new StringBuffer();

    PrintStream outStream = new PrintStream(redirectOut());
    PrintStream oldOutStream = System.out; // backup
    PrintStream errStream = new PrintStream(redirectErr());
    PrintStream oldErrStream = System.err; // backup

    System.setOut(outStream);
    System.setErr(errStream);

    CLI.main(args);

    System.setOut(oldOutStream);
    System.setErr(oldErrStream);

    outMsg = stdOutput.toString();
    errMsg = errOutput.toString();
    logger.debug("stdout: " + outMsg);
    logger.debug("stderr: " + errMsg);
  }

  private void moveMbtPropertiesFile() {
    File mbt_properties = new File("graphwalker.properties");
    if (mbt_properties.exists()) {
      mbt_properties.renameTo(new File("graphwalker.properties.bak"));
    }
    assertFalse(new File("graphwalker.properties").exists());
  }

  private void restoreMbtPropertiesFile() {
    File mbt_properties = new File("graphwalker.properties.bak");
    if (mbt_properties.exists()) {
      mbt_properties.renameTo(new File("graphwalker.properties"));
    }
    assertFalse(new File("graphwalker.properties.bak").exists());
  }

  /**
   * Test command: java -jar mbt.jar
   */
  public void testNoArgs() {
    String args[] = {};
    runCommand(args);
    pattern = Pattern.compile("Type 'java -jar graphwalker.jar help' for usage.", Pattern.MULTILINE);
    matcher = pattern.matcher(errMsg);
    assertTrue(matcher.find());
    assertTrue("Nothing should be written to standard output: " + outMsg, outMsg.isEmpty());
  }

  /**
   * Test command: java -jar mbt.jar -v
   */
  public void testVersion() {
    String args[] = {"-v"};
    runCommand(args);
    assertTrue("No error messages should occur: " + errMsg, errMsg.isEmpty());
    pattern = Pattern.compile("^org\\.graphwalker version " + ModelBasedTesting.getInstance().getVersionString(), Pattern.MULTILINE);
    matcher = pattern.matcher(outMsg);
    assertTrue(matcher.find());
  }

  /**
   * Test command: java -jar mbt.jar
   */
  public void testNoMbtPropertiesFile() {
    String args[] = {};
    moveMbtPropertiesFile();
    runCommand(args);
    restoreMbtPropertiesFile();
    pattern = Pattern.compile("Type 'java -jar graphwalker.jar help' for usage.", Pattern.MULTILINE);
    matcher = pattern.matcher(errMsg);
    assertTrue(matcher.find());
    assertTrue("Nothing should be written to standard output: " + outMsg, outMsg.isEmpty());
  }

  /**
   * Test command: java -jar mbt.jar offline -f graphml/reqtags/ExtendedMain.graphml -g RANDOM -s
   * TEST_LENGTH:10
   */
  public void testNoMbtPropertiesFileOffline() {
    String args[] = {"offline", "-f", "graphml/reqtags/ExtendedMain.graphml", "-g", "RANDOM", "-s", "TEST_LENGTH:10"};
    moveMbtPropertiesFile();
    runCommand(args);
    restoreMbtPropertiesFile();
    System.out.println(errMsg);
    assertEquals("No error messages should occur.", "", errMsg);
    assertEquals(0, getNumMatches(Pattern.compile("INFO").matcher(outMsg)));
  }

  /**
   * Test command: java -jar mbt.jar sputnik
   */
  public void testUnkownCommand() {
    String args[] = {"sputnik"};
    runCommand(args);
    pattern = Pattern.compile("^Unkown command: .*\\s+", Pattern.MULTILINE);
    matcher = pattern.matcher(errMsg);
    assertTrue(matcher.find());
    assertTrue("Nothing should be written to standard output: " + outMsg, outMsg.isEmpty());
  }

  /**
   * Test command: java -jar mbt.jar offline -f graphml/reqtags/ExtendedMain.graphml -g A_STAR -s
   * EDGE_COVERAGE:100
   */
  public void testOfflineA_StarEdgeCoverage() {
    String args[] = {"offline", "-f", "graphml/reqtags/ExtendedMain.graphml", "-g", "A_STAR", "-s", "EDGE_COVERAGE:100"};
    runCommand(args);
    assertEquals(errMsg, "", errMsg);
    assertEquals(outMsg, 0, getNumMatches(Pattern.compile("INFO").matcher(outMsg)));
  }

  private int getNumMatches(Matcher m) {
    int numMatches = 0;
    while (m.find() == true)
      numMatches++;
    return numMatches;
  }

  /**
   * Test command: java -jar mbt.jar offline -f graphml/reqtags/ExtendedMain.graphml -g RANDOM -s
   * EDGE_COVERAGE:100
   */
  public void testOfflineRandomEdgeCoverage() {
    String args[] = {"offline", "-f", "graphml/reqtags/ExtendedMain.graphml", "-g", "RANDOM", "-s", "EDGE_COVERAGE:100"};
    runCommand(args);
    assertTrue("No error messgaes should occur: " + errMsg, errMsg.isEmpty());
    assertTrue("Expected at least 78 lines, got: " + outMsg.split("\r\n|\r|\n").length, outMsg.split("\r\n|\r|\n").length >= 78);
  }

  /**
   * Test command: java -jar mbt.jar offline -f graphml/reqtags/ExtendedMain.graphml -g RANDOM -s
   * VERTEX_COVERAGE:100
   */
  public void testOfflineRandomStateCoverage() {
    String args[] = {"offline", "-f", "graphml/reqtags/ExtendedMain.graphml", "-g", "RANDOM", "-s", "VERTEX_COVERAGE:100"};
    runCommand(args);
    assertTrue("No error messages should occur: " + errMsg, errMsg.isEmpty());
    assertTrue("Expected at least 24 lines, got: " + outMsg.split("\r\n|\r|\n").length, outMsg.split("\r\n|\r|\n").length >= 24);
  }

  /**
   * Test command: java -jar mbt.jar offline -f graphml/reqtags/ExtendedMain.graphml -g A_STAR -s
   * VERTEX_COVERAGE:100
   */
  public void testOfflineA_StarStateCoverage() {
    String args[] = {"offline", "-f", "graphml/reqtags/ExtendedMain.graphml", "-g", "A_STAR", "-s", "VERTEX_COVERAGE:100"};
    runCommand(args);
    assertEquals("No error messages should occur.", "", errMsg);
    assertTrue(outMsg, getNumMatches(Pattern.compile("INFO").matcher(outMsg)) <= 21);
  }

  /**
   * Test command: java -jar mbt.jar offline -f graphml/reqtags/ExtendedMain.graphml -g a_star -s
   * "REACHED_REQUIREMENT:req 78
   */
  public void testOfflineA_StarReachedRequirement() {
    String args[] = {"offline", "-f", "graphml/reqtags/ExtendedMain.graphml", "-g", "a_star", "-s", "REACHED_REQUIREMENT:req 78"};
    runCommand(args);
    assertEquals("No error messages should occur.", "", errMsg);
    assertEquals(0, getNumMatches(Pattern.compile("INFO").matcher(outMsg)));
  }

  /**
   * Test command: java -jar mbt.jar requirements -f graphml/reqtags/ExtendedMain.graphml
   */
  public void testListReqTags() {
    String args[] = {"requirements", "-f", "graphml/reqtags/ExtendedMain.graphml"};
    runCommand(args);
    assertTrue("No error messages should occur: " + errMsg, errMsg.isEmpty());
    assertEquals(6, getNumMatches(Pattern.compile("req[ \\d]+").matcher(outMsg)));
  }

  /**
   * Test command: java -jar mbt.jar source -f graphml/methods/Main.graphml -t
   * templates/perl.template
   */
  public void testGenerateCodeFromTemplateHeaderAndFooter() {
    String args[] = {"source", "-f", "graphml/methods/Main.graphml", "-t", "templates/junit.template"};
    runCommand(args);
    assertTrue("No error messages should occur: " + errMsg, errMsg.isEmpty());
    pattern = Pattern.compile(" implements the ", Pattern.MULTILINE);
    matcher = pattern.matcher(outMsg);
    assertTrue(matcher.find());
  }

  /**
   * Test command: java -jar mbt.jar source -f graphml/methods/Main.graphml -t
   * templates/perl.template
   */
  public void testGenerateCodeFromTemplate() {
    String args[] = {"source", "-f", "graphml/methods/Main.graphml", "-t", "templates/perl.template"};
    runCommand(args);
    assertTrue("No error messages should occur: " + errMsg, errMsg.isEmpty());
    pattern = Pattern.compile(" implements the ", Pattern.MULTILINE);
    matcher = pattern.matcher(outMsg);
    assertTrue(matcher.find());
  }

  /**
   * Test command: java -jar mbt.jar offline -f graphml/misc/missing_inedges.graphml -g RANDOM -s
   * EDGE_COVERAGE:100
   */
  public void testNoVerticesWithNoInEdges() {
    String args[] = {"offline", "-f", "graphml/misc/missing_inedges.graphml", "-g", "RANDOM", "-s", "EDGE_COVERAGE:100"};
    runCommand(args);
    pattern = Pattern.compile("^No in-edges! Vertex: .* is not reachable, from file: 'graphml.misc.missing_inedges.graphml'$", Pattern.MULTILINE);
    matcher = pattern.matcher(errMsg);
    assertTrue(matcher.find());
  }

  /**
   * Test command: java -jar mbt.jar offline -f graphml/misc/missing_inedges.graphml -g RANDOM -s
   * EDGE_COVERAGE:100
   */
  public void testVertexWithNoInEdges() {
    String args[] = {"offline", "-f", "graphml/misc/missing_inedges.graphml", "-g", "RANDOM", "-s", "EDGE_COVERAGE:100"};
    runCommand(args);
    pattern = Pattern.compile("No in-edges! Vertex: 'v_InvalidKey', INDEX=9 is not reachable.", Pattern.MULTILINE);
    matcher = pattern.matcher(errMsg);
    assertTrue(matcher.find());
  }

  /**
   * Test command: java -jar mbt.jar online -f graphml/methods/Main.graphml -g RANDOM -s
   * TEST_DURATION:10
   */
  public void testOnlineRandom10seconds() {
    String args[] = {"online", "-f", "graphml/methods/Main.graphml", "-g", "RANDOM", "-s", "TEST_DURATION:10", "-o", "1"};
    InputStream oldInputStream = System.in; // backup
    System.setIn(redirectIn());
    long startTime = System.currentTimeMillis();
    runCommand(args);
    long runTime = System.currentTimeMillis() - startTime;
    System.setIn(oldInputStream);
    assertEquals("No error messages should occur.", "", errMsg);
    assertTrue((runTime - 10000) < 3000);
  }

  /**
   * Test command: java -jar mbt.jar methods -f graphml/methods/Main.graphml
   */
  public void testCountMethods() {
    String args[] = {"methods", "-f", "graphml/methods/Main.graphml"};
    runCommand(args);
    assertTrue("No error messages should occur: " + errMsg, errMsg.isEmpty());
    pattern =
        Pattern
            .compile(
                "e_Cancel\\s+e_CloseApp\\s+e_CloseDB\\s+e_CloseDialog\\s+e_EnterCorrectKey\\s+e_EnterInvalidKey\\s+e_Initialize\\s+e_No\\s+e_Start\\s+e_StartWithDatabase\\s+e_Yes\\s+v_EnterMasterCompositeMasterKey\\s+v_InvalidKey\\s+v_KeePassNotRunning\\s+v_MainWindowEmpty\\s+v_MainWindow_DB_Loaded\\s+v_SaveBeforeCloseLock",
                Pattern.MULTILINE);
    matcher = pattern.matcher(outMsg);
    assertTrue(matcher.find());
  }

  /**
   * Check for reserved keywords Test command: java -jar mbt.jar methods -f graphml/test24
   */
  public void testReservedKeywords() {
    String args[] = {"methods", "-f", "graphml/test24"};
    runCommand(args);
    pattern =
        Pattern.compile("Could not parse file: '.*graphml.test24.(Camera|Time).graphml'. Edge has a label 'BACKTRACK', which is a reserved keyword",
            Pattern.MULTILINE);
    matcher = pattern.matcher(errMsg);
    System.out.println(errMsg);
    assertTrue(matcher.find());
  }

  /**
   * Check for reserved keywords Test command: java -jar mbt.jar xml -f xml/reqtags/mbt_init6.xml
   */
  public void testXmlSetup() {
    String args[] = {"xml", "-f", "xml/reqtags/mbt_init6.xml"};
    runCommand(args);
    assertTrue("No error messages should occur: " + errMsg, errMsg.isEmpty());
    assertEquals(6, getNumMatches(Pattern.compile("req[ \\d]+").matcher(outMsg)));
  }

  /**
   * Check that xml with java executor. Test command: java -jar mbt.jar xml -f
   * xml/reqtags/mbt_init5.xml
   */
  public void testXmlSetupWithJavaExecutor() {
    String args[] = {"xml", "-f", "xml/javaExecutor.xml"};
    runCommand(args);
    assertTrue("No error messages should occur: " + errMsg, errMsg.isEmpty());
    assertEquals(outMsg, 76, getNumMatches(Pattern.compile("(Vertex:|Edge:)").matcher(outMsg)));
  }

  /**
   * Test command: java -jar mbt.jar soap -f xml/reqtags/mbt_init6.xml
   */
  public void testSOAPWithXML() {
    String args[] = {"soap", "-f", "xml/reqtags/mbt_init6.xml"};
    runCommand(args);
    assertTrue("No error messages should occur: " + errMsg, errMsg.isEmpty());
    assertTrue(outMsg,
        outMsg
            .matches("(?s).*Now running as a SOAP server. For the WSDL file, see: http://.*:9090/mbt-services\\?WSDL\\s+Press Ctrl\\+C to quit\\s+"));
  }

  /**
   * Test command: java -jar mbt.jar soap
   */
  public void testSOAPWithoutXML() {
    String args[] = {"soap"};
    runCommand(args);
    assertTrue("No error messages should occur: " + errMsg, errMsg.isEmpty());
    assertEquals(true,
        outMsg.matches("Now running as a SOAP server. For the WSDL file, see: http://.*:9090/mbt-services\\?WSDL\\s+Press Ctrl\\+C to quit\\s+"));
  }

  /**
   * Test command: java -jar mbt.jar soap -f xml/reqtags/mbt_init6.xml
   */
  public void testSOAPWithCorrectClassPathInXML() {
    String args[] = {"soap", "-f", "xml/reqtags/mbt_init8.xml"};
    runCommand(args);
    assertTrue("No error messages should occur: " + errMsg, errMsg.isEmpty());
    assertTrue(outMsg,
        outMsg
            .matches("(?s).*Now running as a SOAP server. For the WSDL file, see: http://.*:9090/mbt-services\\?WSDL\\s+Press Ctrl\\+C to quit\\s+"));
  }

  /**
   * Test command: java -jar mbt.jar soap -f xml/reqtags/mbt_init9.xml
   */
  public void testSOAPWithIncorrectClassPathInXML() {
    String args[] = {"soap", "-f", "xml/reqtags/mbt_init9.xml"};
    runCommand(args);
    assertTrue(errMsg, errMsg.matches("(?s).*Could not add: 'non-existing-path' to CLASSPATH.*"));
  }

  /**
   * Test command: java -jar mbt.jar xml -f xml/ReachedVertex.xml
   */
  public void testReachedVertexXML() {
    String args[] = {"xml", "-f", "xml/ReachedVertex.xml"};
    runCommand(args);
    System.out.println(errMsg);
    assertTrue("No error messages should occur: " + errMsg, errMsg.isEmpty());
  }

  /**
   * Test command: java -jar mbt.jar analyze -f graphml/analyze/non-infinite-loop.graphml
   */
  public void testAnalyzeMethods() {
    String args[] = {"analyze", "-f", "graphml/analyze/infinite-loop.graphml"};
    runCommand(args);
    assertTrue("No error messages should occur: " + errMsg, errMsg.isEmpty());

    pattern = Pattern.compile("There is no way to reach: Vertex:", Pattern.MULTILINE);
    matcher = pattern.matcher(outMsg);
    int count = 0;
    while (matcher.find()) {
      count++;
    }
    assertEquals("Did not find the expected count of lines.", 21, count);
  }
}
