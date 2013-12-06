package uk.co.q3c.v7.base.navigate;

import java.text.Collator;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.guice.BaseGuiceServletInjector;
import uk.co.q3c.v7.base.navigate.sitemap.PageRecord;
import uk.co.q3c.v7.base.navigate.sitemap.Sitemap;
import uk.co.q3c.v7.base.navigate.sitemap.SitemapNode;
import uk.co.q3c.v7.base.navigate.sitemap.SitemapFileReader;
import uk.co.q3c.v7.base.view.LoginView;
import uk.co.q3c.v7.base.view.LogoutView;
import uk.co.q3c.v7.base.view.PrivateHomeView;
import uk.co.q3c.v7.base.view.PublicHomeView;
import uk.co.q3c.v7.base.view.RequestSystemAccountEnableView;
import uk.co.q3c.v7.base.view.RequestSystemAccountRefreshView;
import uk.co.q3c.v7.base.view.RequestSystemAccountResetView;
import uk.co.q3c.v7.base.view.RequestSystemAccountUnlockView;
import uk.co.q3c.v7.base.view.RequestSystemAccountView;
import uk.co.q3c.v7.base.view.StandardViewModule;
import uk.co.q3c.v7.base.view.SystemAccountView;
import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.I18NKey;
import uk.co.q3c.v7.i18n.Translate;

/**
 * Used during the process of building the {@link Sitemap}. Provides the logic for building standard pages using options
 * provided by the {@link SitemapFileReader} and defaults from {@link StandardPageKey}
 * 
 * @author David Sowerby 28 June 2013
 * 
 * 
 */
@Singleton
public class StandardPageBuilder {
	private static Logger log = LoggerFactory.getLogger(StandardPageBuilder.class);
	private boolean generatePublicHomePage = true;
	private boolean generateAuthenticationPages = true;
	private boolean generateRequestAccount = true;
	private boolean generateRequestAccountReset = true;
	private String systemAccountRoot = "public/system-account";
	private Sitemap sitemap;
	// private final EnumMap<StandardPageKey, String> pageMappings = new EnumMap<>(StandardPageKey.class);
	private Class<? extends Enum<?>> labelKeysClass;
	private Set<String> missingEnums;
	private Set<String> standardPageErrors;
	private final CurrentLocale currentLocale;
	private final Collator collator;
	private final Translate translate;

	@Inject
	protected StandardPageBuilder(CurrentLocale currentLocale, Translate translate) {
		super();
		this.currentLocale = currentLocale;
		this.collator = Collator.getInstance(currentLocale.getLocale());
		this.translate = translate;
	}

	public void generateStandardPages() {
		if (generatePublicHomePage) {
			generatePage(StandardPageKey.Public_Home);
		}
		if (generateAuthenticationPages) {
			generatePage(StandardPageKey.Private_Home);
			generatePage(StandardPageKey.Login);
			generatePage(StandardPageKey.Logout);

			if (generateRequestAccount || generateRequestAccountReset) {
				generatePage(StandardPageKey.System_Account);
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
	 * Creates a Sitemap node and assigns the default values from the {@code key}
	 * 
	 * @param key
	 */
	private void generatePage(StandardPageKey key) {
		log.debug("generating page for {}", key);
		SitemapNode node = sitemap.append(defaultUri(key));
		node.setLabelKey(key, translate, collator);
		node.setViewClass(viewClass(key));
		node.setUriSegment(defaultSegment(key));
		sitemap.getStandardPages().put(key, sitemap.uri(node));
		log.debug("standard page added as node at URI " + sitemap.uri(node) + ", " + node.toString());
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

	public void setGenerateAuthenticationPages(boolean generateAuthenticationPages) {
		this.generateAuthenticationPages = generateAuthenticationPages;
	}

	public void setGenerateRequestAccount(boolean generateRequestAccount) {
		this.generateRequestAccount = generateRequestAccount;
	}

	public void setGenerateRequestAccountReset(boolean generateRequestAccountReset) {
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

	public String defaultUri(StandardPageKey key) {
		switch (key) {
		case Private_Home:
			return sitemap.getPrivateRoot();
		case Public_Home:
			return sitemap.getPublicRoot();
		case Login:
		case Logout:
			return sitemap.getPublicRoot() + "/" + defaultSegment(key);

		case Request_Account:
		case Refresh_Account:
		case Enable_Account:
		case Unlock_Account:
		case Reset_Account:
			return getSystemAccountRoot() + "/" + defaultSegment(key);
		case System_Account:
			return getSystemAccountRoot();
		}
		return null;
	}

	public String uri(StandardPageKey key) {
		return defaultUri(key);
	}

	// public void setSystemAccountURI(String uri) {
	// String[] segments = uri.split("/");
	// if (segments.length == 1) {
	// systemAccountPath = segments[0];
	// systemAccountSegment = segments[0];
	// return;
	// }
	// String[] pathSegments = Arrays.copyOf(segments, segments.length - 1);
	// systemAccountSegment = segments[segments.length - 1];
	// systemAccountPath = Joiner.on("/").join(pathSegments);
	// }

	public String defaultSegment(StandardPageKey key) {
		switch (key) {
		case Public_Home:
			return sitemap.getPublicRoot();
		case Private_Home:
			return sitemap.getPrivateRoot();
		default:
			String s = key.name().toLowerCase().replace("_", "-");
			return s;
		}

	}

	/**
	 * The view class is always the same for a given standard page ... it is an interface, so to implement your own,
	 * provide an implementation a sub-class of {@link StandardViewModule}, and load that module in your sub-class of
	 * {@link BaseGuiceServletInjector}
	 * 
	 * @param key
	 * @return
	 */
	public Class<? extends V7View> viewClass(StandardPageKey key) {
		switch (key) {
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
		case System_Account:
			return SystemAccountView.class;
		}
		return null;
	}

	/**
	 * publicHome=public : WigglyHome ~ Yes
	 * 
	 * @param pageMappings
	 */
	public void setPageMappings(List<String> pageMappings) {
		StandardPageMappingReader dec = new StandardPageMappingReader();
		int i = 0;
		for (String line : pageMappings) {
			i++;
			// check a line for syntax
			PageRecord pr = dec.deconstruct(line, i);
			// null when there are syntax failures
			if (pr != null) {
				// identify the standard page being defined
				try {
					StandardPageKey spk = StandardPageKey.valueOf(pr.getStandardPageKeyName());
					LabelKeyForName labelKeyForName = new LabelKeyForName(labelKeysClass);
					I18NKey<?> labelKey = labelKeyForName.keyForName(pr.getLabelKeyName(), missingEnums);
					SitemapNode node = sitemap.append(pr.getUri());
					node.setLabelKey(labelKey, translate, collator);
					node.setViewClass(viewClass(spk));
					sitemap.getStandardPages().put(spk, sitemap.uri(node));
				} catch (Exception e) {
					standardPageErrors.add(pr.getStandardPageKeyName() + " is not a valid standard page key in line "
							+ i);
				}
			} else {
				// there were errors in syntax
				standardPageErrors.addAll(dec.getSyntaxErrors());
			}
		}
	}

	// public I18NKeys<?> key(StandardPageKey publicHome) {
	// return null;
	// }

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
