package org.graphwalker.intellij.plugin;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

public class GraphWalkerFileTypeFactory extends FileTypeFactory implements ApplicationComponent {

    public void initComponent() {
    }

    public void disposeComponent() {
    }

    @NotNull
    public String getComponentName() {
        return getClass().getSimpleName();
    }

    @Override
    public void createFileTypes(@NotNull FileTypeConsumer consumer) {
        consumer.consume(GraphWalkerFileType.FILE_TYPE_INSTANCE, GraphWalkerFileType.FILE_TYPE_EXTENSION);
    }


}
