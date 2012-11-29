package uk.co.q3c.basic.guice.uiscope;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class UIScopeModule extends AbstractModule {
	private final UIScope uiScope;

	public UIScopeModule() {
		super();
		uiScope = new UIScope();
	}

	@Override
	public void configure() {

		// tell Guice about the scope
		bindScope(UIScoped.class, uiScope);

		// make our scope instance injectable
		bind(UIScope.class).annotatedWith(Names.named("UIScope")).toInstance(uiScope);
	}

	public UIScope getUiScope() {
		return uiScope;
	}

}