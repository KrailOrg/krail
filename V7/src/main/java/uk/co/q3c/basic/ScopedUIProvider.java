package uk.co.q3c.basic;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.basic.guice.navigate.ScopedUI;
import uk.co.q3c.basic.guice.uiscope.UIKey;
import uk.co.q3c.basic.guice.uiscope.UIKeyProvider;
import uk.co.q3c.basic.guice.uiscope.UIScope;
import uk.co.q3c.basic.guice.uiscope.UIScoped;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

/**
 * A Vaadin UI provider which supports the use of Guice scoped UI (see {@link UIScoped}). If you do not need UIScope,
 * then just extend from UIProvider directly
 * 
 * Subclasses should implement getUIClass(UIClassSelectionEvent event) to provide logic for selecting the UI class.
 * 
 * @author David Sowerby
 * 
 */
public abstract class ScopedUIProvider extends UIProvider {
	private static Logger log = LoggerFactory.getLogger(ScopedUIProvider.class);
	private final UIKeyProvider uiKeyProvider;
	private final Map<String, Provider<UI>> uiProMap;
	private final Injector injector;

	@Inject
	protected ScopedUIProvider(Injector injector, Map<String, Provider<UI>> uiProMap, UIKeyProvider uiKeyProvider) {
		super();
		this.uiKeyProvider = uiKeyProvider;
		this.uiProMap = uiProMap;
		this.injector = injector;
	}

	@Override
	public UI createInstance(UICreateEvent event) {
		return createInstance(event.getUIClass());

	}

	public UI createInstance(Class<? extends UI> uiClass) {
		UIKey uiKey = uiKeyProvider.get();
		// hold the key while UI is created
		CurrentInstance.set(UIKey.class, uiKey);
		// and set up the scope
		UIScope.getCurrent().startScope(uiKey);

		// create the UI
		Provider<UI> uiProvider = uiProMap.get(uiClass.getName());
		ScopedUI ui = (ScopedUI) uiProvider.get();
		ui.setInstanceKey(uiKey);
		log.debug("returning instance of " + ui.getClass().getName() + " with key " + uiKey);
		return ui;
	}

}
