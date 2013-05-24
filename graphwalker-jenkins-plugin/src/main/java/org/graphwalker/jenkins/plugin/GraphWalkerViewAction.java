package org.graphwalker.jenkins.plugin;

import hudson.model.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.bind.JavaScriptMethod;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GraphWalkerViewAction implements RootAction, ModelObject {

    private final View view;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public GraphWalkerViewAction(View view) {
        this.view = view;
    }

    public View getView() {
        return view;
    }

    public String getIconFileName() {
        return Messages.view_action_icon_file_name();
    }

    public String getDisplayName() {
        return Messages.view_action_display_name();
    }

    public String getUrlName() {
        return Messages.view_action_url_name();
    }

    @JavaScriptMethod
    public List<String> getJobNames() {
        List<String> names = new ArrayList<String>();
        for (TopLevelItem item: getView().getItems()) {
            names.add(item.getName());
        }
        Collections.sort(names);
        return names;
    }

    @JavaScriptMethod
    public JSONObject getJobAttributes(JSONArray names) {
        JSONObject attributes = new JSONObject();
        for (Object name: names) {
            JSONObject object = new JSONObject();
            Job job = (Job)getView().getItem((String)name);
            if (((AbstractProject)job).isDisabled() || null == job.getLastBuild()) {
                object.put("isDisabled", "true");
            } else {
                object.put("isBuilding", job.isBuilding() ? "true" : "false");
                if (job.isBuilding()) {
                    Executor executor = job.getLastBuild().getExecutor();
                    object.put("isLikelyStuck", executor.isLikelyStuck() ? "true" : "false");
                    object.put("progress", "" + executor.getProgress());
                } else {
                    object.put("success", job.getLastBuild().getResult().isWorseThan(Result.SUCCESS) ? "false" : "true");
                    object.put("failure", job.getLastBuild().getResult().isBetterThan(Result.FAILURE) ? "false" : "true");
                    object.put("time", dateFormat.format(job.getLastBuild().getTime()));
                }
            }
            attributes.put((String)name, object);
        }
        return attributes;
    }

}
