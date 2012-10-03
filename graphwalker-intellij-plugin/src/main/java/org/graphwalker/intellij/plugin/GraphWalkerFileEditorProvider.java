package org.graphwalker.intellij.plugin;

import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class GraphWalkerFileEditorProvider implements FileEditorProvider, DumbAware {

    @NonNls private static final String EDITOR_TYPE_ID = "graphwalker-editor";

    public boolean accept(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        FileTypeManager fileTypeManager = FileTypeManager.getInstance();
        FileType fileTypeByFile = fileTypeManager.getFileTypeByFile(virtualFile);
        return fileTypeByFile instanceof GraphWalkerFileType;
    }

    @NotNull
    public FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        return new GraphWalkerFileEditor(project, virtualFile);
    }

    public void disposeEditor(@NotNull FileEditor fileEditor) {
        Disposer.dispose(fileEditor);
    }

    @NotNull
    public FileEditorState readState(@NotNull Element element, @NotNull Project project, @NotNull VirtualFile virtualFile) {
        return new FileEditorState() {
            public boolean canBeMergedWith(FileEditorState otherState, FileEditorStateLevel level) {
                return false;
            }
        };
    }

    public void writeState(@NotNull FileEditorState fileEditorState, @NotNull Project project, @NotNull Element element) {
    }

    @NotNull
    public String getEditorTypeId() {
        return EDITOR_TYPE_ID;
    }

    @NotNull
    public FileEditorPolicy getPolicy() {
        return FileEditorPolicy.HIDE_DEFAULT_EDITOR;
    }

}
