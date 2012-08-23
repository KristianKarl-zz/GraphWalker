package org.graphwalker.jenkins.plugin;

import hudson.model.Action;
import hudson.model.View;

public class ViewAction implements Action {

    private final View myView;

    public ViewAction(View view) {
        myView = view;
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

    public View getView() {
        return myView;
    }
}
