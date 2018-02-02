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
package uk.q3c.krail.core.view.component;

import com.google.inject.Inject;
import com.vaadin.data.HasValue;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.eventbus.SessionBus;
import uk.q3c.krail.core.eventbus.UIBus;
import uk.q3c.krail.core.i18n.Description;
import uk.q3c.krail.core.i18n.DescriptionKey;
import uk.q3c.krail.core.i18n.I18N;
import uk.q3c.krail.core.i18n.MessageKey;
import uk.q3c.krail.core.user.notify.UserNotifier;
import uk.q3c.krail.eventbus.GlobalMessageBus;
import uk.q3c.krail.eventbus.SubscribeTo;
import uk.q3c.krail.i18n.CurrentLocale;
import uk.q3c.krail.i18n.LocaleChangeBusMessage;

import java.util.Locale;

@I18N
@Listener
@SubscribeTo({UIBus.class, SessionBus.class, GlobalMessageBus.class})
public class DefaultLocaleSelector implements LocaleSelector, HasValue.ValueChangeListener<Locale> {
    private static Logger log = LoggerFactory.getLogger(DefaultLocaleSelector.class);
    private final LocaleContainer container;
    private final CurrentLocale currentLocale;
    private final UserNotifier userNotifier;
    @Description(description = DescriptionKey.Select_from_available_languages)
    private ComboBox<Locale> combo;
    private boolean loaded = false;

    private boolean fireListeners;
    private boolean inhibitMessage;

    public ComboBox<Locale> getCombo() {
        return combo;
    }

    public boolean isLoaded() {
        return loaded;
    }

    @Inject
    protected DefaultLocaleSelector(CurrentLocale currentLocale, LocaleContainer container, UserNotifier userNotifier) {
        super();
        this.container = container;
        this.currentLocale = currentLocale;
        this.userNotifier = userNotifier;
        buildUI();
    }

    private void buildUI() {
        combo = new ComboBox<>();
        combo.setEmptySelectionAllowed(false);
        combo.setWidth(250 + "px");
    }

    /**
     * We cannot set up the combox with data until the UI has finished building
     *
     * @param message not used
     */
    @Handler
    public void afterViewChange(AfterViewChangeBusMessage message) {
        log.debug("Received AfterViewChangeBusMessage, completing set up for Combo");
        combo.setItemCaptionGenerator(container);
        combo.setItemIconGenerator(container.getIconGenerator());
        combo.setDataProvider(container.getDataProvider());
        log.debug("Setting Locale selector to {}", currentLocale.getLocale());
        combo.setValue(currentLocale.getLocale());
        combo.addValueChangeListener(this);
        loaded = true;
        log.debug("Combo set up complete");
    }

    @Handler
    public void localeChanged(LocaleChangeBusMessage busMessage) {
        if (busMessage.getChangeSource() == this) {
            log.debug("response to locale change is disabled");
        } else {
            log.debug("responding in change to new locale of {}", busMessage.getNewLocale().getDisplayName());
            inhibitMessage = true;
            combo.setValue(busMessage.getNewLocale());
            inhibitMessage = false;

        }
    }

    @Override
    public Component getComponent() {
        return combo;
    }


    /**
     * Sets {@link CurrentLocale#setLocale(Locale)} to new value.
     */
    @Override
    public Locale selectedLocale() {
        return combo.getValue();
    }


    @Override
    public void valueChange(HasValue.ValueChangeEvent<Locale> event) {
        if (!fireListeners) {
            Locale newLocale = selectedLocale();
            // only process change if locale has really changed
            if (newLocale != null && newLocale != currentLocale.getLocale()) {
                log.debug("locale selection changed");
                currentLocale.setLocale(newLocale);
                if (!inhibitMessage) {
                    userNotifier.notifyInformation(MessageKey.Locale_Change, newLocale.getDisplayName(newLocale));
                }
            }
        } else {
            log.debug("Initialising, combo value change ignored");
        }
    }
}
