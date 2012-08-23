package org.graphwalker.jenkins.plugin;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Recorder;
import org.graphwalker.core.reports.GraphWalkerReportType;
import org.graphwalker.core.reports.Report;
import org.graphwalker.core.reports.XMLReport;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Publisher extends Recorder {

    private static final String REPORT_FILE_PATTERN = "target/graphwalker-reports/*.xml";
    private Report myReport = new XMLReport();

    @DataBoundConstructor
    public Publisher() {

    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.STEP;
    }

    @Override
    public Collection<? extends Action> getProjectActions(AbstractProject<?, ?> project) {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new ProjectAction(project, this));
        return actions;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, final BuildListener listener) throws InterruptedException, IOException {
        build.getActions().add(new BuildAction(build, this));
        return true;
    }

    public List<GraphWalkerReportType> getBuildReports(AbstractBuild<?, ?> build) {
        List<GraphWalkerReportType> buildReports = new ArrayList<GraphWalkerReportType>();
        try {
            FilePath[] filePaths = build.getWorkspace().list(REPORT_FILE_PATTERN);
            for (FilePath path: filePaths) {
                File file = new File(path.getRemote());
                buildReports.add(myReport.readReport(file));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return buildReports;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<hudson.tasks.Publisher> {

        public DescriptorImpl() {
            super(Publisher.class);
            load();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return PluginImpl.DISPLAY_NAME;
        }
    }
}
