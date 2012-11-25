package uk.co.q3c.basic;

import javax.inject.Provider;

import com.vaadin.ui.UI;

public class GuiceUIProvider implements Provider<Class<? extends UI>> {

	@Override
	public Class<? extends UI> get() {
		// return null;
		throw new RuntimeException("not yet implemented");
	}

}
