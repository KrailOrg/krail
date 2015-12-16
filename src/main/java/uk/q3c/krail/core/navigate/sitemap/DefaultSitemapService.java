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
package uk.q3c.krail.core.navigate.sitemap;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.config.ApplicationConfiguration;
import uk.q3c.krail.core.config.ApplicationConfigurationService;
import uk.q3c.krail.core.config.ConfigKeys;
import uk.q3c.krail.core.config.InheritingConfiguration;
import uk.q3c.krail.core.eventbus.GlobalBusProvider;
import uk.q3c.krail.core.services.AbstractService;
import uk.q3c.krail.core.services.Dependency;
import uk.q3c.krail.core.services.ServicesModel;
import uk.q3c.krail.i18n.DescriptionKey;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.LabelKey;
import uk.q3c.krail.i18n.Translate;
import uk.q3c.util.ResourceUtils;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
public class DefaultSitemapService extends AbstractService implements SitemapService {

    private static Logger log = LoggerFactory.getLogger(DefaultSitemapService.class);
    @Dependency
    private final ApplicationConfigurationService configurationService;
    private final MasterSitemap sitemap;
    private final ApplicationConfiguration configuration;
    private final Provider<DirectSitemapLoader> directSitemapLoaderProvider;
    private final Provider<AnnotationSitemapLoader> annotationSitemapLoaderProvider;
    private final SitemapFinisher sitemapFinisher;
    private boolean loaded;
    private List<SitemapLoader> loaders;
    private StringBuilder report;
    private List<SitemapSourceType> sourceTypes;

    @Inject
    protected DefaultSitemapService(ApplicationConfigurationService configurationService, Translate translate, Provider<DirectSitemapLoader>
            directSitemapLoaderProvider, Provider<AnnotationSitemapLoader> annotationSitemapLoaderProvider, MasterSitemap sitemap, SitemapFinisher
                                            sitemapFinisher, ApplicationConfiguration configuration, ServicesModel servicesModel, GlobalBusProvider
            globalBusProvider) {
        super(translate, servicesModel, globalBusProvider);
        this.configurationService = configurationService;
        this.annotationSitemapLoaderProvider = annotationSitemapLoaderProvider;
        this.directSitemapLoaderProvider = directSitemapLoaderProvider;
        this.sitemap = sitemap;
        this.sitemapFinisher = sitemapFinisher;
        this.configuration = configuration;
        setDescriptionKey(DescriptionKey.Sitemap_Service);
    }


    @Override
    protected void doStart() {
        loadSources();
        LoaderReportBuilder lrb = new LoaderReportBuilder(loaders);
        report = lrb.getReport();
        sitemap.setReport(report.toString());
        if (!loaded) {
            throw new SitemapException("No valid sources found");
        }
        log.info(report.toString());
    }

    /**
     * Loads the Sitemap from all the sources specified in {@link #sourceTypes}. The first call to
     * {@link #loadSource(SitemapSourceType)} has {@code firstLoad} set to true. Subsequent calls have {@code firstLoad}
     * set to false
     */
    private void loadSources() {
        extractSourcesFromConfig();
        loaders = new ArrayList<>();
        for (SitemapSourceType source : sourceTypes) {
            loadSource(source);
        }
        log.debug("Checking Sitemap, sitemap has {} nodes", sitemap.getNodeCount());
        sitemapFinisher.check();
        log.debug("Sitemap checked, no errors found");
    }

    /**
     * Loads the Sitemap with all sources of the specified {@code source type}.
     *
     * @param sourceType the source type to use
     */
    private void loadSource(SitemapSourceType sourceType) {
        log.debug("Loading Sitemap from {}", sourceType);
        switch (sourceType) {

            case DIRECT:
                DirectSitemapLoader directSitemapLoader = directSitemapLoaderProvider.get();
                loaders.add(directSitemapLoader);
                directSitemapLoader.load();
                sitemapFinisher.setSourceModuleNames(directSitemapLoader.sourceModules());
                loaded = true;
                return;
            case ANNOTATION:
                AnnotationSitemapLoader annotationSitemapLoader = annotationSitemapLoaderProvider.get();
                loaders.add(annotationSitemapLoader);
                annotationSitemapLoader.load();
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
                log.warn("'{}' is not a valid Sitemap source type", o.toString(), ConfigKeys.SITEMAP_SOURCES);

            }
        }

        // this will only happen if there is a key with an empty value
        if (sourceTypes.isEmpty()) {
            throw new SitemapException("At least one sitemap source must be specified");
        }

    }

    synchronized public File absolutePathFor(@Nonnull String source) {
        checkNotNull(source);
        if (source.startsWith("/")) {
            return new File(source);
        } else {
            return new File(ResourceUtils.applicationBaseDirectory(), source);
        }

    }

    @Override
    protected void doStop() {
        loaded = false;
    }

    public synchronized StringBuilder getReport() {
        return report;
    }

    @Override
    public synchronized Sitemap<MasterSitemapNode> getSitemap() {
        return sitemap;
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
}
