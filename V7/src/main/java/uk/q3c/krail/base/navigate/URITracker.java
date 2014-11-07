/*
 * Copyright (C) 2013 David Sowerby
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.q3c.krail.base.navigate;

import java.util.Stack;

/**
 * Utility class used in decoding the structure of the [map] section of the Sitemap
 *
 * @author David Sowerby 3 Jul 2013
 */
public class URITracker {

    private final Stack<String> stack = new Stack<>();

    /**
     * From the previous uri, attaches the {@code segment} at the {@code indent} level. If there is no previous uri,
     * {@code segment is taken to be a root segment}. There are three conditions
     * <ol>
     * <li>indent is greater than current indent level. {@code segment} is appended to the current uri
     * <li>indent is less than current level. {@code segment} is appended to the current uri, but at a level which puts
     * {@code segment} at the correct {@code indent} level.
     * <li>indent is the same as the current indent level. segment is attached to the parent of the current last
     * segment, or if the current segment is a root, then the new segment becomes the new root <br>
     * <br>
     * indent starts at 1 (that is, root is at indent 1). An indent level of < 1 is set to 1
     *
     * @param indent
     * @param segment
     *
     * @return
     */
    public String track(int indent, String segment) {
        if (indent < 1) {
            indent = 1;
        }
        int currentIndent = stack.size();
        if (indent > currentIndent) {
            // append to current uri
            stack.push(segment);
            return uri();
        }
        if (indent == currentIndent) {
            // append to last but one segment
            // or replace existing root if indent==1
            stack.pop();
            stack.push(segment);
            return uri();
        }
        // walk back to one less than required indent and attach segment there
        while (stack.size() > (indent - 1)) {
            stack.pop();
        }
        stack.push(segment);
        return uri();
    }

    /**
     * builds URI from stack
     *
     * @return
     */
    public String uri() {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < stack.size(); i++) {
            if (i != 0) {
                buf.append("/");
            }
            buf.append(stack.get(i));

        }
        return buf.toString();
    }

}
