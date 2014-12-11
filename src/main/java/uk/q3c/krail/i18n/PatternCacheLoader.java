package uk.q3c.krail.i18n;

import java.util.List;

/**
 * Created by David Sowerby on 08/12/14.
 */
public interface PatternCacheLoader {


    List<String> bundleSourceOrder(I18NKey key);

    List<String> getOptionSourceOrder(String baseName);

    List<String> getOptionSourceOrderDefault();

    void setOptionSourceOrder(String baseName, String... tags);
}
