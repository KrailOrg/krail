package uk.co.q3c.basic;

import javax.inject.Inject;

import uk.co.q3c.basic.guice.uiscope.UIKeyProvider;

import com.google.inject.Injector;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.ui.UI;

public class BasicProvider extends ScopedUIProvider {

	@Inject
	protected BasicProvider(Injector injector,
			UIKeyProvider mainwindowKeyProvider) {
		super(injector, mainwindowKeyProvider);
	}

	@Override
	public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
		return BasicUI.class;
	}

}