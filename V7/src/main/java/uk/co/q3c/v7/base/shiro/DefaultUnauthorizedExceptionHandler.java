package uk.co.q3c.v7.base.shiro;

import java.io.Serializable;

import uk.co.q3c.v7.base.user.notify.UserNotifier;
import uk.co.q3c.v7.i18n.DescriptionKey;

import com.google.inject.Inject;
import com.vaadin.ui.Notification;

public class DefaultUnauthorizedExceptionHandler implements UnauthorizedExceptionHandler, Serializable {

	private final UserNotifier notifier;

	@Inject
	protected DefaultUnauthorizedExceptionHandler(UserNotifier notifier) {
		super();
		this.notifier = notifier;
	}

	@Override
	public void invoke() {
		notifier.notifyError(DescriptionKey.No_Permission, Notification.Type.ERROR_MESSAGE);
	}
}
