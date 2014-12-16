package uk.q3c.krail.i18n;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;

/**
 * A common interface to enable the writing of {@link EnumResourceBundle} implementations.
 * <p/>
 * Created by David Sowerby on 25/11/14.
 */
public interface BundleWriter<E extends Enum<E>> {

    /**
     * Sets the bundle to write
     *
     * @param bundle
     */
    void setBundle(EnumResourceBundle<E> bundle);

    /**
     * Writes the bundle out, using the bundleName (if appropriate to the implementation)
     *
     * @param locale
     * @param bundleName
     *
     * @throws IOException
     */
    void write(Locale locale, Optional<String> bundleName) throws IOException;
}
