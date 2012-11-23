package uk.co.q3c.basic;

import javax.inject.Provider;

public class MainWindowKeyProvider implements Provider<MainWindowKey> {

	@Override
	public MainWindowKey get() {
		return new MainWindowKey();
	}

}
