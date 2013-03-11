/*
 * #%L
 * GraphWalker Core
 * %%
 * Copyright (C) 2011 - 2012 GraphWalker
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package org.graphwalker.core.reports;

import org.graphwalker.core.GraphWalker;

import java.io.File;
import java.util.Date;

public class XMLReport implements Report {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private final ObjectFactory objectFactory = new ObjectFactory();

    public void writeReport(GraphWalker graphWalker, File reportDirectory, Date startTime) {
        /*
        for (Model model : graphWalker.getConfiguration().getModels()) {
            writeReport(model, reportDirectory);
        }
        */
    }

    public GraphWalkerReportType readReport(File file) {
        /*
        try {
            return ((JAXBElement<GraphWalkerReportType>)createUnmarshaller().unmarshal(file)).getValue();
        } catch (JAXBException e) {
            throw new ReportException(Resource.getText(Bundle.NAME, "exception.report.failure"), e);
        }
        */
        return null;
    }
    /*
    private Marshaller createMarshaller() {
        Marshaller marshaller;
        try {
            marshaller = JAXBContext.newInstance(ObjectFactory.class).createMarshaller();
        } catch (JAXBException e) {
            throw new ReportException(Resource.getText(Bundle.NAME, "exception.report.failure"), e);
        }
        return marshaller;
    }

    private Unmarshaller createUnmarshaller() {
        Unmarshaller unmarshaller;
        try {
            unmarshaller = JAXBContext.newInstance(ObjectFactory.class).createUnmarshaller();
        } catch (JAXBException e) {
            throw new ReportException(Resource.getText(Bundle.NAME, "exception.report.failure"), e);
        }
        return unmarshaller;
    }

    private void writeReport(Model model, File reportDirectory) {
        try {
            GraphWalkerReportType graphWalkerReportType = createGraphWalkerReportType(model);
            JAXBElement<GraphWalkerReportType> report = objectFactory.createGraphwalkerReport(graphWalkerReportType);
            createMarshaller().marshal(report, getOutputStream(model, reportDirectory));
        } catch (JAXBException e) {
            throw new ReportException(Resource.getText(Bundle.NAME, "exception.report.failure"), e);
        }
    }

    private GraphWalkerReportType createGraphWalkerReportType(Model model) {
        GraphWalkerReportType graphWalkerReportType = objectFactory.createGraphWalkerReportType();
        graphWalkerReportType.setClazz(model.getImplementation().getClass().getName());
        graphWalkerReportType.setGroup(model.getGroup());
        graphWalkerReportType.setTimestamp(new Date().getTime());
        graphWalkerReportType.setVertices(createVerticesType(model));
        graphWalkerReportType.setEdges(createEdgesType(model));
        graphWalkerReportType.setRequirements(createRequirementsType(model));
        graphWalkerReportType.setExceptions(createExceptionsType(model));
        return graphWalkerReportType;
    }

    private VerticesType createVerticesType(Model model) {
        VertexStatistics vertexStatistics = new VertexStatistics(model.getVertices());
        VerticesType verticesType = objectFactory.createVerticesType();
        verticesType.setBlocked(BigInteger.valueOf(vertexStatistics.getBlockedVertexCount()));
        verticesType.setCount(BigInteger.valueOf(vertexStatistics.getVertexCount()));
        verticesType.setUnreachable(BigInteger.valueOf(vertexStatistics.getUnreachableVertexCount()));
        verticesType.setVisited(BigInteger.valueOf(vertexStatistics.getVisitedVertexCount()));
        for (Vertex vertex: model.getVertices()) {
            VertexType vertexType = objectFactory.createVertexType();
            vertexType.setId(vertex.getId());
            vertexType.setName(vertex.getName());
            vertexType.setStatus(vertex.getStatus().name());
            vertexType.setVisitCount(BigInteger.valueOf(vertex.getVisitCount()));
            verticesType.getVertex().add(vertexType);
        }
        return verticesType;
    }

    private EdgesType createEdgesType(Model model) {
        EdgeStatistics edgeStatistics = new EdgeStatistics(model.getEdges());
        EdgesType edgesType = objectFactory.createEdgesType();
        edgesType.setBlocked(BigInteger.valueOf(edgeStatistics.getBlockedEdgeCount()));
        edgesType.setCount(BigInteger.valueOf(edgeStatistics.getEdgeCount()));
        edgesType.setUnreachable(BigInteger.valueOf(edgeStatistics.getUnreachableEdgeCount()));
        edgesType.setVisited(BigInteger.valueOf(edgeStatistics.getVisitedEdgeCount()));
        for (Edge edge: model.getEdges()) {
            EdgeType edgeType = objectFactory.createEdgeType();
            edgeType.setId(edge.getId());
            edgeType.setName(edge.getName());
            edgeType.setStatus(edge.getStatus().name());
            edgeType.setVisitCount(BigInteger.valueOf(edge.getVisitCount()));
            edgesType.getEdge().add(edgeType);
        }
        return edgesType;
    }

    private RequirementsType createRequirementsType(Model model) {
        RequirementStatistics requirementStatistics = new RequirementStatistics(model.getRequirements());
        RequirementsType requirementsType = objectFactory.createRequirementsType();
        requirementsType.setCount(BigInteger.valueOf(requirementStatistics.getRequirementCount()));
        requirementsType.setFailed(BigInteger.valueOf(requirementStatistics.getFailedRequirementCount()));
        requirementsType.setNotCovered(BigInteger.valueOf(requirementStatistics.getNotCoveredRequirementCount()));
        requirementsType.setPassed(BigInteger.valueOf(requirementStatistics.getPassedRequirementCount()));
        for (Requirement requirement: model.getRequirements()) {
            RequirementType requirementType = objectFactory.createRequirementType();
            requirementType.setId(requirement.getId());
            requirementType.setStatus(requirement.getStatus().name());
            requirementsType.getRequirement().add(requirementType);
        }
        return requirementsType;
    }

    private ExceptionsType createExceptionsType(Model model) {
        ExceptionsType exceptionsType = objectFactory.createExceptionsType();
        for (Throwable throwable : model.getExceptionStrategy().getExceptions(model)) {
            ExceptionType exceptionType = objectFactory.createExceptionType();
            exceptionType.setContent(getStackTrace(throwable));
            exceptionsType.getException().add(exceptionType);
        }
        return exceptionsType;
    }

    private OutputStream getOutputStream(Model model, File reportDirectory) {
        if (!reportDirectory.mkdirs()) {
            if (!reportDirectory.exists()) {
                throw new ReportException(Resource.getText(Bundle.NAME, "exception.report.directory"));
            }
        }
        File reportFile = new File(reportDirectory, model.getImplementation().getClass().getName() + ".xml");
        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(reportFile);
        } catch (FileNotFoundException e) {
            throw new ReportException(Resource.getText(Bundle.NAME, "exception.report.creating", e.getMessage()), e);
        }
        return outputStream;
    }

    private String getStackTrace(Throwable throwable) {
        StringBuilder stringBuilder = new StringBuilder(LINE_SEPARATOR);
        stringBuilder.append("      ");
        stringBuilder.append(throwable.toString());
        for (StackTraceElement element : throwable.getStackTrace()) {
            stringBuilder.append(LINE_SEPARATOR);
            stringBuilder.append("        at ");
            stringBuilder.append(element.toString());
        }
        if (null != throwable.getCause()) {
            appendStackTraceCause(stringBuilder, throwable.getStackTrace(), throwable.getCause());
        }
        stringBuilder.append(LINE_SEPARATOR);
        stringBuilder.append("    ");
        return stringBuilder.toString();
    }

    private void appendStackTraceCause(StringBuilder stringBuilder, StackTraceElement[] stackTraceElements, Throwable throwable) {
        StackTraceElement[] causedStackTraceElements = throwable.getStackTrace();
        int m = causedStackTraceElements.length - 1, n = stackTraceElements.length - 1;
        while (m >= 0 && n >= 0 && causedStackTraceElements[m].equals(stackTraceElements[n])) {
            m--;
            n--;
        }
        int framesInCommon = causedStackTraceElements.length - 1 - m;
        stringBuilder.append(LINE_SEPARATOR);
        stringBuilder.append("      ").append("Caused by: ");
        stringBuilder.append(throwable.toString());
        for (int i = 0; i <= m; i++) {
            stringBuilder.append(LINE_SEPARATOR);
            stringBuilder.append("        at ");
            stringBuilder.append(causedStackTraceElements[i]);
        }
        if (framesInCommon != 0) {
            stringBuilder.append(LINE_SEPARATOR);
            stringBuilder.append("        ... ");
            stringBuilder.append(framesInCommon);
            stringBuilder.append(" more");
        }
        if (null != throwable.getCause()) {
            appendStackTraceCause(stringBuilder, stackTraceElements, throwable.getCause());
        }
    }
    */
}
