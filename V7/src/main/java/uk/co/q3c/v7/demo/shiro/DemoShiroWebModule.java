package uk.co.q3c.v7.demo.shiro;

import javax.servlet.ServletContext;

import org.apache.shiro.guice.web.ShiroWebModule;

import com.google.inject.name.Names;

public class DemoShiroWebModule extends ShiroWebModule {

	public DemoShiroWebModule(ServletContext sc) {
		super(sc);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void configureShiroWeb() {
		bindRealm().to(ShiroDebugRealm.class);
		addFilterChain("/#public/**", ANON);
		addFilterChain("/#secure/**", AUTHC);
		bindConstant().annotatedWith(Names.named("shiro.globalSessionTimeout")).to(30000L);
	}

}
