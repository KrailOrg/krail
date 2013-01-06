package uk.co.q3c.basic;

import org.apache.shiro.guice.ShiroModule;

import uk.co.q3c.v7.demo.shiro.DemoRealm;

public class TestShiroModule extends ShiroModule {

	@Override
	protected void configureShiro() {
		bindRealm().to(DemoRealm.class);
	}

}
