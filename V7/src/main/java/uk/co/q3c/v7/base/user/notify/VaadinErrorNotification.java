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
package uk.co.q3c.v7.base.user.notify;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

/**
 * Provides an error notification to a user using the Vaadin provided 'Splash' window
 * 
 * @author David Sowerby
 * 
 */
public class VaadinErrorNotification implements ErrorNotification {
	@Override
	public void message(String message) {
		Notification.show(message, Type.ERROR_MESSAGE);
	}
}
