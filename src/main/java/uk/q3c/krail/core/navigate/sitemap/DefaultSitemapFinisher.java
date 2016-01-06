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

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.view.KrailView;
import uk.q3c.krail.i18n.CurrentLocale;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.util.CycleDetectedException;
import uk.q3c.util.DynamicDAG;
import uk.q3c.util.MessageFormat;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Checks the Sitemap for inconsistencies after it has been loaded. The following are considered:
 * <ol>
 * <li>Missing views are not allowed unless the page is redirected
 * <li>Missing enums (label keys) are not allowed unless the page is redirected
 * <li>Redirects from within the {@link MasterSitemap} have their pageAccessControl attribute set to the
 * pageAccessControl of the redirect target.
 * <li>Redirects to a child (for example from 'private' to 'private/home' must have a label key
 * <p>
 * </ol>
 *
 * @author David Sowerby
 */
public class DefaultSitemapFinisher implements SitemapFinisher {
    private static Logger log = LoggerFactory.getLogger(DefaultSitemapFinisher.class);
    private final Set<String> missingViewClasses;
    private final Set<String> missingLabelKeys;
    private final Set<String> missingPageAccessControl;
    private final Set<String> redirectLoops;
    private Set<String> annotationSources;
    private I18NKey defaultKey;
    private Class<? extends KrailView> defaultView;
    private StringBuilder report;
    private Set<String> sourceModuleNames;

    @Inject
    protected DefaultSitemapFinisher(CurrentLocale currentLocale) {
        super();
        missingViewClasses = new HashSet<>();
        missingLabelKeys = new HashSet<>();
        missingPageAccessControl = new HashSet<>();
        redirectLoops = new HashSet<>();
    }



    @Override
    public void check(@Nonnull MasterSitemap sitemap) {
        checkNotNull(sitemap);
        // do this first, because a redirection loop will cause the main check to fail
        redirectCheck(sitemap);
        replaceMissingViews(sitemap);
        replaceMissingKeys(sitemap);
        for (MasterSitemapNode node : sitemap.getAllNodes()) {
            String nodeUri = sitemap.uri(node);
            log.debug("Checking {}", nodeUri);

            // If no redirect, must have a label key, pageAccessControl and view
            if (!sitemap.getRedirects()
                        .containsKey(nodeUri)) {

                if (node.getViewClass() == null) {
                    missingViewClasses.add(nodeUri);
                }

                if (node.getLabelKey() == null) {
                    missingLabelKeys.add(nodeUri);
                }

                if (node.getPageAccessControl() == null) {
                    missingPageAccessControl.add(nodeUri);
                }
            } else {
                // if redirected, take the accessControlPermission from the redirect target
                // note: Sitemap allows for multiple levels of redirect
                MasterSitemapNode targetNode = sitemap.nodeFor(sitemap.getRedirectPageFor(nodeUri));
                MasterSitemapNode newNode = node.modifyPageAccessControl(targetNode.getPageAccessControl());
                sitemap.replaceNode(node, newNode);

                // if redirect is from parent to child, the parent must have a label key, or it cannot display, in a
                // UserNavigationTree for example. Easiest way to check is to take the target node, get the chain
                // of nodes 'above' it, then ensure they all have a label key
                List<MasterSitemapNode> nodeChainForTarget = sitemap.nodeChainFor(targetNode);
                for (MasterSitemapNode n : nodeChainForTarget) {
                    if (n.getLabelKey() == null) {
                        missingLabelKeys.add(sitemap.uri(n));
                    }
                }
            }

        }
        // if there are no missing keys or views, return
        if (missingViewClasses.isEmpty() && missingLabelKeys.isEmpty() && missingPageAccessControl.isEmpty() &&
                redirectLoops.isEmpty()) {
            return;
        }

        report = new StringBuilder();
        report.append("\n================ Sitemap Check ===============\n\n");
        report.append("Direct Modules\n\n");
        if (sourceModuleNames != null) {
            for (String s : sourceModuleNames) {
                report.append(s);
                report.append('\n');
            }
        } else {
            report.append("No direct modules identified\n");
        }
        report.append("-----------------------------------------------\n");
        report.append("Annotation Sources\n\n");
        if (annotationSources != null) {

            for (String s : annotationSources) {
                report.append(s);
                report.append('\n');
            }
        } else {
            report.append("No annotation sources identified\n");
        }
        report.append("-----------------------------------------------\n");
        if (!missingViewClasses.isEmpty()) {
            report.append("------------ URIs with missing Views -----------\n");
            for (String view : missingViewClasses) {
                report.append(view);
                report.append('\n');
            }
        }
        if (!missingLabelKeys.isEmpty()) {
            report.append("--------- URIs with missing label keys -----------\n");
            for (String key : missingLabelKeys) {
                report.append(key);
                report.append('\n');
            }
        }
        if (!missingPageAccessControl.isEmpty()) {
            report.append("--------- URIs with missing page access control -----------\n");
            for (String key : missingPageAccessControl) {
                report.append(key);
                report.append('\n');
            }
        }

        if (!redirectLoops.isEmpty()) {
            report.append("--------- redirect loops -----------\n");
            for (String key : redirectLoops) {
                report.append(key);
                report.append('\n');
            }
        }

        log.info("{}", report.toString());
        // otherwise print a report and throw an exception
        throw new SitemapException("Sitemap check failed, see log for failed items");
    }

    private void replaceMissingViews(MasterSitemap sitemap) {
        //there's nothing to replace missing items with
        if (defaultView == null) {
            return;
        }
        for (MasterSitemapNode node : sitemap.getAllNodes()) {
            if (node.getViewClass() == null) {
                MasterSitemapNode newNode = node.modifyView(defaultView);
                sitemap.replaceNode(node, newNode);
            }
        }
    }

    private void replaceMissingKeys(MasterSitemap sitemap) {
        //there's nothing to replace missing items with
        if (defaultKey == null) {
            return;
        }
        for (MasterSitemapNode node : sitemap.getAllNodes()) {
            if (node.getLabelKey() == null) {
                MasterSitemapNode newNode = node.modifyLabelKey(defaultKey);
                sitemap.replaceNode(node, newNode);
            }
        }
    }

    private void redirectCheck(MasterSitemap sitemap) {
        DynamicDAG<String> dag = new DynamicDAG<>();
        ImmutableMap<String, String> redirectMap = sitemap.getRedirects();
        for (Entry<String, String> entry : redirectMap.entrySet()) {
            try {
                dag.addChild(entry.getKey(), entry.getValue());
            } catch (CycleDetectedException cde) {
                String msg = MessageFormat.format("Redirecting {0} to {1} would cause a loop", entry.getKey(), entry.getValue());
                redirectLoops.add(msg);
                // throw new CycleDetectedException(msg);
            }

        }

    }

    @Override
    public SitemapFinisher replaceMissingViewWith(@Nonnull Class<? extends KrailView> defaultView) {
        checkNotNull(defaultView);
        this.defaultView = defaultView;
        return this;
    }

    @Override
    public SitemapFinisher replaceMissingKeyWith(@Nonnull I18NKey defaultKey) {
        checkNotNull(defaultKey);
        this.defaultKey = defaultKey;
        return this;
    }

    @Override
    public void setSourceModuleNames(@Nonnull Set<String> names) {
        checkNotNull(names);
        this.sourceModuleNames = names;
    }

    @Override
    public void setAnnotationSources(@Nonnull Set<String> sources) {
        checkNotNull(sources);
        this.annotationSources = sources;
    }

    public Set<String> getMissingViewClasses() {
        return missingViewClasses;
    }

    public Set<String> getMissingLabelKeys() {
        return missingLabelKeys;
    }

    public StringBuilder getReport() {
        return report;
    }

    public Set<String> getMissingPageAccessControl() {
        return missingPageAccessControl;
    }

}
