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

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.config.ApplicationConfiguration;
import uk.q3c.krail.config.InheritingConfiguration;
import uk.q3c.krail.config.config.ConfigKeys;
import uk.q3c.krail.core.i18n.DescriptionKey;
import uk.q3c.krail.core.i18n.LabelKey;
import uk.q3c.krail.core.navigate.sitemap.set.MasterSitemapQueue;
import uk.q3c.krail.eventbus.MessageBus;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.Translate;
import uk.q3c.krail.service.AbstractService;
import uk.q3c.krail.service.RelatedServiceExecutor;
import uk.q3c.krail.util.ResourceUtils;
import uk.q3c.util.clazz.ClassNameUtils;
import uk.q3c.util.guice.SerializationSupport;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Singleton
public class DefaultSitemapService extends AbstractService implements SitemapService {

    private static Logger log = LoggerFactory.getLogger(DefaultSitemapService.class);
    private final transient Provider<MasterSitemap> sitemapProvider;
    private final transient Provider<DirectSitemapLoader> directSitemapLoaderProvider;
    private final transient Provider<AnnotationSitemapLoader> annotationSitemapLoaderProvider;
    private final ApplicationConfiguration configuration;
    private final ResourceUtils resourceUtils;
    private final ClassNameUtils classNameUtils;

    private transient List<SitemapLoader> loaders;
    private transient List<SitemapSourceType> sourceTypes;
    private final SitemapFinisher sitemapFinisher;
    private MasterSitemapQueue masterSitemapQueue;
    private boolean loaded;

    private transient StringBuilder report;


    @SuppressFBWarnings({"FCBL_FIELD_COULD_BE_LOCAL"})  // ResourceUtils have to be injected
    @Inject
    protected DefaultSitemapService(Translate translate, Provider<DirectSitemapLoader>
            directSitemapLoaderProvider, Provider<AnnotationSitemapLoader> annotationSitemapLoaderProvider, Provider<MasterSitemap> sitemapProvider,
                                    SitemapFinisher sitemapFinisher, MasterSitemapQueue masterSitemapQueue, ApplicationConfiguration configuration,
                                    MessageBus globalBusProvider, ResourceUtils resourceUtils, ClassNameUtils
                                            classNameUtils, RelatedServiceExecutor servicesExecutor, SerializationSupport serializationSupport) {
        super(translate, globalBusProvider, servicesExecutor, serializationSupport);
        this.annotationSitemapLoaderProvider = annotationSitemapLoaderProvider;
        this.directSitemapLoaderProvider = directSitemapLoaderProvider;
        this.sitemapProvider = sitemapProvider;
        this.sitemapFinisher = sitemapFinisher;
        this.masterSitemapQueue = masterSitemapQueue;
        this.configuration = configuration;
        this.resourceUtils = resourceUtils;
        this.classNameUtils = classNameUtils;
        setDescriptionKey(DescriptionKey.Sitemap_Service);
    }


    @Override
    protected void doStart() {
        //start with a new and empty model
        MasterSitemap sitemap = sitemapProvider.get();
        loadSources(sitemap);
        LoaderReportBuilder lrb = new LoaderReportBuilder(loaders, classNameUtils);
        report = lrb.getReport();
        sitemap.setReport(report.toString());
        if (!loaded) {
            throw new SitemapException("No valid sources found");
        }
        sitemap.lock();
        masterSitemapQueue.addModel(sitemap);
        log.info("{}", report.toString());
    }

    /**
     * Loads the Sitemap from all the sources specified in {@link #sourceTypes}. The first call to
     * {@link #loadSource(SitemapSourceType, MasterSitemap)} has {@code firstLoad} set to true. Subsequent calls have {@code firstLoad}
     * set to false
     */
    private void loadSources(MasterSitemap sitemap) {
        extractSourcesFromConfig();
        loaders = new ArrayList<>();
        for (SitemapSourceType source : sourceTypes) {
            loadSource(source, sitemap);
        }
        log.debug("Checking Sitemap, sitemap has {} nodes", sitemap.getNodeCount());
        sitemapFinisher.check(sitemap);
        log.debug("Sitemap checked, no errors found");
    }

    /**
     * Loads the Sitemap with all sources of the specified {@code source type}.
     *
     * @param sourceType the source type to use
     * @param sitemap    the sitemap to load
     */
    private void loadSource(SitemapSourceType sourceType, MasterSitemap sitemap) {
        log.debug("Loading Sitemap from {}", sourceType);
        switch (sourceType) {

            case DIRECT:
                DirectSitemapLoader directSitemapLoader = directSitemapLoaderProvider.get();
                loaders.add(directSitemapLoader);
                directSitemapLoader.load(sitemap);
                sitemapFinisher.setSourceModuleNames(directSitemapLoader.sourceModules());
                loaded = true;
                return;
            case ANNOTATION:
                AnnotationSitemapLoader annotationSitemapLoader = annotationSitemapLoaderProvider.get();
                loaders.add(annotationSitemapLoader);
                annotationSitemapLoader.load(sitemap);
                Map<String, AnnotationSitemapEntry> sources = annotationSitemapLoader.getSources();
                if (sources != null) {
                    sitemapFinisher.setAnnotationSources(sources.keySet());
                }
                loaded = true;
        }
    }

    /**
     * Extracts the source types from the {@link InheritingConfiguration}, and populates {@link #sourceTypes}. The
     * default is to load from all source types (
     */
    private void extractSourcesFromConfig() {
        List<String> defaultValues = new ArrayList<>();
        defaultValues.add(SitemapSourceType.DIRECT.name());
        defaultValues.add(SitemapSourceType.ANNOTATION.name());
        List<Object> list = configuration.getList(ConfigKeys.SITEMAP_SOURCES, defaultValues);
        sourceTypes = new ArrayList<>();
        for (Object o : list) {
            try {
                SitemapSourceType source = SitemapSourceType.valueOf(o.toString()
                                                                      .toUpperCase());
                sourceTypes.add(source);
            } catch (IllegalArgumentException iae) {
                log.warn("'{}' is not a valid Sitemap source type", o.toString());

            }
        }

        // this will only happen if there is a key with an empty value
        if (sourceTypes.isEmpty()) {
            throw new SitemapException("At least one sitemap source must be specified");
        }

    }


    @Override
    protected void doStop() {
        loaded = false;
    }

    public synchronized StringBuilder getReport() {
        return report;
    }

    public synchronized boolean isLoaded() {
        return loaded;
    }

    public synchronized ImmutableList<SitemapSourceType> getSourceTypes() {
        return ImmutableList.copyOf(sourceTypes);
    }


    @Override
    public I18NKey getNameKey() {
        return LabelKey.Sitemap_Service;
    }


    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }
}
