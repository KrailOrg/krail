package uk.q3c.krail.i18n;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Created by David Sowerby on 14/12/14.
 */
public class DefaultPatternUtility implements PatternUtility {
    private Map<String, BundleReader> bundleReaders;
    private PatternSource patternSource;

    @Inject
    protected DefaultPatternUtility(Map<String, BundleReader> bundleReaders, PatternSource patternSource) {
        this.bundleReaders = bundleReaders;
        this.patternSource = patternSource;
    }

    /**
     * Write out the keys and values for the combined {@code sources}.  This method calls {@link
     * PatternSource#retrievePattern (Enum, Locale)} for each key in {@code keyClass}, then writes out all the keys
     * and values using the {@code writer}.  Keys without values will have a value assigned of the key.name(), with
     * underscores replaced by spaces, so the output will always list all keys.
     * <p>
     * This method effectively merges all the keys for all the sources, using the source ordering provided in {@link
     * PatternSource}, and fills in any missing values according to the auto-stub UserOption settings in the {@link
     * PatternCacheLoader}
     *
     * @param writer
     *         the BundleWriter implementation to use
     * @param keyClass
     * @param locales
     *         the locales to write out.  For class and property writers multiple locales will output multiple files
     * @param bundleName
     *         optionally use a bundle name different to that defined by the {@code keyClass}
     */
    @Override
    public <E extends Enum<E> & I18NKey> void writeOut(BundleWriter writer, Class<E> keyClass, Set<Locale> locales,
                                                       Optional<String> bundleName) throws IOException {
        E[] keys = keyClass.getEnumConstants();


        for (Locale locale : locales) {
            DirectResourceBundle bundle = new DirectResourceBundle(keyClass);
            for (E key : keys) {
                String pattern = patternSource.retrievePattern(key, locale);
                bundle.put(key, pattern);
            }
            writer.setBundle(bundle);
            writer.write(locale, bundleName);
        }
    }

    /**
     * Write out the keys and values for a specific source and locale,  Only those keys which have a value assigned
     * (which could be an empty String) are written out.
     *
     * @param writer
     *         the BundleWriter implementation to use @param keyClass
     * @param locale
     *         the locale to write out.
     * @param bundleName
     *         optionally use a bundle name different to that defined by the {@code keyClass}
     */
    @Override
    public <E extends Enum<E> & I18NKey> void writeOutExclusive(String source, BundleWriter writer, Class<E>
            keyClass, Locale locale, Optional<String> bundleName) throws IOException {
        Preconditions.checkNotNull(source);
        E[] keys = keyClass.getEnumConstants();
        BundleReader reader = bundleReaders.get(source);


        DirectResourceBundle bundle = new DirectResourceBundle(keyClass);
        for (E key : keys) {
            PatternCacheKey cacheKey = new PatternCacheKey(key, locale);
            Optional<String> value = reader.getValue(cacheKey, source, false, false, source);
            if (value.isPresent()) {
                bundle.put(key, value.get());
            }
        }
        writer.setBundle(bundle);
        writer.write(locale, bundleName);
    }

}
