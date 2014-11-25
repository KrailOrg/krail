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
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.user.opt.UserOption;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * The default implementation of {@link PatternSource} to retrieve a localised pattern from an  {{@link
 * EnumResourceBundle}.
 * <p/>
 * <p/>
 * Created by David Sowerby on 04/11/14.
 */
public class DefaultPatternSource implements PatternSource {
    private static Logger log = LoggerFactory.getLogger(DefaultPatternSource.class);
    private EnumResourceBundleControl bundleControl;
    private Set<Locale> supportedLocales;
    private UserOption userOption;

    @Inject
    protected DefaultPatternSource(@SupportedLocales Set<Locale> supportedLocales, UserOption userOption, Map<String,
            BundleReader> bundleReaders) {
        this.supportedLocales = supportedLocales;
        this.userOption = userOption;
        this.bundleControl = new EnumResourceBundleControl(bundleReaders);
    }

    /**
     * Returns the translated String pattern for {@code key}, for {@code locale}, or {@link Optional.isAbsent()} if
     * there is no pattern for the key.
     * <p/>
     * This implementation uses a map to define the patterns for translation.  The {@link MapResourceBundle} used to
     * locate the pattern is determined by the actual parameter value of key (that is, a key of type I18N<Labels> will
     * use the Labels class as the map source).
     * <p/>
     * See https://docs.oracle.com/javase/6/docs/api/java/util/ResourceBundle.html for a description of ResourceBundle,
     * of which {@link MapResourceBundle} is a sub-class.
     * <p/>
     *
     * @param key
     *         the key to look up
     * @param locale
     *
     * @return the String pattern for {@code key}, or {@link Optional.isAbsent()} if there is no pattern for the key
     */
    @Override
    public <E extends Enum<E>> Optional<String> retrievePattern(I18NKey key, Locale locale) {

        EnumResourceBundle<E> bundle = getBundle(key, locale);

        if (getAutoStub()) {
            // this method does not overwrite an existing entry
            generateStub(key, locale);
        }
        String pattern = bundle.getValue((E) key);
        if (pattern == null) {
            return Optional.absent();
        }
        return Optional.of(pattern);

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
    public <E extends Enum<E>> void generateStub(I18NKey key, Set<Locale> locales) {
        for (Locale locale : locales) {
            generateStub(key, locale);
        }
    }

    /**
     * Generates an implementation specific stub for the key - value pair.  This is typically used as part of the
     * process to generate files for translation.  Does not overwrite an existing key
     * <p/>
     * If user option #generateStubWithName is true, the value for the key is set to the name of the key, otherwise it
     * is set to an empty String
     *
     * @param key
     *         the key the stub will be for
     * @param locale
     *         the locale the stub should be generated in
     */
    @Override
    public <E extends Enum<E>> void generateStub(I18NKey key, Locale locale) {

        String value = "";
        if (getGenerateStubWithName()) {
            value = ((Enum<?>) key).name()
                                   .replace("_", " ");
        }
        put(locale, key, value, false);

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
     * @param <E> the Enum type derived from the key
     */
    public <E extends Enum<E>> void put(Locale locale, I18NKey key, String value, boolean overwrite) {
        EnumResourceBundle<E> bundle = getBundle(key, locale);
        put(bundle, (E) key, value, overwrite);
    }

    public <E extends Enum<E>> EnumResourceBundle<E> getBundle(I18NKey key, Locale locale) {
        Class bundleClazz = bundleClass(key);
        EnumResourceBundle<E> bundle = (EnumResourceBundle) ResourceBundle.getBundle(bundleClazz.getName(), locale,
                bundleControl);
        return bundle;
    }

    /**
     * The {@link MapResourceBundle} used to locate the pattern is determined by the actual parameter value of key
     * (that is, a key of type I18N<Labels>  will use the Labels class as the map source)
     */
    private Class bundleClass(I18NKey key) {
        return bundleClass(key.getClass());
    }

    private Class bundleClass(Class<? extends I18NKey> keyClass) {
        Type[] genericInterfaces = keyClass.getGenericInterfaces();
        ParameterizedType parameterizedType = (ParameterizedType) genericInterfaces[0];

        Type actualType = parameterizedType.getActualTypeArguments()[0];
        return (Class) actualType;
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
    public <E extends Enum<E>> void put(EnumResourceBundle<E> bundle, E key, String value, boolean overwrite) {

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

    public boolean getGenerateStubWithName() {
        return userOption.getOptionAsBoolean(getClass().getSimpleName(), OptionProp.generateStubWithName.name(), true);
    }

    public void setGenerateStubWithName(boolean value) {
        userOption.setOption(getClass().getSimpleName(), OptionProp.generateStubWithName.name(), value);
    }

    /**
     * Generates implementation specific stubs for all the supported locales{@code locales}.
     *
     * @param key
     *         the the stub(s) will be for
     */
    @Override
    public void generateStub(I18NKey key) {
        for (Locale locale : supportedLocales) {
            generateStub(key, locale);
        }

    }

    /**
     * Write the key-value set(s) for {@code keyClass}, all supported locales, to persistence.  Individual
     * implementations will provide their own methods for setting up file paths, database connection or other
     * pre-requisites, typically but not necessarily through UserOption
     */
    @Override
    public <E extends Enum<E>> void writeOut(BundleWriter<E> writer, Class<? extends I18NKey> keyClass, boolean
            allKeys) throws IOException {

        writeOut(writer, keyClass, supportedLocales, allKeys);

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
    public <E extends Enum<E>> void writeOut(BundleWriter<E> writer, Class<? extends I18NKey> keyClass, Set<Locale>
            locales, boolean allKeys) throws IOException {
        for (Locale locale : locales) {
            EnumResourceBundle<E> bundle = getBundle(keyClass, locale);
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

    public <E extends Enum<E>> EnumResourceBundle<E> getBundle(Class<? extends I18NKey> keyClass, Locale locale) {
        Class bundleClazz = bundleClass(keyClass);
        EnumResourceBundle<E> bundle = (EnumResourceBundle) EnumResourceBundle.getBundle(bundleClazz.getName(),
                locale, bundleControl);
        return bundle;
    }

    /**
     * Merge key-value pairs from {@code otherSource} into this source, for the given {@code locales}.
     * <p/>
     * If {@code overwrite} is true, all values are transferred from otherSource to this source, overwriting any
     * values that are already in this source. <p/> If {@code overwrite} is false, values from {@code otherSource}
     * are only written to this source where the key is missing or has an empty value (empty String).
     *
     * @param locales
     * @param otherSource
     * @param overwrite
     */
    @Override
    public <E extends Enum<E>> void mergeSource(Class<? extends I18NKey> keyClass, Set<Locale> locales, PatternSource
            otherSource, boolean overwrite) {
        for (Locale locale : locales) {
            EnumResourceBundle<E> bundle = getBundle(keyClass, locale);
            mergeSource(locale, bundle.getMap(), overwrite);
        }
    }

    /**
     * Merge key-value pairs from {@code otherSource} into this source, for the given {@code locale}.  This method is
     * generally used for merging key-value pairs from an external source.  If you are merging another PatternSource
     * implementation, {@link #mergeSource(Set, PatternSource)} is probably a better option.
     * <p/>
     * If {@code overwrite} is true, all values are transferred from otherSource to this source, overwriting any
     * values that are already in this source. <p/> If {@code overwrite} is false, values from {@code otherSource}
     * are only written to this source where the key is missing or has an empty value (empty String).
     *
     * @param locale
     * @param otherSource
     * @param overwrite
     */
    @Override
    public <E extends Enum<E>> void mergeSource(Locale locale, EnumMap<E, String> otherSource, boolean overwrite) {
        E sampleKey = otherSource.keySet()
                                 .iterator()
                                 .next();
        EnumResourceBundle<E> bundle = getBundle((I18NKey) sampleKey, locale);


        // the rules for overwriting are determined by {@link #put}
        for (Map.Entry<E, String> otherEntry : otherSource.entrySet()) {
            put(bundle, otherEntry.getKey(), otherEntry.getValue(), overwrite);
        }


    }

    /**
     * Set the value for a key, for a given Locale
     *
     * @param key
     * @param value
     * @param locale
     */
    @Override
    public <E extends Enum<E>> void setKeyValue(I18NKey key, Locale locale, String value) {
        EnumResourceBundle<E> bundle = getBundle(key, locale);
        put(bundle, (E) key, value, true);
    }

    /**
     * resets any changes that have been made in memory and reverts to the version from persistence.  Applies to all
     * supported locales
     *
     * @param sampleKey
     */
    @Override
    public <E extends Enum<E>> void reset(Class<? extends I18NKey> keyClass) {
        reset(keyClass, supportedLocales);
    }

    /**
     * resets any changes that have been made in memory and reverts to the version from persistence, for all {@code
     * #locales}
     *
     * @param sampleKey
     * @param locales
     */
    @Override
    public <E extends Enum<E>> void reset(Class<? extends I18NKey> keyClass, Set<Locale> locales) {
        for (Locale locale : locales) {
            EnumResourceBundle<E> bundle = getBundle(keyClass, locale);
            bundle.reset();
            log.debug("Reset values from persistence, {} keys, locale {}", keyClass, locale);
        }
    }

    public boolean getAutoStub() {
        return userOption.getOptionAsBoolean(getClass().getSimpleName(), OptionProp.autoStub.name(), false);
    }

    public void setAutoStub(boolean value) {
        userOption.setOption(getClass().getSimpleName(), OptionProp.autoStub.name(), value);
    }

    private enum OptionProp {
        generateStubWithName, autoStub
    }


}
