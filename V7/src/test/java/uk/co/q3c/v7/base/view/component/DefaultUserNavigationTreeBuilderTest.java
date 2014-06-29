package uk.co.q3c.v7.base.view.component;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import uk.co.q3c.v7.base.guice.vsscope.VaadinSessionScopeModule;
import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.navigate.sitemap.UserSitemap;
import uk.co.q3c.v7.base.navigate.sitemap.comparator.DefaultUserSitemapSorters;
import uk.co.q3c.v7.base.user.opt.DefaultUserOption;
import uk.co.q3c.v7.base.user.opt.DefaultUserOptionStore;
import uk.co.q3c.v7.base.user.opt.UserOption;
import uk.co.q3c.v7.base.user.opt.UserOptionStore;
import uk.co.q3c.v7.i18n.I18NModule;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;

import fixture.ReferenceUserSitemap;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ I18NModule.class, VaadinSessionScopeModule.class })
public class DefaultUserNavigationTreeBuilderTest {

	DefaultUserNavigationTreeBuilder builder;

	@Inject
	ReferenceUserSitemap userSitemap;

	@Inject
	DefaultUserSitemapSorters sorters;

	private DefaultUserNavigationTree userNavigationTree;

	@Inject
	DefaultUserOption userOption;
	@Mock
	V7Navigator navigator;

	@Before
	public void setUp() throws Exception {
		builder = new DefaultUserNavigationTreeBuilder(userSitemap);
		userNavigationTree = new DefaultUserNavigationTree(userSitemap, navigator, userOption, builder, sorters);
	}

	@Test
	public void construct() {
		// given
		// when

		// then
		assertThat(builder.getUserNavigationTree()).isEqualTo(userNavigationTree);
	}

	@ModuleProvider
	protected AbstractModule module() {
		return new AbstractModule() {

			@Override
			protected void configure() {
				bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
				bind(UserOption.class).to(DefaultUserOption.class);
				bind(UserOptionStore.class).to(DefaultUserOptionStore.class);
			}

			@Provides
			protected UserSitemap sitemapProvider() {
				return userSitemap;
			}

		};
	}

}