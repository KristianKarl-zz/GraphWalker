package org.graphwalker.core.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Collection {

    public static <T> List<T> unmodifiableList(List<? extends T> modifiableList) {
        if (null != modifiableList) {
            return Collections.unmodifiableList(modifiableList);
        } else {
            return new ArrayList<T>();
        }
    }
}
