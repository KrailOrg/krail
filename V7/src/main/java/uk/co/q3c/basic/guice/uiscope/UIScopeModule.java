package uk.co.q3c.basic.guice.uiscope;

import uk.co.q3c.basic.BasicUI;
import uk.co.q3c.basic.SideBarUI;
import uk.co.q3c.basic.demo.DemoErrorView;
import uk.co.q3c.basic.view.ErrorView;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;
import com.vaadin.ui.UI;

public class UIScopeModule extends AbstractModule {
	private final UIScope uiScope;

	public UIScopeModule() {
		super();
		uiScope = UIScope.getCurrent();

	}

	@Override
	public void configure() {

		// tell Guice about the scope
		bindScope(UIScoped.class, uiScope);

		// make our scope instance injectable
		bind(UIScope.class).annotatedWith(Names.named("UIScope")).toInstance(uiScope);

		MapBinder<String, UI> mapbinder = MapBinder.newMapBinder(binder(), String.class, UI.class);
		mapbinder.addBinding(BasicUI.class.getName()).to(BasicUI.class);
		mapbinder.addBinding(SideBarUI.class.getName()).to(SideBarUI.class);

		// will be used if a view mapping is not found
		bind(ErrorView.class).to(DemoErrorView.class);
	}

	public UIScope getUiScope() {
		return uiScope;
	}

}