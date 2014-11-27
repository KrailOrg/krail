package uk.q3c.krail.i18n;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * Created by David Sowerby on 25/11/14.
 */
public class PropertiesBundleReader extends BundleReaderBase implements BundleReader {
    private static Logger log = LoggerFactory.getLogger(PropertiesBundleReader.class);
    private Properties properties;

    /**
     * Most of the code for this method is taken from the standard ResourceBundle code for loading from a properties
     * file.  The additional part is to transfer the properties into the EnumMap used by EnumResourceBundle
     *
     * @param enumKeyClass
     * @param baseName
     * @param locale
     * @param loader
     * @param reload
     *
     * @return
     *
     * @throws IOException
     */
    @Override
    public KrailResourceBundle newBundle(Class<? extends Enum> enumKeyClass, String baseName, Locale locale,
                                         ClassLoader loader, boolean reload) throws IOException {
        log.debug("locating properties based bundle for baseName {}", baseName);
        final String resourceName1 = this.toResourceName0(baseName, "properties");
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
                log.debug("copying properties to EnumMap, using enum class {}", enumKeyClass);
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

