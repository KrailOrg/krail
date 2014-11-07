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

import uk.q3c.krail.base.navigate.sitemap.SitemapLoader.LoaderErrorEntry;
import uk.q3c.krail.base.navigate.sitemap.SitemapLoader.LoaderInfoEntry;
import uk.q3c.krail.base.navigate.sitemap.SitemapLoader.LoaderWarningEntry;
import uk.q3c.util.ClassnameUtils;
import uk.q3c.util.MessageFormat;

import java.util.List;
import java.util.Map;

public class LoaderReportBuilder {

    private final int width = 80;

    private final List<SitemapLoader> loaders;
    private final StringBuilder report;

    public LoaderReportBuilder(List<SitemapLoader> loaders) {
        super();
        this.loaders = loaders;
        this.report = new StringBuilder();
        buildReport();
    }

    private void buildReport() {

        fillWidth('~');
        fillWidth(' ', "Sitemap Report");
        fillWidth('~');

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
        report.append("\n");
        if (errors + warnings + infos == 0) {
            fillWidth('-');
            report.append("\n");
            fillWidth(' ', "Complete success - absolutely nothing to report");
            report.append("\n");
        } else {

            for (SitemapLoader loader : loaders) {
                String loaderName = ClassnameUtils.simpleNameWithoutEnhance(loader.getClass());
                fillWidth('=', loaderName);
                summary(loader.getErrorCount(), loader.getWarningCount(), loader.getInfoCount());

                report.append("\n");
                fillWidth('-', "errors");
                Map<String, List<LoaderErrorEntry>> errorMap = loader.getErrors();
                for (String source : errorMap.keySet()) {
                    List<LoaderErrorEntry> errorList = errorMap.get(source);
                    if (errorList.size() > 0) {
                        report.append("Source: ");
                        report.append(source);
                        report.append("\n");

                        for (LoaderErrorEntry lee : errorList) {
                            String msg = MessageFormat.format(lee.msgPattern, lee.msgParams);
                            report.append("\t");
                            report.append(msg);
                            report.append("\n");
                        }
                    }
                }
                report.append("\n");
                fillWidth('-', "warnings");
                Map<String, List<LoaderWarningEntry>> warningMap = loader.getWarnings();
                for (String source : warningMap.keySet()) {
                    List<LoaderWarningEntry> warningList = warningMap.get(source);
                    if (warningList.size() > 0) {
                        report.append("Source: ");
                        report.append(source);
                        report.append("\n");

                        for (LoaderWarningEntry lee : warningList) {
                            String msg = MessageFormat.format(lee.msgPattern, lee.msgParams);
                            report.append("\t");
                            report.append(msg);
                            report.append("\n");
                        }
                    }
                }
                report.append("\n");
                fillWidth('-', "infos");
                Map<String, List<LoaderInfoEntry>> infoMap = loader.getInfos();
                for (String source : infoMap.keySet()) {
                    List<LoaderInfoEntry> infoList = infoMap.get(source);
                    if (infoList.size() > 0) {
                        report.append("Source: ");
                        report.append(source);
                        report.append("\n");

                        for (LoaderInfoEntry lee : infoList) {
                            String msg = MessageFormat.format(lee.msgPattern, lee.msgParams);
                            report.append("\t");
                            report.append(msg);
                            report.append("\n");
                        }
                    }
                }

                report.append("\n\n");
            }
        }

        fillWidth('~');
        fillWidth(' ', "End of Sitemap Report");
        fillWidth('~');
    }

    private void summary(int errors, int warnings, int infos) {
        report.append("Summary\n");
        report.append("\terrors:\t");
        report.append(errors);
        report.append("\n\twarns :\t");
        report.append(warnings);
        report.append("\n\tinfos :\t");
        report.append(infos);
        report.append("\n");
    }

    private void fillWidth(char c) {
        for (int i = 0; i < width; i++) {
            report.append(c);
        }
        report.append("\n");
    }

    private void fillWidth(char c, String label) {
        for (int i = 0; i < width; i++) {
            report.append(c);
        }
        String label2 = " " + label + " ";
        int labelWidth = label2.length();
        int midPoint = report.length() - (width / 2) - 1;
        int labelStart = midPoint - (labelWidth / 2);
        int i = labelStart;
        for (int j = 0; j < labelWidth; j++) {
            report.setCharAt(i, label2.charAt(j));
            i++;
        }
        report.append("\n");
    }

    public LoaderReportBuilder startSection(int level, String sectionName) {
        report.append("\n");
        switch (level) {
            case 0:
                report.append(" ==== ");
                report.append(sectionName);
                report.append("==== \n\n");
                break;
        }

        return this;
    }

    public StringBuilder getReport() {
        return report;
    }

}
