package uk.q3c.krail.i18n;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.user.opt.UserOption;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Properties;

/**
 * This default options for this reader assume that the properties files are on the same class path as the key - for
 * example, with default settings, with a key class of com.example.i18n.LabelKey will expect to find the properties
 * file
 * at com.example.i18n.Labels.properties
 * <p>
 * For a Gradle project that means placing the properties file in the directory src/main/resources/com/example/i18n
 * <p>
 * The default options can be changed by calling userOption and setting the options:
 * <p>
 * {@code getUserOption().set(false, UserOptionProperty.USE_KEY_PATH, source);
 *
 * getUserOption().set("path.to.properties.files",UserOptionProperty.USE_KEY_PATH,source);
 * }
 * Created by David Sowerby on 25/11/14.
 */
public class PropertiesFromClasspathBundleReader extends BundleReaderBase implements BundleReader {
    private static Logger log = LoggerFactory.getLogger(PropertiesFromClasspathBundleReader.class);
    private Properties properties;

    @Inject
    protected PropertiesFromClasspathBundleReader(UserOption userOption) {
        super(userOption);
    }

    /**
     * Most of the code for this method is taken from the standard ResourceBundle code for loading from a properties
     * file.  The additional part is to transfer the properties into the EnumMap used by EnumResourceBundle
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
    public KrailResourceBundle newBundle(String source, Class<? extends Enum> enumKeyClass, Locale locale,
                                         ClassLoader loader, boolean reload) throws IOException {
        I18NKey key;
        try {
            key = (I18NKey) enumKeyClass.getEnumConstants()[0];
        } catch (Exception e) {
            throw new BundleReaderException("The enum key class requires at least one constant");
        }

        log.debug("locating properties based bundle for baseName {}", key.bundleName());

        String localisedName = toBundleName(source, key, locale);

        final String resourceName1 = this.toResourceName0(localisedName, "properties");
        log.debug("resource name is {}", resourceName1);

        KrailResourceBundle bundle = null;
        if (resourceName1 == null) {
            log.debug("returning null");
            return bundle;
        }

        final ClassLoader classLoader = loader;
        final boolean reloadFlag = reload;
        InputStream stream = null;

        try {
            stream = (InputStream) AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public InputStream run() throws IOException {
                    InputStream is = null;
                    if (reloadFlag) {
                        log.debug("reload is true");
                        URL url = classLoader.getResource(resourceName1);
                        log.debug("url is {}", url);
                        if (url != null) {
                            URLConnection connection = url.openConnection();
                            if (connection != null) {
                                connection.setUseCaches(false);
                                is = connection.getInputStream();
                            }
                        }
                    } else {
                        log.debug("reload is false");
                        is = classLoader.getResourceAsStream(resourceName1);
                    }

                    return is;
                }
            });
        } catch (PrivilegedActionException var18) {
            throw (IOException) var18.getException();
        }

        if (stream != null) {
            try {
                log.debug("stream is valid");
                properties = new Properties();
                properties.load(stream);
                log.debug("properties loaded");
                bundle = new KrailResourceBundle(enumKeyClass);
                EnumMap map = bundle.getMap();
                log.debug("copying properties to EnumMap, using enum class '{}'", enumKeyClass);
                int i = 0;
                for (Enum e : enumKeyClass.getEnumConstants()) {
                    String s = properties.getProperty(e.name());
                    if (s != null) {
                        map.put(e, s);
                        i++;
                    }

                }
                log.debug("{} properties loaded", i);

            } finally {
                stream.close();
            }
        }
        return bundle;
    }

    private String toResourceName0(String bundleName, String suffix) {
        return bundleName.contains("://") ? null : this.toResourceName(bundleName, suffix);
    }

    public final String toResourceName(String bundleName, String suffix) {
        StringBuilder sb = new StringBuilder(bundleName.length() + 1 + suffix.length());
        sb.append(bundleName.replace('.', '/'))
          .append('.')
          .append(suffix);
        return sb.toString();
    }
}

