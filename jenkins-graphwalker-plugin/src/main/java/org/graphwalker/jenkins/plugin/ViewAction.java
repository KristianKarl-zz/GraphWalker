package org.graphwalker.jenkins.plugin;

import hudson.model.*;
import org.kohsuke.stapler.bind.JavaScriptMethod;

import java.util.*;

public class ViewAction implements RootAction, ModelObject {

    private final View myView;

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
    public Map<String,String> getJobAttributes(String name) {
        Map<String,String> attributes = new HashMap<String,String>();
        Job job = (Job)getView().getItem(name);
        if (((AbstractProject)job).isDisabled()) {
            attributes.put("isDisabled", "true");
        } else {
            attributes.put("isBuilding", job.isBuilding()?"true":"false");
            if (job.isBuilding()) {
                Executor executor = job.getLastBuild().getExecutor();
                attributes.put("isLikelyStuck", executor.isLikelyStuck()?"true":"false");
                attributes.put("progress", ""+executor.getProgress());
            } else {
                attributes.put("success", job.getLastBuild().getResult().isWorseThan(Result.SUCCESS)?"false":"true");
                attributes.put("failure", job.getLastBuild().getResult().isBetterThan(Result.FAILURE)?"false":"true");
            }
        }
        return attributes;
    }
}
