package org.graphwalker.jenkins.plugin;

import hudson.model.AbstractBuild;
import hudson.plugins.analysis.core.BuildHistory;
import hudson.plugins.analysis.core.BuildResult;
import hudson.plugins.analysis.core.ParserResult;
import hudson.plugins.analysis.core.ResultAction;

public class GraphWalkerResult extends BuildResult {

    public GraphWalkerResult(final AbstractBuild<?, ?> build, final ParserResult result, final String defaultEncoding) {
        super(build, new BuildHistory(build, GraphWalkerResultAction.class), result, defaultEncoding);
    }

    @Override
    protected String getSerializationFileName() {
        return GraphWalkerPlugin.RESULT_FILE_NAME;
    }

    @Override
    protected Class<? extends ResultAction<? extends BuildResult>> getResultActionType() {
        return GraphWalkerResultAction.class;
    }

    @Override
    public String getSummary() {
        return GraphWalkerPlugin.DISPLAY_NAME+": "+createDefaultSummary(GraphWalkerPlugin.RESULT_URL, getNumberOfAnnotations(), getNumberOfModules());
    }

    public String getDisplayName() {
        return GraphWalkerPlugin.DISPLAY_NAME;
    }
}
