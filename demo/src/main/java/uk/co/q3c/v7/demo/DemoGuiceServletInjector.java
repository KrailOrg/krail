package uk.co.q3c.v7.demo;

import java.util.List;

import uk.co.q3c.v7.base.guice.BaseGuiceServletInjector;
import uk.co.q3c.v7.base.navigate.sitemap.SystemAccountManagementPages;
import uk.co.q3c.v7.demo.view.DemoPages;
import uk.co.q3c.v7.demo.view.DemoViewModule;

import com.google.inject.Module;

public class DemoGuiceServletInjector extends BaseGuiceServletInjector {

	@Override
	protected void addAppModules(List<Module> modules) {
		modules.add(new DemoUIModule());
	}

	@Override
	protected Module viewModule() {
		return new DemoViewModule();
	}

	@Override
	protected void addSitemapModules(List<Module> baseModules) {
		super.addSitemapModules(baseModules);
		baseModules.add(new SystemAccountManagementPages());
		baseModules.add(new DemoPages());
	}

	@Override
	protected Module servletModule() {
		return new DemoServletModule();
	}

}
