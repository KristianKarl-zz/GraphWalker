package org.graphwalker.jenkins.plugin;

import hudson.model.AbstractProject;
import hudson.model.ProminentProjectAction;

public class ProjectAction implements ProminentProjectAction {

    private final AbstractProject<?, ?> myProject;
    private final Publisher myPublisher;

    public ProjectAction(AbstractProject<?, ?> project, Publisher publisher) {
        myProject = project;
        myPublisher = publisher;
    }

    public AbstractProject<?, ?> getProject() {
        return myProject;
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
}
