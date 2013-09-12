package uk.co.q3c.v7.base.ui;

import org.apache.shiro.subject.Subject;

import uk.co.q3c.v7.base.data.V7DefaultConverterFactory;
import uk.co.q3c.v7.base.navigate.DefaultV7Navigator;
import uk.co.q3c.v7.base.navigate.StrictURIFragment;
import uk.co.q3c.v7.base.navigate.StrictUriFragmentFactory;
import uk.co.q3c.v7.base.navigate.URIFragment;
import uk.co.q3c.v7.base.navigate.UriFragmentFactory;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.shiro.LoginStatusMonitor;
import uk.co.q3c.v7.base.shiro.VaadinSecurityContext;
import uk.co.q3c.v7.base.view.component.LoginStatusPanel;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.MapBinder;
import com.vaadin.data.util.converter.ConverterFactory;
import com.vaadin.server.WebBrowser;
import com.vaadin.ui.UI;

public abstract class V7UIModule extends AbstractModule {

	@Override
	protected void configure() {
		MapBinder<String, UI> mapbinder = MapBinder.newMapBinder(binder(), String.class, UI.class);

		bind(WebBrowser.class).toProvider(BrowserProvider.class);
		bind(Subject.class).toProvider(VaadinSecurityContext.class);                
		bindUIProvider();
		addUIBindings(mapbinder);
		bindNavigator();
		bindURIFragmentFactory();
		bindConverterFactory();
		bindLoginStatusMonitor();
	}

	/**
	 * Override to bind your choice of ConverterFactory
	 */
	protected void bindConverterFactory() {
		bind(ConverterFactory.class).to(V7DefaultConverterFactory.class);
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
	protected abstract void bindUIProvider();

	/**
	 * Override to bind your choice of URI fragment
	 */
	protected void bindURIFragmentFactory() {
		bind(UriFragmentFactory.class).to(StrictUriFragmentFactory.class).in(Singleton.class);
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
	}

}
