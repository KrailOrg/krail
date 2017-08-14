package uk.q3c.krail.i18n.locale;


import uk.q3c.krail.i18n.CurrentLocale;

import java.util.Locale;

/**
 * Created by David Sowerby on 01 Aug 2017
 */
public class DefaultCurrentLocale implements CurrentLocale {
    private Locale locale = Locale.getDefault();

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public void setLocale(Locale locale) {
        setLocale(locale, true);
    }

    /**
     * There are no listeners to fire in this basic implementation
     */
    @Override
    public void setLocale(Locale locale, boolean fireListeners) {
        this.locale = locale;
    }


    /**
     * simply reads the current Locale from the OS
     */
    @Override
    public void readFromEnvironment() {
        locale = Locale.getDefault();
    }


}