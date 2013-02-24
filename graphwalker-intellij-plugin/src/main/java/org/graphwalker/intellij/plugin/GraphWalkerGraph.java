package org.graphwalker.intellij.plugin;

import com.intellij.openapi.vfs.VirtualFile;

import java.awt.*;

public class GraphWalkerGraph {

    private final VirtualFile myFile;

    public GraphWalkerGraph(VirtualFile file) {
        myFile = file;
    }

    public boolean isModified() {
        return false;
    }

    public boolean isValid() {
        return myFile.isValid();
    }

    public Rectangle getBounds() {
        return new Rectangle(0, 0, 1000, 1000);
    }
}
