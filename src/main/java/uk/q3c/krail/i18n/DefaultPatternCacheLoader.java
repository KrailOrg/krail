package uk.q3c.krail.i18n;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheLoader;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.user.opt.UserOption;
import uk.q3c.krail.core.user.opt.UserOptionContext;

import java.util.*;

/**
 * Created by David Sowerby on 08/12/14.
 */
public class DefaultPatternCacheLoader extends CacheLoader<PatternCacheKey, String> implements PatternCacheLoader,
        UserOptionContext {
    public enum UserOptionProperty {SOURCE_ORDER_DEFAULT, SOURCE_ORDER}

    private static Logger log = LoggerFactory.getLogger(DefaultPatternCacheLoader.class);
    private Map<String, BundleReader> bundleReaders;
    private Map<String, Set<String>> bundleSourceOrder;
    private Set<String> bundleSourceOrderDefault;
    private UserOption userOption;

    @Inject
    public DefaultPatternCacheLoader(Map<String, BundleReader> bundleReaders, UserOption userOption,
                                     @BundleSourceOrder Map<String, Set<String>> bundleSourceOrder,
                                     @BundleSourceOrderDefault Set<String> bundleSourceOrderDefault) {
        this.bundleReaders = bundleReaders;
        this.userOption = userOption;
        this.bundleSourceOrder = bundleSourceOrder;
        this.bundleSourceOrderDefault = bundleSourceOrderDefault;
        userOption.configure(this, UserOptionProperty.class);
    }

    /**
     * Computes or retrieves the value corresponding to {@code key}.
     *
     * @param key
     *         the non-null key whose value should be loaded
     *
     * @return the value associated with {@code key}; <b>must not be null</b>
     *
     * @throws Exception
     *         if unable to load the result
     * @throws InterruptedException
     *         if this method is interrupted. {@code InterruptedException} is
     *         treated like any other {@code Exception} in all respects except that, when it is caught,
     *         the thread's interrupt status is set
     */
    @Override
    public String load(PatternCacheKey key) throws Exception {
        I18NKey i18NKey = (I18NKey) key.getKey()
                                       .getClass()
                                       .getEnumConstants()[0];


        //        Use standard Java call to get candidates
        KrailResourceBundleControl bundleControl = new KrailResourceBundleControl();
        List<Locale> candidateLocales = bundleControl.getCandidateLocales(i18NKey.bundleName(), key.getLocale());
        String result = null;

        for (Locale candidateLocale : candidateLocales) {
            //try each source in turn for a valid pattern
            for (String source : bundleSourceOrder((I18NKey) key.getKey())) {
                BundleReader reader = bundleReaders.get(source);
                reader.getValue(key, source);
                if (result != null) {
                    break;
                }
            }
            if (result != null) {
                break;
            }
        }
        if (result == null) {
            result = key.getKey()
                        .name();
        }
        return result;


    }

    @Override
    public List<String> bundleSourceOrder(I18NKey key) {

        List<String> sourceOrder = getOptionSourceOrder(key.bundleName());
        if (sourceOrder != null) {
            return sourceOrder;
        }

        sourceOrder = getOptionSourceOrderDefault();
        if (sourceOrder != null) {
            return sourceOrder;
        }

        Set<String> order = bundleSourceOrder.get(key.bundleName());
        if (order != null) {
            return new ArrayList<>(order);
        }

        sourceOrder = new ArrayList<>(bundleSourceOrderDefault);
        if (!sourceOrder.isEmpty()) {
            return sourceOrder;
        }

        return new ArrayList(bundleReaders.keySet());

    }

    @Override
    public List<String> getOptionSourceOrder(String baseName) {
        return userOption.get(null, UserOptionProperty.SOURCE_ORDER, baseName);
    }

    @Override
    public List<String> getOptionSourceOrderDefault() {
        return userOption.get(null, UserOptionProperty.SOURCE_ORDER_DEFAULT);
    }

    @Override
    public UserOption getUserOption() {
        return userOption;
    }

    @Override
    public void setOptionSourceOrder(String baseName, String... tags) {
        Preconditions.checkNotNull(baseName);
        if (tags.length < 1) {
            log.warn("Attempted to setOptionSourceOrder with no source tags.  No change has been made ");
            return;
        }

        List<String> list = Arrays.asList(tags);
        userOption.set(list, UserOptionProperty.SOURCE_ORDER, baseName);
    }

}
