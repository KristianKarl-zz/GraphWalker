/*
 * #%L
 * Maven GraphWalker Plugin
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
package org.graphwalker.maven.plugin.reports;

import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomWriter;
import org.graphwalker.core.GraphWalker;
import org.graphwalker.core.utils.Resource;
import org.graphwalker.maven.plugin.Bundle;

import java.io.*;

public class XMLReportGenerator {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private final GraphWalker myGraphWalker;
    private final File myReportsDirectory;

    public XMLReportGenerator(GraphWalker graphWalker, File reportsDirectory) {
        myGraphWalker = graphWalker;
        myReportsDirectory = reportsDirectory;
        myReportsDirectory.mkdirs();
    }

    private File getConfigurationFile() {
        return myGraphWalker.getConfiguration().getConfigurationFile();
    }
    
    private File getReportFile() {
        return new File(myReportsDirectory, getConfigurationFile().getName());
    }

    private Xpp3Dom generateReport() {
        Xpp3Dom report = new Xpp3Dom("report");
        report.setAttribute("file", getConfigurationFile().getAbsolutePath());
        return report;
    }

    public void writeReport() {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getReportFile()), "UTF-8")));
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+LINE_SEPARATOR);
            Xpp3DomWriter.write(new PrettyPrintXMLWriter(writer), generateReport());
        } catch (UnsupportedEncodingException e) {
            throw new ReportException(Resource.getText(Bundle.NAME, "exception.report.encoding"), e);
        } catch (FileNotFoundException e) {
            throw new ReportException(Resource.getText(Bundle.NAME, "exception.report.creating", e.getMessage()), e);
        } finally {
            IOUtil.close(writer);
        }
    }
}
