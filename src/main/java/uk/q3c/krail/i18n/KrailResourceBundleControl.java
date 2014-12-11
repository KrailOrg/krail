package uk.q3c.krail.i18n;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by David Sowerby on 08/12/14.
 */
public class KrailResourceBundleControl extends ResourceBundle.Control {

    /**
     * Makes this method public so it can be used by {@link DefaultPatternSource}
     *
     * @param baseName
     * @param locale
     *
     * @return
     */
    @Override
    public List<Locale> getCandidateLocales(String baseName, Locale locale) {
        return super.getCandidateLocales(baseName, locale);
    }
}
