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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.util.ResourceUtils;
import uk.co.q3c.v7.base.config.ApplicationConfigurationModule;
import uk.co.q3c.v7.base.config.ApplicationConfigurationService;
import uk.co.q3c.v7.base.config.ConfigKeys;
import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.navigate.sitemap.DefaultSitemapServiceTest.TestDirectSitemapModuleBase;
import uk.co.q3c.v7.base.services.ServicesMonitorModule;
import uk.co.q3c.v7.base.view.PublicHomeView;
import uk.co.q3c.v7.i18n.AnnotationI18NTranslator;
import uk.co.q3c.v7.i18n.DescriptionKey;
import uk.co.q3c.v7.i18n.I18NTranslator;
import uk.co.q3c.v7.i18n.LabelKey;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import com.vaadin.server.VaadinService;

import fixture.TestConfigurationException;

/**
 * This test uses all standard implementations to inject into {@link DefaultSitemapService}. The other test suite,
 * {@link DefaultSitemapServiceTest2} uses a mock for the configuration service
 * 
 * @author David Sowerby
 * 
 */

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ ServicesMonitorModule.class, ApplicationConfigurationModule.class, TestDirectSitemapModuleBase.class,
		DefaultStandardPagesModule.class })
public class DefaultSitemapServiceTest {

	public static class TestDirectSitemapModuleBase extends DirectSitemapModule {

		@Override
		protected void define() {
			addEntry("direct/a", PublicHomeView.class, LabelKey.Home, true, null);
		}

	}

	static VaadinService vaadinService;

	@BeforeClass
	public static void setupClass() {
		vaadinService = mock(VaadinService.class);
		when(vaadinService.getBaseDirectory()).thenReturn(ResourceUtils.userTempDirectory());
		VaadinService.setCurrent(vaadinService);
	}

	@Inject
	DefaultSitemapService service;

	@Inject
	ApplicationConfigurationService configService;

	@Inject
	Sitemap sitemap;

	HierarchicalINIConfiguration iniConfig;

	@Before
	public void setup() throws ConfigurationException {

		File inifile = new File(ResourceUtils.userTempDirectory(), "V7.ini");
		iniConfig = new HierarchicalINIConfiguration(inifile);
		iniConfig.clear();
		iniConfig.save();
	}

	@After
	public void teardown() throws ConfigurationException {
		service.stop();
		configService.stop();
		iniConfig.clear();
		iniConfig.save();
	}

	@Test
	public void start() throws Exception {

		// given
		copySitemapPropertiesToTemp();
		// when
		service.start();
		// then
		assertThat(service.getReport()).isNotNull();
		assertThat(service.isStarted()).isTrue();
		assertThat(sitemap.getNodeCount()).isEqualTo(13);
		assertThat(service.getSources()).containsOnly("file");
	}

	@Test
	public void nameAndDescription() {

		// given

		// when

		// then

		assertThat(service.getNameKey()).isEqualTo(LabelKey.Sitemap_Service);
		assertThat(service.getDescriptionKey()).isEqualTo(DescriptionKey.Sitemap_Service);
		assertThat(service.getName()).isEqualTo("Sitemap Service");
		assertThat(service.getDescription()).isEqualTo(
				"This service creates the Sitemap using options from the application configuration");
	}

	public void sourcesPropertyMissing() throws Exception {

		// given
		iniConfig.clear();
		iniConfig.save();

		// when
		service.start();
		// then
		assertThat(service.getReport()).isNotNull();
		assertThat(service.isStarted()).isTrue();
		assertThat(sitemap.getNodeCount()).isEqualTo(13);
		assertThat(service.getSources()).containsOnly("file");
	}

	/**
	 * Key is there but value is not
	 * 
	 * @throws Exception
	 */
	public void sourcesPropertyEmpty() throws Exception {

		// given
		List<String> sources = new ArrayList<>();
		iniConfig.setDelimiterParsingDisabled(true);
		iniConfig.setProperty(ConfigKeys.SITEMAP_SOURCES_KEY, sources);
		iniConfig.setDelimiterParsingDisabled(false);
		iniConfig.save();

		// when
		service.start();
		// then
		assertThat(service.getReport()).isNotNull();
		assertThat(service.isStarted()).isTrue();
		assertThat(sitemap.getNodeCount()).isEqualTo(13);
		assertThat(service.getSources()).containsOnly("file");
	}

	@Test
	public void invalidSource_butValidOnePresent() throws Exception {
		// given
		List<String> sources = new ArrayList<>();
		sources.add("wiggly");
		sources.add("file");
		iniConfig.setDelimiterParsingDisabled(true);
		iniConfig.setProperty(ConfigKeys.SITEMAP_SOURCES_KEY, sources);
		iniConfig.setDelimiterParsingDisabled(false);
		iniConfig.save();

		// when
		service.start();
		// then
		assertThat(service.getReport()).isNotNull();
		assertThat(service.isStarted()).isTrue();
		assertThat(sitemap.getNodeCount()).isEqualTo(13);
	}

	@Test
	public void stop() {

		// given

		// when

		// then
		assertThat(service.isLoaded()).isFalse();

	}

	@Test
	public void absolutePathFor() {

		// given

		// when

		// then

		assertThat(service.absolutePathFor("wiggly.ini")).isEqualTo(
				new File(ResourceUtils.applicationBaseDirectory(), "wiggly.ini"));
		assertThat(service.absolutePathFor("/wiggly.ini")).isEqualTo(new File("/wiggly.ini"));
	}

	@Test(expected = SitemapException.class)
	public void invalidSource_noGoodOnes() throws Exception {

		// given
		List<String> sources = new ArrayList<>();
		sources.add("wiggly");
		sources.add("wobbly");
		iniConfig.setDelimiterParsingDisabled(true);
		iniConfig.setProperty(ConfigKeys.SITEMAP_SOURCES_KEY, sources);
		iniConfig.setDelimiterParsingDisabled(false);
		iniConfig.save();

		// when
		service.start();
		// then

	}

	private void setConfig_FileOnly() throws ConfigurationException {
		iniConfig.setDelimiterParsingDisabled(true);
		iniConfig.addProperty(ConfigKeys.SITEMAP_SOURCES_KEY, new String[] { "file" });
		iniConfig.save();
		iniConfig.setDelimiterParsingDisabled(false);
	}

	private void setConfigAll_File_Module_Annotation() throws ConfigurationException {
		iniConfig.setDelimiterParsingDisabled(true);
		iniConfig.addProperty(ConfigKeys.SITEMAP_SOURCES_KEY, new String[] { "file,module,annotation" });
		iniConfig.save();
		iniConfig.setDelimiterParsingDisabled(false);
	}

	@ModuleProvider
	protected AbstractModule module() {
		return new AbstractModule() {

			@Override
			protected void configure() {
				bind(I18NTranslator.class).to(AnnotationI18NTranslator.class);
				bind(FileSitemapLoader.class).to(DefaultFileSitemapLoader.class);
				bind(AnnotationSitemapLoader.class).to(DefaultAnnotationSitemapLoader.class);
				bind(DirectSitemapLoader.class).to(DefaultDirectSitemapLoader.class);
				bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
			}

		};
	}

	/**
	 * Copies a 'good' version of sitemap.properties to the
	 */
	private void copySitemapPropertiesToTemp() {
		File source = new File("src/test/java/uk/co/q3c/v7/base/navigate/sitemap_good.properties");
		if (!source.exists()) {
			throw new TestConfigurationException("Source file missing");
		}
		File destination = new File(ResourceUtils.applicationBaseDirectory(), "sitemap.properties");
		if (destination.exists()) {
			destination.delete();
		}
		try {
			FileUtils.copyFile(source, destination);
		} catch (IOException e) {
			throw new TestConfigurationException("Unable to copy sitemap.properties", e);
		}
	}

}
