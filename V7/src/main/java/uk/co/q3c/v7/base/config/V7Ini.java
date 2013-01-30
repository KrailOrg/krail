package uk.co.q3c.v7.base.config;

import java.util.EnumMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.shiro.config.ConfigurationException;
import org.apache.shiro.config.Ini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extends the Shiro {@link Ini} class validate that all required entries are defined. Missing entries will either have
 * defaults set or an exception raised if appropriate. <b>NOTE:</b>Only {@link #loadFromPath(String)} has been
 * overloaded; if you need to use any of the other load methods from {@link Ini}, you will need to call
 * {@link #validate()} after the load to provide defaults.
 * 
 */
@Singleton
public class V7Ini extends Ini {
	private static Logger log = LoggerFactory.getLogger(V7Ini.class);

	public static enum StandardPageKey {
		publicHome, // The home page for non-authenticated users
		secureHome, // The home page for authenticated users
		login, // the login page
		logout, // the page to go to after logging out
		resetAccount, // page for the user to request an account reset
		unlockAccount, // the page to go to for the user to request their account be unlocked
		refreshAccount, // the page to go to for the user to refresh their account after credentials have expired
		requestAccount, // the page to go to for the user to request an account (Equivalent to 'register')
		enableAccount // the page to go to for the user to request that their account is enabled
	};

	/**
	 * The login page
	 */
	public static final String loginKey = "login";

	/**
	 * The page to be presented after a user has logged out
	 */
	public static final String logoutKey = "logout";

	/**
	 * The page to be presented after a user has logged out
	 */
	public static final String resetAccountKey = "resetAccount";

	private static final EnumMap<StandardPageKey, String> standardPagesDefaults = new EnumMap<>(StandardPageKey.class);
	static {
		standardPagesDefaults.put(StandardPageKey.publicHome, "public/home");
		standardPagesDefaults.put(StandardPageKey.secureHome, "secure/home");
		standardPagesDefaults.put(StandardPageKey.login, "public/login");
		standardPagesDefaults.put(StandardPageKey.logout, "public/logout");
		standardPagesDefaults.put(StandardPageKey.resetAccount, "public/reset-account");
		standardPagesDefaults.put(StandardPageKey.unlockAccount, "public/unlock-account");
		standardPagesDefaults.put(StandardPageKey.refreshAccount, "public/refresh-account");
		standardPagesDefaults.put(StandardPageKey.requestAccount, "public/request-account");
		standardPagesDefaults.put(StandardPageKey.enableAccount, "public/enable-account");
	}

	@Inject
	protected V7Ini() {
		super();
	}

	public void validate() {
		validatePages(checkSection("pages"));
	}

	private Section checkSection(String sectionName) {
		Section section = getSection(sectionName);
		if (section == null) {
			log.warn("The section {} is missing from V7.ini, using default values", sectionName);
			section = this.addSection(sectionName);
		}
		return section;
	}

	protected void validatePages(Section section) {
		for (StandardPageKey pageKey : standardPagesDefaults.keySet()) {
			if (!section.containsKey(pageKey.name())) {
				log.warn("The property {} is missing from V7.ini, using the default value", pageKey.name());
				section.put(pageKey.name(), standardPagesDefaults.get(pageKey));
			}
		}
	}

	@Override
	public void loadFromPath(String resourcePath) throws ConfigurationException {
		try {
			super.loadFromPath(resourcePath);
		} catch (ConfigurationException ce) {
			log.warn("Unable to load V7.ini from {}, using defaults.", resourcePath);
		} finally {
			validate();
		}
	}

	public String standardPageURI(StandardPageKey pageKey) {
		Section section = getSection("pages");
		String path = section.get(pageKey.name());
		return path;

	}

}
