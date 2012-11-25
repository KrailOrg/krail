package uk.co.q3c.basic.guice.uiscope;

import javax.inject.Provider;


public class UIKeyProvider implements Provider<UIKey> {

	@Override
	public UIKey get() {
		return new UIKey();
	}

}
