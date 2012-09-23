package org.graphwalker.jenkins.plugin;

import hudson.model.AbstractBuild;
import hudson.model.Action;

import java.text.DecimalFormat;

public class GraphWalkerBuildAction implements Action {

    private final AbstractBuild<?, ?> myBuild;

    public GraphWalkerBuildAction(AbstractBuild<?, ?> build) {
        myBuild = build;
    }

    public AbstractBuild<?, ?> getBuild() {
        return myBuild;
    }

    public String getIconFileName() {
        return Messages.build_action_icon_file_name();
    }

    public String getDisplayName() {
        return Messages.build_action_display_name();
    }

    public String getUrlName() {
        return Messages.build_action_url_name();
    }

    public GraphWalkerResult getResult() {
        return myBuild.getAction(GraphWalkerResultAction.class).getResult();
    }

    public String format(DecimalFormat decimalFormat, double value) {
        if (value < 1d && value > .99d) {
            return "<100%";
        }
        if (value > 0d && value < .01d) {
            return ">0%";
        }
        return decimalFormat.format(value);
    }
}
