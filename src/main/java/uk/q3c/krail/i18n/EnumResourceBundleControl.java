package uk.q3c.krail.i18n;

import com.google.inject.Inject;

import java.io.IOException;
import java.util.*;

/**
 * Used with {@link ResourceBundle} to make the selection of I18N sources configurable through Guice.
 * <p/>
 * Created by David Sowerby on 18/11/14.
 */
public class EnumResourceBundleControl extends ResourceBundle.Control {

    private Map<String, BundleReader> bundleReaders;

    /**
     *
     *
     * @param bundleReaders The readers to to attempt to read a bundle from
     */
    @Inject
    public EnumResourceBundleControl(Map<String, BundleReader> bundleReaders) {
        this.bundleReaders = bundleReaders;
    }

    /**
     * Retrieves a bundle using the format given
     *
     * @param baseName
     * @param locale
     * @param format
     * @param loader
     * @param reload
     *
     * @return
     *
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IOException
     */
    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
            throws IllegalAccessException, InstantiationException, IOException {


        BundleReader reader = bundleReaders.get(format);
        return reader.newBundle(baseName, locale, loader, reload);


    }

    @Override
    public List<String> getFormats(String baseName) {
        return new ArrayList(bundleReaders.keySet());
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
