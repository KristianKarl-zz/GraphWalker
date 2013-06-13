package org.graphwalker.jenkins.plugin;

import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

public class Descriptor extends BuildStepDescriptor<hudson.tasks.Publisher> {

    private static Descriptor descriptor = null;

    private Descriptor() {
        super(Publisher.class);
    }

    public static Descriptor getInstance() {
        if (null == descriptor) {
            descriptor = new Descriptor();
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
    public Publisher newInstance(StaplerRequest request, JSONObject data) throws FormException {
        Publisher publisher = request.bindParameters(Publisher.class, Messages.plugin_id());

        return publisher;
    }

}
