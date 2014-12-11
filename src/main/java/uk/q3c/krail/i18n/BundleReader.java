package uk.q3c.krail.i18n;

import com.google.common.base.Optional;

import java.util.ResourceBundle;

/**
 * Common interface for implementations which locate and read a ResourceBundle from implementation specific sources -
 * for example, Java class, properties files, database. Krail uses this to replace code which normally has to be
 * provided in a {@link ResourceBundle.Control} sub-class.
 * <p/>
 * <p/>
 * Created by David Sowerby on 18/11/14.
 */
public interface BundleReader {

    //    ResourceBundle newBundle(String source, Class<? extends Enum> enumKeyClass, Locale locale, ClassLoader loader,
    //                             boolean reload) throws IOException;


    Optional<String> getValue(PatternCacheKey cacheKey, String source);
}
