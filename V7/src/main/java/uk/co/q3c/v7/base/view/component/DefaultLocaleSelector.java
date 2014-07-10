/*
 * Copyright (C) 2014 David Sowerby
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
package uk.co.q3c.v7.base.view.component;

import com.google.inject.Inject;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.q3c.util.ID;
import uk.co.q3c.v7.base.user.notify.UserNotifier;
import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.DescriptionKey;
import uk.co.q3c.v7.i18n.I18N;
import uk.co.q3c.v7.i18n.MessageKey;

import java.util.Locale;

@I18N
public class DefaultLocaleSelector implements LocaleSelector, ValueChangeListener {
    private static Logger log = LoggerFactory.getLogger(DefaultLocaleSelector.class);
    private final LocaleContainer container;
    private final CurrentLocale currentLocale;
    private final UserNotifier userNotifier;
    @I18N(description = DescriptionKey.Select_from_available_languages)
    private ComboBox combo;
    private boolean respondToLocaleChange = true;
    private boolean initialising;

    @Inject
    protected DefaultLocaleSelector(CurrentLocale currentLocale, LocaleContainer container, UserNotifier userNotifier) {
        super();
        this.container = container;
        this.currentLocale = currentLocale;
        this.userNotifier = userNotifier;
        buildUI();
    }

    private void buildUI() {
        combo = new ComboBox(null, container);
        combo.setImmediate(true);
        combo.setNullSelectionAllowed(false);

        combo.setWidth(200 + "px");

        combo.setId(ID.getId(this, combo));
        combo.setContainerDataSource(container);

        // Sets the combobox to show a certain property as the item caption
        combo.setItemCaptionPropertyId(LocaleContainer.PropertyName.NAME);
        combo.setItemCaptionMode(ItemCaptionMode.PROPERTY);

        // Sets the icon to use with the items
        combo.setItemIconPropertyId(LocaleContainer.PropertyName.FLAG);

        combo.addValueChangeListener(this);
        initialising = true;
        localeChanged(currentLocale.getLocale());
        initialising = false;
    }

    @Override
    public void localeChanged(Locale locale) {
        if (respondToLocaleChange) {
            log.debug("responding in change to new locale of {}", locale.getDisplayName());
            combo.setValue(locale.toLanguageTag());
        } else {
            log.debug("response to locale change is disabled");
        }
    }

    @Override
    public Component getComponent() {
        return combo;
    }

    /**
     * Sets {@link CurrentLocale#setLocale(Locale)} to new value. Inhibits {@link #localeChanged(Locale)} from
     * responding to this change.
     */
    @Override
    public void valueChange(ValueChangeEvent event) {
        if (!initialising) {
            Locale newLocale = selectedLocale();
            // only process change if locale has really changed
            if (newLocale != null && newLocale != currentLocale.getLocale()) {
                log.debug("locale selection changed");
                respondToLocaleChange = false;
                currentLocale.setLocale(newLocale);
                userNotifier.notifyInformation(MessageKey.LocaleChange, newLocale.getDisplayName(newLocale));
                respondToLocaleChange = true;
            }
        } else {
            log.debug("Initialising, combo value change ignored");
        }
    }

    @Override
    public Locale selectedLocale() {
        String selectedId = (String) combo.getValue();
        Locale newLocale = Locale.forLanguageTag(selectedId);
        return newLocale;
    }

    public boolean isRespondToLocaleChange() {
        return respondToLocaleChange;
    }

    public void setRespondToLocaleChange(boolean respondToLocaleChange) {
        this.respondToLocaleChange = respondToLocaleChange;
    }
}
