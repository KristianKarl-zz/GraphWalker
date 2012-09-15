package org.graphwalker.jenkins.plugin;

import hudson.FilePath;
import hudson.Launcher;
import hudson.matrix.MatrixAggregator;
import hudson.matrix.MatrixBuild;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.plugins.analysis.core.BuildResult;
import hudson.plugins.analysis.core.HealthAwarePublisher;
import hudson.plugins.analysis.core.ParserResult;
import hudson.plugins.analysis.util.PluginLogger;
import hudson.tasks.BuildStepMonitor;
import org.graphwalker.core.reports.GraphWalkerReportType;
import org.graphwalker.core.reports.Report;
import org.graphwalker.core.reports.XMLReport;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GraphWalkerPublisher extends HealthAwarePublisher {

    private static final String REPORT_FILE_PATTERN = "target/graphwalker-reports/*.xml";
    private Report myReport = new XMLReport();

    @DataBoundConstructor
    public GraphWalkerPublisher(final String healthy
            , final String unHealthy
            , final String thresholdLimit
            , final String defaultEncoding
            , final boolean useDeltaValues
            , final String unstableTotalAll
            , final String unstableTotalHigh
            , final String unstableTotalNormal
            , final String unstableTotalLow
            , final String unstableNewAll
            , final String unstableNewHigh
            , final String unstableNewNormal
            , final String unstableNewLow
            , final String failedTotalAll
            , final String failedTotalHigh
            , final String failedTotalNormal
            , final String failedTotalLow
            , final String failedNewAll
            , final String failedNewHigh
            , final String failedNewNormal
            , final String failedNewLow
            , final boolean canRunOnFailed
            , final boolean canComputeNew) {
        super(healthy, unHealthy, thresholdLimit, defaultEncoding, useDeltaValues,
            unstableTotalAll, unstableTotalHigh, unstableTotalNormal, unstableTotalLow,
            unstableNewAll, unstableNewHigh, unstableNewNormal, unstableNewLow,
            failedTotalAll, failedTotalHigh, failedTotalNormal, failedTotalLow,
            failedNewAll, failedNewHigh, failedNewNormal, failedNewLow,
            canRunOnFailed, false, canComputeNew, "GW-COLLECTOR");
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.STEP;
    }

    @Override
    public Collection<? extends Action> getProjectActions(AbstractProject<?, ?> project) {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new GraphWalkerProjectAction(project, this));
        return actions;
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

    public GraphWalkerDescriptor getDescriptor() {
        return (GraphWalkerDescriptor)super.getDescriptor();
    }

    @Override
    protected BuildResult perform(AbstractBuild<?, ?> build, PluginLogger pluginLogger) throws InterruptedException, IOException {
        ParserResult parserResult = new ParserResult(build.getWorkspace());
        GraphWalkerResult result = new GraphWalkerResult(build, parserResult, getDefaultEncoding());
        build.getActions().add(new GraphWalkerBuildAction(build, this));
        return result;
    }

    public MatrixAggregator createAggregator(MatrixBuild build, Launcher launcher, BuildListener listener) {
        return new GraphWalkerAnnotationAggregator(build,launcher,listener,this,getDefaultEncoding());
    }
}
