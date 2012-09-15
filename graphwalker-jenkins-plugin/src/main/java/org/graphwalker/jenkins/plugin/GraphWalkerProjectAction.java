package org.graphwalker.jenkins.plugin;

import hudson.model.AbstractProject;
import hudson.model.ProminentProjectAction;

public class GraphWalkerProjectAction implements ProminentProjectAction {

    private final AbstractProject<?, ?> myProject;
    private final GraphWalkerPublisher myPublisher;

    public GraphWalkerProjectAction(AbstractProject<?, ?> project, GraphWalkerPublisher publisher) {
        myProject = project;
        myPublisher = publisher;
    }

    public AbstractProject<?, ?> getProject() {
        return myProject;
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
}
