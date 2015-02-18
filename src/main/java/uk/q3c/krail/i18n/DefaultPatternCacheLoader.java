/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.i18n;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheLoader;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.user.opt.Option;
import uk.q3c.krail.core.user.opt.OptionContext;

import java.util.*;

/**
 * Loads the cache by calling each of the readers in turn to provide a pattern.  The order in which they are called is
 * determined by a combination of {@link Option}, {@code bundleReaderOrderDefault} and {@code bundleReaderOrder}.
 * (see {@link #bundleSourceOrder(I18NKey)} for a description of how the order is derived.
 * configured in {{@link I18NModule}}
 * <p>
 * Created by David Sowerby on 08/12/14.
 */
public class DefaultPatternCacheLoader extends CacheLoader<PatternCacheKey, String> implements PatternCacheLoader,
        OptionContext {
    public enum OptionProperty {AUTO_STUB, SOURCE_ORDER_DEFAULT, SOURCE_ORDER, STUB_WITH_KEY_NAME, STUB_VALUE}

    private static Logger log = LoggerFactory.getLogger(DefaultPatternCacheLoader.class);
    private Map<String, Set<String>> bundleReaderOrder;
    private Set<String> bundleReaderOrderDefault;
    private Map<String, BundleReader> bundleReaders;
    private Option option;

    @Inject
    public DefaultPatternCacheLoader(Map<String, BundleReader> bundleReaders, Option option, @BundleReaderOrder
    Map<String, Set<String>> bundleReaderOrder, @BundleReaderOrderDefault Set<String> bundleReaderOrderDefault) {
        this.bundleReaders = bundleReaders;
        this.option = option;
        this.bundleReaderOrder = bundleReaderOrder;
        this.bundleReaderOrderDefault = bundleReaderOrderDefault;
        option.init(this, OptionProperty.class);
    }

    /**
     * Retrieves the value corresponding to {@code key}. The required Locale (from the {@code cacheKey})
     * is checked for each Reader in turn, and if that fails to provide a result then the next candidate Locale is
     * used, and each source tried again.  If all candidate locales, for all Readers, are exhausted and still no
     * pattern is found, then the name of the key is returned.
     * <p>
     * The order that readers are accessed is determined by {@link #bundleReaderOrder}
     * <p>
     * The native Java method for identifying candidate locales is used - see ResourceBundle.Control
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
                Boolean autoStub = option.get(false, OptionProperty.AUTO_STUB, source);
                Boolean stubWithKeyName = option.get(true, OptionProperty.STUB_WITH_KEY_NAME, source);
                String stubValue = option.get("undefined", OptionProperty.STUB_VALUE, source);

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
     * Returns the order in which Readers are processed.  The first non-null of the following is used:
     * <ol>
     * <li>the order returned by{@link #getOptionReaderOrder(String)} (a value from {@link Option}</li>
     * <li>the order returned by {@link #getOptionReaderOrderDefault()}  (a value from {@link Option}</li>
     * <li>{@link #bundleReaderOrder}, which is defined by {@link I18NModule#setBundleReaderOrder(String,
     * String...)}</li>
     * <li>{@link #bundleReaderOrderDefault}, which is defined by {@link I18NModule#setDefaultBundleReaderOrder} </li>
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

        List<String> sourceOrder = getOptionReaderOrder(key.bundleName());
        if (sourceOrder != null) {
            return sourceOrder;
        }

        sourceOrder = getOptionReaderOrderDefault();
        if (sourceOrder != null) {
            return sourceOrder;
        }

        Set<String> order = bundleReaderOrder.get(key.bundleName());
        if (order != null) {
            return new ArrayList<>(order);
        }

        sourceOrder = new ArrayList<>(bundleReaderOrderDefault);
        if (!sourceOrder.isEmpty()) {
            return sourceOrder;
        }

        return new ArrayList(bundleReaders.keySet());

    }

    @Override
    public List<String> getOptionReaderOrder(String baseName) {
        return option.get(null, OptionProperty.SOURCE_ORDER, baseName);
    }

    @Override
    public List<String> getOptionReaderOrderDefault() {
        return option.get(null, OptionProperty.SOURCE_ORDER_DEFAULT);
    }

    @Override
    public void setOptionReaderOrderDefault(String... sources) {
        List<String> order = Arrays.asList(sources);
        option.set(order, OptionProperty.SOURCE_ORDER_DEFAULT);
    }

    @Override
    public Option getOption() {
        return option;
    }

    @Override
    public void setOptionReaderOrder(String baseName, String... sources) {
        Preconditions.checkNotNull(baseName);
        if (sources.length < 1) {
            log.warn("Attempted to setOptionReaderOrder with no sources.  No change has been made ");
            return;
        }

        List<String> list = Arrays.asList(sources);
        option.set(list, OptionProperty.SOURCE_ORDER, baseName);
    }

    @Override
    public void setOptionAutoStub(boolean autoStub, String source) {
        option.set(autoStub, OptionProperty.AUTO_STUB, source);
    }

    @Override
    public void setOptionStubWithKeyName(boolean useKeyName, String source) {
        option.set(useKeyName, OptionProperty.STUB_WITH_KEY_NAME, source);
    }

    @Override
    public void setOptionStubValue(String stubValue, String source) {
        option.set(stubValue, OptionProperty.STUB_VALUE, source);
    }


}
