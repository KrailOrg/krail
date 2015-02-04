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

package uk.q3c.krail.core.validation;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import org.apache.bval.guice.ValidationModule;
import uk.q3c.krail.i18n.I18NKey;

import java.lang.annotation.Annotation;

/**
 * This module defines the Krail specific elements to integrate javax validation with Krail and Vaadin. It adds to
 * and supersedes the module provides with Bval, {@link ValidationModule}
 * <p>
 * Created by David Sowerby on 04/02/15.
 */
public class KrailValidationModule extends AbstractModule {


    private Multibinder<Class<? extends I18NKey>> fieldNameBundles;
    private MapBinder<Class<? extends Annotation>, I18NKey> javaxValidationSubstitutes;

    /**
     * Configures a {@link Binder} via the exposed methods.
     */
    @Override
    protected void configure() {
        TypeLiteral<Class<? extends Annotation>> annotationTypeLiteral = new TypeLiteral<Class<? extends Annotation>>
                () {
        };
        TypeLiteral<I18NKey> i18NKeyTypeLiteral = new TypeLiteral<I18NKey>() {
        };

        TypeLiteral<Class<? extends I18NKey>> i18NclassTypeLiteral = new TypeLiteral<Class<? extends I18NKey>>() {
        };

        javaxValidationSubstitutes = MapBinder.newMapBinder(binder(), annotationTypeLiteral, i18NKeyTypeLiteral,
                JavaxValidationSubstitutes.class);

        fieldNameBundles = Multibinder.newSetBinder(binder(), i18NclassTypeLiteral, FieldNameBundles.class);

        bindMessageInterpolator();
        bindBeanValidator();
        define();
    }

    /**
     * Override this method to make your calls to {@link #addJavaxValidationSubstitute(Class, I18NKey)} and
     * {@link #addFieldNameBundle(Class)}
     */
    protected void define() {

    }

    private void bindBeanValidator() {
        bind(BeanValidator.class).to(DefaultBeanValidator.class);
    }


    private void bindMessageInterpolator() {
        bind(KrailInterpolator.class).to(DefaultKrailInterpolator.class);
    }

    /**
     * Map a validation annotation to an I18NKey.  This allows the default message key associated with the annotation
     * to be replaced by a Krail I18N message where desired.
     *
     * @param annotationClass
     * @param key
     */
    protected void addJavaxValidationSubstitute(Class<? extends Annotation> annotationClass, I18NKey key) {
        // TODO validate the annotation
        javaxValidationSubstitutes.addBinding(annotationClass)
                                  .toInstance(key);
    }

    /**
     * If you want to use {{@link I18NKey}s to translate field (property) names as part of the validation messages, add
     * the bundle class here.  You can specify more than one, but the first one that returns a match for a field name
     * will be used.
     *
     * @param clazz
     */
    protected <E extends Enum<E> & I18NKey> void addFieldNameBundle(Class<E> clazz) {
        fieldNameBundles.addBinding()
                        .toInstance(clazz);
    }
}
