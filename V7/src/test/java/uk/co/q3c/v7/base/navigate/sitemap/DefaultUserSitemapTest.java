package uk.co.q3c.v7.base.navigate.sitemap;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;

import org.junit.Before;
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
import uk.co.q3c.v7.i18n.I18NModule;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;

import fixture.ReferenceUserSitemap;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ I18NModule.class, VaadinSessionScopeModule.class })
public class DefaultUserSitemapTest implements UserSitemapChangeListener {

	@Inject
	ReferenceUserSitemap userSitemap;

	@Inject
	CurrentLocale currentLocale;

	private boolean labelsChanged;

	private boolean structureChanged;

	@Before
	public void setup() {
		labelsChanged = false;
		structureChanged = false;
		userSitemap.addChangeListener(this);
	}

	@Test
	public void localeChange() {

		// given
		currentLocale.setLocale(Locale.UK);
		userSitemap.populate();
		// when
		currentLocale.setLocale(Locale.GERMANY);
		// then
		assertThat(userSitemap.getRoots()).containsOnly(userSitemap.publicNode, userSitemap.privateNode);
		assertThat(userSitemap.publicNode.getLabel()).isEqualTo("Öffentlichkeit");
		assertThat(userSitemap.privateNode.getLabel()).isEqualTo("Privat");
		assertThat(userSitemap.getParent(userSitemap.a11Node)).isEqualTo(userSitemap.a1Node);
		assertThat(labelsChanged).isTrue();
	}

	/**
	 * Loaded is set by the builder, after selected content copied across from master sitemap. Should trigger structure
	 * change events
	 */
	@Test
	public void setLoaded() {

		// given
		currentLocale.setLocale(Locale.UK);
		userSitemap.populate();
		// when
		userSitemap.setLoaded(true);
		// then
		assertThat(structureChanged).isTrue();
	}

	@ModuleProvider
	protected AbstractModule moduleProvider() {
		return new AbstractModule() {

			@Override
			protected void configure() {

				bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
				bind(UserOption.class).to(DefaultUserOption.class);
				bind(UserOptionStore.class).to(DefaultUserOptionStore.class);
			}

		};
	}

	@Override
	public void labelsChanged() {
		labelsChanged = true;

	}

	@Override
	public void structureChanged() {
		structureChanged = true;

	}
}
