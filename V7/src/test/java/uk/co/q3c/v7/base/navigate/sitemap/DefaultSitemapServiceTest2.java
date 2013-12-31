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

import static org.mockito.Mockito.*;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import uk.co.q3c.v7.base.config.ApplicationConfigurationService;
import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.services.Service.Status;
import uk.co.q3c.v7.base.services.ServicesMonitorModule;
import uk.co.q3c.v7.i18n.AnnotationI18NTranslator;
import uk.co.q3c.v7.i18n.I18NTranslator;
import uk.co.q3c.v7.i18n.Translate;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;

/**
 * This uses a mock for the configuration service. The other test suite, {@link DefaultSitemapServiceTest}, injects all
 * "standard" implementations
 * 
 * @author David Sowerby
 * 
 */
@RunWith(MycilaJunitRunner.class)
@GuiceContext({ ServicesMonitorModule.class, DefaultStandardPagesModule.class })
public class DefaultSitemapServiceTest2 {

	@Inject
	DefaultSitemapService service;

	@Mock
	ApplicationConfigurationService configurationService;

	@Mock
	Translate translate;

	@Mock
	Provider<FileSitemapLoader> sitemapFileReaderProvider;

	@Mock
	Sitemap sitemap;

	@SuppressWarnings("unchecked")
	@Test(expected = SitemapException.class)
	public void dependencyFailed() throws Exception {

		// given
		when(configurationService.start()).thenThrow(ConfigurationException.class);
		// when
		service.start();
		// then
		verify(configurationService).setStatus(Status.FAILED_TO_START);

	}

	@ModuleProvider
	protected AbstractModule moduleProvider() {
		return new AbstractModule() {

			@Override
			protected void configure() {
				bind(ApplicationConfigurationService.class).toInstance(configurationService);
				bind(I18NTranslator.class).to(AnnotationI18NTranslator.class);
				bind(FileSitemapLoader.class).to(DefaultFileSitemapLoader.class);
				bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
				bind(AnnotationSitemapLoader.class).to(DefaultAnnotationSitemapLoader.class);
				bind(DirectSitemapLoader.class).to(DefaultDirectSitemapLoader.class);
			}

		};
	}

}
