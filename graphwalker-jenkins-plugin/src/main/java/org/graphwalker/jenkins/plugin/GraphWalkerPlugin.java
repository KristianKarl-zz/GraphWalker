package org.graphwalker.jenkins.plugin;

import hudson.Plugin;
import hudson.model.Hudson;
import hudson.model.TransientViewActionFactory;
import hudson.plugins.analysis.core.PluginDescriptor;

public class GraphWalkerPlugin extends Plugin {

    public static final String ICON_FILE_NAME = "";
    public static final String DISPLAY_NAME = "GraphWalker Result";
    public static final String URL_NAME = "graphwalker";
    public static final String RESULT_FILE_NAME = "graphwalker.xml";
    public static final String RESULT_URL = PluginDescriptor.createResultUrlName(URL_NAME);

    @Override
    public void start() throws Exception {
        Hudson.getInstance().getExtensionList(TransientViewActionFactory.class).add(0, new GraphWalkerViewActionFactory());
        super.start();
    }
}
