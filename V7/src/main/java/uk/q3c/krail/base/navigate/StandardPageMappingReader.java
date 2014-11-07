/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.base.navigate;

import uk.q3c.krail.base.navigate.sitemap.PageRecord;

import java.util.HashSet;
import java.util.Set;

/**
 * Splits the supplied line (originally from sitemap.properties, [standardPageMapping] section). Checks syntax and
 * records errors as necessary
 *
 * @author David Sowerby
 */
public class StandardPageMappingReader {

    public static final String missingLabelKeyMsg = "Line must contain label key prefixed with '~'";
    public static final String missingViewMsg = "Line must contain view name prefixed with ':'";
    public static final String missingUriMsg = "Line must contain URI prefixed with '='";
    public static final String emptyViewMsg = "View class name cannot be empty";
    public static final String emptyLabelKeyMsg = "Label key cannot be empty";
    public static final String emptyStandardPageKey = "Standard page key name cannot be empty";
    private final Set<String> syntaxErrors = new HashSet<>();

    public PageRecord deconstruct(String line, int lineNumber) {
        if (!line.contains("~")) {
            syntaxErrors.add(missingLabelKeyMsg + " at line " + lineNumber);
            return null;
        }
        // if (!line.contains(":")) {
        // syntaxErrors.add(missingViewMsg + " at line " + lineNumber);
        // return null;
        // }
        if (!line.contains("=")) {
            syntaxErrors.add(missingUriMsg + " at line " + lineNumber);
            return null;
        }
        // Public_Home=public ~ Yes
        PageRecord pr = new PageRecord();
        String[] s = line.split("=");
        pr.setStandardPageKeyName(s[0].trim());
        String[] s1 = s[1].split("~");
        pr.setUri(s1[0].trim());
        pr.setSegment(segmentFromUri(s1[0]).trim());
        pr.setLabelKeyName(s1[1].trim());

        if (pr.getStandardPageKeyName()
              .isEmpty()) {
            syntaxErrors.add(emptyStandardPageKey + " at line " + lineNumber);
            return null;
        }
        if (pr.getLabelKeyName()
              .isEmpty()) {
            syntaxErrors.add(emptyLabelKeyMsg + " at line " + lineNumber);
            return null;
        }
        return pr;
    }

    private String segmentFromUri(String uri) {
        if (!(uri.contains("/"))) {
            return uri;
        }
        String[] segments = uri.split("/");
        return segments[segments.length - 1];
    }

    public Set<String> getSyntaxErrors() {
        return syntaxErrors;
    }
}
