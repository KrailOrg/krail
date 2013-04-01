package uk.co.q3c.v7.base.ui;

import java.util.Map;

import javax.inject.Inject;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.config.V7ConfigurationException;
import uk.co.q3c.v7.base.guice.BaseModule;
import uk.co.q3c.v7.base.guice.uiscope.UIKeyProvider;
import uk.co.q3c.v7.base.guice.uiscope.UIScopeModule;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.shiro.V7ShiroVaadinModule;
import uk.co.q3c.v7.demo.view.DemoViewModule;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.multibindings.MapBinder;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import com.vaadin.data.util.converter.ConverterFactory;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import fixture.TestIniModule;
import fixture.UITestBase;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ BaseModule.class, UIScopeModule.class, V7ShiroVaadinModule.class, DemoViewModule.class,
		TestIniModule.class })
public class InvalidUITest extends UITestBase {

	static class DummyUIProvider extends ScopedUIProvider {
		@Inject
		protected DummyUIProvider(Injector injector, Map<String, Provider<UI>> uiProMap, UIKeyProvider uiKeyProvider) {
			super(injector, uiProMap, uiKeyProvider);
		}

		@Override
		public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
			return DummyUI.class;
		}

	}

	static class DummyUI extends ScopedUI {
		@Inject
		protected DummyUI(V7Navigator navigator, ErrorHandler errorHandler, ConverterFactory converterFactory) {
			super(navigator, errorHandler, converterFactory);
		}

		@Override
		protected AbstractOrderedLayout screenLayout() {
			return new VerticalLayout();
		}

		@Override
		protected String pageTitle() {
			return "dummy";
		}

	}

	@BeforeClass
	public static void setupClass() {

	}

	@Test(expected = V7ConfigurationException.class)
	public void noVIewDisplayPanel() {

		// given

		// when
		createUI(DummyUI.class);
		// then
		// expected exception

	}

	@ModuleProvider
	protected AbstractModule uiProvider() {
		return new V7UIModule() {

			@Override
			protected void addUIBindings(MapBinder<String, UI> mapbinder) {
				super.addUIBindings(mapbinder);
				mapbinder.addBinding(DummyUI.class.getName()).to(DummyUI.class);
			}

			@Override
			protected void bindUIProvider() {
				bind(UIProvider.class).to(DummyUIProvider.class);
			}

		};
	}

}
