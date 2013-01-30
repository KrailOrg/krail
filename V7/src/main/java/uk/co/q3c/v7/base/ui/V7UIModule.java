package uk.co.q3c.v7.base.ui;

import uk.co.q3c.v7.base.navigate.DefaultV7Navigator;
import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.shiro.LoginStatusMonitor;
import uk.co.q3c.v7.base.view.components.DefaultHeaderBar;
import uk.co.q3c.v7.base.view.components.HeaderBar;
import uk.co.q3c.v7.base.view.components.LoginStatusPanel;
import uk.co.q3c.v7.demo.ui.DemoUIProvider;
import uk.co.q3c.v7.demo.ui.SideBarUI;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.vaadin.server.UIProvider;
import com.vaadin.server.WebBrowser;
import com.vaadin.ui.UI;

public class V7UIModule extends AbstractModule {

	@Override
	protected void configure() {
		MapBinder<String, UI> mapbinder = MapBinder.newMapBinder(binder(), String.class, UI.class);

		bind(WebBrowser.class);

		bindUIProvider();
		addUIBindings(mapbinder);
		bindNavigator();
		bindHeader();
		bindFooter();
		bindURIHandler();
		bindLoginStatusMonitor();
	}

	/**
	 * Override to bind your choice of LoginStatusMonitor
	 */
	protected void bindLoginStatusMonitor() {
		bind(LoginStatusMonitor.class).to(LoginStatusPanel.class);
	}

	/**
	 * Override to bind your ScopedUIProvider implementation
	 */
	protected void bindUIProvider() {
		bind(UIProvider.class).to(DemoUIProvider.class);
	}

	/**
	 * Override to bind your choice of URI handler
	 */
	protected void bindURIHandler() {
		bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
	}

	/**
	 * Override to bind your choice of Headerbar
	 */
	protected void bindHeader() {
		bind(HeaderBar.class).to(DefaultHeaderBar.class);
	}

	/**
	 * Override to bind your choice of Footer
	 */
	protected void bindFooter() {
		// TODO FooterBar
	}

	protected void bindNavigator() {
		bind(V7Navigator.class).to(DefaultV7Navigator.class);
	}

	/**
	 * Override with your UI bindings
	 * 
	 * @param mapbinder
	 */
	protected void addUIBindings(MapBinder<String, UI> mapbinder) {
		mapbinder.addBinding(BasicUI.class.getName()).to(BasicUI.class);
		mapbinder.addBinding(SideBarUI.class.getName()).to(SideBarUI.class);
	}

}
