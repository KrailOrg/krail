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

import com.google.inject.Inject;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * If a Map<String, DirectSitemapEntry> binding has been created (using Guice modules sub-classed from
 * {@link DirectSitemapModule}), then {@link #pageMap} will be non-null. If so, its contents are transferred to the
 * {@link MasterSitemap}. Also loads the standard pages.
 *
 * @author David Sowerby
 */
public class DefaultDirectSitemapLoader extends SitemapLoaderBase implements DirectSitemapLoader {


    //uses method injection in case there are none
    private Map<String, DirectSitemapEntry> pageMap;
    private Map<String, RedirectEntry> redirects;
    private Set<String> sourceModules;


    @Inject
    protected DefaultDirectSitemapLoader() {
    }

    public Set<String> sourceModules() {
        return sourceModules;
    }

    @Override
    public boolean load(@Nonnull MasterSitemap sitemap) {
        checkNotNull(sitemap);
        sourceModules = new HashSet<>();
        if (pageMap != null) {
            for (Entry<String, DirectSitemapEntry> entry : pageMap.entrySet()) {
                NodeRecord nodeRecord = new NodeRecord(entry.getKey());
                DirectSitemapEntry value = entry.getValue();
                nodeRecord.setLabelKey(value.getLabelKey());
                nodeRecord.setPageAccessControl(value.getPageAccessControl());
                nodeRecord.setViewClass(value.getViewClass());
                nodeRecord.setPositionIndex(value.getPositionIndex());
                sitemap.append(nodeRecord);
                sourceModules.add(value.getModuleName());
            }
            processRedirects(sitemap);
            for (String sourceModule : sourceModules) {
                addInfo("Source Module:", "Module name: " + sourceModule);
            }
            return true;
        }
        processRedirects(sitemap);
        return false;
    }

    /**
     * Transfers directly defined URI redirects to the {@code sitemap}
     *
     * @param sitemap the sitemap to pass the redirects to
     */
    protected void processRedirects(MasterSitemap sitemap) {
        if (redirects != null) {
            for (Entry<String, RedirectEntry> entry : redirects.entrySet()) {
                sitemap.addRedirect(entry.getKey(), entry.getValue()
                                                         .getRedirectTarget());
            }
        }
    }

    /**
     *
     * @param map map of {@link DirectSitemapEntry} entries configured through Guice - which may not have happened, hence use of optional
     */
    @Inject(optional = true)
    protected void setMap(Map<String, DirectSitemapEntry> map) {
        this.pageMap = map;
    }

    /**
     *
     * @param redirects map of redirect entries configured through Guice - which may not have happened, hence use of optional
     */
    @Inject(optional = true)
    protected void setRedirects(Map<String, RedirectEntry> redirects) {
        this.redirects = redirects;
    }

}
