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
package uk.q3c.krail.core.user;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import uk.q3c.krail.core.user.notify.*;
import uk.q3c.krail.core.user.profile.DefaultUserHierarchy;
import uk.q3c.krail.core.user.profile.SimpleUserHierarchy;
import uk.q3c.krail.core.user.profile.UserHierarchy;
import uk.q3c.krail.core.user.status.DefaultUserStatus;
import uk.q3c.krail.core.user.status.UserStatus;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.LabelKey;

public class UserModule extends AbstractModule {
    @SuppressWarnings("rawtypes")
    @Override
    protected void configure() {

        MapBinder<I18NKey, ErrorNotification> errorNotificationBinder = MapBinder.newMapBinder(binder(), I18NKey
                .class, ErrorNotification.class);
        MapBinder<I18NKey, WarningNotification> warningNotificationBinder = MapBinder.newMapBinder(binder(), I18NKey
                .class, WarningNotification.class);
        MapBinder<I18NKey, InformationNotification> informationNotificationBinder = MapBinder.newMapBinder(binder(),
                I18NKey.class, InformationNotification.class);
        bindUserNotifier();
        bindErrorNotifications(errorNotificationBinder);
        bindWarningNotifications(warningNotificationBinder);
        bindInformationNotifications(informationNotificationBinder);
        bindUserStatus();
        bindUserHierarchies();

    }

    /**
     * Bind you own {@link UserHierarchy} implementations, using binding annotations to identify them.  At least one
     * must marked as default by using the {@link DefaultUserHierarchy} annotation.
     */
    protected void bindUserHierarchies() {
        bind(UserHierarchy.class).annotatedWith(DefaultUserHierarchy.class)
                                 .to(SimpleUserHierarchy.class);
    }

    /**
     * Override this method if you want to remove any of the notification implementations. If you want to add
     * notifications, create your own module with a MapBinder instance of the same type signature, and Guice will
     * combine the mapbinders
     *
     * @param errorNotificationBinder the binder used to collect the bindings together
     */
    @SuppressWarnings("rawtypes")
    protected void bindErrorNotifications(MapBinder<I18NKey, ErrorNotification> errorNotificationBinder) {
        errorNotificationBinder.addBinding(LabelKey.Splash)
                               .to(VaadinErrorNotification.class);
        errorNotificationBinder.addBinding(LabelKey.Message_Bar)
                               .to(MessageBarErrorNotification.class);
    }

    /**
     * Override this method if you want to remove any of the notification implementations. If you want to add
     * notifications, create your own module with a MapBinder instance of the same type signature, and Guice will
     * combine the mapbinders
     *
     * @param warningNotificationBinder the binder used to collect the bindings together
     */
    @SuppressWarnings("rawtypes")
    protected void bindWarningNotifications(MapBinder<I18NKey, WarningNotification> warningNotificationBinder) {
        warningNotificationBinder.addBinding(LabelKey.Splash)
                                 .to(VaadinWarningNotification.class);
        warningNotificationBinder.addBinding(LabelKey.Message_Bar)
                                 .to(MessageBarWarningNotification.class);
    }

    /**
     * Override this method if you want to remove any of the notification implementations. If you want to add
     * notifications, create your own module with a MapBinder instance of the same type signature, and Guice will
     * combine the mapbinders
     *
     * @param informationNotificationBinder the binder used to collect the bindings together
     */
    @SuppressWarnings("rawtypes")
    protected void bindInformationNotifications(MapBinder<I18NKey, InformationNotification>
                                                            informationNotificationBinder) {
        informationNotificationBinder.addBinding(LabelKey.Splash)
                                     .to(VaadinInformationNotification.class);
        informationNotificationBinder.addBinding(LabelKey.Message_Bar)
                                     .to(MessageBarInformationNotification.class);
    }

    /**
     * Override this method to bind your own UserNotifier implementation
     */
    protected void bindUserNotifier() {
        bind(UserNotifier.class).to(DefaultUserNotifier.class);
    }

    protected void bindUserStatus() {
        bind(UserStatus.class).to(DefaultUserStatus.class);
    }


}
