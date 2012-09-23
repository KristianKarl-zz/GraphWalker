package org.graphwalker.jenkins.plugin;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import org.graphwalker.core.reports.GraphWalkerReportType;
import org.graphwalker.core.reports.Report;
import org.graphwalker.core.reports.XMLReport;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.File;
import java.io.IOException;

public class GraphWalkerPublisher extends Recorder {

    @Extension
    public static final GraphWalkerDescriptor DESCRIPTOR = GraphWalkerDescriptor.getInstance();

    @DataBoundConstructor
    public GraphWalkerPublisher() {

    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException {
        final FilePath workspace = build.getWorkspace();
        try {
            if (workspace.exists() && workspace.isDirectory()) {
                final FilePath targetDirectory = workspace.child("target");
                if (targetDirectory.exists() && targetDirectory.isDirectory()) {
                    final FilePath reportDirectory = targetDirectory.child(Messages.plugin_report_directory());
                    if (reportDirectory.exists() && reportDirectory.isDirectory()) {
                        GraphWalkerResult result = new GraphWalkerResult();
                        Report parser = new XMLReport();
                        for (FilePath file: reportDirectory.list()) {
                            processReport(parser, file, result);
                        }
                        build.addAction(new GraphWalkerResultAction(build, result, listener));
                    }
                }
            }
        } catch (IOException e) {
            Util.displayIOException(e, listener);
            build.setResult(Result.FAILURE);
        }
        build.addAction(new GraphWalkerBuildAction(build));
        return true;
    }

    private void processReport(Report parser, FilePath file, GraphWalkerResult result) throws IOException, InterruptedException {
        if (file.exists() && !file.isDirectory()) {
            File reportFile = new File(file.getRemote());
            GraphWalkerReportType report = parser.readReport(reportFile);
            result.addReport(report);
        }
    }

    @Override
    public Action getProjectAction(AbstractProject<?,?> project) {
        return new GraphWalkerProjectAction(project);
    }

    @Override
    public BuildStepDescriptor<Publisher> getDescriptor() {
        return DESCRIPTOR;
    }

}
