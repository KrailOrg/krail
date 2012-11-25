package uk.co.q3c.basic;

import javax.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.basic.guice.uiscope.UIKey;
import uk.co.q3c.basic.guice.uiscope.UIKeyProvider;

import com.google.inject.Inject;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

public class BasicProvider extends UIProvider {
	private static Logger log = LoggerFactory.getLogger(BasicProvider.class);
	private final Provider<BasicUI> basicUiPro;
	private final UIKeyProvider mainwindowKeyProvider;

	@Inject
	protected BasicProvider(Provider<BasicUI> basicUiPro, UIKeyProvider mainwindowKeyProvider) {
		super();
		this.basicUiPro = basicUiPro;
		this.mainwindowKeyProvider = mainwindowKeyProvider;
	}

	@Override
	public UI createInstance(UICreateEvent event) {
		UIKey instanceKey = mainwindowKeyProvider.get();
		CurrentInstance.set(UIKey.class, instanceKey);
		log.debug("returning instance of " + BasicUI.class.getName());
		BasicUI ui = basicUiPro.get();
		ui.setInstanceKey(instanceKey);
		return ui;
	}

	@Override
	public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
		return BasicUI.class;
	}

}