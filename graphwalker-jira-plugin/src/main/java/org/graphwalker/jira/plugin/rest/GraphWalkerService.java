/*
 * #%L
 * graphwalker-jira-plugin
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
package org.graphwalker.jira.plugin.rest;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.bc.project.ProjectService;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.jql.builder.JqlClauseBuilder;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.query.Query;
import org.graphwalker.jira.plugin.rest.response.Ping;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class GraphWalkerService {

    private IssueService issueService;
    private UserManager userManager;
    private SearchService searchService;
    private ProjectService projectService;

    public GraphWalkerService(IssueService issueService, UserManager userManager, SearchService searchService, ProjectService projectService) {
        this.issueService = issueService;
        this.userManager = userManager;
        this.searchService = searchService;
        this.projectService = projectService;
    }

    @GET
    @Path("ping")
    @AnonymousAllowed
    public Response doPing(@QueryParam("nonce") String nonce) {
        Ping ping = new Ping();
        if (null != nonce) {
            ping.setNonce(nonce);
        }
        return Response.ok(ping).build();
    }

    @POST
    @Path("issue")
    public Response createIssue() {

        User user = userManager.getUser("graphwalker");

        Project project = projectService.getProjectByKey(user, "GRAPHWALKER").getProject();

        IssueInputParameters issueInputParameters = issueService.newIssueInputParameters();
        issueInputParameters.setProjectId(project.getId())
            .setIssueTypeId("2")
            .setSummary("This is a summary")
            .setReporterId("joeuser")
            .setAssigneeId("otheruser")
            .setDescription("I am a description")
            .setEnvironment("I am an environment")
            .setStatusId("2")
            .setPriorityId("2")
            .setResolutionId("2")
            .setSecurityLevelId(10000L)
            .setFixVersionIds(10000L, 10001L);

        IssueService.CreateValidationResult createValidationResult = issueService.validateCreate(user, issueInputParameters);
        if (createValidationResult.isValid()) {
            IssueService.IssueResult createResult = issueService.create(user, createValidationResult);
            if (!createResult.isValid()) {
                return Response.ok(createResult).build();
            }
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @GET
    @Path("issues")
    public Response getIssues() {

        User user = userManager.getUser("graphwalker");

        JqlClauseBuilder jqlClauseBuilder = JqlQueryBuilder.newClauseBuilder();
        Query query = jqlClauseBuilder.project("GRAPHWALKER").buildQuery();
        PagerFilter pagerFilter = PagerFilter.getUnlimitedFilter();
        SearchResults searchResults = null;
        try {
            searchResults = searchService.search(user, query, pagerFilter);
        } catch (SearchException e) {
            e.printStackTrace();  // TODO: log the exception instead
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok(searchResults.getIssues()).build();
    }

}
