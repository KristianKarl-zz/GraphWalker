package org.graphwalker.jenkins.plugin;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Recorder;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

//import org.graphwalker.core.report.GraphWalkerReportType;
//import org.graphwalker.core.report.XMLReport;

public class Publisher extends Recorder {

    @Extension
    public static final Descriptor DESCRIPTOR = Descriptor.getInstance();

    @DataBoundConstructor
    public Publisher() {

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
                        TestResult result = new TestResult();
                        //XMLReport parser = new XMLReport();
                        for (FilePath file: reportDirectory.list()) {
                        //    processReport(parser, file, result);
                        }
                        build.addAction(new ResultAction(build, result, listener));
                    }
                }
            }
        } catch (IOException e) {
            Util.displayIOException(e, listener);
            build.setResult(hudson.model.Result.FAILURE);
        }
        build.addAction(new BuildAction(build));
        return true;
    }
    /*
    private void processReport(XMLReport parser, FilePath file, TestResult result) throws IOException, InterruptedException {
        if (file.exists() && !file.isDirectory()) {
            File reportFile = new File(file.getRemote());
            GraphWalkerReportType report = parser.readReport(reportFile);
            result.addReport(report);
        }
    }
    */
    @Override
    public Action getProjectAction(AbstractProject<?,?> project) {
        return new ProjectAction(project);
    }

    @Override
    public BuildStepDescriptor<hudson.tasks.Publisher> getDescriptor() {
        return DESCRIPTOR;
    }

}
