/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */
package uk.q3c.krail.core.user.notify;

import com.google.inject.Inject;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.engio.mbassy.bus.common.PubSubSupport;
import uk.q3c.krail.core.eventbus.BusMessage;
import uk.q3c.krail.core.eventbus.UIBusProvider;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.Translate;

import java.io.Serializable;

@SuppressFBWarnings("SE_BAD_FIELD")
public class DefaultUserNotifier implements UserNotifier, Serializable {
    private static final long serialVersionUID = 1L;
    private PubSubSupport<BusMessage> eventBus;
    private Translate translate;


    @Inject
    protected DefaultUserNotifier(UIBusProvider uiBusProvider, Translate translate) {
        this.eventBus = uiBusProvider.get();
        this.translate = translate;
    }

    @Override
    public void notifyError(I18NKey msg, Object... params) {
        String translatedMessage = translate.from(msg, params);
        ErrorNotificationMessage message = new ErrorNotificationMessage(translatedMessage);
        eventBus.publish(message);
    }

    @Override
    public void notifyWarning(I18NKey msg, Object... params) {
        String translatedMessage = translate.from(msg, params);
        WarningNotificationMessage message = new WarningNotificationMessage(translatedMessage);
        eventBus.publish(message);
    }

    @Override
    public void notifyInformation(I18NKey msg, Object... params) {
        String translatedMessage = translate.from(msg, params);
        InformationNotificationMessage message = new InformationNotificationMessage(translatedMessage);
        eventBus.publish(message);
    }

}
