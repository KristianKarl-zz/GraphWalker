package org.graphwalker.jenkins.plugin;

import hudson.plugins.analysis.core.AbstractHealthDescriptor;
import hudson.plugins.analysis.core.HealthDescriptor;
import hudson.plugins.analysis.util.model.AnnotationProvider;
import org.jvnet.localizer.Localizable;
import org.jvnet.localizer.ResourceBundleHolder;

public class GraphWalkerHealthDescriptor extends AbstractHealthDescriptor {

    public GraphWalkerHealthDescriptor(final HealthDescriptor descriptor) {
        super(descriptor);
    }

    @Override
    protected Localizable createDescription(AnnotationProvider result) {
        return new Localizable(ResourceBundleHolder.get(GraphWalkerPlugin.class), "health.description", result.getNumberOfAnnotations());
    }
}
