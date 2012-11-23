package uk.co.q3c.basic;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;

public class MainWindowScope implements Scope {
	private static Logger log = LoggerFactory.getLogger(MainWindowScope.class);

	private final Map<MainWindowKey, Map<Key<?>, Object>> values = new HashMap<MainWindowKey, Map<Key<?>, Object>>();

	@Override
	public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
		return new Provider<T>() {
			@Override
			public T get() {
				// get the scope cache for the current main window
				Map<Key<?>, Object> scopedObjects = getScopedObjectMap();

				// retrieve an existing instance if possible
				@SuppressWarnings("unchecked")
				T current = (T) scopedObjects.get(key);

				if (current != null) {
					log.debug("returning existing instance of {0}", current.getClass().getSimpleName());
					return current;
				}

				// or create the first instance and cache it
				current = unscoped.get();
				scopedObjects.put(key, current);
				log.debug("new instance of {0} created for main window key: {1}", current.getClass().getSimpleName(),
						new Serializable() {
							@Override
							public String toString() {
								MainWindow mw = (MainWindow) CampusApplication.getCurrentNavigableAppLevelWindow();
								return mw == null ? "" + CampusApplication.getMainWindowKey() : ""
										+ mw.getInstanceKey();
							}
						});
				return current;
			}
		};
	}

	private <T> Map<Key<?>, Object> getScopedObjectMap() {
		MainWindowKey instanceKey = null;

		MainWindow mw = (MainWindow) CampusApplication.getCurrentNavigableAppLevelWindow();
		// if main window is null, it is because we have arrived here to construct something for injection
		// into the MainWindow constructor - in other words, MainWindow is not yet constructed. The instanceKey is
		// transferred from CampusApplication to MainWindow once MainWindow has finished construction, so that it can be
		// identified later
		if (mw == null) {
			instanceKey = CampusApplication.getMainWindowKey();
		} else {
			instanceKey = mw.getInstanceKey();
		}

		// return an existing one
		if (values.containsKey(instanceKey)) {
			Map<Key<?>, Object> scopedObjects = values.get(instanceKey);
			log.debug("scope cache retrieved for key: {0}", instanceKey);
			return scopedObjects;
		}

		// or create one if it does not exist
		log.debug("creating a scope cache for MainWindow with key: {0}", instanceKey);
		HashMap<Key<?>, Object> mwEntry = new HashMap<Key<?>, Object>();
		values.put(instanceKey, mwEntry);
		return mwEntry;

	}

}