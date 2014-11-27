package uk.q3c.krail.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Reads a {@link ResourceBundle} from a Java class.  The result (if it exists) is expected to be an {@link
 * EnumResourceBundle}.
 * <p/>
 * The code for this was lifted directly from {@link ResourceBundle.Control} newBundle and toBundleName methods
 * <p/>
 * Created by David Sowerby on 18/11/14.
 */
public class ClassBundleReader extends BundleReaderBase implements BundleReader {
    @Override
    public EnumResourceBundle newBundle(Class<? extends Enum> enumKeyClass, String baseName, Locale locale,
                                        ClassLoader loader, boolean reload) {
        String bundleName = this.toBundleName(baseName, locale);
        try {
            Class resourceName = loader.loadClass(bundleName);
            if (!EnumResourceBundle.class.isAssignableFrom(resourceName)) {
                throw new ClassCastException(resourceName.getName() + " cannot be cast to EnumResourceBundle");
            }

            EnumResourceBundle bundle = (EnumResourceBundle) resourceName.newInstance();
            bundle.loadMap(enumKeyClass);
            return bundle;

        } catch (Exception e) {
            return null;
        }
    }


}
