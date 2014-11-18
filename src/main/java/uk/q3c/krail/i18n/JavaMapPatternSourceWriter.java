package uk.q3c.krail.i18n;

import java.io.File;
import java.io.IOException;

/**
 * Created by David Sowerby on 16/11/14.
 */
public interface JavaMapPatternSourceWriter<E extends Enum<E>> {
    /**
     * Writes the file to {@code outputDir}
     *
     * @param outputDir
     *
     * @throws IOException
     */
    void write(File outputDir) throws IOException;

    /**
     * Sets the bundle to write
     *
     * @param bundle
     */
    void setBundle(MapResourceBundle<E> bundle);
}
