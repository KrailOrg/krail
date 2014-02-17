package uk.co.q3c.v7.base.shiro;

import java.io.Serializable;

import uk.co.q3c.v7.base.notify.UserNotifier;
import uk.co.q3c.v7.i18n.DescriptionKey;

import com.google.inject.Inject;
import com.vaadin.ui.Notification;

public class DefaultUnauthenticatedExceptionHandler implements UnauthenticatedExceptionHandler, Serializable {

	private final UserNotifier notifier;

	@Inject
	protected DefaultUnauthenticatedExceptionHandler(UserNotifier notifier) {
		super();
		this.notifier = notifier;
	}

	@Override
	public void invoke() {
		notifier.notifyError(DescriptionKey.You_have_not_logged_in, Notification.Type.ERROR_MESSAGE);
	}

}
