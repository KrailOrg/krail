package uk.co.q3c.v7.base.guice.threadscope;

import com.google.inject.Inject;

@ThreadScoped
public class SomeClass {

	@Inject
	public SomeClass() {
		super();
	}

}
