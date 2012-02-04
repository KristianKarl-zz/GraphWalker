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

import org.apache.maven.doxia.sink.Sink;
import org.graphwalker.core.utils.Resource;
import org.graphwalker.maven.plugin.Bundle;

/**
 * <p>HTMLReportGenerator class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class HTMLReportGenerator implements ReportGenerator {

    private Sink mySink;
    
    /**
     * <p>Constructor for HTMLReportGenerator.</p>
     *
     * @param sink a {@link org.apache.maven.doxia.sink.Sink} object.
     */
    public HTMLReportGenerator(Sink sink) {
        mySink = sink;
    }

    private Sink getSink() {
        return mySink;
    }

    /** {@inheritDoc} */
    @Override
    public void writeReport() {
        getSink().head();
        getSink().title();
        getSink().text(Resource.getText(Bundle.NAME, "report.graphwalker.header"));
        getSink().title_();
        getSink().head_();
        getSink().body();

        getSink().body_();
        getSink().flush();
        getSink().close();
    }
}
