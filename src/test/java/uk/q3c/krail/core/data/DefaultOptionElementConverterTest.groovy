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

package uk.q3c.krail.core.data

import org.apache.shiro.authz.annotation.RequiresAuthentication
import spock.lang.Specification
import uk.q3c.krail.core.config.DefaultApplicationConfiguration
import uk.q3c.krail.core.i18n.I18NKey
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.core.option.AnnotationOptionList
import uk.q3c.krail.core.option.OptionList
import uk.q3c.krail.core.persist.clazz.i18n.ClassPatternSource
import uk.q3c.krail.core.services.Service

import java.time.LocalDateTime
/**
 * Created by David Sowerby on 21 Jan 2016
 */
class DefaultOptionElementConverterTest extends Specification {

    DefaultOptionElementConverter converter

    def setup() {
        converter = new DefaultOptionElementConverter()
    }

    def "from other to String"() {
        expect:
        converter.convertValueToString('ab').equals("ab")
        converter.convertValueToString(3).equals("3")
        converter.convertValueToString(33L).equals("33")
        converter.convertValueToString(true).equals('true')
        converter.convertValueToString(Locale.UK).equals('en-GB')
        converter.convertValueToString(LocalDateTime.of(2014, 07, 13, 12, 15)).equals('2014-07-13T12:15:00')
        converter.convertValueToString(LabelKey.Yes).equals('uk.q3c.krail.core.i18n.LabelKey.Yes')
        converter.convertValueToString(Service.State.FAILED).equals('uk.q3c.krail.core.services.Service$State.FAILED')
        converter.convertValueToString(BigDecimal.valueOf(433)).equals('433')
        converter.convertValueToString(new OptionList<>(Integer.class, 1, 3)).equals('1~~3')
        converter.convertValueToString(new AnnotationOptionList(ClassPatternSource, RequiresAuthentication)).equals('uk.q3c.krail.core.persist.clazz.i18n.ClassPatternSource~~org.apache.shiro.authz.annotation.RequiresAuthentication')
    }

    def "from String to other"() {
        expect:
        converter.convertStringToValue(String.class, 'ab').equals("ab")
        converter.convertStringToValue(Integer.class, '3').equals(3)
        converter.convertStringToValue(Long, '33').equals(33L)
        converter.convertStringToValue(Boolean, 'true').equals(true)
        converter.convertStringToValue(Locale, 'en-GB').equals(Locale.UK)
        converter.convertStringToValue(LocalDateTime, '2014-07-13T12:15:00').equals(LocalDateTime.of(2014, 07, 13, 12, 15))
        converter.convertStringToValue(I18NKey, 'uk.q3c.krail.core.i18n.LabelKey.Yes').equals(LabelKey.Yes)
        converter.convertStringToValue(Enum, 'uk.q3c.krail.core.services.Service$State.FAILED').equals(Service.State.FAILED)
        converter.convertStringToValue(BigDecimal, '433').equals(BigDecimal.valueOf(433))
        converter.convertStringToValue(AnnotationOptionList, 'uk.q3c.krail.core.persist.clazz.i18n.ClassPatternSource~~org.apache.shiro.authz.annotation.RequiresAuthentication').equals(new AnnotationOptionList(ClassPatternSource, RequiresAuthentication))
    }

    def "convertToString, unknown converter type throws ConverterException "() {
        when:
        converter.convertValueToString(new DefaultApplicationConfiguration())

        then:
        thrown(ConverterException)
    }

    def "convertToModel, unknown converter type throws ConverterException "() {
        when:
        converter.convertStringToValue(DefaultApplicationConfiguration.class, "")

        then:
        thrown(ConverterException)
    }

}
