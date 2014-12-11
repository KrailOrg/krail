package uk.q3c.krail.i18n;

import java.util.Locale;

/**
 * Created by David Sowerby on 07/12/14.
 */
public interface PatternSource {


    <E extends Enum<E> & I18NKey> String retrievePattern(E key, Locale locale);

}
