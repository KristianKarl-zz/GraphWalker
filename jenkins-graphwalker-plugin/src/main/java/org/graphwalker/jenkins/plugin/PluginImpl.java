package org.graphwalker.jenkins.plugin;

import hudson.Plugin;
import hudson.model.Hudson;
import hudson.model.TransientViewActionFactory;

public class PluginImpl extends Plugin {

    public static final String ICON_FILE_NAME = "";
    public static final String DISPLAY_NAME = "GraphWalker Result";
    public static final String URL_NAME = "graphwalker";

    @Override
    public void start() throws Exception {
        Hudson.getInstance().getExtensionList(TransientViewActionFactory.class).add(0, new ViewActionFactory());
        super.start();
    }
}
