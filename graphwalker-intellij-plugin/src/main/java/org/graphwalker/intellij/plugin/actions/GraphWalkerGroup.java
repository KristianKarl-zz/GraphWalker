package org.graphwalker.intellij.plugin.actions;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GraphWalkerGroup extends ActionGroup {

    @NotNull
    @Override
    public AnAction[] getChildren(@Nullable AnActionEvent e) {
        return new AnAction[0];  // TODO: Fix me (Auto generated)
    }
}
