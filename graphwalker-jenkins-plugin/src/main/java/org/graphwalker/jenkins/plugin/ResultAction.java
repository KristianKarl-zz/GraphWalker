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

public class ResultAction extends AbstractTestResultAction<ResultAction> {

    private static final XStream XSTREAM = new XStream2();
    private transient WeakReference<TestResult> result = null;

    protected ResultAction(AbstractBuild owner, TestResult result, BuildListener listener) {
        super(owner);
        setResult(result, listener);
    }

    public synchronized void setResult(TestResult result, BuildListener listener) {
        saveResult(result, listener);
        this.result = new WeakReference<TestResult>(result);
    }

    public synchronized TestResult getResult() {
        TestResult result = null;
        if (null != this.result && null != this.result.get()) {
            result = this.result.get();
        } else {
            result = loadResult();
        }
        return result;
    }

    private void saveResult(TestResult result, BuildListener listener) {
        try {
            getPersistentFile().write(result);
        } catch (IOException e) {
            e.printStackTrace(listener.fatalError(Messages.result_save_error()));
        }
    }

    public TestResult loadResult() {
        try {
            result = new WeakReference<TestResult>((TestResult)getPersistentFile().read());
        } catch (IOException e) {
            result = new WeakReference<TestResult>(new TestResult());
        }
        return result.get();
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
