package uk.q3c.krail.i18n;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * Used with {@link ResourceBundle} to make the selection of I18N sources configurable through Guice.
 * <p>
 * Created by David Sowerby on 18/11/14.
 */
public class EnumResourceBundleControl extends ResourceBundle.Control {
    private static Logger log = LoggerFactory.getLogger(EnumResourceBundleControl.class);
    private Map<String, BundleReader> bundleReaders;
    private Class<? extends Enum> enumKeyClass;
    private String format;

    /**
     * @param bundleReaders
     *         The readers to to attempt to read a bundle from
     * @param bundleSourceOrderDefault
     *         The default sort order for bundles sources specified by the {@link I18NModule} or its sub-class
     * @param userOption
     *         User options, properties used are specified by {@link #OptionProp}
     */
    @Inject
    public EnumResourceBundleControl(Map<String, BundleReader> bundleReaders) {
        this.bundleReaders = bundleReaders;
    }

    public void setSource(String format) {
        this.format = format;
    }

    /**
     * Retrieves a bundle using the format given
     *
     * @param baseName not used
     * @param locale locale
     * @param source in ResourceBundle terms 'format' in Krail terms 'source'
     * @param loader class loader to use, usually the caller's class loader
     * @param reload instruct the reader to reload from persistence
     *
     * @return
     *
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IOException
     */
    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String source, ClassLoader loader, boolean
            reload) throws IllegalAccessException, InstantiationException, IOException {

        log.debug("Locating reader for source '{}', base name '{}'", source, baseName);
        BundleReader reader = bundleReaders.get(source);
        ResourceBundle bundle = reader.newBundle(source, enumKeyClass, locale, loader, reload);
        return bundle;

    }

    /**
     * This callback is used by {@link ResourceBundle} to determine the available "formats" (sources in Krail terms)
     * and the order in which they are called to provide a bundle.  However, Krail overrides this behaviour by using
     * {@link PatternSource}, and the "formats" returned is always a single Krail source.
     * <p>
     * </ol>
     *
     * @param baseName
     *
     * @return
     */
    @Override
    public List<String> getFormats(String baseName) {
        List<String> list = new ArrayList<>();
        list.add(format);
        return list;
    }


    public <E extends Enum<E>> void setEnumKeyClass(Class<? extends Enum> enumKeyClass) {
        this.enumKeyClass = enumKeyClass;
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
