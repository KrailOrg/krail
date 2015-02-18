/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.q3c.krail.core.view.component;

import com.google.inject.Inject;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.user.opt.Option;
import uk.q3c.krail.core.user.opt.OptionContext;
import uk.q3c.krail.i18n.LabelKey;
import uk.q3c.krail.i18n.SupportedLocales;
import uk.q3c.util.ResourceUtils;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Locale;
import java.util.Set;

public class LocaleContainer extends IndexedContainer implements OptionContext {


    public enum PropertyName {
        NAME, FLAG
    }

    private static Logger log = LoggerFactory.getLogger(LocaleContainer.class);
    private final Set<Locale> supportedLocales;
    private final Option option;

    @Inject
    protected LocaleContainer(@SupportedLocales Set<Locale> supportedLocales, Option option) {
        super();
        this.supportedLocales = supportedLocales;
        this.option = option;
        option.init(this);
        fillContainer();
    }

    /**
     * Loads the container with text from {@link Locale#getDisplayName(Locale)}, and an icon for the country flag if
     * there is one. If there is no image flag, the flag property is left as null.  The result is that the combo
     * contains an entry for a country in the language of that country (for example Germany is always Deutsch
     * (Deutschland), regardless of the current locale).  This means the user looking for a language will see it in its
     * most familiar form.
     */
    @SuppressWarnings("unchecked")
    private void fillContainer() {

        addContainerProperty(PropertyName.NAME, String.class, null);
        addContainerProperty(PropertyName.FLAG, Resource.class, null);

        File webInfDir = ResourceUtils.configurationDirectory();
        File iconsDir = new File(webInfDir, "icons");
        File flagsDir = new File(iconsDir, "flags_iso");

        File flagSizedDir = new File(flagsDir, getOptionFlagSize().toString());

        for (Locale supportedLocale : supportedLocales) {
            String id = supportedLocale.toLanguageTag();
            log.debug("Added supported locale with id: '{}'", id);
            Item item = addItem(id);
            item.getItemProperty(PropertyName.NAME)
                .setValue(supportedLocale.getDisplayName(supportedLocale));

            // if the directory is missing don't bother with file
            if (flagSizedDir.exists()) {
                String filename = supportedLocale.getCountry()
                                                 .toLowerCase() + ".png";
                File file = new File(flagSizedDir, filename);
                if (file.exists()) {
                    FileResource resource = new FileResource(file);
                    item.getItemProperty(PropertyName.FLAG)
                        .setValue(resource);
                } else {
                    log.warn("File {} for locale flag does not exist.", file.getAbsolutePath());
                }

            } else {
                log.warn("{} directory for flags does not exist.", flagSizedDir.getAbsolutePath());
            }
        }

        sort(new Object[]{PropertyName.NAME}, new boolean[]{true});
    }

    public Integer getOptionFlagSize() {
        return option.get(32, LabelKey.Locale_Flag_Size);
    }

    @Nonnull
    @Override
    public Option getOption() {
        return option;
    }
}
