/*
 * #%L
 * GraphWalker Core
 * %%
 * Copyright (C) 2011 - 2012 GraphWalker
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package org.graphwalker.core.util;

import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * <p>MessageBundle class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class Resource {

    private static final String BUNDLE = "messages.message";

    private Resource() {
    }

    /**
     * <p>getText.</p>
     *
     * @param key a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getText(final String key) {
        return getText(key, Locale.getDefault());
    }

    private static String getText(final String key, final Locale locale) {
        return ResourceBundle.getBundle(BUNDLE, locale).getString(key);
    }

    /**
     * <p>getFile.</p>
     *
     * @param filename a {@link java.lang.String} object.
     * @return a {@link java.io.File} object.
     */
    public static File getFile(final String filename) {
        File file = new File(filename);
        if (file.exists()) {
            return file;
        } else {
            return getResource(filename);
        }
    }

    private static File getResource(final String filename) {
        URL resource = Resource.class.getResource(filename);
        if (null == resource) {
            resource = Resource.class.getResource(File.separator+filename);
        }
        if (null != resource) {
            return new File(resource.getFile());
        }
        throw new ResourceException(Resource.getText("exception.model.file.missing"));
    }

}
