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
package uk.q3c.krail.testutil.i18n;

import com.google.inject.Inject;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.engio.mbassy.bus.common.PubSubSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.eventbus.SessionBus;
import uk.q3c.krail.eventbus.BusMessage;
import uk.q3c.krail.i18n.CurrentLocale;
import uk.q3c.krail.i18n.LocaleChangeBusMessage;

import java.util.Locale;

/**
 * For testing all that is usually needed is to return a current locale and fire listeners on a change.  This class
 * gets used by so many things it makes test setup a pain if the full version is used
 */
public class MockCurrentLocale implements CurrentLocale {
    private static Logger log = LoggerFactory.getLogger(MockCurrentLocale.class);
    @Inject
    @SessionBus
    private PubSubSupport<BusMessage> eventBus;
    private Locale locale;

    @SuppressFBWarnings("PCOA_PARTIALLY_CONSTRUCTED_OBJECT_ACCESS")
    @Inject
    public MockCurrentLocale() {
        super();
        readFromEnvironment();
    }

    /**
     * Sets up the locale from the environment (typically browser locale and user option settings)
     */
    @Override
    public void readFromEnvironment() {
        setLocale(Locale.UK, false);
    }

    @Override
    public void setLocale(Locale locale, boolean fireListeners) {


        if (locale != this.locale) {
            this.locale = locale;
            //                Locale.setDefault(locale);
            log.debug("CurrentLocale set to {}", locale);
            if (fireListeners) {
                log.debug("publish locale change");
                eventBus.publish(new LocaleChangeBusMessage(this, locale));
            }
        }

    }


    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public void setLocale(Locale locale) {
        setLocale(locale, true);
    }


}
