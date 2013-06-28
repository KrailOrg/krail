package uk.co.q3c.v7.base.navigate;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

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

public class StandardPageBuilderTest {

	private StandardPageBuilder builder;
	private Sitemap sitemap;

	@Before
	public void setup() {
		builder = new StandardPageBuilder();
		sitemap = new Sitemap();
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
		assertThat(builder.defaultViewInterface(StandardPageKey.Public_Home)).isEqualTo(PublicHomeView.class);
		assertThat(builder.defaultViewInterface(StandardPageKey.Login)).isEqualTo(LoginView.class);
		assertThat(builder.defaultViewInterface(StandardPageKey.Logout)).isEqualTo(LogoutView.class);
		assertThat(builder.defaultViewInterface(StandardPageKey.Private_Home)).isEqualTo(PrivateHomeView.class);
		assertThat(builder.defaultViewInterface(StandardPageKey.System_Account)).isEqualTo(SystemAccountView.class);
		assertThat(builder.defaultViewInterface(StandardPageKey.Enable_Account)).isEqualTo(
				RequestSystemAccountEnableView.class);
		assertThat(builder.defaultViewInterface(StandardPageKey.Unlock_Account)).isEqualTo(
				RequestSystemAccountUnlockView.class);
		assertThat(builder.defaultViewInterface(StandardPageKey.Reset_Account)).isEqualTo(
				RequestSystemAccountResetView.class);
		assertThat(builder.defaultViewInterface(StandardPageKey.Request_Account)).isEqualTo(
				RequestSystemAccountView.class);
		assertThat(builder.defaultViewInterface(StandardPageKey.Refresh_Account)).isEqualTo(
				RequestSystemAccountRefreshView.class);
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

}
