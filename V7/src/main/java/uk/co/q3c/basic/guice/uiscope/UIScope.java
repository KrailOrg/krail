package uk.co.q3c.basic.guice.uiscope;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.basic.ScopedUI;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

public class UIScope implements Scope {
	private static Logger log = LoggerFactory.getLogger(UIScope.class);

	private final Map<UIKey, Map<Key<?>, Object>> values = new HashMap<UIKey, Map<Key<?>, Object>>();

	@Override
	public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
		return new Provider<T>() {
			@Override
			public T get() {
				// get the scope cache for the current UI
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
						key);
				return current;
			}
		};
	}

	private <T> Map<Key<?>, Object> getScopedObjectMap() {
		UI ui = UI.getCurrent();
		UIKey instanceKey;
		// if ui is null, it is because we have arrived here to construct something for injection
		// into the UI constructor - in other words, UI is not yet constructed. That means there is no key to reference.
		// Currently this is not allowed, and the solution is to only use field or method injection for anything which
		// has UI scope.
		if (ui == null) {
			instanceKey = CurrentInstance.get(UIKey.class);
		} else {
			instanceKey = ((ScopedUI) ui).getInstanceKey();
		}

		// return an existing cache instance
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

	public boolean cacheHasEntryFor(UIKey uiKey) {
		return values.containsKey(uiKey);
	}

	public boolean cacheHasEntryFor(ScopedUI ui) {
		return cacheHasEntryFor(ui.getInstanceKey());
	}

	public void release(ScopedUI scopedUI) {
		values.remove(scopedUI);
	}

}