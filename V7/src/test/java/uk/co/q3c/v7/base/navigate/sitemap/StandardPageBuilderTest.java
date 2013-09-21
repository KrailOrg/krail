package uk.co.q3c.v7.base.navigate.sitemap;

import static org.fest.assertions.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.navigate.StandardPageBuilder;
import uk.co.q3c.v7.base.navigate.StandardPageKey;
import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.view.LoginView;
import uk.co.q3c.v7.base.view.LogoutView;
import uk.co.q3c.v7.base.view.PrivateHomeView;
import uk.co.q3c.v7.base.view.PublicHomeView;
import uk.co.q3c.v7.base.view.RequestSystemAccountEnableView;
import uk.co.q3c.v7.base.view.RequestSystemAccountRefreshView;
import uk.co.q3c.v7.base.view.RequestSystemAccountResetView;
import uk.co.q3c.v7.base.view.RequestSystemAccountUnlockView;
import uk.co.q3c.v7.base.view.RequestSystemAccountView;
import uk.co.q3c.v7.base.view.SystemAccountView;
import uk.co.q3c.v7.i18n.AnnotationI18NTranslator;
import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.I18NTranslator;
import uk.co.q3c.v7.i18n.TestLabelKey;

import com.google.inject.AbstractModule;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class StandardPageBuilderTest {

	@Inject
	private StandardPageBuilder builder;
	private Sitemap sitemap;
	private SitemapURIConverter converter;

	@Inject
	private URIFragmentHandler uriHandler;

	@Inject
	CurrentLocale currentLocale;

	@Before
	public void setup() {
		sitemap = new Sitemap();
		converter = new SitemapURIConverter(sitemap, uriHandler);
		builder.setSitemap(sitemap);
	}

	@Test
	public void defaultUri() {

		// given
		// defaults
		// when

		// then
		assertThat(builder.defaultUri(StandardPageKey.Public_Home)).isEqualTo("public");
		assertThat(builder.defaultUri(StandardPageKey.Login)).isEqualTo("public/login");
		assertThat(builder.defaultUri(StandardPageKey.Logout)).isEqualTo("public/logout");
		assertThat(builder.defaultUri(StandardPageKey.Private_Home)).isEqualTo("private");
		assertThat(builder.defaultUri(StandardPageKey.System_Account)).isEqualTo("public/system-account");
		assertThat(builder.defaultUri(StandardPageKey.Enable_Account))
				.isEqualTo("public/system-account/enable-account");
		assertThat(builder.defaultUri(StandardPageKey.Unlock_Account))
				.isEqualTo("public/system-account/unlock-account");
		assertThat(builder.defaultUri(StandardPageKey.Reset_Account)).isEqualTo("public/system-account/reset-account");
		assertThat(builder.defaultUri(StandardPageKey.Request_Account)).isEqualTo(
				"public/system-account/request-account");
		assertThat(builder.defaultUri(StandardPageKey.Refresh_Account)).isEqualTo(
				"public/system-account/refresh-account");

	}

	@Test
	public void defaultUri_changeSystemAccount() {

		// given
		builder.setSystemAccountRoot("public/sysaccount");
		// when

		// then
		assertThat(builder.defaultUri(StandardPageKey.Public_Home)).isEqualTo("public");
		assertThat(builder.defaultUri(StandardPageKey.Login)).isEqualTo("public/login");
		assertThat(builder.defaultUri(StandardPageKey.Logout)).isEqualTo("public/logout");
		assertThat(builder.defaultUri(StandardPageKey.Private_Home)).isEqualTo("private");
		assertThat(builder.defaultUri(StandardPageKey.System_Account)).isEqualTo("public/sysaccount");
		assertThat(builder.defaultUri(StandardPageKey.Enable_Account)).isEqualTo("public/sysaccount/enable-account");
		assertThat(builder.defaultUri(StandardPageKey.Unlock_Account)).isEqualTo("public/sysaccount/unlock-account");
		assertThat(builder.defaultUri(StandardPageKey.Reset_Account)).isEqualTo("public/sysaccount/reset-account");
		assertThat(builder.defaultUri(StandardPageKey.Request_Account)).isEqualTo("public/sysaccount/request-account");
		assertThat(builder.defaultUri(StandardPageKey.Refresh_Account)).isEqualTo("public/sysaccount/refresh-account");

	}

	@Test
	public void defaultUri_changePublicPrivateRoot() {

		// given
		sitemap.setPublicRoot("open");
		sitemap.setPrivateRoot("secret");
		builder.setSystemAccountRoot("open/system-account");
		// when

		// then
		assertThat(builder.defaultUri(StandardPageKey.Public_Home)).isEqualTo("open");
		assertThat(builder.defaultUri(StandardPageKey.Login)).isEqualTo("open/login");
		assertThat(builder.defaultUri(StandardPageKey.Logout)).isEqualTo("open/logout");
		assertThat(builder.defaultUri(StandardPageKey.Private_Home)).isEqualTo("secret");
		assertThat(builder.defaultUri(StandardPageKey.System_Account)).isEqualTo("open/system-account");
		assertThat(builder.defaultUri(StandardPageKey.Enable_Account)).isEqualTo("open/system-account/enable-account");
		assertThat(builder.defaultUri(StandardPageKey.Unlock_Account)).isEqualTo("open/system-account/unlock-account");
		assertThat(builder.defaultUri(StandardPageKey.Reset_Account)).isEqualTo("open/system-account/reset-account");
		assertThat(builder.defaultUri(StandardPageKey.Request_Account))
				.isEqualTo("open/system-account/request-account");
		assertThat(builder.defaultUri(StandardPageKey.Refresh_Account))
				.isEqualTo("open/system-account/refresh-account");

	}

	@Test
	public void defaultViewInterface() {

		// given

		// when

		// then
		assertThat(builder.viewClass(StandardPageKey.Public_Home)).isEqualTo(PublicHomeView.class);
		assertThat(builder.viewClass(StandardPageKey.Login)).isEqualTo(LoginView.class);
		assertThat(builder.viewClass(StandardPageKey.Logout)).isEqualTo(LogoutView.class);
		assertThat(builder.viewClass(StandardPageKey.Private_Home)).isEqualTo(PrivateHomeView.class);
		assertThat(builder.viewClass(StandardPageKey.System_Account)).isEqualTo(SystemAccountView.class);
		assertThat(builder.viewClass(StandardPageKey.Enable_Account)).isEqualTo(RequestSystemAccountEnableView.class);
		assertThat(builder.viewClass(StandardPageKey.Unlock_Account)).isEqualTo(RequestSystemAccountUnlockView.class);
		assertThat(builder.viewClass(StandardPageKey.Reset_Account)).isEqualTo(RequestSystemAccountResetView.class);
		assertThat(builder.viewClass(StandardPageKey.Request_Account)).isEqualTo(RequestSystemAccountView.class);
		assertThat(builder.viewClass(StandardPageKey.Refresh_Account)).isEqualTo(RequestSystemAccountRefreshView.class);
	}

	@Test
	public void generateStandardPages_default() {

		// given

		// when
		builder.generateStandardPages();
		// then
		assertThat(sitemap.uris()).containsOnly("public", "private", "public/login", "public/logout",
				"public/system-account", "public/system-account/reset-account", "public/system-account/enable-account",
				"public/system-account/unlock-account", "public/system-account/refresh-account",
				"public/system-account/request-account");
	}

	@Test
	public void defaultSegment() {

		// given

		// when

		// then
		assertThat(builder.defaultSegment(StandardPageKey.Public_Home)).isEqualTo("public");
		assertThat(builder.defaultSegment(StandardPageKey.Private_Home)).isEqualTo("private");
		assertThat(builder.defaultSegment(StandardPageKey.Login)).isEqualTo("login");
		assertThat(builder.defaultSegment(StandardPageKey.Logout)).isEqualTo("logout");
		assertThat(builder.defaultSegment(StandardPageKey.System_Account)).isEqualTo("system-account");
		assertThat(builder.defaultSegment(StandardPageKey.Request_Account)).isEqualTo("request-account");
		assertThat(builder.defaultSegment(StandardPageKey.Unlock_Account)).isEqualTo("unlock-account");
		assertThat(builder.defaultSegment(StandardPageKey.Refresh_Account)).isEqualTo("refresh-account");
		assertThat(builder.defaultSegment(StandardPageKey.Reset_Account)).isEqualTo("reset-account");
		assertThat(builder.defaultSegment(StandardPageKey.Enable_Account)).isEqualTo("enable-account");
	}

	/**
	 * publicHome=public : WigglyHome ~ Yes
	 */
	@Test
	public void standardPageMapping() {

		// given
		List<String> pageMappings = new ArrayList<>();
		pageMappings.add("Public_Home=public  ~ Yes");
		builder.setLabelKeysClass(TestLabelKey.class);

		// when
		builder.setPageMappings(pageMappings);
		Map<StandardPageKey, String> standardPages = builder.getSitemap().getStandardPages();
		SitemapNode node = converter.nodeForUri("public", false);
		// then
		assertThat(node).isNotNull();
		assertThat(standardPages.get(StandardPageKey.Public_Home)).isEqualTo("public");
		assertThat(node.getLabelKey()).isEqualTo(TestLabelKey.Yes);
		assertThat(node.getUriSegment()).isEqualTo("public");
		assertThat(node.getViewClass()).isEqualTo(builder.viewClass(StandardPageKey.Public_Home));
		assertThat(builder.getSitemap().uri(node)).isEqualTo("public");

	}

	@Test
	public void standardPageMapping_different() {

		// given
		List<String> pageMappings = new ArrayList<>();
		pageMappings.add("Public_Home=  wildly/different  ~ Yes");
		builder.setLabelKeysClass(TestLabelKey.class);

		// when
		builder.setPageMappings(pageMappings);
		Map<StandardPageKey, String> standardPages = builder.getSitemap().getStandardPages();
		SitemapNode node = converter.nodeForUri("wildly/different", false);
		// then
		assertThat(node).isNotNull();
		assertThat(standardPages.get(StandardPageKey.Public_Home)).isEqualTo("wildly/different");
		assertThat(node.getLabelKey()).isEqualTo(TestLabelKey.Yes);
		assertThat(node.getUriSegment()).isEqualTo("different");
		assertThat(node.getViewClass()).isEqualTo(builder.viewClass(StandardPageKey.Public_Home));
		assertThat(builder.getSitemap().uri(node)).isEqualTo("wildly/different");

	}

	@Test
	public void standardPageMapping_multiple() {

		// given
		List<String> pageMappings = new ArrayList<>();
		pageMappings.add("Public_Home=  wildly/different  ~ Yes");
		pageMappings.add("Private_Home=  almost/different  ~ No");
		builder.setLabelKeysClass(TestLabelKey.class);

		// when
		builder.setPageMappings(pageMappings);
		Map<StandardPageKey, String> standardPages = builder.getSitemap().getStandardPages();
		SitemapNode node = converter.nodeForUri("wildly/different", false);
		// then
		assertThat(node).isNotNull();
		assertThat(standardPages.get(StandardPageKey.Public_Home)).isEqualTo("wildly/different");
		assertThat(node.getLabelKey()).isEqualTo(TestLabelKey.Yes);
		assertThat(node.getUriSegment()).isEqualTo("different");
		assertThat(node.getViewClass()).isEqualTo(builder.viewClass(StandardPageKey.Public_Home));
		assertThat(builder.getSitemap().uri(node)).isEqualTo("wildly/different");

		node = converter.nodeForUri("almost/different", false);
		// then
		assertThat(node).isNotNull();
		assertThat(standardPages.get(StandardPageKey.Private_Home)).isEqualTo("almost/different");
		assertThat(node.getLabelKey()).isEqualTo(TestLabelKey.No);
		assertThat(node.getUriSegment()).isEqualTo("different");
		assertThat(node.getViewClass()).isEqualTo(builder.viewClass(StandardPageKey.Private_Home));
		assertThat(builder.getSitemap().uri(node)).isEqualTo("almost/different");

	}

	@ModuleProvider
	protected AbstractModule module() {
		return new AbstractModule() {

			@Override
			protected void configure() {
				bind(I18NTranslator.class).to(AnnotationI18NTranslator.class);
				bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
			}

		};
	}

}
