package uk.co.q3c.basic;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class MainWindowScopeModule extends AbstractModule {
	@Override
	public void configure() {
		MainWindowScope mwScope = new MainWindowScope();

		// tell Guice about the scope
		bindScope(MainWindowScoped.class, mwScope);

		// make our scope instance injectable
		bind(MainWindowScope.class).annotatedWith(Names.named("mainWindowScope")).toInstance(mwScope);
	}
}