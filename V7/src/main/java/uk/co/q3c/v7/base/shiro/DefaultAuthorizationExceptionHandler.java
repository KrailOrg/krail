package uk.co.q3c.v7.base.shiro;

import java.io.Serializable;

import org.apache.shiro.authz.AuthorizationException;

import com.vaadin.server.Page;
import com.vaadin.ui.Notification;

public class DefaultAuthorizationExceptionHandler implements AuthorizationExceptionHandler, Serializable {
	// TODO i18N
	@Override
	public boolean invoke(AuthorizationException exception) {
		Notification n = new Notification("Authorization", "Go away, you are not allowed to do that",
				Notification.TYPE_WARNING_MESSAGE, false);
		n.show(Page.getCurrent());
		return true;
	}

}
