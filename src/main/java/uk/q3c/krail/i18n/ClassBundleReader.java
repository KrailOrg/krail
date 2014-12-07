package uk.q3c.krail.i18n;

import com.google.inject.Inject;
import uk.q3c.krail.core.user.opt.UserOption;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Reads a {@link ResourceBundle} from a Java class.  The result (if it exists) is expected to be an {@link
 * EnumResourceBundle}.
 * <p/>
 * The code for this was lifted directly from {@link ResourceBundle.Control} newBundle and toBundleName methods
 * <p/>
 * /**
 * This default options for this reader assume that the bundle classes are on the same class path as the key - for
 * example, with default settings, with a key class of com.example.i18n.LabelKey will expect to find the bundle at
 * com.example.i18n.Labels
 * <p>
 * For a Gradle project that means placing the properties file in the directory src/main/resources/com/example/i18n
 * <p>
 * The default options can be changed by calling userOption and setting the options:
 * <p>
 * {@code getUserOption().set(false, UserOptionProperty.USE_KEY_PATH, source);
 *
 * getUserOption().set("path.to.classes",UserOptionProperty.USE_KEY_PATH,source);
 * }
 * Created by David Sowerby on 25/11/14.
 */
public class ClassBundleReader extends BundleReaderBase implements BundleReader {

    @Inject
    protected ClassBundleReader(UserOption userOption) {
        super(userOption);
    }

    /**
     * Most of the code for this method is taken from the standard ResourceBundle code for loading from a class
     *
     * @param enumKeyClass
     *         the class of enum keys to use
     * @param baseName
     *         not used
     * @param locale
     * @param loader
     *         the class loader to use, generally the class loader of the caller
     * @param reload
     *         not used
     *
     * @return
     *
     * @throws BundleReaderException
     *         if the enumKeyClass has no constants
     */
    @Override
    public EnumResourceBundle newBundle(String source,Class<? extends Enum> enumKeyClass,  Locale locale,

                                        ClassLoader loader, boolean reload) {
        I18NKey key;
        try {
            key = (I18NKey) enumKeyClass.getEnumConstants()[0];
        } catch (Exception e) {
            throw new BundleReaderException("The enum key class requires at least one constant");
        }
        String bundleName = this.toBundleName(source,key, locale);
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
