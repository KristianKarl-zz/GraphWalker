package org.graphwalker.jenkins.plugin;

import hudson.model.ModelObject;
import org.graphwalker.report.GraphWalkerReportType;
import org.graphwalker.report.RequirementsType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GraphWalkerResult implements ModelObject, Serializable {

    private static final long serialVersionUID = 1341889337197236412L;

    private String clazz;
    private int passedRequirementCount = 0;
    private int failedRequirementCount = 0;
    private int notCoveredRequirementCount = 0;
    private int totalRequirementCount = 0;
    private long timestamp = 0;

    List<GraphWalkerResult> reportResults = new ArrayList<GraphWalkerResult>();

    public GraphWalkerResult() {}

    public GraphWalkerResult(String clazz, RequirementsType requirements, long timestamp) {
        this.clazz = clazz;
        failedRequirementCount = requirements.getFailed().intValue();
        notCoveredRequirementCount = requirements.getNotCovered().intValue();
        passedRequirementCount = requirements.getPassed().intValue();
        totalRequirementCount = requirements.getCount().intValue();
        this.timestamp = timestamp;
    }

    public void addReport(GraphWalkerReportType report) {
        GraphWalkerResult reportResult = new GraphWalkerResult(report.getClazz(), report.getRequirements(), report.getTimestamp());
        reportResults.add(reportResult);
        failedRequirementCount += reportResult.getFailedRequirementCount();
        notCoveredRequirementCount += reportResult.getNotCoveredRequirementCount();
        passedRequirementCount += reportResult.getPassedRequirementCount();
        totalRequirementCount += reportResult.getTotalRequirementCount();
        if (0 == timestamp || timestamp > report.getTimestamp()) {
            timestamp = report.getTimestamp();
        }
    }

    public List<GraphWalkerResult> getReportResults() {
        return reportResults;
    }

    public String getClazz() {
        return clazz;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getFailedRequirementCount() {
        return failedRequirementCount;
    }

    public double getFailedRequirementPercentage() {
        return (0!=getTotalRequirementCount()?((double)getFailedRequirementCount()/getTotalRequirementCount()):0d);
    }

    public int getPassedRequirementCount() {
        return passedRequirementCount;
    }

    public double getPassedRequirementPercentage() {
        return (0!=getTotalRequirementCount()?((double)getPassedRequirementCount()/getTotalRequirementCount()):0d);
    }

    public int getNotCoveredRequirementCount() {
        return notCoveredRequirementCount;
    }

    public double getNotCoveredRequirementPercentage() {
        return (0!=getTotalRequirementCount()?((double)getNotCoveredRequirementCount()/getTotalRequirementCount()):0d);
    }

    public int getTotalRequirementCount() {
        return totalRequirementCount;
    }

    public String getDisplayName() {
        return Messages.result_display_name();
    }
}

