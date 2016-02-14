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

package uk.q3c.krail.core.validation

import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.util.Modules
import org.apache.bval.guice.ValidationModule
import spock.lang.Specification
import uk.q3c.krail.core.data.TestEntity2
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule
import uk.q3c.krail.core.persist.inmemory.common.InMemoryModule
import uk.q3c.krail.testutil.eventbus.TestEventBusModule
import uk.q3c.krail.testutil.guice.uiscope.TestUIScopeModule
import uk.q3c.krail.testutil.i18n.TestI18NModule
import uk.q3c.krail.testutil.option.TestOptionModule

import javax.validation.ConstraintViolation
import javax.validation.ConstraintViolationException
import javax.validation.MessageInterpolator
import javax.validation.ValidationException

import static com.vaadin.data.Validator.InvalidValueException

/**
 * Integration test for Apache BVal validation
 *
 * Created by David Sowerby on 19/07/15.
 */

class Validation_IntegrationTest extends Specification {

    Injector injector

    MessageInterpolator interpolator;

    TestEntity2 te1

    BeanValidator beanValidator

    def setup() {
        injector = Guice.createInjector(new VaadinSessionScopeModule(), new TestUIScopeModule(), new TestOptionModule(), new TestEventBusModule(), new InMemoryModule(), new TestI18NModule(), Modules.override(new ValidationModule()).with(new KrailValidationModule()))
        interpolator = injector.getInstance(MessageInterpolator.class)
        te1 = new TestEntity2()
        beanValidator = injector.getInstance(BeanValidator.class)
    }

    def "validation fails, javax annotation with no custom message"() {

        given:

        beanValidator.init(TestEntity2.class, "age")
        te1.setAge(4)

        when:

        beanValidator.validate(te1.getAge())


        then:

        InvalidValueException exception = thrown()
        exception.getCauses()[0].getMessage().equals("must be greater than or equal to 5")
    }

    def "validation passes, javax annotation with no custom message"() {

        given:

        beanValidator.init(TestEntity2.class, "age")
        te1.setAge(5)

        when:

        beanValidator.validate(te1.getAge())


        then:

        notThrown(InvalidValueException)
    }

    def "validation fails, javax annotation with custom message"() {

        given:

        beanValidator.init(TestEntity2.class, "height")
        te1.setHeight(19)

        when:

        beanValidator.validate(te1.getHeight())


        then:

        InvalidValueException exception = thrown()
        exception.getCauses()[0].getMessage().equals("a custom message with limit 20")
    }

    def "validation fails, javax annotation with custom message key"() {

        given:

        beanValidator.init(TestEntity2.class, "weight")
        te1.setWeight(19)

        when:

        beanValidator.validate(te1.getWeight())


        then:

        InvalidValueException exception = thrown()
        exception.getCauses()[0].getMessage().equals("is far too big, it should be less than 5")
    }

    def "validation fails, javax annotation with invalid custom message key"() {

        given:

        beanValidator.init(TestEntity2.class, "wrinkles")
        te1.setWrinkles(19)

        when:

        beanValidator.validate(te1.getWrinkles())


        then:

        InvalidValueException exception = thrown()
        exception.getCauses()[0].getMessage().equals("key is null")
    }

    def "custom annotation with no custom message"() {
        given:

        beanValidator.init(TestEntity2.class, "speed")
        te1.setSpeed(19)

        when:

        beanValidator.validate(te1.getSpeed())


        then:

        InvalidValueException exception = thrown()
        exception.getCauses()[0].getMessage().equals("is far too big, it should be less than 1")
    }

    def "custom annotation with custom message key"() {
        given:

        beanValidator.init(TestEntity2.class, "volume")
        te1.setVolume(19)

        when:

        beanValidator.validate(te1.getVolume())


        then:

        InvalidValueException exception = thrown()
        exception.getCauses()[0].getMessage().equals("Must be an Adult")
    }

    def "custom annotation with no messageKey attribute"() {
        given:

        beanValidator.init(TestEntity2.class, "wrongAnnotation")
        te1.setWrongAnnotation(19)

        when:

        beanValidator.validate(te1.getWrongAnnotation())


        then:

        ValidationException exception = thrown()
        exception.getCause().getMessage().equals("A custom validation annotation must have a messageKey() method and return value of type I18NKey")

    }

    def "standard BVal annotation"() {
        given:

        beanValidator.init(TestEntity2.class, "email")
        te1.setEmail("rubbish")

        when:

        beanValidator.validate(te1.getEmail())


        then:

        InvalidValueException exception = thrown()
        exception.getCauses()[0].getMessage().equals("not a well-formed email address")
    }

    def "method annotation with standard javax annotation"() {
        given:

        te1 = injector.getInstance(TestEntity2.class)

        when:
        te1.setANumber(1)

        then:
        ConstraintViolationException exception = thrown()
        def Set<ConstraintViolation<?>> violations = exception.getConstraintViolations()
        violations.iterator().next().message.equals("must be greater than or equal to 100")
    }

    def "method annotation with custom annotation"() {
        given:

        te1 = injector.getInstance(TestEntity2.class)

        when:
        te1.setAnAdult(1)

        then:
        ConstraintViolationException exception = thrown()
        def Set<ConstraintViolation<?>> violations = exception.getConstraintViolations()
        violations.iterator().next().message.equals("Must be an Adult")
    }

}