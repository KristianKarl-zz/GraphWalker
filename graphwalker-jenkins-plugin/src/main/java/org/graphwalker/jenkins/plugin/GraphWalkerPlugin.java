package org.graphwalker.jenkins.plugin;

import hudson.Plugin;
import hudson.model.Hudson;
import hudson.model.TransientViewActionFactory;

public class GraphWalkerPlugin extends Plugin {

    @Override
    public void start() throws Exception {
        Hudson.getInstance().getExtensionList(TransientViewActionFactory.class).add(0, new ViewActionFactory());
        super.start();
    }
}
