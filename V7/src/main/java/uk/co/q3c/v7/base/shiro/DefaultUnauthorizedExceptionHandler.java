package uk.co.q3c.v7.base.shiro;

import java.io.Serializable;

import com.vaadin.server.Page;
import com.vaadin.ui.Notification;

public class DefaultUnauthorizedExceptionHandler implements UnauthorizedExceptionHandler, Serializable {
	// TODO i18N
	@Override
	public void invoke() {
		Notification n = new Notification("Authorization", "Go away, you are not allowed to do that",
				Notification.TYPE_WARNING_MESSAGE, false);
		n.show(Page.getCurrent());
	}

}
