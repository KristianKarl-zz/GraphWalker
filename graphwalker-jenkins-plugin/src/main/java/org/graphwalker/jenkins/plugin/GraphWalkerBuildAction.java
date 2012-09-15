package org.graphwalker.jenkins.plugin;

import hudson.model.AbstractBuild;
import hudson.model.Action;
import org.graphwalker.core.reports.GraphWalkerReportType;
import org.graphwalker.jenkins.plugin.charts.RingChart;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;

public class GraphWalkerBuildAction implements Action {

    private final AbstractBuild<?, ?> myBuild;
    private final GraphWalkerPublisher myPublisher;

    public GraphWalkerBuildAction(AbstractBuild<?, ?> build, GraphWalkerPublisher publisher) {
        myBuild = build;
        myPublisher = publisher;
    }

    public synchronized AbstractBuild<?, ?> getBuild() {
        return myBuild;
    }

    public String getIconFileName() {
        return GraphWalkerPlugin.ICON_FILE_NAME;
    }

    public String getDisplayName() {
        return GraphWalkerPlugin.DISPLAY_NAME;
    }

    public String getUrlName() {
        return GraphWalkerPlugin.URL_NAME;
    }

    public void doGraph(final StaplerRequest request, StaplerResponse response) throws IOException {

        RingChart ringChart = new RingChart();
        for (GraphWalkerReportType report: myPublisher.getBuildReports(myBuild)) {
            ringChart.setValue(report.getClazz(), 1);
        }
        ringChart.doPng(request, response);

    }

}
