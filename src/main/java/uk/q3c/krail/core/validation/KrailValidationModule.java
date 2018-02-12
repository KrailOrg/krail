/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */

package uk.q3c.krail.core.validation;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import org.apache.bval.constraints.Email;
import org.apache.bval.constraints.NotEmpty;
import org.apache.bval.guice.ValidationModule;
import org.apache.bval.jsr303.ApacheValidatorFactory;
import uk.q3c.krail.i18n.I18NKey;

import javax.validation.MessageInterpolator;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.lang.annotation.Annotation;

/**
 * This module defines the Krail specific elements to integrate javax validation with Krail and Vaadin. It adds to
 * and supersedes the module provides with Bval, {@link ValidationModule}
 * <p>
 * Created by David Sowerby on 04/02/15.
 */
public class KrailValidationModule extends AbstractModule {


    private MapBinder<Class<? extends Annotation>, I18NKey> javaxValidationSubstitutes;

    /**
     * Configures a {@link Binder} via the exposed methods.
     */
    @Override
    protected void configure() {


        TypeLiteral<Class<? extends Annotation>> annotationTypeLiteral = new TypeLiteral<Class<? extends Annotation>>() {
        };
        TypeLiteral<I18NKey> i18NKeyTypeLiteral = new TypeLiteral<I18NKey>() {
        };

        javaxValidationSubstitutes = MapBinder.newMapBinder(binder(), annotationTypeLiteral, i18NKeyTypeLiteral, JavaxValidationSubstitutes.class);


        bindMessageInterpolator();
//        bindBeanValidator();
        substituteJavaxMessagesWithKrailKeys();
        define();
    }


    protected void substituteJavaxMessagesWithKrailKeys() {
        addJavaxValidationSubstitute(Min.class, ValidationKey.Min);
        addJavaxValidationSubstitute(Max.class, ValidationKey.Max);
        addJavaxValidationSubstitute(Size.class, ValidationKey.Size);
        addJavaxValidationSubstitute(Digits.class, ValidationKey.Digits);
        addJavaxValidationSubstitute(AssertFalse.class, ValidationKey.AssertFalse);
        addJavaxValidationSubstitute(AssertTrue.class, ValidationKey.AssertTrue);
        addJavaxValidationSubstitute(DecimalMax.class, ValidationKey.DecimalMax);
        addJavaxValidationSubstitute(DecimalMin.class, ValidationKey.DecimalMin);
        addJavaxValidationSubstitute(Email.class, ValidationKey.Email);
        addJavaxValidationSubstitute(Future.class, ValidationKey.Future);
        addJavaxValidationSubstitute(Past.class, ValidationKey.Past);
        addJavaxValidationSubstitute(NotEmpty.class, ValidationKey.NotEmpty);
        addJavaxValidationSubstitute(NotNull.class, ValidationKey.NotNull);
        addJavaxValidationSubstitute(Null.class, ValidationKey.Null);
        addJavaxValidationSubstitute(Pattern.class, ValidationKey.Pattern);
    }

    /**
     * Map a validation annotation to an I18NKey.  This allows the default message key associated with the annotation
     * to be replaced by a Krail I18N message where desired. Called by {@link #substituteJavaxMessagesWithKrailKeys} to map ALL the BVal annotations to Krail
     * keys, so it is unlikely there will be a need to call this method directly
     *
     * @param annotationClass
     *         the BVal (or javax) annotation
     * @param key
     *         the I18NKey to use as the message pattern
     */
    protected void addJavaxValidationSubstitute(Class<? extends Annotation> annotationClass, I18NKey key) {
        javaxValidationSubstitutes.addBinding(annotationClass)
                                  .toInstance(key);
    }

    /**
     * Override this method to make your calls to {@link #addJavaxValidationSubstitute(Class, I18NKey)}
     */
    protected void define() {

    }

//    private void bindBeanValidator() {
//        bind(BeanValidator.class).to(DefaultBeanValidator.class);
//    }

    protected void bindMessageInterpolator() {
        bind(MessageInterpolator.class).to(KrailInterpolator.class);
    }

    @Provides
    protected Validator validatorProvider(KrailInterpolator interpolator) {
        ApacheValidatorFactory validatorFactory = (ApacheValidatorFactory) Validation.buildDefaultValidatorFactory();
        validatorFactory.setMessageInterpolator(interpolator);
        return validatorFactory.getValidator();
    }


}
