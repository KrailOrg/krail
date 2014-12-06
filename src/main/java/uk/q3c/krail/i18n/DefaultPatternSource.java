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


    /**
     * Returns the translated String pattern for {@code key}, for {@code locale}. Returns {@link Optional.isAbsent()}
     * if there is no pattern for the key, or if the value for the key an empty String.
     * <p>
     * Each bundle source relevant to {@code key} is tried, as specified by {@link #bundleSourceOrder(I18NKey)}
     * <p>
     *
     * @param key
     *         the key to look up
     * @param locale
     *         the locale the pattern is required for
     *
     * @return Returns the translated String pattern for {@code key}, for {@code locale}. Returns {@link Optional
     * .isAbsent()} if there is no pattern for the key, or if the value for the key an empty String.
     */
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
                    generateStub(source, key, locale);
                    return Optional.of(bundle.getValue((E) key));
                }
            }
        }

        return result;
    }

    public <E extends Enum<E> & I18NKey> EnumResourceBundle<E> getBundle(String source, E sampleKey, Locale locale) {
        // a not very elegant way of getting the key class to the bundle reader
        Class<? extends Enum> enumClass = sampleKey.getClass();
        bundleControl.setEnumKeyClass(enumClass);
        bundleControl.setSource(source);

        EnumResourceBundle<E> bundle = (EnumResourceBundle) EnumResourceBundle.getBundle(sampleKey.bundleName(),
                locale, bundleControl);

        return bundle;
    }

    /**
     * This native Java {@link ResourceBundle} uses the term 'formats' to describe different sources of localised data,
     * but this reflects a rather old assumption that the source will be in a file - it excludes the idea of a database
     * or web service providing the data. Krail therefore prefers the term 'bundleSource'.
     * <p>
     * The order in which these sources are requested to provide a bundle is described below.  Krail uses the first
     * which provides a result:<ol>
     * <li> UserOption, property sourceOrder, for a specific key class</li>
     * <li> UserOption, property sourceOrderDefault, for all key classes</li>
     * <li> {@link #bundleSourceOrder}, which can be defined in the I18NModule, and applies to a specific key
     * class</li>
     * <li> {@link #bundleSourceOrderDefault}, which can be defined in the I18Module, and applies to all key
     * classes</li>
     * <li> the natural order of #bundleReaders keySet</li>
     * <p>
     * </ol>
     *
     * @param key
     *         used to retrieve the key class, which is used to identify key class specific ordering (see {@link
     *         I18NModule#setBundleSourceOrder}
     *
     * @return
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
     * Generates an implementation specific stub for the key - value pair.  This is typically used as part of the
     * process to generate files for translation.  Does not overwrite an existing key
     * <p>
     * If user option #generateStubWithName is true, the value for the key is set to the name of the key, otherwise it
     * is set to an empty String
     *
     * @param key
     *         the key the stub will be for
     * @param locale
     *         the locale the stub should be generated in
     */
    @Override
    public <E extends Enum<E> & I18NKey> void generateStub(String source, E key, Locale locale) {

        String value = "";
        if (getGenerateStubWithName()) {
            value = ((Enum<?>) key).name()
                                   .replace("_", " ");
        }
        put(source, key, locale, value, false);

    }

    public boolean getGenerateStubWithName() {
        return userOption.get(true, UserOptionProperty.GENERATE_STUB_WITH_NAME);
    }

    public void setGenerateStubWithName(boolean value) {
        userOption.set(value, UserOptionProperty.GENERATE_STUB_WITH_NAME);
    }

    /**
     * Puts the key-value pair onto the bundle, but only if either overwrite is true, or the existing value for the key
     * is missing or empty
     *
     * @param bundle
     *         the bundle to put the key-value pair into
     * @param key
     *         the key to use
     * @param value
     *         the value for the key
     * @param overwrite
     *         if true, overwrite the existing value.  If false, only put the new key-value if the existing one is
     *         missing or empty
     * @param <E>
     *         the Enum type derived from the key
     */
    public <E extends Enum<E> & I18NKey> void put(String source, E key, Locale locale, String value, boolean
            overwrite) {
        EnumResourceBundle<E> bundle = getBundle(source, key, locale);
        put(bundle, (E) key, value, overwrite);
    }

    /**
     * Puts the key-value pair onto the bundle, but only if either overwrite is true, or the existing value for the key
     * is missing or empty
     *
     * @param bundle
     *         the bundle to put the key-value pair into
     * @param key
     *         the key to use
     * @param value
     *         the value for the key
     * @param overwrite
     *         if true, overwrite the existing value.  If false, only put the new key-value if the existing one is
     *         missing or empty
     * @param <E>
     */
    public <E extends Enum<E> & I18NKey> void put(EnumResourceBundle<E> bundle, E key, String value, boolean
            overwrite) {

        if (overwrite) {
            bundle.put(key, value);
            return;
        }
        //Not overwrite so conditional - put only if key missing or current value empty
        String existing = bundle.getValueExclusive(key);
        if (StringUtils.isEmpty(existing)) {
            bundle.put(key, value);
        }
    }

    /**
     * Generates implementation specific stubs for all the {@code locales}.  For some implementations this may be more
     * efficient than repeated calls to {@link #generateStub(I18NKey, Locale)}. Does not overwrite an existing key
     *
     * @param key
     *         the the stub(s) will be for
     * @param locales
     */
    @Override
    public <E extends Enum<E> & I18NKey> void generateStub(String source, E key, Set<Locale> locales) {
        for (Locale locale : locales) {
            generateStub(source, key, locale);
        }
    }

    /**
     * Generates implementation specific stubs for all the supported locales{@code locales}.
     *
     * @param key
     *         the the stub(s) will be for
     */
    @Override
    public <E extends Enum<E> & I18NKey> void generateStub(String source, E key) {
        for (Locale locale : supportedLocales) {
            generateStub(source, key, locale);
        }

    }

    /**
     * Write the key-value set(s) for {@code keyClass}, all supported locales, to persistence.  Individual
     * implementations will provide their own methods for setting up file paths, database connection or other
     * pre-requisites, typically but not necessarily through UserOption
     */
    @Override
    public <E extends Enum<E> & I18NKey> void writeOut(String source, BundleWriter<E> writer, E sampleKey, boolean
            allKeys) throws IOException {

        writeOut(source, writer, sampleKey, supportedLocales, allKeys);

    }

    /**
     * Write the key-value set(s) to persistence, for all {@code locales}.  The path to write to is defined by {@link
     * UserOption} {@link #getWritePath()}
     *
     * @param keyClass
     *         the keys to use
     * @param locales
     *         the locales to write files for
     * @param allKeys
     *         if true, all the keys for the keyClass are generated, otherwise only the keys which have a non-empty
     *         value in a locale are
     *         written out for that locale. If {@code allKeys} is true, but a key does not have a non-empty value, the
     *         value is set according to the value of {{@link #getGenerateStubWithName()}}
     * @param <E>
     *         the Enum class represented by the keyClass
     *
     * @throws IOException
     * @see #writeOut(Class, boolean)
     */
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

    /**
     * Merge key-value pairs "down" through the {@code sources}. The first source is merged into the second, and the
     * second into the third, and so on, for the given {@code locales}.
     * <p>
     * If {@code overwrite} is true, all values are transferred from one source to the next, overwriting values that
     * are already there. <p/> If {@code overwrite} is false, values from one source are only written to the next
     * source where the key is missing or has an empty value (empty String).
     * <p>
     * At least 2 sources are required for a merge, if less are provided the method returns with nothing done
     *
     * @param sampleKey
     *         any key from the I18NKey class required
     * @param locales
     *         the locales you wish to merge
     * @param sources
     *         the sources which should be merged
     * @param overwrite
     *         If {@code overwrite} is true, all values are transferred from one source to the next,
     *         overwriting values that are already there. <p/> If {@code overwrite} is false, values from
     *         one source are only written to the next source where the key is missing or has an empty value
     *         (empty String).
     */
    @Override
    public <E extends Enum<E> & I18NKey> void mergeSources(E sampleKey, Set<Locale> locales, boolean overwrite,
                                                           String... sources) {

        if (sources.length < 2) {
            log.info("At least 2 sources are required for a merge, you have provided {}", sources.length);
            return;
        }

        EnumMap<E, String>[] maps = null;
        for (Locale locale : locales) {
            maps = new EnumMap[sources.length];
            int i = 0;
            for (String source : sources) {
                EnumResourceBundle<E> bundle = getBundle(source, sampleKey, locale);
                maps[i] = bundle.getMap();
                i++;
            }
        }
        mergeMaps(overwrite, maps);
    }

    public <E extends Enum<E> & I18NKey> void mergeMaps(boolean overwrite, EnumMap<E, String>... maps) {
        if (maps.length < 2) {
            log.info("At least 2 sources are required for a merge, you have provided {}", maps.length);
            return;
        }

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

    public <E extends Enum<E> & I18NKey> void mergeMapAndSources(E sampleKey, Set<Locale> locales, boolean overwrite,
                                                                 String... sources) {

    }

    /**
     * Set the value for a key, for a given Locale
     *
     * @param key
     * @param value
     * @param locale
     */
    @Override
    public <E extends Enum<E> & I18NKey> void setKeyValue(String source, E key, Locale locale, String value) {
        Preconditions.checkNotNull(source);
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(locale);
        EnumResourceBundle<E> bundle = getBundle(source, key, locale);
        put(bundle, (E) key, value, true);
    }

    /**
     * resets any changes that have been made in memory and reverts to the version from persistence.  Applies to all
     * supported locales
     *
     * @param sampleKey
     */
    @Override
    public <E extends Enum<E> & I18NKey> void reset(String source, E sampleKey) {
        Preconditions.checkNotNull(source);
        Preconditions.checkNotNull(sampleKey);
        reset(source, sampleKey, supportedLocales);
    }

    /**
     * resets any changes that have been made in memory and reverts to the version from persistence, for all {@code
     * #locales}
     *
     * @param sampleKey
     * @param locales
     */
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
