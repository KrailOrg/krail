/*
 * Copyright (C) 2013 David Sowerby
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.co.q3c.v7.i18n;

import javax.inject.Inject;

import com.vaadin.server.Page;
import com.vaadin.ui.Notification;

/**
 * Wraps the {@link Notification} class to ease the use of {@link I18NKey} instances to generate the caption and message
 * 
 * @author David Sowerby 4 Aug 2013
 * 
 */
public class Notifier {
	private final I18NValue i18nValue;

	@Inject
	protected Notifier(I18NValue i18nValue) {
		super();
		this.i18nValue = i18nValue;
	}
	
	public void notify(I18NKey<?> captionKey, I18NKey<?> messageKey, Notification.Type messageType,
			Object... messageArguments) {
		String msg = i18nValue.message(messageKey, messageArguments);
		String caption = i18nValue.message(captionKey);
		Notification n = new Notification(caption, msg, messageType, false);
		n.show(Page.getCurrent());
	}

	/**
	 * Defaults the message type to Notification.Type.HUMANIZED_MESSAGE
	 * 
	 * @param captionKey
	 * @param messageKey
	 * @param messageArguments
	 */
	public void notify(I18NKey<?> captionKey, I18NKey<?> messageKey, Object... messageArguments) {
		notify(captionKey, messageKey, Notification.Type.HUMANIZED_MESSAGE, messageArguments);
	}

}
