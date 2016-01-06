/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */
package uk.q3c.krail.core.navigate.sitemap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class SitemapLoaderBase implements SitemapLoader {
    private final Map<String, List<SitemapLoader.LogEntry>> errors;
    private final Map<String, List<SitemapLoader.LogEntry>> warnings;
    private final Map<String, List<SitemapLoader.LogEntry>> infos;
    private int errorCount;
    private int infoCount;
    private int warningCount;


    protected SitemapLoaderBase() {
        errors = new TreeMap<>();
        warnings = new TreeMap<>();
        infos = new TreeMap<>();
    }

    protected void addError(String source, String msgPattern, Object... msgParams) {
        SitemapLoader.LogEntry errorEntry = new SitemapLoader.LogEntry();
        errorEntry.msgPattern = msgPattern;
        errorEntry.msgParams = msgParams;
        List<SitemapLoader.LogEntry> list = errors.get(source);
        if (list == null) {
            list = new ArrayList<>();
            errors.put(source, list);
        }
        list.add(errorEntry);
        errorCount++;

    }

    protected void addWarning(String source, String msgPattern, Object... msgParams) {
        SitemapLoader.LogEntry warningEntry = new SitemapLoader.LogEntry();
        warningEntry.msgPattern = msgPattern;
        warningEntry.msgParams = msgParams;
        List<SitemapLoader.LogEntry> list = warnings.get(source);
        if (list == null) {
            list = new ArrayList<>();
            warnings.put(source, list);
        }
        list.add(warningEntry);
        warningCount++;

    }

    protected void addInfo(String source, String msgPattern, Object... msgParams) {
        SitemapLoader.LogEntry infoEntry = new SitemapLoader.LogEntry();
        infoEntry.msgPattern = msgPattern;
        infoEntry.msgParams = msgParams;
        List<SitemapLoader.LogEntry> list = infos.get(source);
        if (list == null) {
            list = new ArrayList<>();
            infos.put(source, list);
        }
        list.add(infoEntry);
        infoCount++;
    }

    @Override
    public Map<String, List<SitemapLoader.LogEntry>> getErrors() {
        return errors;
    }

    @Override
    public Map<String, List<SitemapLoader.LogEntry>> getWarnings() {
        return warnings;
    }

    @Override
    public Map<String, List<SitemapLoader.LogEntry>> getInfos() {
        return infos;
    }

    @Override
    public int getErrorCount() {
        return errorCount;
    }

    @Override
    public int getWarningCount() {
        return warningCount;
    }

    @Override
    public int getInfoCount() {
        return infoCount;
    }

    protected void clearCounts() {
        errorCount = 0;
        warningCount = 0;
        infoCount = 0;
    }


}
