package uk.co.q3c.v7.base.shiro;

import java.io.Serializable;

import javax.inject.Inject;

import uk.co.q3c.v7.i18n.DescriptionKey;
import uk.co.q3c.v7.i18n.LabelKey;
import uk.co.q3c.v7.i18n.Notifier;

import com.vaadin.ui.Notification;

public class DefaultUnauthenticatedExceptionHandler implements UnauthenticatedExceptionHandler, Serializable {

	private final Notifier notifier;

	@Inject
	protected DefaultUnauthenticatedExceptionHandler(Notifier notifier) {
		super();
		this.notifier = notifier;
	}

	@Override
	public void invoke() {
		notifier.notify(LabelKey.Authentication, DescriptionKey.You_have_not_logged_in, Notification.Type.ERROR_MESSAGE);
	}

}
