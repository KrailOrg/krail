package uk.co.q3c.v7.base.navigate.sitemap;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.guice.vsscope.VaadinSessionScopeModule;
import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.user.opt.DefaultUserOption;
import uk.co.q3c.v7.base.user.opt.DefaultUserOptionStore;
import uk.co.q3c.v7.base.user.opt.UserOption;
import uk.co.q3c.v7.base.user.opt.UserOptionStore;
import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.DefaultCurrentLocale;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;

import fixture.ReferenceUserSitemap;

/**
 * Tests the structure of the reference site map
 * 
 * @author dsowerby
 *
 */
@RunWith(MycilaJunitRunner.class)
@GuiceContext({ VaadinSessionScopeModule.class })
public class ReferenceSitemapTest {

	private static String[] expected = new String[] { "", "-Public", "--Log Out", "--ViewA", "---ViewA1",
			"----ViewA11", "--Log In", "--Public Home", "", "-Private", "--Private Home", "--ViewB", "---ViewB1",
			"----ViewB11" };

	@Inject
	ReferenceUserSitemap userSitemap;

	@Test
	public void output() {

		// given
		userSitemap.clear();
		// when
		userSitemap.populate();
		// then
		System.out.println(userSitemap);
		String output[] = userSitemap.toString().split("\\r?\\n");
		List<String> actualList = Arrays.asList(output);
		assertThat(actualList).containsOnly(expected);
	}

	@ModuleProvider
	protected AbstractModule module() {
		return new AbstractModule() {

			@Override
			protected void configure() {
				bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
				bind(CurrentLocale.class).to(DefaultCurrentLocale.class);
				bind(UserOption.class).to(DefaultUserOption.class);
				bind(UserOptionStore.class).to(DefaultUserOptionStore.class);
			}

		};
	}
}
