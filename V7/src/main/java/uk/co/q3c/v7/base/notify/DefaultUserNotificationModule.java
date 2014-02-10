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
package uk.co.q3c.v7.base.notify;

import uk.co.q3c.v7.i18n.I18NKey;
import uk.co.q3c.v7.i18n.LabelKey;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;

public class DefaultUserNotificationModule extends AbstractModule {
	@SuppressWarnings("rawtypes")
	@Override
	protected void configure() {

		MapBinder<I18NKey, ErrorNotification> errorNotificationBinder = MapBinder.newMapBinder(binder(), I18NKey.class,
				ErrorNotification.class);
		MapBinder<I18NKey, WarningNotification> warningNotificationBinder = MapBinder.newMapBinder(binder(),
				I18NKey.class, WarningNotification.class);
		MapBinder<I18NKey, InformationNotification> informationNotificationBinder = MapBinder.newMapBinder(binder(),
				I18NKey.class, InformationNotification.class);
		bindUserNotifier();
		bindErrorNotifications(errorNotificationBinder);
		bindWarningNotifications(warningNotificationBinder);
		bindInformationNotifications(informationNotificationBinder);
	}

	/**
	 * Override this method if you want to remove any of the notification implementations. If you want to add
	 * notifications, create your own module with a MapBinder instance of the same type signature, and Guice will
	 * combine the mapbinders
	 * 
	 * @param informationNotificationBinder
	 */
	@SuppressWarnings("rawtypes")
	protected void bindErrorNotifications(MapBinder<I18NKey, ErrorNotification> errorNotificationBinder) {
		errorNotificationBinder.addBinding(LabelKey.Splash).to(VaadinErrorNotification.class);
		errorNotificationBinder.addBinding(LabelKey.Message_Bar).to(MessageBarErrorNotification.class);
	}

	/**
	 * Override this method if you want to remove any of the notification implementations. If you want to add
	 * notifications, create your own module with a MapBinder instance of the same type signature, and Guice will
	 * combine the mapbinders
	 * 
	 * @param informationNotificationBinder
	 */
	@SuppressWarnings("rawtypes")
	protected void bindWarningNotifications(MapBinder<I18NKey, WarningNotification> warningNotificationBinder) {
		warningNotificationBinder.addBinding(LabelKey.Splash).to(VaadinWarningNotification.class);
		warningNotificationBinder.addBinding(LabelKey.Message_Bar).to(MessageBarWarningNotification.class);
	}

	/**
	 * Override this method if you want to remove any of the notification implementations. If you want to add
	 * notifications, create your own module with a MapBinder instance of the same type signature, and Guice will
	 * combine the mapbinders
	 * 
	 * @param informationNotificationBinder
	 */
	@SuppressWarnings("rawtypes")
	protected void bindInformationNotifications(
			MapBinder<I18NKey, InformationNotification> informationNotificationBinder) {
		informationNotificationBinder.addBinding(LabelKey.Splash).to(VaadinInformationNotification.class);
		informationNotificationBinder.addBinding(LabelKey.Message_Bar).to(MessageBarInformationNotification.class);
	}

	/**
	 * Override this method to bind your own UserNotifier implementation
	 */
	protected void bindUserNotifier() {
		bind(UserNotifier.class).to(DefaultUserNotifier.class);
	}

}
