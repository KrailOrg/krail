package uk.co.q3c.v7.testapp;

import java.util.List;

import javax.servlet.annotation.WebListener;

import uk.co.q3c.v7.base.guice.BaseGuiceServletInjector;
import uk.co.q3c.v7.base.navigate.sitemap.SystemAccountManagementPages;
import uk.co.q3c.v7.testapp.view.TestAppPages;
import uk.co.q3c.v7.testapp.view.TestAppViewModule;

import com.google.inject.Module;

@WebListener
public class TestAppGuiceServletInjector extends BaseGuiceServletInjector {

	@Override
	protected void addAppModules(List<Module> modules) {
		modules.add(new TestAppUIModule());
	}

	@Override
	protected Module standardViewsModule() {
		return new TestAppViewModule();
	}

	@Override
	protected void addSitemapModules(List<Module> baseModules) {
		super.addSitemapModules(baseModules);
		baseModules.add(new SystemAccountManagementPages());
		baseModules.add(new TestAppPages());
	}

	@Override
	protected Module servletModule() {
		return new TestAppServletModule();
	}

}
