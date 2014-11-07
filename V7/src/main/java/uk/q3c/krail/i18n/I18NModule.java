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
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import uk.q3c.krail.base.guice.vsscope.VaadinSessionScoped;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Locale;

import static com.google.inject.multibindings.Multibinder.newSetBinder;

public class I18NModule extends AbstractModule {

    private MapBinder<Integer, PatternSource> patternSources;
    private Multibinder<String> registeredAnnotations;
    private Multibinder<String> registeredValueAnnotations;
    private Multibinder<Locale> supportedLocales;

    @Override
    protected void configure() {
        registeredAnnotations = newSetBinder(binder(), String.class, I18N.class);
        registeredValueAnnotations = newSetBinder(binder(), String.class, I18NValue.class);
        supportedLocales = newSetBinder(binder(), Locale.class, SupportedLocales.class);
        patternSources = MapBinder.newMapBinder(binder(), Integer.class, PatternSource.class, PatternSources.class);

        registerAnnotation(I18N.class);
        registerAnnotation(I18NFlex.class);
        registerValueAnnotation(I18NValue.class);
        registerValueAnnotation(I18NValueFlex.class);

        bindProcessor();
        bindCurrentLocale();
        bindDefaultLocale();
        bindTranslate();

        define();
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
     * Override this to define more registered annotations, or registered value annotations, by calling
     * {@link #registerAnnotation(Class)} or {@link #registerValueAnnotation(Class)}, or make a copy of this module and
     * use the same structure (multiple instances of the {@link #registeredAnnotations} and
     * {@link #registerValueAnnotation(Class)} will be merged by Guice}.
     * <p/>
     * Here you should also define the locales your application supports, with calls to
     * {@link #addSupportedLocale(Locale)}.  Make sure this includes the {@link DefaultLocale}
     */
    protected void define() {
        addSupportedLocale(Locale.UK);
        addPatternSource(10, JavaMapPatternSource.class);
    }

    protected void addSupportedLocale(Locale locale) {
        supportedLocales.addBinding()
                        .toInstance(locale);
    }

    /**
     * Add a source for I18NKey patterns. {@code order} determines the order in which multiple sources are accessed.
     * {@link DefaultTranslate} uses the first result returned, using ascending order (order need not be sequential,
     * but
     * must be unique).
     * <p/>
     * Other implementations of {@link Translate} may behave differently
     *
     * @param order
     * @param patternSource
     */
    protected void addPatternSource(Integer order, Class<? extends PatternSource> patternSource) {
        patternSources.addBinding(order)
                      .to(patternSource);
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
}
