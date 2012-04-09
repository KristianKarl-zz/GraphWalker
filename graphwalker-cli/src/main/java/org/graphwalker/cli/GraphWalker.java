/*
 * #%L
 * GraphWalker CLI
 * %%
 * Copyright (C) 2011 - 2012 GraphWalker
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package org.graphwalker.cli;

import org.apache.commons.cli.*;
import org.graphwalker.core.utils.Resource;

import java.io.File;

/**
 * <p>GraphWalkerCLI class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class GraphWalker {

    private final Options myOptions = new Options();
    private final CommandLineParser myParser = new PosixParser();

    GraphWalker() {
        createOptions();
    }

    private void createOptions() {
        myOptions.addOption(Resource.getText(Bundle.NAME, "option.file.mnemonic")
                , Resource.getText(Bundle.NAME, "option.file.label")
                , true
                , Resource.getText(Bundle.NAME, "option.file.description"));

        myOptions.addOption(Resource.getText(Bundle.NAME, "option.help.mnemonic")
                , Resource.getText(Bundle.NAME, "option.help.label")
                , false
                , Resource.getText(Bundle.NAME, "option.help.description"));
    }

    private void parse(String[] arguments) {
        try {
            CommandLine commandLine = myParser.parse(myOptions, arguments);
            if (commandLine.hasOption(Resource.getText(Bundle.NAME, "option.file.mnemonic"))) {
                executeFile(commandLine.getOptionValue(Resource.getText(Bundle.NAME, "option.file.mnemonic")));
            } else {
                printHelp();
            }
        } catch (ParseException e) {
            printHelp();
        }
    }

    private void executeFile(String filename) {
        File file = Resource.getFile(filename);

    }

    private void printHelp() {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(Resource.getText(Bundle.NAME, "options.label"), myOptions);
    }

    /**
     * <p>main.</p>
     *
     * @param arguments an array of {@link java.lang.String} objects.
     */
    public static void main(String[] arguments) {
        GraphWalker graphWalker = new GraphWalker();
        graphWalker.parse(arguments);
    }
}
