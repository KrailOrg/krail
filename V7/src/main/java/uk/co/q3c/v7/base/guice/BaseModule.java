package uk.co.q3c.v7.base.guice;

import uk.co.q3c.v7.base.navigate.DefaultV7Navigator;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.navigate.V7View;
import uk.co.q3c.v7.base.view.ErrorView;

import com.google.inject.servlet.ServletModule;

public class BaseModule extends ServletModule {

	@Override
	protected void configureServlets() {
		serve("/*").with(BaseServlet.class);

		// the fallback in case a View is not defined
		bind(V7View.class).to(ErrorView.class);

		bind(V7Navigator.class).to(DefaultV7Navigator.class);

	}
}