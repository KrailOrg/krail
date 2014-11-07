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
package uk.q3c.krail.base.navigate.sitemap;

import com.google.common.base.Splitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.base.shiro.PageAccessControl;

import java.util.Iterator;

/**
 * Reads a line from the [map] section during the processing of sitemap.properties, and returns a {@link MapLineRecord}
 * of the constituent parts. Records syntax errors and other failures for the {@link FileSitemapLoader} to provide a
 * report
 *
 * @author David Sowerby 5 Jul 2013
 */

public class MapLineReader {
    private static Logger log = LoggerFactory.getLogger(MapLineReader.class);
    private final String leadCharSet = "+-~#";
    private String line;
    private MapLineRecord lineRecord;

    public MapLineRecord processLine(DefaultFileSitemapLoader loader, String source, int lineIndex, String line,
                                     int currentIndent, String attributeSeparator) {
        log.debug("processing line {}", line);
        this.line = line;
        lineRecord = new MapLineRecord();

        // split columns on segmentSeparator
        Splitter splitter = Splitter.on(attributeSeparator)
                                    .trimResults();
        Iterable<String> attributes = splitter.split(line);
        Iterator<String> iterator = attributes.iterator();
        // split first column into indentation and uri segment
        boolean indentOk = indentAndSegment(loader, source, iterator.next(), lineIndex);

        if (indentOk) {
            String viewAttribute = (iterator.hasNext()) ? iterator.next() : "";
            view(viewAttribute);
            String labelKeyAttribute = (iterator.hasNext()) ? iterator.next() : "";
            labelKey(labelKeyAttribute);
            String permissionAttribute = (iterator.hasNext()) ? iterator.next() : "";
            roles(permissionAttribute);
            if (lineRecord.getIndentLevel() > currentIndent + 1) {
                loader.addWarning(source, FileSitemapLoader.LINE_FORMAT_INDENTATION_INCORRECT,
                        lineRecord.getSegment(), lineIndex);
            }
        }
        return lineRecord;
    }

    /**
     * see the documentation at https://sites.google.com/site/q3cjava/sitemap#TOC-map- for description of use of
     * rolesAttribute
     *
     * @param rolesAttribute
     */
    private void roles(String rolesAttribute) {
        lineRecord.setRoles(rolesAttribute);

        if (lineRecord.getPageAccessControl() == PageAccessControl.PERMISSION) {
            if (rolesAttribute.startsWith("roles=")) {
                lineRecord.setPageAccessControl(PageAccessControl.ROLES);
                lineRecord.setRoles(rolesAttribute);
            } else {
                if (rolesAttribute.equals("*")) {
                    lineRecord.setPageAccessControl(PageAccessControl.AUTHENTICATION);
                }
            }
        }
    }

    private void labelKey(String labelKeyAttribute) {
        lineRecord.setKeyName(labelKeyAttribute);
    }

    private void view(String viewAttribute) {
        lineRecord.setViewName(viewAttribute);
    }

    private boolean indentAndSegment(DefaultFileSitemapLoader loader, String source, String s, int lineIndex) {
        if (s.isEmpty()) {
            return false;
        }
        // trimmed by splitter, safe to assume index 0 unless empty
        // any of the leading chars will do
        char leadChar = s.charAt(0);
        boolean indentFound = leadCharSet.indexOf(leadChar) >= 0;

        if (!indentFound) {
            loader.addError(source, FileSitemapLoader.LINE_FORMAT_MISSING_START_CHAR, lineIndex);
            return false;
        }

        switch (leadChar) {
            case '+':
                lineRecord.setPageAccessControl(PageAccessControl.PUBLIC);
                break;
            case '-':
                lineRecord.setPageAccessControl(PageAccessControl.PERMISSION);
                break;
            case '#':
                lineRecord.setPageAccessControl(PageAccessControl.GUEST);
                break;
            case '~':
                lineRecord.setPageAccessControl(PageAccessControl.USER);
                break;

        }

        int index = 0;
        int indent = 0;
        while (index < line.length()) {
            char c = s.charAt(index);
            if (leadCharSet.indexOf(c) >= 0) {
                indent++;
            } else {
                break;
            }
            index++;
        }
        String segment = s.substring(indent)
                          .trim();
        lineRecord.setSegment(segment);
        lineRecord.setIndentLevel(indent);
        return true;

    }

}
