package org.graphwalker.jenkins.plugin;

import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

public class GraphWalkerDescriptor extends BuildStepDescriptor<Publisher> {

    private static GraphWalkerDescriptor descriptor = null;

    private GraphWalkerDescriptor() {
        super(GraphWalkerPublisher.class);
    }

    public static GraphWalkerDescriptor getInstance() {
        if (null == descriptor) {
            descriptor = new GraphWalkerDescriptor();
        }
        return descriptor;
    }

    @Override
    public boolean isApplicable(Class<? extends AbstractProject> jobType) {
        return true;
    }

    @Override
    public String getDisplayName() {
        return Messages.plugin_display_name();
    }

    @Override
    public boolean configure(StaplerRequest request, JSONObject data) throws FormException {
        request.bindParameters(this, Messages.plugin_id());
        save();
        return super.configure(request, data);
    }

    @Override
    public GraphWalkerPublisher newInstance(StaplerRequest request, JSONObject data) throws FormException {
        GraphWalkerPublisher publisher = request.bindParameters(GraphWalkerPublisher.class, Messages.plugin_id());

        return publisher;
    }

}
