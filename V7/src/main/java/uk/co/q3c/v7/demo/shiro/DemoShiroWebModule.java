package uk.co.q3c.v7.demo.shiro;

import javax.servlet.ServletContext;

import org.apache.shiro.guice.web.ShiroWebModule;

public class DemoShiroWebModule extends ShiroWebModule {

	public DemoShiroWebModule(ServletContext sc) {
		super(sc);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void configureShiroWeb() {

		// bind the authentication realm
		bindRealm().to(DemoRealm.class);
		addFilterChain("/#public/**", ANON);
		addFilterChain("/#secure/**", AUTHC);

	}

}
