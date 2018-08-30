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
package uk.q3c.krail.core.navigate.sitemap

import com.google.common.base.Preconditions.checkNotNull
import com.google.inject.AbstractModule
import com.google.inject.multibindings.MapBinder
import org.apache.commons.lang3.StringUtils
import uk.q3c.krail.core.form.EmptyFormConfiguration
import uk.q3c.krail.core.form.Form
import uk.q3c.krail.core.form.FormConfiguration
import uk.q3c.krail.core.guice.DefaultServletContextListener
import uk.q3c.krail.core.shiro.PageAccessControl
import uk.q3c.krail.core.view.EmptyViewConfiguration
import uk.q3c.krail.core.view.KrailView
import uk.q3c.krail.core.view.ViewConfiguration
import uk.q3c.krail.i18n.I18NKey

/**
 * If you want to create Sitemap entries for your own code using a direct coding approach, you can either subclass this
 * module and provide the entries in the [.define] method, or just simply use this as an example and create your
 * own. The module then needs to be added to your subclass of [DefaultServletContextListener]. By convention, modules
 * relating to the Sitemap are added in the addSitemapModules() method.
 *
 *
 * You can add any number of modules this way, but any duplicated map keys (the URI segments) will cause the map
 * injection to fail. There is an option to change this behaviour in MapBinder#permitDuplicates()
 *
 *
 * You can use multiple subclasses of this, Guice will merge all of the bindings into a single MapBinder<String></String>,
 * DirectSitemapEntry> for use by the [DirectSitemapLoader]
 *
 * @author David Sowerby
 */
abstract class DirectSitemapModule : AbstractModule() {

    protected var rootURI = ""
    private lateinit var redirectBinder: MapBinder<String, RedirectEntry>
    private lateinit var sitemapBinder: MapBinder<String, DirectSitemapEntry>


    override fun configure() {
        sitemapBinder = MapBinder.newMapBinder(binder(), String::class.java, DirectSitemapEntry::class.java)
        redirectBinder = MapBinder.newMapBinder(binder(), String::class.java, RedirectEntry::class.java)
        define()
    }

    /**
     * Override this method to define [MasterSitemap] entries with one or more calls to [.addEntry],
     * something like this:
     *
     *
     * addEntry("public/home", PublicHomeView.class, LabelKey.Home, false, "permission");
     *
     *
     * and redirects with [addRedirect]
     *
     * @see .addEntry
     */
    protected abstract fun define()

    /**
     * Adds an entry to be place in the [MasterSitemap] by the [DirectSitemapLoader].
     *
     * @param uri
     * the URI for this page, relative to [.rootURI].  Leading slash is not required and is ignored if there
     * @param viewClass
     * the class of the KrailView for this page. This can be null if a redirection will prevent it from
     * actually
     * being displayed, but it is up to the developer to ensure that the redirection is in place
     * @param labelKey
     * the I18NKey for a localised label for the view
     * @param pageAccessControl
     * the type of page access control to use
     * @param roles
     * the comma separated list of roles which may access this page, may be an empty String. Is ignored unless `pageAccessControl` is [PageAccessControl.ROLES]
     * @param positionIndex
     * the position of a page in relation to its siblings.  Used as a sort order, relative numbering does not need to be sequential. A positionIndex
     * < 0 indicates that the page should not be displayed in a navigation component
     * @param viewConfiguration the configuration to be applied to [viewClass]
     */
    @JvmOverloads
    protected fun addEntry(uri: String, labelKey: I18NKey, pageAccessControl: PageAccessControl = PageAccessControl.PERMISSION, viewClass: Class<out KrailView> = EmptyView::class.java, viewConfiguration: Class<out ViewConfiguration> = EmptyViewConfiguration::class.java, roles: String = "", positionIndex: Int = 1) {

        val entry = DirectSitemapEntry(moduleName = this.javaClass.simpleName,
                viewClass = viewClass,
                labelKey = labelKey,
                pageAccessControl = pageAccessControl,
                roles = roles,
                positionIndex = positionIndex,
                viewConfiguration = viewConfiguration
        )
        if (StringUtils.isEmpty(uri) && StringUtils.isEmpty(rootURI)) {
            throw SitemapException("Either the rootURI or the uri must be non-empty")
        }
        sitemapBinder.addBinding(relativeUri(uri)).toInstance(entry)

    }

    @JvmOverloads
    protected fun addForm(uri: String, labelKey: I18NKey, pageAccessControl: PageAccessControl = PageAccessControl.PERMISSION, formConfiguration: Class<out FormConfiguration> = EmptyFormConfiguration::class.java, positionIndex: Int = 1, formClass: Class<out KrailView> = Form::class.java, roles: String = "") {
        addEntry(uri = uri, labelKey = labelKey, pageAccessControl = pageAccessControl, viewConfiguration = formConfiguration, viewClass = formClass, positionIndex = positionIndex, roles = roles)
    }

    private fun relativeUri(uri: String): String? {
        var result: String
        if (rootURI.isEmpty()) {
            result = StringUtils.removeStart(uri, "/")

        } else {
            result = rootURI + '/'.toString() + StringUtils.removeStart(uri, "/")
        }
        result = StringUtils.removeStart(result, "/")
        return StringUtils.removeEnd(result, "/")
    }


    /**
     * Adds a redirect
     *
     * @param fromURI
     * the uri to redirect from
     * @param toURI
     * the target uri relative to [.rootURI]
     */
    protected fun addRedirect(fromURI: String, toURI: String) {

        redirectBinder.addBinding(fromURI)
                .toInstance(RedirectEntry(relativeUri(toURI)!!))
    }

    /**
     * Specifies where in the Sitemap tree this set of pages should occur.
     */
    fun rootURI(uri: String): DirectSitemapModule {
        checkNotNull(uri)
        this.rootURI = StringUtils.removeEnd(uri.trim { it <= ' ' }, "/")
        return this
    }
}

