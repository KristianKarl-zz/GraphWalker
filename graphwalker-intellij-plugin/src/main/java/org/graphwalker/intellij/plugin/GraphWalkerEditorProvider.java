package org.graphwalker.intellij.plugin;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

public class GraphWalkerEditorProvider implements ApplicationComponent, FileEditorProvider {

    public void initComponent() {
        // TODO: Fix me (Auto generated)
    }

    public void disposeComponent() {
        // TODO: Fix me (Auto generated)
    }

    public boolean accept(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        return false;  // TODO: Fix me (Auto generated)
    }

    @NotNull
    public FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        return null;  // TODO: Fix me (Auto generated)
    }

    public void disposeEditor(@NotNull FileEditor fileEditor) {
        // TODO: Fix me (Auto generated)
    }

    @NotNull
    public FileEditorState readState(@NotNull Element element, @NotNull Project project, @NotNull VirtualFile virtualFile) {
        return null;  // TODO: Fix me (Auto generated)
    }

    public void writeState(@NotNull FileEditorState fileEditorState, @NotNull Project project, @NotNull Element element) {
        // TODO: Fix me (Auto generated)
    }

    @NotNull
    public String getEditorTypeId() {
        return null;  // TODO: Fix me (Auto generated)
    }

    @NotNull
    public FileEditorPolicy getPolicy() {
        return null;  // TODO: Fix me (Auto generated)
    }

    @NotNull
    public String getComponentName() {
        return null;  // TODO: Fix me (Auto generated)
    }
}
