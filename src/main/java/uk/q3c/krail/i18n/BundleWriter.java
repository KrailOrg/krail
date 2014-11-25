package uk.q3c.krail.i18n;

import java.io.IOException;

/**
 * A common interface to enable the writing of {@link EnumResourceBundle} implementations.
 * <p/>
 * Created by David Sowerby on 25/11/14.
 */
public interface BundleWriter<E extends Enum<E>> {
    void setBundle(EnumResourceBundle<E> bundle);

    void write() throws IOException;
}
