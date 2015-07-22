/*
 * Copyright (C) 2013 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.q3c.krail.i18n;


import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import org.apache.commons.lang3.LocaleUtils;
import uk.q3c.krail.core.guice.uiscope.UIScoped;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScoped;
import uk.q3c.krail.core.persist.DefaultActivePatternDao;
import uk.q3c.krail.core.persist.InMemoryBundleReader;
import uk.q3c.krail.core.user.opt.InMemory;
import uk.q3c.krail.core.user.opt.Option;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.inject.multibindings.Multibinder.newSetBinder;

public class I18NModule extends AbstractModule {

    private Class<? extends Annotation> activeDaoAnnotation;
    private MapBinder<String, BundleReader> bundleSources;
    private MapBinder<String, Set<String>> bundleSourcesOrder;
    private Multibinder<String> bundleSourcesOrderDefault;
    private Locale defaultLocale = Locale.UK;
    private Map<String, Class<? extends BundleReader>> prepBundleSources = new LinkedHashMap<>(); // retain order;
    private Map<String, Set<String>> prepBundleSourcesOrder = new LinkedHashMap<>();
    private Set<String> prepBundleSourcesOrderDefault = new LinkedHashSet<>();

    private Set<Locale> prepSupportedLocales = new LinkedHashSet<>();
    private Multibinder<Locale> supportedLocales;

    @Override
    protected void configure() {
        supportedLocales = newSetBinder(binder(), Locale.class, SupportedLocales.class);
        bundleSourcesOrderDefault = newSetBinder(binder(), String.class, BundleSourcesOrderDefault.class);
        bundleSources = MapBinder.newMapBinder(binder(), String.class, BundleReader.class);

        define();

        TypeLiteral<Set<String>> setString = new TypeLiteral<Set<String>>() {
        };
        TypeLiteral<String> keyClass = new TypeLiteral<String>() {
        };

        bundleSourcesOrder = MapBinder.newMapBinder(binder(), keyClass, setString, BundleSourcesOrder.class);

        bindProcessor();
        bindCurrentLocale();

        bindDefaultLocale();
        bindTranslate();
        bindPatternSource();
        bindPatternCacheLoader();
        bindPatternUtility();
        bindFieldScanner();
        bindHostClassIdentifier();
        //        bindDatabaseBundleReader();

        bindSupportedLocales();
        bindBundleSources();
        bindBundleSourcesOrderDefault();
        bindBundleSourcesOrder();
        bindDao();


    }

    /**
     * Binds the active Dao, or if none has been defined, uses {@link InMemory}
     */
    protected void bindDao() {
        Class<? extends Annotation> annotationClass = (activeDaoAnnotation == null) ? InMemory.class : activeDaoAnnotation;
        TypeLiteral<Class<? extends Annotation>> annotationTypeLiteral = new TypeLiteral<Class<? extends Annotation>>() {
        };
        bind(annotationTypeLiteral).annotatedWith(DefaultActivePatternDao.class)
                                   .toInstance(annotationClass);
    }


    /**
     * Binds sources to {@link BundleReader} classes as defined by {@link #prepBundleSources}, setting "class",{@link ClassBundleReader} as default if nothing
     * defined.
     */
    public void bindBundleSources() {
        if (prepBundleSources.isEmpty()) {
            prepBundleSources.put("class", ClassBundleReader.class);
        }
        for (Map.Entry<String, Class<? extends BundleReader>> entry : prepBundleSources.entrySet()) {
            bundleSources.addBinding(entry.getKey())
                         .to(entry.getValue());
        }
    }

    /**
     * Binds {@link Locale} in {@link SupportedLocales} as defined by {@link #prepSupportedLocales}, setting {@link Locale#UK} as default if nothing defined.
     */
    protected void bindSupportedLocales() {
        if (prepSupportedLocales.isEmpty()) {
            prepSupportedLocales.add(Locale.UK);
        }
        for (Locale locale : prepSupportedLocales) {
            supportedLocales.addBinding()
                            .toInstance(locale);
        }
    }

    /**
     * See javadoc for {@link I18NHostClassIdentifier} for an explanation of what this is for.  Override this method if you provide your own implementation
     */
    protected void bindHostClassIdentifier() {
        bind(I18NHostClassIdentifier.class).to(DefaultI18NHostClassIdentifier.class);
    }

    /**
     * See javadoc for {@link I18NFieldScanner} for an explanation of what this is for.  Override this method if you provide your own implementation
     */
    protected void bindFieldScanner() {
        bind(I18NFieldScanner.class).to(DefaultI18NFieldScanner.class);
    }

    /**
     * See javadoc for {@link PatternUtility} for an explanation of what this is for.  Override this method if you provide your own implementation
     */
    protected void bindPatternUtility() {
        bind(PatternUtility.class).to(DefaultPatternUtility.class);
    }

    /**
     * See javadoc for {@link DefaultPatternCacheLoader} for an explanation of what this is for.  Override this method if you provide your own implementation
     */
    protected void bindPatternCacheLoader() {
        bind(PatternCacheLoader.class).to(DefaultPatternCacheLoader.class);
    }

    /**
     * It is generally advisable to use the same scope for this as for current locale (see {@link #bindCurrentLocale()}.   See javadoc for {@link
     * DefaultPatternSource} for an explanation of what this is for.  Override this method if you provide your own implementation
     */
    protected void bindPatternSource() {
        bind(PatternSource.class).to(DefaultPatternSource.class)
                                 .in(VaadinSessionScoped.class);
    }

    /**
     * See javadoc for {@link DefaultTranslate} for an explanation of what this is for.  Override this method if you provide your own implementation
     */
    protected void bindTranslate() {
        bind(Translate.class).to(DefaultTranslate.class);
    }

    /**
     * Override this method to provide your own implementation of {@link CurrentLocale} or to change the scope used.
     * Choose between {@link UIScoped} or {@link VaadinSessionScoped}, depending on whether you want users to set the
     * language for each browser tab or each browser instance, respectively.
     */
    protected void bindCurrentLocale() {
        bind(CurrentLocale.class).to(DefaultCurrentLocale.class)
                                 .in(VaadinSessionScoped.class);
    }

    /**
     * If you don't wish to configure this module from your Binding Manager, sub-class and override this method to define calls to {@link
     * #supportedLocales(Locale...)}, {@link #defaultLocale(Locale)} etc - then modify your Binding Manager to use your sub-class
     * <p>
     * If you are using a single module to define all your bundle sources, they will be processed in the order you specify them by calls to {@link
     * #bundleSource(String, Class)}.
     * However, Guice does not guarantee order if multiple MapBinders are combined (through the use of multiple modules) - the order must then be explicitly
     * specified using {{@link #bundleSourcesOrderDefault(String...)}} and/or {@link #bundleSourcesOrder(String, String...)}
     */
    protected void define() {
    }

    /**
     * Override this method to provide your own implementation of {@link I18NProcessor}
     */
    protected void bindProcessor() {
        bind(I18NProcessor.class).to(DefaultI18NProcessor.class);
    }

    /**
     * Binds {{@link #defaultLocale} to annotation {@link DefaultLocale}
     */
    protected void bindDefaultLocale() {
        bind(Locale.class).annotatedWith(DefaultLocale.class)
                          .toInstance(defaultLocale);
    }

    protected void bindBundleSourcesOrderDefault() {
        for (String source : prepBundleSourcesOrderDefault) {
            bundleSourcesOrderDefault.addBinding()
                                     .toInstance(source);
        }

    }

    protected void bindBundleSourcesOrder() {
        for (Map.Entry<String, Set<String>> entry : prepBundleSourcesOrder.entrySet()) {
            bundleSourcesOrder.addBinding(entry.getKey())
                              .toInstance(entry.getValue());
        }

    }

    /**
     * This locale is used when all else fails - that is, when the neither the browser locale or user option is valid {@link DefaultCurrentLocale} for more
     * detail. This is also added to {@link #supportedLocales}, so if you only ant to support one Locale, just call this method.
     *
     * @param localeString
     *         valid locale string to be used as default
     *
     * @return this for fluency
     *
     * @throw IllegalArgumentException if the locale string is invalid (see {@link LocaleUtils#toLocale(String)} for format)
     */
    public I18NModule defaultLocale(@Nonnull String localeString) {
        checkNotNull(localeString);
        checkArgument(!localeString.isEmpty());
        defaultLocale(localeFromString(localeString));
        return this;
    }

    /**
     * This locale is used when all else fails - that is, when the neither the browser locale or user option is valid.  See {@link DefaultCurrentLocale} for
     * more
     * detail. This is also added to {@link #supportedLocales}, so if you only want to support one Locale, just call this method.
     *
     * @param locale
     *         Locale object for the default
     */
    public I18NModule defaultLocale(@Nonnull Locale locale) {
        checkNotNull(locale);
        defaultLocale = locale;
        prepSupportedLocales.add(defaultLocale);
        return this;
    }

    /**
     * Converts String to Locale, strictly
     *
     * @param localeString
     *         the String to convert, see {@link LocaleUtils#toLocale(String)} for format
     *
     * @return selected Locale
     *
     * @throws IllegalArgumentException
     *         if the {@code localeString} is not valid
     */
    protected Locale localeFromString(String localeString) {
        return LocaleUtils.toLocale(localeString);
    }

    /**
     * These are the locales that you will provide language support for.  Attempts to change to any other Locale will throw an exception.  {@link
     * #defaultLocale} is automatically added
     *
     * @param locales
     *         the locales to support
     *
     * @return this for fluency
     */
    public I18NModule supportedLocales(@Nonnull Locale... locales) {
        for (Locale locale : locales) {
            prepSupportedLocales.add(locale);
        }
        return this;
    }

    /**
     * These are the locales that you will provide language support for.  Attempts to change to any other Locale will throw an exception.  {@link
     * #defaultLocale} is automatically added
     *
     * @param localeStrings
     *         the locales to support
     *
     * @return this for fluency
     *
     * @throw IllegalArgumentException if a locale string is invalid (see {@link LocaleUtils#toLocale(String)} for format)
     */
    public I18NModule supportedLocales(@Nonnull String... localeStrings) {
        for (String localeString : localeStrings) {
            prepSupportedLocales.add(localeFromString(localeString));
        }
        return this;
    }

    /**
     * If you are using just a single module to define your bundle sources, there is no need to use this method.
     * <p>
     * However, Guice does not guarantee order if multiple MapBinders are combined (through the use of multiple modules) - the order must then be explicitly
     * specified using this method.
     * <p>
     * This order is used for ALL key classes, unless overridden by {{@link #bundleSourcesOrder(String, String...)}},
     * or by {@link Option} in {@link DefaultPatternSource}
     * <p>
     * If you have only one source - you definitely won't need this method
     *
     * @return this for fluency
     */

    public I18NModule bundleSourcesOrderDefault(@Nonnull String... sources) {
        checkNotNull(sources);
        for (String source : sources) {
            prepBundleSourcesOrderDefault.add(source);
        }
        return this;
    }

    /**
     * This method sets the order in which to poll the I18N pattern sources, but for a specific bundle (I18NKey
     * class)
     * <p>
     * {@link #bundleSourcesOrderDefault(String...)} applies to all key classes, and is usually only needed when
     * combining sources from different modules.
     * <p>
     * <p>
     * <p>
     * If you have only one source - you definitely won't need this method
     *
     * @param baseName
     *         the ResourceBundle 'bundleName', for example "Labels", as defined by {@link I18NKey#bundleName()}
     * @param sources
     *         a set of sources, (or 'formats' in resourceBundle terms).  These should be all, or a subset, of the
     *         {@link #bundleSources} key set
     *
     * @return this for fluency
     */

    protected I18NModule bundleSourcesOrder(@Nonnull String baseName, @Nonnull String... sources) {
        checkNotNull(baseName);
        checkNotNull(sources);
        Set<String> tagSet = new LinkedHashSet<>(Arrays.asList(sources));
        prepBundleSourcesOrder.put(baseName, tagSet);
        return this;
    }

    public I18NModule activeDao(Class<? extends Annotation> annotationClass) {
        activeDaoAnnotation = annotationClass;
        return this;
    }

    /**
     * Sets up a "database" reader for use with the in memory store
     *
     * @return this for fluency
     */
    public I18NModule inMemory() {
        bundleSource("in memory", InMemoryBundleReader.class);
        return this;
    }

    /**
     * Adds a bundle source, identified by {@code source} (source is roughly equivalent to 'format' in the native Java I18N support, except that it does not
     * imply any particular type of source - it is just an identifier)
     *
     * @param source
     *         An arbitrary identifier for a reader implementation- no assumptions are made about the meaning of the source identifier.
     * @param implementationClass
     *         the class used to read a bundle
     *
     * @return this for fluency
     */
    public I18NModule bundleSource(@Nonnull String source, @Nonnull Class<? extends BundleReader> implementationClass) {
        checkNotNull(source);
        checkNotNull(implementationClass);
        prepBundleSources.put(source, implementationClass);
        return this;
    }
}

