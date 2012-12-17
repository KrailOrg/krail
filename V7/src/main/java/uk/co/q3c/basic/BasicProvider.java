package uk.co.q3c.basic;

import java.util.Map;

import javax.inject.Inject;

import uk.co.q3c.basic.guice.uiscope.UIKeyProvider;

import com.google.inject.Injector;
import com.google.inject.Provider;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.ui.UI;

public class BasicProvider extends ScopedUIProvider {

	private final UISelectCounter selectCounter;

	@Inject
	protected BasicProvider(Injector injector, Map<String, Provider<UI>> uiProMap, UIKeyProvider mainwindowKeyProvider,
			UISelectCounter selectCounter) {
		super(injector, uiProMap, mainwindowKeyProvider);
		this.selectCounter = selectCounter;
	}

	/**
	 * The logic here is to select a UI to display. In this simple case we are using a singleton {@link UISelectCounter}
	 * to alternate between {@link BasicUI} and {@link SideBarUI}
	 * 
	 * @see com.vaadin.server.UIProvider#getUIClass(com.vaadin.server.UIClassSelectionEvent)
	 */
	@Override
	public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
		int uiSelect = selectCounter.getCounter() % 2;

		switch (uiSelect) {
		case 0:
			return BasicUI.class;
		case 1:
			return SideBarUI.class;
		default:
			return BasicUI.class;
		}
	}

	/**
	 * Used by the demo just to increment the UI Counter, after invoking super. See
	 * {@link #getUIClass(UIClassSelectionEvent)}
	 * 
	 * @see uk.co.q3c.basic.ScopedUIProvider#createInstance(java.lang.Class)
	 */

	@Override
	public UI createInstance(Class<? extends UI> uiClass) {
		UI ui = super.createInstance(uiClass);
		selectCounter.inc();
		return ui;
	}

}