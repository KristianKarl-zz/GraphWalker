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
package org.graphwalker.maven.plugin;

import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.graphwalker.core.util.Resource;
import org.graphwalker.maven.plugin.report.HTMLReportGenerator;
import org.graphwalker.maven.plugin.report.ReportGenerator;

import java.io.File;
import java.util.Locale;

/**
 * <p>ExecuteMojo class.</p>
 *
 * @author nilols
 * @version $Id: $
 * @goal report
 * @execute phase="test" lifecycle="graphwalker"
 */
public class ReportMojo extends AbstractMavenReport {

    /**
     * @parameter expression="${project}"
     * @required @readonly
     */
    private MavenProject project;

    /**
     * @parameter expression="${project.reporting.outputDirectory}"
     * @noinspection UnusedDeclaration
     */
    private File outputDirectory;

    /**
     * @component
     * @noinspection UnusedDeclaration
     */
    private Renderer siteRenderer;

    /**
     * @parameter expression="${outputName}" default-value="graphwalker-report"
     * @required
     */
    private String outputName;

    @Override
    protected Renderer getSiteRenderer() {
        return siteRenderer;
    }

    @Override
    protected String getOutputDirectory() {
        return outputDirectory.getAbsolutePath();
    }

    @Override
    protected MavenProject getProject() {
        return project;
    }

    @Override
    public String getOutputName() {
        return outputName;
    }

    @Override
    public String getName(Locale locale) {
        return Resource.getText(Bundle.NAME, "report.graphwalker.name");
    }

    @Override
    public String getDescription(Locale locale) {
        return Resource.getText(Bundle.NAME, "report.graphwalker.description");
    }

    @Override
    protected void executeReport(Locale locale) throws MavenReportException {
        new HTMLReportGenerator(getSink()).writeReport();
    }
}
