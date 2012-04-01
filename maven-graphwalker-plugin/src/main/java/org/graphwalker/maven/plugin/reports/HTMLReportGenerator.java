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
import org.graphwalker.core.utils.Resource;
import org.graphwalker.maven.plugin.Bundle;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>HTMLReportGenerator class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class HTMLReportGenerator implements ReportGenerator {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private final File myReportDirectory;

    /**
     * <p>Constructor for HTMLReportGenerator.</p>
     *
     * @param reportDirectory a {@link java.io.File} object.
     */
    public HTMLReportGenerator(File reportDirectory) {
        myReportDirectory = reportDirectory;
    }

    private File getReportFile() {
        if (!myReportDirectory.mkdirs()) {
            if (!myReportDirectory.exists()) {
                throw new ReportException(Resource.getText(Bundle.NAME, "exception.report.directory"));
            }
        }
        return new File(myReportDirectory, "report.html");
    }

    private void createAssets() throws IOException {
        File assets = new File(myReportDirectory, "assets");
        if (assets.mkdirs()) {
            File css = new File(assets, "css");
            if (css.mkdirs()) {
                IOUtil.copy(Resource.getInputStream("/bootstrap/css/bootstrap.css"), new FileOutputStream(new File(css, "bootstrap.css")));
            }
            File img = new File(assets, "img");
            if (img.mkdirs()) {

            }
            File js = new File(assets, "js");
            if (js.mkdirs()) {
                IOUtil.copy(Resource.getInputStream("/bootstrap/js/jquery.js"), new FileOutputStream(new File(js, "jquery.js")));
                IOUtil.copy(Resource.getInputStream("/bootstrap/js/bootstrap.js"), new FileOutputStream(new File(js, "bootstrap.js")));
            }
        }
    }

    private Xpp3Dom generateReport() {
        Xpp3Dom report = new Xpp3Dom("html");
        report.addChild(generateHead());
        report.addChild(generateBody());
        return report;
    }

    private Xpp3Dom generateHead() {
        Xpp3Dom head = new Xpp3Dom("head");
        head.addChild(createElement("meta", new HashMap<String, String>() {{
            put("charset", "utf-8");
        }}, null));
        head.addChild(createElement("meta", new HashMap<String, String>() {{
            put("name", "viewport");
            put("content", "width=device-width, initial-scale=1.0");
        }}, null));
        head.addChild(createElement("title", null, "GraphWalker Report"));
        head.addChild(createElement("link", new HashMap<String, String>() {{
            put("rel", "stylesheet");
            put("href", "assets/css/bootstrap.css");
        }}, null));
        head.addChild(createElement("style", null, "body {padding-top: 60px;}"));
        return head;
    }

    private Xpp3Dom createElement(String type, Map<String, String> attributes, String value) {
        Xpp3Dom element = new Xpp3Dom(type);
        if (null != attributes) {
            for (String key : attributes.keySet()) {
                element.setAttribute(key, attributes.get(key));
            }
        }
        if (null != value) {
            element.setValue(value);
        }
        return element;
    }

    private Xpp3Dom generateBody() {
        Xpp3Dom body = new Xpp3Dom("body");
        body.addChild(getNavbar());
        body.addChild(getContainer());
        body.addChild(createElement("script", new HashMap<String, String>() {{
            put("src", "assets/js/jquery.js");
        }}, null));
        body.addChild(createElement("script", new HashMap<String, String>() {{
            put("src", "assets/js/bootstrap.js");
        }}, null));
        return body;
    }

    private Xpp3Dom getNavbar() {
        Xpp3Dom navbar = new Xpp3Dom("div");
        navbar.setAttribute("class", "navbar navbar-fixed-top");
        Xpp3Dom navbarInner = new Xpp3Dom("div");
        navbarInner.setAttribute("class", "navbar-inner");
        Xpp3Dom container = new Xpp3Dom("div");
        container.setAttribute("class", "container");

        Xpp3Dom brand = new Xpp3Dom("a");
        brand.setAttribute("class", "brand");
        brand.setAttribute("href", "#");
        brand.setValue("GraphWalker");

        container.addChild(brand);
        navbarInner.addChild(container);

        navbar.addChild(navbarInner);

        /*
        <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        </a>

        <a class="brand" href="#">Project name</a>
        <div class="nav-collapse">
        <ul class="nav">
        <li class="active"><a href="#">Home</a></li>
        <li><a href="#about">About</a></li>
        <li><a href="#contact">Contact</a></li>
        </ul>
        </div><!--/.nav-collapse -->
        */


        return navbar;
    }

    private Xpp3Dom getContainer() {
        Xpp3Dom element = new Xpp3Dom("div");
        element.setAttribute("class", "container");

        Xpp3Dom start = new Xpp3Dom("h1");
        start.setValue("Content");
        element.addChild(start);

        return element;
    }

    /**
     * {@inheritDoc}
     */
    public void writeReport() {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getReportFile()), "UTF-8")));
            writer.write("<!DOCTYPE html>" + LINE_SEPARATOR);
            Xpp3DomWriter.write(new PrettyPrintXMLWriter(writer), generateReport());
            writer.flush();
            writer.close();
            createAssets();
        } catch (UnsupportedEncodingException e) {
            throw new ReportException(Resource.getText(Bundle.NAME, "exception.report.encoding"), e);
        } catch (FileNotFoundException e) {
            throw new ReportException(Resource.getText(Bundle.NAME, "exception.report.creating", e.getMessage()), e);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            IOUtil.close(writer);
        }
    }
}
