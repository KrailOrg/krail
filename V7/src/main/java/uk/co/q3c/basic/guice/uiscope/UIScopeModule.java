package uk.co.q3c.basic.guice.uiscope;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class UIScopeModule extends AbstractModule {
	@Override
	public void configure() {
		UIScope btScope = new UIScope();

		// tell Guice about the scope
		bindScope(UIScoped.class, btScope);

		// make our scope instance injectable
		bind(UIScope.class).annotatedWith(Names.named("UIScope")).toInstance(btScope);
	}
}