package uk.co.q3c.v7.demo;

import uk.co.q3c.v7.base.ui.ScopedUIProvider;

import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.ui.UI;

public class DemoUIProvider extends ScopedUIProvider {

	@Override
	public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
		return DemoUI.class;
	}

}
