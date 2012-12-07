package uk.co.q3c.basic;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UISelectCounter {

	private int counter;

	@Inject
	protected UISelectCounter() {
		super();
	}

	public int getCounter() {
		return counter;
	}

	/**
	 * Just a reminder that singletons should be thread-safe http://code.google.com/p/google-guice/wiki/Scopes
	 */
	public synchronized void inc() {
		counter++;
	}

}
