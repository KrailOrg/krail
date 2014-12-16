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
    public enum UserOptionProperty {AUTO_STUB, SOURCE_ORDER_DEFAULT, SOURCE_ORDER, STUB_WITH_KEY_NAME, STUB_VALUE}

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
     * Retrieves the value corresponding to {@code key}. The required Locale (from the {@code cacheKey})
     * is checked for each source in turn, and if that fails to provide a result then the next candidate Locale is
     * used, and each source tried again.  If all candidate locales, for all sources, are exhausted and still no
     * pattern is found, then the name of the key is returned.
     * <p>
     * The that sources are accessed is determined by {@link #bundleSourceOrder}
     * <p>
     * The standard Java method for identifying candidate locales is used - see ResourceBundle.Control
     * .getCandidateLocales
     *
     * @param cacheKey
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
    public String load(PatternCacheKey cacheKey) throws Exception {
        I18NKey i18NKey = (I18NKey) cacheKey.getKey()
                                            .getClass()
                                            .getEnumConstants()[0];


        //        Use standard Java call to get candidates
        KrailResourceBundleControl bundleControl = new KrailResourceBundleControl();
        List<Locale> candidateLocales = bundleControl.getCandidateLocales(i18NKey.bundleName(), cacheKey
                .getRequestedLocale());
        Optional<String> value = Optional.empty();

        for (Locale candidateLocale : candidateLocales) {
            cacheKey.setActualLocale(candidateLocale);
            //try each source in turn for a valid pattern
            for (String source : bundleSourceOrder((I18NKey) cacheKey.getKey())) {
                cacheKey.setSource(source);

                //get auto-stub options for the source
                Boolean autoStub = userOption.get(false, UserOptionProperty.AUTO_STUB, source);
                Boolean stubWithKeyName = userOption.get(true, UserOptionProperty.STUB_WITH_KEY_NAME, source);
                String stubValue = userOption.get("undefined", UserOptionProperty.STUB_VALUE, source);

                //get the reader
                BundleReader reader = bundleReaders.get(source);

                //get value from reader, auto-stubbing as required
                value = reader.getValue(cacheKey, source, autoStub, stubWithKeyName, stubValue);
                if (value.isPresent()) {
                    break;
                }
            }
            if (value.isPresent()) {
                break;
            }
        }
        if (!value.isPresent()) {
            value = Optional.of(cacheKey.getKey()
                                        .name()
                                        .replace("_", " "));
        }
        return value.get();
    }

    /**
     * Returns the order in which sources are processed - the first which returns a valid value for a key is used.  The
     * first non-null of the following is used:
     * <ol>
     * <li>the order returned by{@link #getOptionSourceOrder()} (a value from UserOption</li>
     * <li>the order returned by {@link #getOptionSourceOrderDefault()}  (a value from UserOption</li>
     * <li>{@link #bundleSourceOrder}, which is defined by {@link I18NModule#setBundleSourceOrder()}</li>
     * <li>{@link #bundleSourceOrderDefault}, which is defined by {@link I18NModule#setDefaultBundleSourceOrder} </li>
     * <li>the keys from {@link #bundleReaders} - note that the order for this will be unreliable if bundleReaders has
     * been defined by multiple Guice modules</li>
     * <p>
     * <p>
     * </ol>
     *
     * @param key
     *         used to identify the bundle, from {@link I18NKey#bundleName()}
     *
     * @return a list containing the sources to be processed, in the order that they should be processed
     */
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
    public void setOptionSourceOrderDefault(String... sources) {
        List<String> order = Arrays.asList(sources);
        userOption.set(order, UserOptionProperty.SOURCE_ORDER_DEFAULT);
    }

    @Override
    public UserOption getUserOption() {
        return userOption;
    }

    @Override
    public void setOptionSourceOrder(String baseName, String... sources) {
        Preconditions.checkNotNull(baseName);
        if (sources.length < 1) {
            log.warn("Attempted to setOptionSourceOrder with no sources.  No change has been made ");
            return;
        }

        List<String> list = Arrays.asList(sources);
        userOption.set(list, UserOptionProperty.SOURCE_ORDER, baseName);
    }

    @Override
    public void setOptionAutoStub(boolean autoStub, String source) {
        userOption.set(autoStub, UserOptionProperty.AUTO_STUB, source);
    }

    @Override
    public void setOptionStubWithKeyName(boolean useKeyName, String source) {
        userOption.set(useKeyName, UserOptionProperty.STUB_WITH_KEY_NAME, source);
    }

    @Override
    public void setOptionStubValue(String stubValue, String source) {
        userOption.set(stubValue, UserOptionProperty.STUB_VALUE, source);
    }


}
