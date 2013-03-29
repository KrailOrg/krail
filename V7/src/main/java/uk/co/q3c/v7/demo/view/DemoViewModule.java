package uk.co.q3c.v7.demo.view;

import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.view.LoginView;
import uk.co.q3c.v7.base.view.LogoutView;
import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.base.view.V7ViewModule;
import uk.co.q3c.v7.util.A;

import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;

/**
 * This becomes the site map. Simply map a virtual page (see @link {@link URIFragmentHandler}) to the {@link V7View}
 * class which will represent it
 * 
 * @author david
 * 
 */
public class DemoViewModule extends V7ViewModule {

	@Override
	protected void configure() {
		super.configure();

		// some Strings for the demo
		bind(String.class).annotatedWith(Names.named(A.title)).toInstance(
				"Guice Vaadin and Shiro demonstration application");
		bind(String.class).annotatedWith(Names.named(A.version)).toInstance("Vaadin 7.0.0");

	}

	@Override
	protected void bindViews(MapBinder<String, V7View> mapbinder) {
		mapbinder.addBinding("").to(PublicHomeView.class);
		mapbinder.addBinding("public/home").to(PublicHomeView.class);
		mapbinder.addBinding("secure/home").to(SecureHomeView.class);
		mapbinder.addBinding("secure/view1").to(View1.class);
		mapbinder.addBinding("public/view2").to(View2.class);
		mapbinder.addBinding("public/login").to(LoginView.class);
		mapbinder.addBinding("public/logout").to(LogoutView.class);
		mapbinder.addBinding("public/reset-account").to(AccountRequestView.class);
		mapbinder.addBinding("public/unlock-account").to(AccountRequestView.class);
		mapbinder.addBinding("public/refresh-account").to(AccountRequestView.class);
		mapbinder.addBinding("public/request-account").to(AccountRequestView.class);
		mapbinder.addBinding("public/enable-account").to(AccountRequestView.class);
	}

}
