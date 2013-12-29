package uk.co.q3c.v7.demo;

import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Provider;

import uk.co.q3c.v7.base.guice.uiscope.UIKeyProvider;
import uk.co.q3c.v7.base.ui.ScopedUIProvider;

import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.ui.UI;

public class DemoUIProvider extends ScopedUIProvider {

	@Inject
	protected DemoUIProvider(Map<String, Provider<UI>> uiProMap, UIKeyProvider uiKeyProvider) {
		super(uiProMap, uiKeyProvider);
	}

	@Override
	public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
		return DemoUI.class;
	}

}
