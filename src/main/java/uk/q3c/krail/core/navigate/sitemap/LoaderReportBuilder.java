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

import uk.q3c.util.clazz.ClassNameUtils;
import uk.q3c.util.text.DefaultMessageFormat;

import java.util.List;
import java.util.Map;

public class LoaderReportBuilder {


    private final List<SitemapLoader> loaders;
    private final StringBuilder report;
    private ClassNameUtils classNameUtils;

    public LoaderReportBuilder(List<SitemapLoader> loaders, ClassNameUtils classNameUtils) {
        super();
        this.loaders = loaders;
        this.classNameUtils = classNameUtils;
        this.report = new StringBuilder();
        buildReport();
    }

    private void buildReport() {
        final int width = 80;

        fillWidth(width, '~');
        fillWidth(width, ' ', "Sitemap Report");
        fillWidth(width, '~');

        // get the summary scores
        int errors = 0;
        int warnings = 0;
        int infos = 0;
        for (SitemapLoader loader : loaders) {
            errors += loader.getErrorCount();
            warnings += loader.getWarningCount();
            infos += loader.getInfoCount();
        }
        summary(errors, warnings, infos);
        report.append('\n');
        if (errors + warnings + infos == 0) {
            fillWidth(width, '-');
            report.append('\n');
            fillWidth(width, ' ', "Complete success - absolutely nothing to report");
            report.append('\n');
        } else {

            for (SitemapLoader loader : loaders) {
                String loaderName = classNameUtils.simpleClassNameEnhanceRemoved(loader.getClass());
                fillWidth(width, '=', loaderName);
                summary(loader.getErrorCount(), loader.getWarningCount(), loader.getInfoCount());

                report.append('\n');
                fillWidth(width, '-', "errors");
                logBlock(loader.getErrors());
                report.append('\n');
                fillWidth(width, '-', "warnings");
                logBlock(loader.getWarnings());
                report.append('\n');
                fillWidth(width, '-', "infos");
                logBlock(loader.getInfos());
                report.append("\n\n");
            }
        }

        fillWidth(width, '~');
        fillWidth(width, ' ', "End of Sitemap Report");
        fillWidth(width, '~');
    }

    private void logBlock(Map<String, List<SitemapLoader.LogEntry>> entryMap) {
        for (Map.Entry<String, List<SitemapLoader.LogEntry>> source : entryMap.entrySet()) {
            if (!source.getValue()
                       .isEmpty()) {
                report.append("Source: ");
                report.append(source.getKey());
                report.append('\n');

                for (SitemapLoader.LogEntry lee : source.getValue()) {
                    String msg = new DefaultMessageFormat().format(lee.msgPattern, lee.msgParams);
                    report.append('\t');
                    report.append(msg);
                    report.append('\n');
                }
            }
        }
    }

    private void summary(int errors, int warnings, int infos) {
        report.append("Summary\n");
        report.append("\terrors:\t");
        report.append(errors);
        report.append("\n\twarns :\t");
        report.append(warnings);
        report.append("\n\tinfos :\t");
        report.append(infos);
        report.append('\n');
    }

    private void fillWidth(int width, char c) {

        for (int i = 0; i < width; i++) {
            report.append(c);
        }
        report.append('\n');
    }

    private void fillWidth(int width, char c, String label) {
        for (int i = 0; i < width; i++) {
            report.append(c);
        }
        String label2 = ' ' + label + ' ';
        int labelWidth = label2.length();
        int midPoint = report.length() - (width / 2) - 1;
        int labelStart = midPoint - (labelWidth / 2);
        int i = labelStart;
        for (int j = 0; j < labelWidth; j++) {
            report.setCharAt(i, label2.charAt(j));
            i++;
        }
        report.append('\n');
    }

    public LoaderReportBuilder startSection(int level, String sectionName) {
        report.append('\n');
        switch (level) {
            case 0:
                report.append(" ==== ");
                report.append(sectionName);
                report.append("==== \n\n");
                break;
            default:
                report.append("==== \n\n");
        }

        return this;
    }

    public StringBuilder getReport() {
        return report;
    }

}
