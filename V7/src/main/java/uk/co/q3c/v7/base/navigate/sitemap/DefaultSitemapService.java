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
package uk.co.q3c.v7.base.navigate.sitemap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.util.MessageFormat;
import uk.co.q3c.util.ResourceUtils;
import uk.co.q3c.v7.base.config.ApplicationConfiguration;
import uk.co.q3c.v7.base.config.ApplicationConfigurationService;
import uk.co.q3c.v7.base.config.ConfigKeys;
import uk.co.q3c.v7.base.config.InheritingConfiguration;
import uk.co.q3c.v7.base.services.AbstractServiceI18N;
import uk.co.q3c.v7.base.services.AutoStart;
import uk.co.q3c.v7.base.services.Service;
import uk.co.q3c.v7.i18n.DescriptionKey;
import uk.co.q3c.v7.i18n.LabelKey;
import uk.co.q3c.v7.i18n.Translate;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class DefaultSitemapService extends AbstractServiceI18N implements SitemapService {

	private static Logger log = LoggerFactory.getLogger(DefaultSitemapService.class);
	@AutoStart
	private final ApplicationConfigurationService configurationService;
	private final Provider<FileSitemapLoader> fileSitemapLoaderProvider;
	private List<SitemapSourceType> sourceTypes;
	private final Sitemap sitemap;
	private StringBuilder report;
	private final ApplicationConfiguration configuration;
	private boolean loaded;
	private final Provider<DirectSitemapLoader> directSitemapLoaderProvider;
	private final Provider<AnnotationSitemapLoader> annotationSitemapLoaderProvider;
	private final SitemapChecker sitemapChecker;
	private List<SitemapLoader> loaders;

	@Inject
	protected DefaultSitemapService(ApplicationConfigurationService configurationService, Translate translate,
			Provider<FileSitemapLoader> fileSitemapLoaderProvider,
			Provider<DirectSitemapLoader> directSitemapLoaderProvider,
			Provider<AnnotationSitemapLoader> annotationSitemapLoaderProvider, Sitemap sitemap,
			SitemapChecker sitemapChecker, ApplicationConfiguration configuration) {
		super(translate);
		this.configurationService = configurationService;
		this.annotationSitemapLoaderProvider = annotationSitemapLoaderProvider;
		this.directSitemapLoaderProvider = directSitemapLoaderProvider;
		this.fileSitemapLoaderProvider = fileSitemapLoaderProvider;
		this.sitemap = sitemap;
		this.sitemapChecker = sitemapChecker;
		this.configuration = configuration;
		configure();
	}

	protected void configure() {
		setNameKey(LabelKey.Sitemap_Service);
		setDescriptionKey(DescriptionKey.Sitemap_Service);
	}

	@Override
	public Status start() throws Exception {
		if (getStatus().equals(Status.DEPENDENCY_FAILED)) {
			String msg = MessageFormat.format("Unable to start {0}, because it depends on {1}", getName(),
					configurationService.getName());
			log.error(msg);
			setStatus(Status.DEPENDENCY_FAILED);
			throw new SitemapException(msg);
		}
		loadSources();
		LoaderReportBuilder lrb = new LoaderReportBuilder(loaders);
		report = lrb.getReport();
		sitemap.setReport(report.toString());
		if (!loaded) {
			throw new SitemapException("No valid sources found");
		}
		return Status.STARTED;
	}

	/**
	 * Loads the Sitemap from all the sources specified in {@link #sourceTypes}. The first call to
	 * {@link #loadSource(String, boolean)} has {@code firstLoad} set to true. Subsequent calls have {@code firstLoad}
	 * set to false
	 */
	private void loadSources() {
		extractSourcesFromConfig();
		loaders = new ArrayList<>();
		for (SitemapSourceType source : sourceTypes) {
			loadSource(source);
		}
		log.debug("Checking Sitemap");
		sitemapChecker.check();
		log.debug("Sitemap checked, no errors found");
	}

	/**
	 * Loads the Sitemap with all sources of the specified {@code source type}.
	 * 
	 * @param sourceType
	 */
	private void loadSource(SitemapSourceType sourceType) {
		log.debug("Loading Sitemap from {}", sourceType);
		switch (sourceType) {
		case FILE:
			FileSitemapLoader fileSitemapLoader = fileSitemapLoaderProvider.get();
			loaders.add(fileSitemapLoader);
			fileSitemapLoader.load();
			loaded = true;
			return;
		case DIRECT:
			DirectSitemapLoader directSitemapLoader = directSitemapLoaderProvider.get();
			loaders.add(directSitemapLoader);
			directSitemapLoader.load();
			loaded = true;
			return;
		case ANNOTATION:
			AnnotationSitemapLoader annotationSitemapLoader = annotationSitemapLoaderProvider.get();
			loaders.add(annotationSitemapLoader);
			annotationSitemapLoader.load();
			loaded = true;
			return;
		}
	}

	public File absolutePathFor(String source) {

		if (source.startsWith("/")) {
			return new File(source);
		} else {
			return new File(ResourceUtils.applicationBaseDirectory(), source);
		}

	}

	/**
	 * Extracts the source types from the {@link InheritingConfiguration}, and populates {@link #sourceTypes}. The
	 * default is to load from all source types (
	 */
	private void extractSourcesFromConfig() {
		List<String> defaultValues = new ArrayList<>();
		defaultValues.add(SitemapSourceType.FILE.name());
		defaultValues.add(SitemapSourceType.DIRECT.name());
		defaultValues.add(SitemapSourceType.ANNOTATION.name());
		List<Object> list = configuration.getList(ConfigKeys.SITEMAP_SOURCES_KEY, defaultValues);
		sourceTypes = new ArrayList<>();
		for (Object o : list) {
			try {
				SitemapSourceType source = SitemapSourceType.valueOf(o.toString().toUpperCase());
				sourceTypes.add(source);
			} catch (IllegalArgumentException iae) {
				log.warn("'{}' is not a valid Sitemap source type", o.toString(), ConfigKeys.SITEMAP_SOURCES_KEY);

			}
		}

		// this will only happen if there is a key with an empty value
		if (sourceTypes.isEmpty()) {
			throw new SitemapException("At least one sitemap source must be specified");
		}

	}

	@Override
	public Status stop() {
		loaded = false;
		return Status.STOPPED;
	}

	/**
	 * If the {@link #configurationService} stops after this service is started, it does not matter. The only values
	 * used are used during the start up of this service, so if anything changes after that there is no reason to
	 * respond.
	 * 
	 * @see uk.co.q3c.v7.base.services.ServiceStatusChangeListener#serviceStatusChange(uk.co.q3c.v7.base.services.Service,
	 *      uk.co.q3c.v7.base.services.Service.Status, uk.co.q3c.v7.base.services.Service.Status)
	 */
	@Override
	public void serviceStatusChange(Service service, Status fromStatus, Status toStatus) {
		// nothing to do
	}

	public StringBuilder getReport() {
		return report;
	}

	@Override
	public Sitemap getSitemap() {
		return sitemap;
	}

	public boolean isLoaded() {
		return loaded;
	}

	public ImmutableList<SitemapSourceType> getSourceTypes() {
		return ImmutableList.copyOf(sourceTypes);
	}

}
