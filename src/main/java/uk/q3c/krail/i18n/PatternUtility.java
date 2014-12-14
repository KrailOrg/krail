package uk.q3c.krail.i18n;

import com.google.common.base.Optional;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;

/**
 * Implementations provide some general purpose utilities for managing I18N Patterns
 * <p>
 * Created by David Sowerby on 14/12/14.
 */
public interface PatternUtility {


    <E extends Enum<E> & I18NKey> void writeOut(BundleWriter writer, Class<E> keyClass, Set<Locale> locales,
                                                Optional<String> bundleName) throws IOException;


    <E extends Enum<E> & I18NKey> void writeOutExclusive(String source, BundleWriter writer, Class<E> keyClass,
                                                         Locale locale, Optional<String> bundleName) throws IOException;
}
