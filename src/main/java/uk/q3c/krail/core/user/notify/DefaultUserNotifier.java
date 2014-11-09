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
package uk.q3c.krail.core.user.notify;

import com.google.inject.Inject;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.Translate;

import java.io.Serializable;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class DefaultUserNotifier implements UserNotifier, Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<I18NKey, ErrorNotification> errorNotifications;
    private final Map<I18NKey, WarningNotification> warningNotifications;
    private final Map<I18NKey, InformationNotification> informationNotifications;
    private final Translate translate;

    @Inject
    protected DefaultUserNotifier(Map<I18NKey, ErrorNotification> errorNotifications, Map<I18NKey,
            WarningNotification> warningNotifications, Map<I18NKey,
            InformationNotification> informationNotifications, Translate translate) {
        this.errorNotifications = errorNotifications;
        this.warningNotifications = warningNotifications;
        this.informationNotifications = informationNotifications;
        this.translate = translate;

    }

    @Override
    public void notifyError(I18NKey<?> msg, Object... params) {
        String translatedMessage = translate.from(msg, params);
        for (ErrorNotification notification : errorNotifications.values()) {
            notification.message(translatedMessage);
        }
    }

    @Override
    public void notifyWarning(I18NKey<?> msg, Object... params) {
        String translatedMessage = translate.from(msg, params);
        for (WarningNotification notification : warningNotifications.values()) {
            notification.message(translatedMessage);
        }
    }

    @Override
    public void notifyInformation(I18NKey<?> msg, Object... params) {
        String translatedMessage = translate.from(msg, params);
        for (InformationNotification notification : informationNotifications.values()) {
            notification.message(translatedMessage);
        }
    }

}
