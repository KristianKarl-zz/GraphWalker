package org.graphwalker.jenkins.plugin;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Actionable;
import hudson.model.ProminentProjectAction;
import hudson.util.DataSetBuilder;
import org.jfree.data.category.CategoryDataset;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;

public class GraphWalkerProjectAction extends Actionable implements ProminentProjectAction {

    private final AbstractProject<?,?> project;

    public GraphWalkerProjectAction(AbstractProject<?,?> project) {
        this.project = project;
    }

    public AbstractProject<?, ?> getProject() {
        return project;
    }

    public String getIconFileName() {
        return Messages.project_action_icon_file_name();
    }

    public String getUrlName() {
        return Messages.project_action_url_name();
    }

    public String getDisplayName() {
        return Messages.project_action_display_name();
    }

    public String getSearchUrl() {
        return Messages.project_action_url_name();
    }

    public void doGraph(final StaplerRequest request, StaplerResponse response) throws IOException {
        GraphWalkerTrendChart trendChart = new GraphWalkerTrendChart(createDataSet());
        trendChart.doPng(request, response);
    }

    private CategoryDataset createDataSet() {
        DataSetBuilder<String, Long> dataSetBuilder = new DataSetBuilder<String, Long>();
        for (AbstractBuild<?, ?> build: getProject().getBuilds()) {
            GraphWalkerResultAction action = build.getAction(GraphWalkerResultAction.class);
            if (null != action) {
                GraphWalkerResult result = action.getResult();
                if (null != result) {
                    dataSetBuilder.add(result.getPassedRequirementCount(), Messages.project_trend_passed(), result.getTimestamp());
                    dataSetBuilder.add(result.getFailedRequirementCount(), Messages.project_trend_failed(), result.getTimestamp());
                    dataSetBuilder.add(result.getNotCoveredRequirementCount(), Messages.project_trend_not_covered(), result.getTimestamp());
                }
            }
        }
        return dataSetBuilder.build();
    }

}
