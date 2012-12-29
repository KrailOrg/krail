package uk.co.q3c.v7.base.guice.threadscope;

import uk.co.q3c.v7.base.guice.threadscope.ThreadCache.Cache;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;

public class ThreadScope implements Scope {
	private final ThreadCache thread;

	ThreadScope(ThreadCache thread) {
		this.thread = thread;
	}

	/**
	 * @see com.google.inject.Scope#scope(com.google.inject.Key, com.google.inject.Provider)
	 */
	@Override
	public <T> Provider<T> scope(final Key<T> key, final Provider<T> creator) {
		return new Provider<T>() {
			@Override
			public T get() {
				Cache cache = thread.getCache();
				T value = cache.get(key);
				if (value == null) {
					value = creator.get();
					cache.add(key, value);
				}
				return value;
			}
		};
	}
}