package org.graphwalker.jenkins.plugin;

import com.thoughtworks.xstream.XStream;
import hudson.XmlFile;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.tasks.test.AbstractTestResultAction;
import hudson.util.XStream2;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class GraphWalkerResultAction extends AbstractTestResultAction<GraphWalkerResultAction> {

    private static final XStream XSTREAM = new XStream2();
    private transient WeakReference<GraphWalkerResult> myResult = null;

    protected GraphWalkerResultAction(AbstractBuild owner, GraphWalkerResult result, BuildListener listener) {
        super(owner);
        setResult(result, listener);
    }

    public synchronized void setResult(GraphWalkerResult result, BuildListener listener) {
        saveResult(result, listener);
        myResult = new WeakReference<GraphWalkerResult>(result);
    }

    public synchronized GraphWalkerResult getResult() {
        GraphWalkerResult result = null;
        if (null != myResult && null != myResult.get()) {
            result = myResult.get();
        } else {
            result = loadResult();
        }
        return result;
    }

    private void saveResult(GraphWalkerResult result, BuildListener listener) {
        try {
            getPersistentFile().write(result);
        } catch (IOException e) {
            e.printStackTrace(listener.fatalError(Messages.result_save_error()));
        }
    }

    public GraphWalkerResult loadResult() {
        try {
            myResult = new WeakReference<GraphWalkerResult>((GraphWalkerResult)getPersistentFile().read());
        } catch (IOException e) {
            myResult = new WeakReference<GraphWalkerResult>(new GraphWalkerResult());
        }
        return myResult.get();
    }

    private XmlFile getPersistentFile() {
        return new XmlFile(XSTREAM, new File(owner.getRootDir(), Messages.plugin_persistent_file_name()));
    }

    @Override
    public int getFailCount() {
        return getResult().getFailedRequirementCount();
    }

    @Override
    public int getTotalCount() {
        return getResult().getTotalRequirementCount();
    }


}
