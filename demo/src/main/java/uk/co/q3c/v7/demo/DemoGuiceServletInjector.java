package uk.co.q3c.v7.demo;

import java.util.List;

import uk.co.q3c.v7.base.guice.BaseGuiceServletInjector;
import uk.co.q3c.v7.demo.view.DemoViewModule;

import com.google.inject.Module;

public class DemoGuiceServletInjector extends BaseGuiceServletInjector {

	@Override
	protected void addAppModules(List<Module> modules) {
		modules.add(new DemoModule());
		modules.add(new DemoUIModule());
	}

	@Override
	protected Module standardViewModule() {
		return new DemoViewModule();
	}

}
