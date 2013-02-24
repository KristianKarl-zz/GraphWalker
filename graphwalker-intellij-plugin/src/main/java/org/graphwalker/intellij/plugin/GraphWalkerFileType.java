package org.graphwalker.intellij.plugin;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class GraphWalkerFileType implements FileType {

    @NonNls public static final String FILE_TYPE_NAME = "graphml";
    public static final Icon FILE_TYPE_ICON = PlatformIcons.CUSTOM_FILE_ICON; //IconLoader.getIcon("");
    @NonNls public static final String FILE_TYPE_EXTENSION = "graphml";
    @NonNls public static final String FILE_TYPE_DESCRIPTION = "graphml file";
    public static final GraphWalkerFileType FILE_TYPE_INSTANCE = new GraphWalkerFileType();

    @NotNull
    public String getName() {
        return FILE_TYPE_NAME;
    }

    @NotNull
    public String getDescription() {
        return FILE_TYPE_DESCRIPTION;
    }

    @NotNull
    public String getDefaultExtension() {
        return FILE_TYPE_EXTENSION;
    }

    public Icon getIcon() {
        return FILE_TYPE_ICON;
    }

    public boolean isBinary() {
        return false;
    }

    public boolean isReadOnly() {
        return false;
    }

    public String getCharset(@NotNull VirtualFile file, byte[] content) {
        return CharsetToolkit.UTF8;
    }
}
