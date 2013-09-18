package uk.co.q3c.v7.testapp.ui;

import java.util.Map;

import javax.inject.Inject;

import uk.co.q3c.v7.base.guice.uiscope.UIKeyProvider;
import uk.co.q3c.v7.base.ui.ScopedUIProvider;
import uk.co.q3c.v7.testapp.TestAppUI;

import com.google.inject.Injector;
import com.google.inject.Provider;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.ui.UI;

public class TestAppUIProvider extends ScopedUIProvider {

	@Inject
	protected TestAppUIProvider(Injector injector,
			Map<String, Provider<UI>> uiProMap, UIKeyProvider uiKeyProvider) {
		super(injector, uiProMap, uiKeyProvider);
	}

	@Override
	public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
		return TestAppUI.class;
	}

}
