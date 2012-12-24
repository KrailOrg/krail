package uk.co.q3c.basic.guice.threadscope;

import javax.inject.Inject;

@ThreadScoped
public class SomeClass {

	@Inject
	public SomeClass() {
		super();
	}

}
