package com.example.v7demo;

import java.util.Map;

import javax.inject.Inject;

import uk.co.q3c.v7.base.guice.uiscope.UIKeyProvider;
import uk.co.q3c.v7.base.ui.ScopedUIProvider;

import com.google.inject.Injector;
import com.google.inject.Provider;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.ui.UI;

public class DemoUIProvider extends ScopedUIProvider {

	@Inject
	protected DemoUIProvider(Injector injector,
			Map<String, Provider<UI>> uiProMap, UIKeyProvider uiKeyProvider) {
		super(injector, uiProMap, uiKeyProvider);
	}

	@Override
	public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
		return DemoUI.class;
	}

}
