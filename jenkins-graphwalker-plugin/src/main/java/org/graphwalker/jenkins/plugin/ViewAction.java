package org.graphwalker.jenkins.plugin;

import hudson.model.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.Converter;
import org.kohsuke.stapler.bind.JavaScriptMethod;

import java.text.SimpleDateFormat;
import java.util.*;

public class ViewAction implements RootAction, ModelObject {

    private final View myView;
    private final SimpleDateFormat myFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public ViewAction(View view) {
        myView = view;
    }

    public View getView() {
        return myView;
    }

    public String getIconFileName() {
        return PluginImpl.ICON_FILE_NAME;
    }

    public String getDisplayName() {
        return PluginImpl.DISPLAY_NAME;
    }

    public String getUrlName() {
        return PluginImpl.URL_NAME;
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
            if (((AbstractProject)job).isDisabled()) {
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
                    object.put("time", myFormater.format(job.getLastBuild().getTime()));
                }
            }
            attributes.put((String)name, object);
        }
        return attributes;
    }

}
