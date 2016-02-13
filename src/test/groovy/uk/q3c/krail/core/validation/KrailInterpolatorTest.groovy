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

import org.apache.bval.constraints.Email
import spock.lang.Specification
import uk.q3c.krail.core.i18n.CurrentLocale
import uk.q3c.krail.core.i18n.I18NKey
import uk.q3c.krail.core.i18n.Translate

import javax.validation.constraints.Min
import javax.validation.metadata.ConstraintDescriptor
import java.lang.annotation.Annotation
/**
 * Created by david on 18/07/15.
 */

class KrailInterpolatorTest extends Specification {


    SimpleContext context = Mock()

    ConstraintDescriptor constraintDescriptor = Mock();

    Annotation annotation = Mock()

    Map<Class<? extends Annotation>, I18NKey> javaxValidationSubstitutes= Mock()
//    Injector injector;

    KrailInterpolator interpolator;
    Translate translate = Mock()
    CurrentLocale currentLocale = Mock()

    def setup() {

        interpolator = new KrailInterpolator(currentLocale, translate, javaxValidationSubstitutes)
        context.getConstraintDescriptor() >> constraintDescriptor
        constraintDescriptor.getAnnotation() >> annotation
    }

    def "isJavaxAnnotation() returns true for a javax annotation"() {
        
        given:
        annotation.annotationType() >> Min.class

        expect: interpolator.isJavaxAnnotation(context)==true
        
    }

    def "isJavaxAnnotation() returns false for a custom annotation"() {

        given:

        annotation.annotationType() >> Adult.class

        expect: interpolator.isJavaxAnnotation(context)==false

    }

    def "isJavaxAnnotation() returns false for a BVal extension annotation"() {

        given:

        annotation.annotationType() >> Email.class

        expect: interpolator.isJavaxAnnotation(context)==false

    }

    def "isBValAnnotation() returns false for a javax annotation"() {

        given:
        annotation.annotationType() >> Min.class

        expect: interpolator.isBValAnnotation(context)==false

    }

    def "isBValAnnotation() returns false for a custom annotation"() {

        given:

        annotation.annotationType() >> Adult.class

        expect: interpolator.isBValAnnotation(context)==false

    }

    def "isBValAnnotation() returns true for a BVal extension annotation"() {

        given:

        annotation.annotationType() >> Email.class

        expect: interpolator.isBValAnnotation(context)==true

    }

    def "patternOrKey returns correctly for pattern and key"() {
        expect:

        interpolator.isPattern("{com.anything}") == false
        interpolator.isPattern("{com.anything") == true
        interpolator.isPattern("com.anything") == true
        interpolator.isPattern("com.anything}") == true
    }


}
