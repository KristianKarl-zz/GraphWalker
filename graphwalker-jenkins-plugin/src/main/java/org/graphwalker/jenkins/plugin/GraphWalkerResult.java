package org.graphwalker.jenkins.plugin;

import hudson.model.ModelObject;
import org.graphwalker.core.reports.GraphWalkerReportType;
import org.graphwalker.core.reports.RequirementsType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GraphWalkerResult implements ModelObject, Serializable {

    private static final long serialVersionUID = 1341889337197236412L;

    private String myClazz;
    private int myPassedRequirementCount = 0;
    private int myFailedRequirementCount = 0;
    private int myNotCoveredRequirementCount = 0;
    private int myTotalRequirementCount = 0;
    private long myTimestamp = 0;

    List<GraphWalkerResult> myReportResults = new ArrayList<GraphWalkerResult>();

    public GraphWalkerResult() {}

    public GraphWalkerResult(String clazz, RequirementsType requirements, long timestamp) {
        myClazz = clazz;
        myFailedRequirementCount = requirements.getFailed().intValue();
        myNotCoveredRequirementCount = requirements.getNotCovered().intValue();
        myPassedRequirementCount = requirements.getPassed().intValue();
        myTotalRequirementCount = requirements.getCount().intValue();
        myTimestamp = timestamp;
    }

    public void addReport(GraphWalkerReportType report) {
        GraphWalkerResult reportResult = new GraphWalkerResult(report.getClazz(), report.getRequirements(), report.getTimestamp());
        myReportResults.add(reportResult);
        myFailedRequirementCount += reportResult.getFailedRequirementCount();
        myNotCoveredRequirementCount += reportResult.getNotCoveredRequirementCount();
        myPassedRequirementCount += reportResult.getPassedRequirementCount();
        myTotalRequirementCount += reportResult.getTotalRequirementCount();
        if (0 == myTimestamp || myTimestamp > report.getTimestamp()) {
            myTimestamp = report.getTimestamp();
        }
    }

    public List<GraphWalkerResult> getReportResults() {
        return myReportResults;
    }

    public String getClazz() {
        return myClazz;
    }

    public long getTimestamp() {
        return myTimestamp;
    }

    public int getFailedRequirementCount() {
        return myFailedRequirementCount;
    }

    public double getFailedRequirementPercentage() {
        return (0!=getTotalRequirementCount()?((double)getFailedRequirementCount()/getTotalRequirementCount()):0d);
    }

    public int getPassedRequirementCount() {
        return myPassedRequirementCount;
    }

    public double getPassedRequirementPercentage() {
        return (0!=getTotalRequirementCount()?((double)getPassedRequirementCount()/getTotalRequirementCount()):0d);
    }

    public int getNotCoveredRequirementCount() {
        return myNotCoveredRequirementCount;
    }

    public double getNotCoveredRequirementPercentage() {
        return (0!=getTotalRequirementCount()?((double)getNotCoveredRequirementCount()/getTotalRequirementCount()):0d);
    }

    public int getTotalRequirementCount() {
        return myTotalRequirementCount;
    }

    public String getDisplayName() {
        return Messages.result_display_name();
    }
}

