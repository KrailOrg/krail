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

import com.google.common.collect.ImmutableList
import com.google.inject.Inject
import com.google.inject.Provider
import com.google.inject.Singleton
import org.slf4j.LoggerFactory
import uk.q3c.krail.config.ApplicationConfiguration
import uk.q3c.krail.config.InheritingConfiguration
import uk.q3c.krail.config.config.ConfigKeys
import uk.q3c.krail.core.i18n.DescriptionKey
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.eventbus.MessageBus
import uk.q3c.krail.i18n.I18NKey
import uk.q3c.krail.i18n.Translate
import uk.q3c.krail.service.AbstractService
import uk.q3c.util.clazz.ClassNameUtils
import uk.q3c.util.guice.SerializationSupport
import java.util.*

@Singleton
class DefaultSitemapService
@Inject
protected constructor(translate: Translate,
                      @field:Transient private val directSitemapLoaderProvider: Provider<DirectSitemapLoader>,
                      @field:Transient private val annotationSitemapLoaderProvider: Provider<AnnotationSitemapLoader>,
                      @field:Transient private val sitemapProvider: Provider<MasterSitemap>,
                      @field:Transient private val sitemapFinisher: SitemapFinisher,
                      private val configuration: ApplicationConfiguration,
                      globalBusProvider: MessageBus,
                      private val classNameUtils: ClassNameUtils,
                      serializationSupport: SerializationSupport)

    : AbstractService(translate, globalBusProvider, serializationSupport), SitemapService {


    private val lock = arrayOfNulls<Any>(0)
    @Transient
    private var log = LoggerFactory.getLogger(this.javaClass.name)
    @Transient
    private var loaders: MutableList<SitemapLoader> = mutableListOf()
    private var sourceTypes: MutableList<SitemapSourceType> = mutableListOf()
    private var loaded: Boolean = false
    @Transient
    private var report: StringBuilder = StringBuilder()

    val isLoaded: Boolean
        get() = synchronized(lock) {
            return loaded
        }


    init {
        descriptionKey = DescriptionKey.Sitemap_Service
    }


    override fun doStart() {
        //start with a new and empty model
        val sitemap = sitemapProvider.get()
        loadSources(sitemap)
        val lrb = LoaderReportBuilder(loaders, classNameUtils)
        report = lrb.report
        sitemap.report = report.toString()
        if (!loaded) {
            throw SitemapException("No valid sources found")
        }
        sitemap.lock()
        log.info("{}", report.toString())
    }

    /**
     * Loads the Sitemap from all the sources specified in [.sourceTypes]. The first call to
     * [.loadSource] has `firstLoad` set to true. Subsequent calls have `firstLoad`
     * set to false
     */
    private fun loadSources(sitemap: MasterSitemap) {
        extractSourcesFromConfig()
        loaders = ArrayList()
        for (source in sourceTypes) {
            loadSource(source, sitemap)
        }
        log.debug("Checking Sitemap, sitemap has {} nodes", sitemap.nodeCount)
        sitemapFinisher.check(sitemap)
        log.debug("Sitemap checked, no errors found")
    }

    /**
     * Loads the Sitemap with all sources of the specified `source type`.
     *
     * @param sourceType the source type to use
     * @param sitemap    the sitemap to load
     */
    private fun loadSource(sourceType: SitemapSourceType, sitemap: MasterSitemap) {
        log.debug("Loading Sitemap from {}", sourceType)
        when (sourceType) {

            SitemapSourceType.DIRECT -> {
                val directSitemapLoader = directSitemapLoaderProvider.get()
                loaders.add(directSitemapLoader)
                directSitemapLoader.load(sitemap)
                sitemapFinisher.setSourceModuleNames(directSitemapLoader.sourceModules())
                loaded = true
                return
            }
            SitemapSourceType.ANNOTATION -> {
                val annotationSitemapLoader = annotationSitemapLoaderProvider.get()
                loaders.add(annotationSitemapLoader)
                annotationSitemapLoader.load(sitemap)
                val sources = annotationSitemapLoader.sources
                if (sources != null) {
                    sitemapFinisher.setAnnotationSources(sources.keys)
                }
                loaded = true
            }
        }
    }

    /**
     * Extracts the source types from the [InheritingConfiguration], and populates [.sourceTypes]. The
     * default is to load from all source types (
     */
    private fun extractSourcesFromConfig() {
        val defaultValues = ArrayList<String>()
        defaultValues.add(SitemapSourceType.DIRECT.name)
        defaultValues.add(SitemapSourceType.ANNOTATION.name)
        val list = configuration.getList(ConfigKeys.SITEMAP_SOURCES, defaultValues)
        sourceTypes = ArrayList()
        for (o in list) {
            try {
                val source = SitemapSourceType.valueOf(o.toString()
                        .toUpperCase())
                sourceTypes.add(source)
            } catch (iae: IllegalArgumentException) {
                log.warn("'{}' is not a valid Sitemap source type", o.toString())

            }

        }

        // this will only happen if there is a key with an empty value
        if (sourceTypes.isEmpty()) {
            throw SitemapException("At least one sitemap source must be specified")
        }

    }


    override fun doStop() {
        loaded = false
    }

    fun getReport(): StringBuilder {

        synchronized(lock) {
            if (report.isEmpty()) {
                log.warn("The sitemap report is not serialised, there is little need for it and it just adds to the session size")
            }
            return report
        }
    }


    fun getSourceTypes(): ImmutableList<SitemapSourceType> {
        synchronized(lock) {
            return ImmutableList.copyOf(sourceTypes)
        }
    }


    override fun getNameKey(): I18NKey {
        return LabelKey.Sitemap_Service
    }


    override fun beforeTransientInjection() {
        super.beforeTransientInjection()
        log = LoggerFactory.getLogger(this.javaClass.name)
        serializationSupport.excludedFieldNames = ImmutableList.of("loaders", "report")
    }

}
