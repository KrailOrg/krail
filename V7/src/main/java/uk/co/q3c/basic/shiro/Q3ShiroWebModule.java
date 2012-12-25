package uk.co.q3c.basic.shiro;

import java.util.Collection;

import javax.servlet.ServletContext;

import org.apache.shiro.guice.web.ShiroWebModule;
import org.apache.shiro.io.ResourceUtils;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.ServletContainerSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.basic.A;

import com.google.inject.Provides;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

public class Q3ShiroWebModule extends ShiroWebModule {

	public Q3ShiroWebModule(ServletContext sc) {
		super(sc);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void configureShiroWeb() {
		bindRealm().to(ShiroBaseRealm.class);
		addFilterChain("/rest/**", ANON);
		addFilterChain("/**", ANON/* AUTHC_BASIC */);
		bindConstant().annotatedWith(Names.named("shiro.globalSessionTimeout")).to(30000L);
	}

	@Provides
	@Named(A.SHIRO_CONFIG_PATH)
	String getConfigPath() {
		return ResourceUtils.CLASSPATH_PREFIX + "shiro.ini";
	}

	@Override
	protected void bindSessionManager(AnnotatedBindingBuilder<SessionManager> bind) {
		bind.to(Q3WebSessionManager.class).asEagerSingleton();
	}

	@Override
	protected void bindWebSecurityManager(AnnotatedBindingBuilder<? super WebSecurityManager> bind) {
		try {
			bind.toConstructor(Q3WebSecurityManager.class.getConstructor(Collection.class)).asEagerSingleton();
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Shiro configuration", e);
		}
	}

	static class Q3WebSessionManager extends DefaultWebSessionManager {
		public static final String Q3_SESSION_ID_NAME = "Q3ID";

		public Q3WebSessionManager() {
			Cookie cookie = new SimpleCookie(Q3_SESSION_ID_NAME);
			cookie.setHttpOnly(true); // more secure, protects against XSS attacks
			setSessionIdCookie(cookie);
			setSessionIdCookieEnabled(true);
		}

	}

	static class Q3WebSecurityManager extends DefaultWebSecurityManager {
		private static Logger log = LoggerFactory.getLogger(Q3ShiroWebModule.Q3WebSecurityManager.class);

		public Q3WebSecurityManager(Collection<Realm> realms) {
			super(realms);
		}

		@SuppressWarnings("deprecation")
		@Override
		protected SessionManager createSessionManager(String sessionMode) {
			if (sessionMode == null || !sessionMode.equalsIgnoreCase(NATIVE_SESSION_MODE)) {
				log.info("enabling Shiro Http Mode");
				return new ServletContainerSessionManager();
			} else {
				log.info("enabling Shiro Native Mode");
				return new Q3WebSessionManager();
			}
		}

	}

}
