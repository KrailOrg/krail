package uk.co.q3c.v7.base.guice.threadscope;

import com.google.inject.Inject;

import uk.co.q3c.v7.base.guice.threadscope.ThreadScoped;

@ThreadScoped
public class SomeClass {

	@Inject
	public SomeClass() {
		super();
	}

}
