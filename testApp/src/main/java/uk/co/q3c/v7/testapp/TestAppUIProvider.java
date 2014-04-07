package uk.co.q3c.v7.testapp;

import uk.co.q3c.v7.base.ui.ScopedUIProvider;

import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.ui.UI;

public class TestAppUIProvider extends ScopedUIProvider {

	@Override
	public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
		return TestAppUI.class;
	}

}
