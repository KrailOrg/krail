package uk.q3c.krail.i18n;

import java.util.List;

/**
 * Created by David Sowerby on 08/12/14.
 */
public interface PatternCacheLoader {

    /**
     * Returns the order in which sources are processed - the first which returns a valid value for a key is used.  The
     * way in which the order is decided is determined is defined by the implementation
     *
     * @param key
     *
     * @return
     */
    List<String> bundleSourceOrder(I18NKey key);

    List<String> getOptionSourceOrder(String baseName);

    List<String> getOptionSourceOrderDefault();

    void setOptionSourceOrderDefault(String... sources);

    void setOptionSourceOrder(String baseName, String... sources);

    void setOptionAutoStub(boolean autoStub, String source);

    void setOptionStubWithKeyName(boolean useKeyName, String source);

    void setOptionStubValue(String stubValue, String source);
}
