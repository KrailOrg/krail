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
import uk.q3c.krail.core.guice.uiscope.UIScoped;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScoped;
import uk.q3c.krail.core.user.opt.Option;

import javax.annotation.Nonnull;
import java.util.*;

import static com.google.inject.multibindings.Multibinder.newSetBinder;

public class I18NModule extends AbstractModule {

    private MapBinder<String, BundleReader> bundleReaders;
    private MapBinder<String, Set<String>> bundleSourceOrder;
    private Multibinder<String> bundleSourceOrderDefault;
    private Multibinder<String> drillDownExclusions;
    private Multibinder<Locale> supportedLocales;

    @Override
    protected void configure() {
        drillDownExclusions = newSetBinder(binder(), String.class, DrillDownExclusions.class);
        supportedLocales = newSetBinder(binder(), Locale.class, SupportedLocales.class);
        bundleSourceOrderDefault = newSetBinder(binder(), String.class, BundleReaderOrderDefault.class);
        bundleReaders = MapBinder.newMapBinder(binder(), String.class, BundleReader.class);

        TypeLiteral<Set<String>> setString = new TypeLiteral<Set<String>>() {
        };
        TypeLiteral<String> keyClass = new TypeLiteral<String>() {
        };

        bundleSourceOrder = MapBinder.newMapBinder(binder(), keyClass, setString, BundleReaderOrder.class);


        bindProcessor();
        bindCurrentLocale();
        bindDefaultLocale();
        bindTranslate();
        bindPatternSource();
        bindPatternCacheLoader();
        bindPatternUtility();
        bindFieldScanner();
        bindHostClassIdentifier();


        define();
    }

    protected void bindHostClassIdentifier() {
        bind(I18NHostClassIdentifier.class).to(DefaultI18NHostClassIdentifier.class);
    }

    protected void bindFieldScanner() {
        bind(I18NFieldScanner.class).to(DefaultI18NFieldScanner.class);
    }

    protected void bindPatternUtility() {
        bind(PatternUtility.class).to(DefaultPatternUtility.class);
    }

    protected void bindPatternCacheLoader() {
        bind(PatternCacheLoader.class).to(DefaultPatternCacheLoader.class);
    }

    /**
     * It is generally advisable to use the same scope for this as for current locale (see {@link #bindCurrentLocale()}
     */
    protected void bindPatternSource() {
        bind(PatternSource.class).to(DefaultPatternSource.class)
                                 .in(VaadinSessionScoped.class);
    }


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
     * Here you should also define the locales your application supports, with calls to {@link #addSupportedLocale
     * (Locale)}.  Make sure your supportedLocales includes your {@link DefaultLocale}
     * <p>
     * There are some components you know will never have I18N annotations inside them, and you can exclude them from
     * I18N scanning by using {@link #excludeFromI18NDrillDown(String)}
     * <p>
     * The source(s) of your I18N patterns should be defined here using calls to {@link #addBundleReader(String,
     * Class)}.
     * <p>
     * If you are using just a single module to define your {{@link BundleReader} implementations,
     * they will be processed in the order you specify them here.  However, Guice does not guarantee order if multiple
     * MapBinders are combined (through the use of multiple modules) - the order must then be explicitly specified
     * using {{@link #setDefaultBundleReaderOrder(String...)}} and/or {@link #setBundleReaderOrder(String, String...)}
     */
    protected void define() {
        excludeFromI18NDrillDown("com.vaadin");
        addSupportedLocale(Locale.UK);
        addBundleReader("class", ClassBundleReader.class);
        addBundleReader("properties", PropertiesFromClasspathBundleReader.class);
    }


    protected void addSupportedLocale(@Nonnull Locale locale) {
        supportedLocales.addBinding()
                        .toInstance(locale);
    }

    /**
     * There are some components you know will never have I18N annotations inside them, (anything from Vaadin for
     * example).  Normally the default is to drill down into a component for I18N, after it has been processed itself.
     * You can exclude them from drill down by calling this method with enough of a package prefix to identify them
     * (the
     * filtering is done by getClass().getName().startsWith()
     *
     * @param packagePrefix
     */
    protected void excludeFromI18NDrillDown(String packagePrefix) {
        drillDownExclusions.addBinding()
                           .toInstance(packagePrefix);
    }


    /**
     * Adds a bundle reader, identified by {@code source} (source is roughly equivalent to 'format' in the native Java I18N support, except that it does not
     * imply any particular type of source - it is just an identifier)
     *
     * @param source
     *         An arbitrary identifier for a reader implementation- no assumptions are made about the
     *         meaning of the source identifier.
     * @param implementationClass
     *         the class of the BundleReader implementation you want to use for this source
     */
    protected void addBundleReader(String source, Class<? extends BundleReader> implementationClass) {
        bundleReaders.addBinding(source)
                     .to(implementationClass);
    }

    /**
     * Override this method to provide your own implementation of {@link I18NProcessor}
     */
    protected void bindProcessor() {
        bind(I18NProcessor.class).to(DefaultI18NProcessor.class);
    }


    /**
     * This locale is used when all else fails - that is, when the neither the browser locale or user option is valid
     * {@link DefaultCurrentLocale} for more detail. This MUST ALSO be in the {@link #supportedLocales}
     */
    protected void bindDefaultLocale() {
        bind(Locale.class).annotatedWith(DefaultLocale.class)
                          .toInstance(Locale.UK);
    }

    protected void addSupportedLocale(@Nonnull List<String> locales) {
        for (String locale : locales) {
            addSupportedLocale(locale);
        }
    }

    protected void addSupportedLocale(@Nonnull String locale) {
        addSupportedLocale(Locale.forLanguageTag(locale));
    }

    /**
     * If you are using just a single module to define your {{@link BundleReader} implementations, there would normally
     * be no need to use this method.
     * <p>
     * However, Guice does not guarantee order if multiple MapBinders are combined (through the use of multiple
     * modules) - the order must then be explicitly specified using this method.
     * <p>
     * This order is used for ALL key classes, unless overridden by {{@link #setBundleReaderOrder(String, String...)}},
     * or by {@link Option} in {@link DefaultPatternSource}
     * <p>
     * If you have only one source - you definitely won't need this method
     */

    protected void setDefaultBundleReaderOrder(@Nonnull String... sources) {
        for (String source : sources) {
            bundleSourceOrderDefault.addBinding()
                                    .toInstance(source);
        }
    }

    /**
     * {@link #setDefaultBundleReaderOrder(String...)} applies to all key classes, and is usually only needed when
     * combining sources from different modules.
     * <p>
     * This method also sets the order in which to poll the I18N pattern sources, but for a particular bundle (I18NKey
     * class)
     * <p>
     * If you have only one source - you definitely won't need this method
     *
     * @param baseName
     *         the ResourceBundle 'bundleName', for example "Labels"
     * @param sources
     *         a set of sources, (or 'formats' in resourceBundle terms).  These should be all, or a subset, of the
     *         {@link
     *         #bundleReaders} key set
     */

    protected void setBundleReaderOrder(@Nonnull String baseName, @Nonnull String... sources) {
        Set<String> tagSet = new LinkedHashSet<>(Arrays.asList(sources));
        bundleSourceOrder.addBinding(baseName)
                         .toInstance(tagSet);
    }
}
