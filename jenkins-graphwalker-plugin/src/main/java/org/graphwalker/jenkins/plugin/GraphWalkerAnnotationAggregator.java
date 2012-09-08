package org.graphwalker.jenkins.plugin;

import hudson.Launcher;
import hudson.matrix.MatrixBuild;
import hudson.matrix.MatrixRun;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.plugins.analysis.core.AnnotationsAggregator;
import hudson.plugins.analysis.core.BuildResult;
import hudson.plugins.analysis.core.HealthDescriptor;
import hudson.plugins.analysis.core.ParserResult;

import javax.annotation.Nonnull;

public class GraphWalkerAnnotationAggregator extends AnnotationsAggregator {

    public GraphWalkerAnnotationAggregator(final MatrixBuild build, final Launcher launcher, final BuildListener listener, final HealthDescriptor descriptor, final String encoding) {
        super(build, launcher, listener, descriptor, encoding);
    }

    @Override
    protected boolean hasResult(final MatrixRun run) {
        return null != run.getAction(GraphWalkerResultAction.class);
    }

    @Nonnull
    @Override
    protected BuildResult getResult(MatrixRun run) {
        return run.getAction(GraphWalkerResultAction.class).getResult();
    }

    @Override
    protected Action createAction(HealthDescriptor descriptor, String encoding, ParserResult result) {
        return new GraphWalkerResultAction(build, descriptor, new GraphWalkerResult(build, result, encoding));
    }
}
