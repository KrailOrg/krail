package uk.co.q3c.v7.base.shiro;

import java.io.Serializable;

import com.google.inject.Inject;

import uk.co.q3c.v7.i18n.DescriptionKey;
import uk.co.q3c.v7.i18n.LabelKey;
import uk.co.q3c.v7.i18n.Notifier;

import com.vaadin.ui.Notification;

public class DefaultUnauthorizedExceptionHandler implements UnauthorizedExceptionHandler, Serializable {

	private final Notifier notifier;

	@Inject
	protected DefaultUnauthorizedExceptionHandler(Notifier notifier) {
		super();
		this.notifier = notifier;
	}

	@Override
	public void invoke() {
		notifier.notify(LabelKey.Authorisation, DescriptionKey.No_Permission, Notification.Type.ERROR_MESSAGE);
	}
}
