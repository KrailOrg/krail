package uk.q3c.krail.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Common interface for implementations which locate a ResourceBundle from implementation specific sources - for
 * example, Java class, properties files, database.
 * <p/>
 * Created by David Sowerby on 18/11/14.
 */
public interface BundleLocator {
    ResourceBundle newBundle(String baseName, Locale locale, ClassLoader loader, boolean reload);
}
