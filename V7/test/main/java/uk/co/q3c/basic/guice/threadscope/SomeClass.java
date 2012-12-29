package uk.co.q3c.basic.guice.threadscope;

import javax.inject.Inject;

import uk.co.q3c.v7.base.guice.threadscope.ThreadScoped;

@ThreadScoped
public class SomeClass {

	@Inject
	public SomeClass() {
		super();
	}

}
