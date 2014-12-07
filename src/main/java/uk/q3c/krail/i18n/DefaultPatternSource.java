/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.i18n;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.user.opt.UserOption;
import uk.q3c.krail.core.user.opt.UserOptionConsumer;

import java.io.IOException;
import java.util.*;

/**
 * The default implementation of {@link PatternSource} to retrieve a localised pattern from an  {{@link
 * EnumResourceBundle}.
 * <p>
 * <p>
 * Created by David Sowerby on 04/11/14.
 */
public class DefaultPatternSource implements PatternSource, UserOptionConsumer {
    public enum UserOptionProperty {AUTO_STUB, GENERATE_STUB_WITH_NAME, SOURCE_ORDER, SOURCE_ORDER_DEFAULT}

    private static Logger log = LoggerFactory.getLogger(DefaultPatternSource.class);
    private EnumResourceBundleControl bundleControl;
    private Map<String, BundleReader> bundleReaders;
    private Map<String, Set<String>> bundleSourceOrder;
    private Set<String> bundleSourceOrderDefault;
    private Set<Locale> supportedLocales;
    private UserOption userOption;


    @Inject
    protected DefaultPatternSource(@SupportedLocales Set<Locale> supportedLocales, UserOption userOption, Map<String,
            BundleReader> bundleReaders, EnumResourceBundleControl bundleControl, @BundleSourceOrderDefault
    Set<String> bundleSourceOrderDefault, @BundleSourceOrder Map<String, Set<String>> bundleSourceOrder) {
        this.supportedLocales = supportedLocales;
        this.userOption = userOption;
        this.bundleControl = bundleControl;
        this.bundleSourceOrderDefault = bundleSourceOrderDefault;
        this.bundleSourceOrder = bundleSourceOrder;
        this.bundleReaders = bundleReaders;
        userOption.configure(this, UserOptionProperty.class);
    }


    @Override
    public <E extends Enum<E> & I18NKey> Optional<String> retrievePattern(E key, Locale locale) {


        //try each source in turn for a valid pattern
        Optional<String> result = Optional.absent();
        for (String source : bundleSourceOrder(key)) {
            EnumResourceBundle<E> bundle = getBundle(source, key, locale);
            String pattern = bundle.getValue((E) key);
            if (pattern != null) {
                result = Optional.of(pattern);
                break;
            } else {
                if (getAutoStub()) {
                    // this call does not overwrite an existing entry
                    generateStub(source, key, locale, false);
                    return Optional.of(bundle.getValue((E) key));
                }
            }
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
    public void setOptionSourceOrderDefault(String... tags) {
        List<String> defaults = Arrays.asList(tags);
        userOption.set(defaults, UserOptionProperty.SOURCE_ORDER_DEFAULT);
    }

    public boolean getAutoStub() {
        return userOption.get(false, UserOptionProperty.AUTO_STUB);
    }

    public void setAutoStub(boolean value) {
        userOption.set(value, UserOptionProperty.AUTO_STUB);
    }

    /**
     * Generates a stub value for the specified {@code key}, {@code source} and {@code locale}.  The value of the stub
     * is determined by {@link UserOptionProperty#GENERATE_STUB_WITH_NAME} if that option is true, the name of the key
     * is sued as the stub value, otherwsie the value is an empty string.
     *
     * @param source
     * @param key
     *         the key the stub will be for
     * @param locale
     * @param <E>
     */
    @Override
    public <E extends Enum<E> & I18NKey> void generateStub(String source, E key, Locale locale, boolean overwrite) {

        String value = "";
        if (getGenerateStubWithName()) {
            value = ((Enum<?>) key).name()
                                   .replace("_", " ");
        }
        put(source, key, locale, value, overwrite);

    }

    public boolean getGenerateStubWithName() {
        return userOption.get(true, UserOptionProperty.GENERATE_STUB_WITH_NAME);
    }

    public void setGenerateStubWithName(boolean value) {
        userOption.set(value, UserOptionProperty.GENERATE_STUB_WITH_NAME);
    }

    public <E extends Enum<E> & I18NKey> void put(String source, E key, Locale locale, String value, boolean
            overwrite) {
        EnumResourceBundle<E> bundle = getBundle(source, key, locale);
        put(bundle, (E) key, value, overwrite);
    }

    public <E extends Enum<E> & I18NKey> void put(EnumResourceBundle<E> bundle, E key, String value, boolean
            overwrite) {

        if (overwrite) {
            bundle.put(key, value);
            return;
        }
        //Overwrite is false, so conditional - put only if key missing or current value empty
        String existing = bundle.getValueExclusive(key);
        if (StringUtils.isEmpty(existing)) {
            bundle.put(key, value);
        }
    }

    public <E extends Enum<E> & I18NKey> EnumResourceBundle<E> getBundle(String source, E sampleKey, Locale locale) {
        // a not very elegant way of getting the key class to the bundle reader
        log.debug("getting bundle for source '{}', locale '{}'", source, locale);
        Class<? extends Enum> enumClass = sampleKey.getClass();
        bundleControl.setEnumKeyClass(enumClass);
        bundleControl.setSource(source);

        EnumResourceBundle<E> bundle = (EnumResourceBundle) EnumResourceBundle.getBundle(sampleKey.bundleName(),
                locale, bundleControl);
        log.debug("Bundle located for baseName {}", sampleKey.bundleName());
        return bundle;
    }

    @Override
    public <E extends Enum<E> & I18NKey> void generateStub(String source, E key, Set<Locale> locales, boolean
            overwrite) {
        for (Locale locale : locales) {
            generateStub(source, key, locale, overwrite);
        }
    }


    @Override
    public <E extends Enum<E> & I18NKey> void generateStub(String source, E key, boolean overwrite) {
        for (Locale locale : supportedLocales) {
            generateStub(source, key, locale, overwrite);
        }

    }


    @Override
    public <E extends Enum<E> & I18NKey> void writeOut(String source, BundleWriter<E> writer, E sampleKey, boolean
            allKeys) throws IOException {

        writeOut(source, writer, sampleKey, supportedLocales, allKeys);

    }


    @Override
    public <E extends Enum<E> & I18NKey> void writeOut(String source, BundleWriter<E> writer, E sampleKey,
                                                       Set<Locale> locales, boolean allKeys) throws IOException {
        for (Locale locale : locales) {
            EnumResourceBundle<E> bundle = getBundle(source, sampleKey, locale);
            Class<E> enumClass = bundle.getKeyClass();
            // if we want all keys, populate any missing from the map
            // and fill as determined by UserOption


            if (allKeys) {
                E[] keys = enumClass.getEnumConstants();
                for (E key : keys) {
                    String fillValue = getGenerateStubWithName() ? key.name()
                                                                      .replace("_", " ") : "";
                    bundle.put(key, "\"" + fillValue + "\"");
                }


            }
            writer.setBundle(bundle);
            writer.write();
        }

    }

    @Override
    public <E extends Enum<E> & I18NKey> void mergeSources(E sampleKey, Set<Locale> locales, boolean overwrite,
                                                           String... sources) {

        if (sources.length < 2) {
            log.info("At least 2 sources are required for a merge, you have provided {}", sources.length);
            return;
        }
        log.debug("Merging {} sources, for bundle base name '{}'", sources.length, sampleKey.bundleName());
        EnumMap<E, String>[] maps = null;
        //for each Locale
        for (Locale locale : locales) {
            log.debug("Locale is '{}'", locale);
            maps = new EnumMap[sources.length];
            // Load the map for each source
            int i = 0;
            for (String source : sources) {
                log.debug("Load bundle for source '{}'", source);
                EnumResourceBundle<E> bundle = getBundle(source, sampleKey, locale);
                maps[i] = bundle.getMap();
                log.debug("map {} has '{}' entries", i, maps[i].size());
                i++;
            }
            mergeMaps(overwrite, maps);
        }

    }

    public <E extends Enum<E> & I18NKey> void mergeMaps(boolean overwrite, EnumMap<E, String>... maps) {
        if (maps.length < 2) {
            log.info("At least 2 sources are required for a merge, you have provided {}", maps.length);
            return;
        }
        log.debug("Merging '{}' maps, with overwrite={}", maps.length, overwrite);
        for (int i = 0; i < maps.length - 1; i++) {
            EnumMap<E, String> fromMap = maps[i];
            EnumMap<E, String> toMap = maps[i + 1];
            mergeMaps(fromMap, toMap, overwrite);
        }
    }

    protected <E extends Enum<E> & I18NKey> void mergeMaps(EnumMap<E, String> fromMap, EnumMap<E, String> toMap,
                                                           boolean overwrite) {
        Preconditions.checkNotNull(fromMap);
        Preconditions.checkNotNull(toMap);

        for (Map.Entry<E, String> fromEntry : fromMap.entrySet()) {
            if (overwrite) {
                toMap.put(fromEntry.getKey(), fromEntry.getValue());
            } else {
                String toValue = toMap.get(fromEntry.getKey());
                if (StringUtils.isEmpty(toValue)) {
                    toMap.put(fromEntry.getKey(), fromEntry.getValue());
                }
            }
        }
    }


    @Override
    public <E extends Enum<E> & I18NKey> void setKeyValue(String source, E key, Locale locale, String value) {
        Preconditions.checkNotNull(source);
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(locale);
        EnumResourceBundle<E> bundle = getBundle(source, key, locale);
        put(bundle, (E) key, value, true);
    }

    @Override
    public <E extends Enum<E> & I18NKey> void reset(String source, E sampleKey) {
        Preconditions.checkNotNull(source);
        Preconditions.checkNotNull(sampleKey);
        reset(source, sampleKey, supportedLocales);
    }


    @Override
    public <E extends Enum<E> & I18NKey> void reset(String source, E sampleKey, Set<Locale> locales) {
        Preconditions.checkNotNull(source);
        Preconditions.checkNotNull(sampleKey);
        Preconditions.checkNotNull(locales);
        for (Locale locale : locales) {
            EnumResourceBundle<E> bundle = getBundle(source, sampleKey, locale);
            bundle.reset();
            log.debug("Reset values from persistence, {} keys, locale {}" + " source: " + source, sampleKey.getClass
                    (), locale);
        }
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

    @Override
    public UserOption getUserOption() {
        return userOption;
    }
}
