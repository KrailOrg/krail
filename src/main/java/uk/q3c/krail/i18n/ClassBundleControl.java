package uk.q3c.krail.i18n;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Bundle control to allow loading from classes only, and only the requested locale (that is, it does not look at other candidate locales - that is managed through {@link DefaultPatternSource} instead
 * <p>
 * Created by David Sowerby on 08/12/14.
 */
public class ClassBundleControl extends ResourceBundle.Control {


    @Override
    public List<String> getFormats(String baseName) {
        return ResourceBundle.Control.FORMAT_CLASS;
    }


    @Override
    public List<Locale> getCandidateLocales(String baseName, Locale locale) {
        return Arrays.asList(locale);
    }
}
