package uk.co.q3c.v7.base.ui;

import uk.co.q3c.v7.base.data.V7DefaultConverterFactory;
import uk.co.q3c.v7.base.guice.uiscope.UIScoped;
import uk.co.q3c.v7.base.navigate.DefaultV7Navigator;
import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.view.component.DefaultLoginStatusPanel;
import uk.co.q3c.v7.base.view.component.LoginStatusPanel;
import uk.co.q3c.v7.i18n.I18NKey;
import uk.co.q3c.v7.i18n.LabelKey;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.vaadin.data.util.converter.ConverterFactory;
import com.vaadin.server.WebBrowser;
import com.vaadin.ui.UI;

public abstract class V7UIModule extends AbstractModule {

	@Override
	protected void configure() {
		bindApplicationTitle();
		MapBinder<String, UI> mapbinder = MapBinder.newMapBinder(binder(), String.class, UI.class);

		bind(WebBrowser.class).toProvider(BrowserProvider.class);

		bindUIProvider();
		addUIBindings(mapbinder);
		bindNavigator();
		bindURIHandler();
		bindConverterFactory();
		bindLoginStatusMonitor();

	}

	/**
	 * override this method to provide the I18Nkey which defines your application title (which appears in your browser
	 * tab)
	 */
	protected void bindApplicationTitle() {
		bind(I18NKey.class).annotatedWith(ApplicationTitle.class).toInstance(LabelKey.V7);
	}

	private void bindConverterFactory() {
		bind(ConverterFactory.class).to(V7DefaultConverterFactory.class);
	}

	/**
	 * Override to bind your choice of LoginStatusMonitor
	 */
	protected void bindLoginStatusMonitor() {
		bind(LoginStatusPanel.class).to(DefaultLoginStatusPanel.class);
	}

	/**
	 * Override to bind your ScopedUIProvider implementation
	 */
	protected abstract void bindUIProvider();

	/**
	 * Override to bind your choice of URI handler
	 */
	protected void bindURIHandler() {
		bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
	}

	protected void bindNavigator() {
		bind(V7Navigator.class).to(DefaultV7Navigator.class).in(UIScoped.class);
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
