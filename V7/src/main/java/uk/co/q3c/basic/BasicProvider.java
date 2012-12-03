package uk.co.q3c.basic;

import javax.inject.Inject;

import uk.co.q3c.basic.guice.uiscope.UIKeyProvider;

import com.google.inject.Injector;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.ui.UI;

public class BasicProvider extends ScopedUIProvider {

	private int uiSelect = 0;

	@Inject
	protected BasicProvider(Injector injector, UIKeyProvider mainwindowKeyProvider) {
		super(injector, mainwindowKeyProvider);
	}

	@Override
	public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
		switch (uiSelect) {
		case 0:
			return BasicUI.class;
		case 1:
			return SideBarUI.class;
		default:
			return BasicUI.class;
		}

	}

	public int getUiSelect() {
		return uiSelect;
	}

	public void setUiSelect(int uiSelect) {
		this.uiSelect = uiSelect;
	}

}