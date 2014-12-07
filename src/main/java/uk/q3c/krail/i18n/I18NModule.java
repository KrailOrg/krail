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
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScoped;

import java.lang.annotation.Annotation;
import java.util.*;

import static com.google.inject.multibindings.Multibinder.newSetBinder;

public class I18NModule extends AbstractModule {

    private MapBinder<String, BundleReader> bundleReaders;
    private MapBinder<String, Set<String>> bundleSourceOrder;
    private Multibinder<String> bundleSourceOrderDefault;
    private Multibinder<String> registeredAnnotations;
    private Multibinder<String> registeredValueAnnotations;
    private Multibinder<Locale> supportedLocales;

    @Override
    protected void configure() {
        registeredAnnotations = newSetBinder(binder(), String.class, I18N.class);
        registeredValueAnnotations = newSetBinder(binder(), String.class, I18NValue.class);
        supportedLocales = newSetBinder(binder(), Locale.class, SupportedLocales.class);
        bundleSourceOrderDefault = newSetBinder(binder(), String.class, BundleSourceOrderDefault.class);
        bundleReaders = MapBinder.newMapBinder(binder(), String.class, BundleReader.class);

        TypeLiteral<Set<String>> setString = new TypeLiteral<Set<String>>() {
        };
        TypeLiteral<String> keyClass = new TypeLiteral<String>() {
        };

        bundleSourceOrder = MapBinder.newMapBinder(binder(), keyClass, setString, BundleSourceOrder.class);

        registerAnnotation(I18N.class);
        registerAnnotation(I18NFlex.class);
        registerValueAnnotation(I18NValue.class);
        registerValueAnnotation(I18NValueFlex.class);

        bindProcessor();
        bindCurrentLocale();
        bindDefaultLocale();
        bindTranslate();
        bindPatternSource();


        define();
    }

    protected void bindPatternSource() {
        bind(PatternSource.class).to(DefaultPatternSource.class);
    }


    protected void bindTranslate() {
        bind(Translate.class).to(DefaultTranslate.class);
    }

    /**
     * Override this method to provide your own implementation of {@link CurrentLocale} or to change the scope used.
     * Choose
     * between {@link UIScoped} or {@link VaadinSessionScoped}, depending on whether you want users to set the
     * language for each browser tab or each browser instance, respectively.
     */
    protected void bindCurrentLocale() {
        bind(CurrentLocale.class).to(DefaultCurrentLocale.class)
                                 .in(VaadinSessionScoped.class);
    }

    /**
     * Override this to define more registered annotations, or registered value annotations, by calling {@link
     * #registerAnnotation(Class)} or {@link #registerValueAnnotation(Class)}, or make a copy of this module and use
     * the same structure. Multiple instances of the {@link #registeredAnnotations} and {@link
     * #registerValueAnnotation(Class)} will be merged by Guice.
     * <p/>
     * Here you should also define the locales your application supports, with calls to {@link #addSupportedLocale
     * (Locale)}.  Make sure your supportedLocales includes your {@link DefaultLocale}
     * <p/>
     * The source of your I18N patterns (held in ResourceBundles) should be defined here using calls to {@link
     * #addBundleReader(String, Class)}.
     * <p/>
     * If you are using just a single module to define your {{@link BundleReader} implementations,
     * they will be processed in the order you specify them here.  However, Guice does not guarantee order if multiple
     * MapBinders are combined (through the use of multiple modules) - the order must then be explicitly specified
     * using {{@link #setDefaultBundleSourceOrder(String...)}} and/or {@link #setBundleSourceOrder(Class, String...)}
     */
    protected void define() {
        addSupportedLocale(Locale.UK);
        addBundleReader("class", ClassBundleReader.class);
        addBundleReader("properties", PropertiesFromClasspathBundleReader.class);
    }


    protected void addSupportedLocale(Locale locale) {
        supportedLocales.addBinding()
                        .toInstance(locale);
    }


    /**
     * Adds a bundle reader, identified by {@code format}
     *
     * @param format
     *         It would be neater for Krail just to use injected readers, and remove the need for the
     *         format property - but some {@link ResourceBundle} caching logic - including the construction of a cache
     *         key -
     *         use the value of the format property, so it has been retained.  You can use any identifier
     *         unique to the BundleReader implementations you are using - no assumptions are made about the
     *         meaning of the format..
     * @param implementationClass
     *         the class of the BundleReader implementation you want to use for this format value
     */
    protected void addBundleReader(String format, Class<? extends BundleReader> implementationClass) {
        bundleReaders.addBinding(format)
                     .to(implementationClass);
    }

    /**
     * Override this method to provide your own implementation of {@link I18NProcessor}
     */
    protected void bindProcessor() {
        bind(I18NProcessor.class).to(DefaultI18NProcessor.class);
    }

    protected <T extends Annotation> void registerAnnotation(Class<T> i18Nclass) {
        registeredAnnotations.addBinding()
                             .toInstance(i18Nclass.getName());
    }

    protected <T extends Annotation> void registerValueAnnotation(Class<T> i18Nclass) {
        registeredValueAnnotations.addBinding()
                                  .toInstance(i18Nclass.getName());
    }

    /**
     * This locale is used when all else fails - that is, when the neither the browser locale or user option is valid
     * {@link DefaultCurrentLocale} for more detail. This MUST ALSO be in the {@link #supportedLocales}
     */
    protected void bindDefaultLocale() {
        bind(Locale.class).annotatedWith(DefaultLocale.class)
                          .toInstance(Locale.UK);
    }

    protected void addSupportedLocale(List<String> locales) {
        for (String locale : locales) {
            addSupportedLocale(locale);
        }
    }

    protected void addSupportedLocale(String locale) {
        addSupportedLocale(Locale.forLanguageTag(locale));
    }

    /**
     * This method overrides the order of processing sources when looking for a resource bundle.  If this method is not
     * called, sources will be processed in the order they are added using {@link #addPatternSource(Integer, Class)}.
     * <p/>
     * If you are using just a single module to define your {{@link BundleReader} implementations, there would normally
     * be no need to use this method.
     * <p/>
     * However, Guice does not guarantee order if multiple MapBinders are combined (through the use of multiple
     * modules) - the order must then be explicitly specified using this method.
     * <p/>
     * This order is used for ALL key classes, unless overridden by {{@link #setBundleSourceOrder(Class, String...)}},
     * or by UserOption in {@link DefaultPatternSource}
     * <p/>
     * See {@link EnumResourceBundleControl#getFormats(String)} for a full description of the logic of selecting the
     * order.
     * <p/>
     * If you have only one source - you definitely won't need this method
     */

    protected void setDefaultBundleSourceOrder(String... tags) {
        for (String tag : tags) {
            bundleSourceOrderDefault.addBinding()
                                    .toInstance(tag);
        }
    }

    /**
     * {@link #setDefaultBundleSourceOrder(String...)} applies to all key classes, and is usually only needed when
     * combining sources from different modules.
     * <p/>
     * See {@link EnumResourceBundleControl#getFormats(String)} for a full description of the logic of selecting the
     * order.
     * <p/>
     * If you have only one source - you definitely won't need this method
     *
     * @param baseName
     *         the ResourceBundle 'bundleName', for example "Labels"
     * @param sources
     *         a set of sources, (or 'formats' in resourceBundle terms).  These should be all, or a subset, of the
     *         {@link
     *         #bundleReaders} key set
     */

    protected void setBundleSourceOrder(String baseName, String... sources) {
        Set<String> tagSet = new LinkedHashSet<>(Arrays.asList(sources));
        bundleSourceOrder.addBinding(baseName)
                         .toInstance(tagSet);
    }
}
