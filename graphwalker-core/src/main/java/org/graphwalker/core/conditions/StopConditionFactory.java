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
package org.graphwalker.core.conditions;

import org.graphwalker.core.Bundle;
import org.graphwalker.core.conditions.support.*;
import org.graphwalker.core.utils.Resource;

/**
 * <p>StopConditionFactory class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public final class StopConditionFactory {

    private StopConditionFactory() {
    }

    /**
     * <p>create.</p>
     *
     * @param type a {@link java.lang.String} object.
     * @return a {@link org.graphwalker.core.conditions.StopCondition} object.
     */
    public static StopCondition create(String type) {
        if ("Never".equalsIgnoreCase(type)) {
            return new Never();
        }
        throw new StopConditionException(Resource.getText(Bundle.NAME, "exception.condition.unknown"));
    }

    /**
     * <p>create.</p>
     *
     * @param type a {@link java.lang.String} object.
     * @param value a {@link java.lang.String} object.
     * @return a {@link org.graphwalker.core.conditions.StopCondition} object.
     */
    public static StopCondition create(String type, String value) {
        if ("Never".equalsIgnoreCase(type)) {
            return new Never();
        } else if ("EdgeCoverage".equalsIgnoreCase(type)) {
            return create(type, Integer.parseInt(value));
        } else if ("VertexCoverage".equalsIgnoreCase(type))  {
            return create(type, Integer.parseInt(value));
        } else if ("ReachedEdge".equalsIgnoreCase(type)) {
            return new ReachedEdge(value);
        } else if ("ReachedVertex".equalsIgnoreCase(type)) {
            return new ReachedVertex(value);
        } else if ("Length".equalsIgnoreCase(type)) {
            return create(type, Integer.parseInt(value));
        } else if ("TimeDuration".equalsIgnoreCase(type)) {
            return create(type, Integer.parseInt(value));
        }
        throw new StopConditionException(Resource.getText(Bundle.NAME, "exception.condition.unknown"));
    }

    /**
     * <p>create.</p>
     *
     * @param type a {@link java.lang.String} object.
     * @param value a {@link java.lang.Integer} object.
     * @return a {@link org.graphwalker.core.conditions.StopCondition} object.
     */
    public static StopCondition create(String type, long value) {
        if ("EdgeCoverage".equalsIgnoreCase(type)) {
            return new EdgeCoverage(value);
        } else if ("VertexCoverage".equalsIgnoreCase(type)) {
            return new VertexCoverage(value);
        } else if ("TimeDuration".equalsIgnoreCase(type)) {
            return new TimeDuration(value);
        } else if ("Length".equalsIgnoreCase(type)) {
            return new Length(value);
        }
        throw new StopConditionException(Resource.getText(Bundle.NAME, "exception.condition.unknown"));
    }
}
