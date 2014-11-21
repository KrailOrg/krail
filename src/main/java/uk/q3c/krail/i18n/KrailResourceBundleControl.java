package uk.q3c.krail.i18n;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Used with {@link ResourceBundle} to make the selection of I18N sources configurable through Guice.
 * <p/>
 * Created by David Sowerby on 18/11/14.
 */
public class KrailResourceBundleControl extends ResourceBundle.Control {

    private Map<String, BundleLocator> bundleLocators;

    /**
     * If {@code bundleOrder} is empty, the order is determined by the order of keyset of {@code bundleSources}
     *
     * @param bundleLocators
     * @param bundleOrder
     */
    public KrailResourceBundleControl(Map<String, BundleLocator> bundleLocators) {
        this.bundleLocators = bundleLocators;
    }

    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String tag, ClassLoader loader, boolean reload)
            throws IllegalAccessException, InstantiationException, IOException {


        BundleLocator creator = bundleLocators.get(tag);
        return creator.newBundle(baseName, locale, loader, reload);


    }

    //    needs reload
    //    :
    //
    //
    //    The
    //    default implementation compares
    //    loadTime and
    //    the last
    //    modified time
    //    of the
    //    source data
    //    of the
    //    resource bundle
    //    .
    //    If it
    //    's determined that the source data has been modified since loadTime, true is returned. Otherwise, false is
    //    returned. This implementation assumes that the given format is the same string as its file suffix if it'
    //    s not
    //    one of
    //    the default formats,"java.class"or"java.properties".
}
