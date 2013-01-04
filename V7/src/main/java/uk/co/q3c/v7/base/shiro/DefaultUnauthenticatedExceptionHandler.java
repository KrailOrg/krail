package uk.co.q3c.v7.base.shiro;

import java.io.Serializable;

import com.vaadin.server.Page;
import com.vaadin.ui.Notification;

public class DefaultUnauthenticatedExceptionHandler implements UnauthenticatedExceptionHandler, Serializable {
	// TODO i18N
	@Override
	public void invoke() {
		Notification n = new Notification("Authentication", "You have not logged in", Notification.TYPE_ERROR_MESSAGE,
				false);
		n.show(Page.getCurrent());
	}

}
