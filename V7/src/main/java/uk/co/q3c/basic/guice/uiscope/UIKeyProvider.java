package uk.co.q3c.basic.guice.uiscope;

import javax.inject.Provider;

public class UIKeyProvider implements Provider<UIKey> {
	private static int counter = 0;

	@Override
	public UIKey get() {
		counter++;
		return new UIKey(counter);
	}

}
