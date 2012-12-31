package uk.co.q3c.v7.base.guice;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinServlet;

@Singleton
public class BaseServlet extends VaadinServlet implements SessionInitListener {

	/**
	 * Cannot use constructor injection. Container expects servlet to have no-arg public constructor
	 */
	@Inject
	private UIProvider basicProvider;

	@Override
	protected void servletInitialized() {
		getService().addSessionInitListener(this);
	}

	@Override
	public void sessionInit(SessionInitEvent event) throws ServiceException {
		event.getSession().addUIProvider(basicProvider);
	}

}