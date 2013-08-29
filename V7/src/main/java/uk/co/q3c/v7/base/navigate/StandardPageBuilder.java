package uk.co.q3c.v7.base.navigate;

import java.text.Collator;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.guice.BaseGuiceServletInjector;
import uk.co.q3c.v7.base.view.LoginView;
import uk.co.q3c.v7.base.view.LogoutView;
import uk.co.q3c.v7.base.view.PrivateHomeView;
import uk.co.q3c.v7.base.view.PublicHomeView;
import uk.co.q3c.v7.base.view.RequestSystemAccountEnableView;
import uk.co.q3c.v7.base.view.RequestSystemAccountRefreshView;
import uk.co.q3c.v7.base.view.RequestSystemAccountResetView;
import uk.co.q3c.v7.base.view.RequestSystemAccountUnlockView;
import uk.co.q3c.v7.base.view.RequestSystemAccountView;
import uk.co.q3c.v7.base.view.RootView;
import uk.co.q3c.v7.base.view.StandardViewModule;
import uk.co.q3c.v7.base.view.SystemAccountView;
import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.I18NKey;

/**
 * Used during the process of building the {@link Sitemap}. Provides the logic
 * for building standard pages using options provided by the
 * {@link SitemapProvider} and defaults from {@link StandardPageKeyTest}
 * 
 * @author David Sowerby 28 June 2013
 * 
 * 
 */
@Singleton
public class StandardPageBuilder {
	private static Logger log = LoggerFactory
			.getLogger(StandardPageBuilder.class);

	private boolean generatePublicHomePage = true;
	private boolean generateAuthenticationPages = true;
	private boolean generateRequestAccount = true;
	private boolean generateRequestAccountReset = true;
	private String systemAccountRoot = "public/system-account";

	private Sitemap sitemap;

	private Class<? extends Enum<?>> labelKeysClass;
	private Set<String> missingEnums;
	private Set<String> standardPageErrors;
	private final CurrentLocale currentLocale;

	@Inject
	protected StandardPageBuilder(CurrentLocale currentLocale) {
		super();
		this.currentLocale = currentLocale;
	}

	public void generateStandardPages() {
		generatePage(StandardPageKey.Root);
		
		if (generatePublicHomePage) {
			generatePage(StandardPageKey.Public_Home);
		}
		if (generateAuthenticationPages) {
			generatePage(StandardPageKey.Private_Home);
			generatePage(StandardPageKey.Login);
			generatePage(StandardPageKey.Logout);

			if (generateRequestAccount || generateRequestAccountReset) {
				generatePage(StandardPageKey.Account);
			}

			if (generateRequestAccount) {
				generatePage(StandardPageKey.Request_Account);
			}

			if (generateRequestAccountReset) {
				generatePage(StandardPageKey.Unlock_Account);
				generatePage(StandardPageKey.Refresh_Account);
				generatePage(StandardPageKey.Enable_Account);
				generatePage(StandardPageKey.Reset_Account);
			}
		}

	}

	/**
	 * Creates a Sitemap node and assigns the default values from the
	 * {@code key}
	 * 
	 * @param key
	 */
	private void generatePage(StandardPageKey key) {
		log.debug("generating page for {}", key);
		SitemapNode node = sitemap.addNode(key, defaultUri(key));
		node.setLabelKey(key, currentLocale.getLocale());
		node.setViewClass(viewClass(key));
		log.debug("standard page added as node at URI " + node.getUri() + ", "
				+ node.toString());
	}

	public boolean isGeneratePublicHomePage() {
		return generatePublicHomePage;
	}

	public boolean isGenerateAuthenticationPages() {
		return generateAuthenticationPages;
	}

	public boolean isGenerateRequestAccount() {
		return generateRequestAccount;
	}

	public boolean isGenerateRequestAccountReset() {
		return generateRequestAccountReset;
	}

	public void setGeneratePublicHomePage(boolean generatePublicHomePage) {
		this.generatePublicHomePage = generatePublicHomePage;
	}

	public void setGenerateAuthenticationPages(
			boolean generateAuthenticationPages) {
		this.generateAuthenticationPages = generateAuthenticationPages;
	}

	public void setGenerateRequestAccount(boolean generateRequestAccount) {
		this.generateRequestAccount = generateRequestAccount;
	}

	public void setGenerateRequestAccountReset(
			boolean generateRequestAccountReset) {
		this.generateRequestAccountReset = generateRequestAccountReset;
	}

	public void setSitemap(Sitemap sitemap) {
		this.sitemap = sitemap;

	}

	public String getSystemAccountRoot() {
		return systemAccountRoot;
	}

	public void setSystemAccountRoot(String systemAccountRoot) {
		this.systemAccountRoot = systemAccountRoot;
	}

	public String defaultUri(PageKey key) {
		return key.getUri();
	}

	public String uri(PageKey key) {
		return defaultUri(key);
	}

	/**
	 * The view class is always the same for a given standard page ... it is an
	 * interface, so to implement your own, provide an implementation a
	 * sub-class of {@link StandardViewModule}, and load that module in your
	 * sub-class of {@link BaseGuiceServletInjector}
	 * 
	 * @param key
	 * @return
	 */
	public Class<? extends V7View> viewClass(StandardPageKey key) {
		switch (key) {
		case Root:
			return RootView.class;
		case Public_Home:
			return PublicHomeView.class;
		case Private_Home:
			return PrivateHomeView.class;
		case Login:
			return LoginView.class;
		case Logout:
			return LogoutView.class;
		case Reset_Account:
			return RequestSystemAccountResetView.class;
		case Unlock_Account:
			return RequestSystemAccountUnlockView.class;
		case Refresh_Account:
			return RequestSystemAccountRefreshView.class;
		case Request_Account:
			return RequestSystemAccountView.class;
		case Enable_Account:
			return RequestSystemAccountEnableView.class;
		case Account:
			return SystemAccountView.class;
		}
		return null;
	}

	public void setLabelKeysClass(Class<? extends Enum<?>> labelKeysClass) {
		this.labelKeysClass = labelKeysClass;
	}

	public void setMissingEnums(Set<String> missingEnums) {
		this.missingEnums = missingEnums;
	}

	public void setStandardPageErrors(Set<String> standardPageErrors) {
		this.standardPageErrors = standardPageErrors;
	}

	public Sitemap getSitemap() {
		return sitemap;
	}

}
