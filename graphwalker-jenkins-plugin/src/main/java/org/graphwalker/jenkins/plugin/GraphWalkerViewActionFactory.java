package org.graphwalker.jenkins.plugin;

import hudson.model.Action;
import hudson.model.TransientViewActionFactory;
import hudson.model.View;

import java.util.ArrayList;
import java.util.List;

public class GraphWalkerViewActionFactory extends TransientViewActionFactory {

    @Override
    public List<Action> createFor(View view) {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new GraphWalkerViewAction(view));
        return actions;
    }
}
