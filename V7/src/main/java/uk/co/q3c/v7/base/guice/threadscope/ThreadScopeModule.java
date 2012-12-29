package uk.co.q3c.v7.base.guice.threadscope;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class ThreadScopeModule extends AbstractModule {
	private final ThreadScope scope;

	public ThreadScopeModule() {
		super();
		scope = new ThreadScope(new ThreadCache());
	}

	@Override
	public void configure() {

		// tell Guice about the scope
		bindScope(ThreadScoped.class, scope);

		// make our scope instance injectable
		bind(ThreadScope.class).annotatedWith(Names.named("ThreadScope")).toInstance(scope);

	}

	public ThreadScope getScope() {
		return scope;
	}

}