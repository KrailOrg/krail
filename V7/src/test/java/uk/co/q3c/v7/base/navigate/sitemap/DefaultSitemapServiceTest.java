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

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import com.vaadin.server.VaadinService;
import fixture.TestConfigurationException;
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
import uk.co.q3c.v7.base.config.ConfigKeys;
import uk.co.q3c.v7.base.config.InheritingConfiguration;
import uk.co.q3c.v7.base.guice.uiscope.UIScopeModule;
import uk.co.q3c.v7.base.guice.vsscope.VaadinSessionScopeModule;
import uk.co.q3c.v7.base.navigate.DefaultV7Navigator;
import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.navigate.sitemap.DefaultSitemapServiceTest.TestDirectSitemapModule;
import uk.co.q3c.v7.base.navigate.sitemap.DefaultSitemapServiceTest.TestFileSitemapModule;
import uk.co.q3c.v7.base.services.ServiceException;
import uk.co.q3c.v7.base.shiro.PageAccessControl;
import uk.co.q3c.v7.base.shiro.ShiroVaadinModule;
import uk.co.q3c.v7.base.shiro.StandardShiroModule;
import uk.co.q3c.v7.base.ui.BasicUIProvider;
import uk.co.q3c.v7.base.ui.ScopedUIProvider;
import uk.co.q3c.v7.base.user.UserModule;
import uk.co.q3c.v7.base.view.PublicHomeView;
import uk.co.q3c.v7.base.view.ViewModule;
import uk.co.q3c.v7.base.view.component.StandardComponentModule;
import uk.co.q3c.v7.i18n.DescriptionKey;
import uk.co.q3c.v7.i18n.I18NModule;
import uk.co.q3c.v7.i18n.LabelKey;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This test uses all standard implementations to inject into {@link DefaultSitemapService}. The other test suite,
 * {@link DefaultSitemapServiceTest2} uses a mock for the configuration service
 * 
 * @author David Sowerby
 * 
 */

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ TestDirectSitemapModule.class, TestFileSitemapModule.class, UIScopeModule.class,
		ViewModule.class, ShiroVaadinModule.class, I18NModule.class, SitemapServiceModule.class,
		UserModule.class, ApplicationConfigurationModule.class, StandardShiroModule.class,
		StandardComponentModule.class, StandardPagesModule.class, VaadinSessionScopeModule.class })
public class DefaultSitemapServiceTest {

	private final int FILE_NODE_COUNT = 4;
	private final int DIRECT_NODE_COUNT = 2;
	private final int STANDARD_NODE_COUNT = 5;

	public static class TestDirectSitemapModule extends DirectSitemapModule {

		@Override
		protected void define() {
			addEntry("direct", null, LabelKey.Home_Page, PageAccessControl.PUBLIC);
			addEntry("direct/a", PublicHomeView.class, LabelKey.Home_Page, PageAccessControl.PUBLIC);
			addRedirect("direct", "direct/a");
		}

	}

	public static class TestFileSitemapModule extends FileSitemapModule {

		@Override
		protected void define() {
			addEntry("a", new SitemapFile("src/test/java/uk/co/q3c/v7/base/navigate/sitemap_good.properties"));
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
	InheritingConfiguration applicationConfiguration;

	@Inject
	MasterSitemap sitemap;

	HierarchicalINIConfiguration iniConfig;

	@Before
	public void setup() throws ConfigurationException {

		File inifile = new File(ResourceUtils.userTempDirectory(), "WEB-INF/V7.ini");
		iniConfig = new HierarchicalINIConfiguration(inifile);
		iniConfig.clear();
		iniConfig.save();
	}

	@After
	public void teardown() throws Exception {
		service.stop();
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
		assertThat(sitemap.getNodeCount()).isEqualTo(STANDARD_NODE_COUNT + FILE_NODE_COUNT + DIRECT_NODE_COUNT);
		assertThat(service.getSourceTypes()).containsOnly(SitemapSourceType.FILE, SitemapSourceType.DIRECT,
				SitemapSourceType.ANNOTATION);
		assertThat(sitemap.getReport()).isNotEmpty();
		System.out.println(sitemap.getReport());
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
		assertThat(service.getSourceTypes()).containsOnly(SitemapSourceType.FILE);
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
		iniConfig.setProperty(ConfigKeys.SITEMAP_SOURCES, sources);
		iniConfig.setDelimiterParsingDisabled(false);
		iniConfig.save();

		// when
		service.start();
		// then
		assertThat(service.getReport()).isNotNull();
		assertThat(service.isStarted()).isTrue();
		assertThat(sitemap.getNodeCount()).isEqualTo(13);
		assertThat(service.getSourceTypes()).containsOnly(SitemapSourceType.FILE);
	}

	@Test
	public void invalidSource_butValidOnePresent() throws Exception {
		// given
		List<String> sources = new ArrayList<>();
		sources.add("wiggly");
		sources.add("file");
		iniConfig.setDelimiterParsingDisabled(true);
		iniConfig.setProperty(ConfigKeys.SITEMAP_SOURCES, sources);
		iniConfig.setDelimiterParsingDisabled(false);
		iniConfig.save();

		// when
		service.start();
		// then
		assertThat(service.getReport()).isNotNull();
		assertThat(service.isStarted()).isTrue();
		assertThat(sitemap.getNodeCount()).isEqualTo(4);
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

	@Test(expected = ServiceException.class)
	public void invalidSource_noGoodOnes() throws Exception {

		// given
		List<String> sources = new ArrayList<>();
		sources.add("wiggly");
		sources.add("wobbly");
		iniConfig.setDelimiterParsingDisabled(true);
		iniConfig.setProperty(ConfigKeys.SITEMAP_SOURCES, sources);
		iniConfig.setDelimiterParsingDisabled(false);
		iniConfig.save();
		System.out.println(iniConfig.getProperty(ConfigKeys.SITEMAP_SOURCES));

		// when
		service.start();
		// then

	}

	@ModuleProvider
	protected AbstractModule module() {
		return new AbstractModule() {

			@Override
			protected void configure() {
				bind(V7Navigator.class).to(DefaultV7Navigator.class);
				bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
				bind(ScopedUIProvider.class).to(BasicUIProvider.class);
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
