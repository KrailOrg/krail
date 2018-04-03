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

import uk.q3c.krail.core.view.KrailView;
import uk.q3c.krail.i18n.I18NKey;

import java.io.Serializable;
import java.util.Set;

/**
 * Implementations check the Sitemap for inconsistencies, specifically:
 * <ol>
 * <li>nodes without a view class
 * <li>nodes without a label key
 * </ol>
 * Optionally missing views or keys can be replaced with defaults by making calls to
 * {@link #replaceMissingViewWith(Class)} and/or {@link #replaceMissingKeyWith(I18NKey)} before calling {@link
 * #check(MasterSitemap)}
 *
 * @author David Sowerby
 */
public interface SitemapFinisher extends Serializable {

    /**
     * Throws a {@link SitemapException} if the check finishes with one or more nodes not having a view or a key. This
     * will also depend on whether {@link #replaceMissingKeyWith(I18NKey)} and/or {@link
     * #replaceMissingViewWith(Class)}
     * have been called. If a redirect is in place for a node, then a check will not fail if a view class or label key
     * is missing
     *
     * @param sitemap the sitemap to check
     */
    void check(MasterSitemap sitemap);

    /**
     * If a node has no view class defined, it has its view class set to {@code defaultView}
     *
     * @param defaultView the view to use as default
     *
     * @return this for fluency
     */
    SitemapFinisher replaceMissingViewWith(Class<? extends KrailView> defaultView);

    /**
     * If a node has no label key defined, it has its label key set to {@code defaultKey}
     *
     * @param defaultKey the I18NKey to use as default
     *
     * @return this for fluency
     */
    SitemapFinisher replaceMissingKeyWith(I18NKey defaultKey);

    /**
     * Module names for the report
     *
     * @param names Module names for the report
     */
    void setSourceModuleNames(Set<String> names);

    /**
     * Annotation sources for the report
     *
     * @param sources Annotation sources for the report
     */
    void setAnnotationSources(Set<String> sources);


}
