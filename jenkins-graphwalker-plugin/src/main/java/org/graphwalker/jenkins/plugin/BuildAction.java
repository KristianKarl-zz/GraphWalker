package org.graphwalker.jenkins.plugin;

import hudson.model.AbstractBuild;
import hudson.model.Action;
import org.graphwalker.jenkins.plugin.utils.Chart;
import org.jfree.chart.JFreeChart;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;

public class BuildAction implements Action {

    private final AbstractBuild<?, ?> myBuild;
    private final Publisher myPublisher;

    public BuildAction(AbstractBuild<?, ?> build, Publisher publisher) {
        myBuild = build;
        myPublisher = publisher;
    }

    public synchronized AbstractBuild<?, ?> getBuild() {
        return myBuild;
    }

    public String getIconFileName() {
        return PluginImpl.ICON_FILE_NAME;
    }

    public String getDisplayName() {
        return PluginImpl.DISPLAY_NAME;
    }

    public String getUrlName() {
        return PluginImpl.URL_NAME;
    }

    public void doGraph(final StaplerRequest req, StaplerResponse rsp) throws IOException {
        new hudson.util.Graph(-1, 500, 200) {
            protected JFreeChart createGraph() {
                return Chart.create();
            }
        }.doPng(req, rsp);
    }

}
