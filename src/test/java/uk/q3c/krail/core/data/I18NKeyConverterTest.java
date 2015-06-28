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

package uk.q3c.krail.core.data;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.data.util.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.i18n.I18NException;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.LabelKey;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class I18NKeyConverterTest {

    private I18NKeyConverter converter;

    @Before
    public void setup() {
        converter = new I18NKeyConverter();
    }

    @Test
    public void roundTrip() {
        //given
        String stringRep = "uk.q3c.krail.i18n.LabelKey.Alphabetic_Ascending";
        //when
        String actualString = converter.convertToPresentation(LabelKey.Alphabetic_Ascending, String.class, Locale.UK);
        //then
        assertThat(actualString).isEqualTo(stringRep);

        //when
        I18NKey actualEnum = converter.convertToModel(stringRep, I18NKey.class, Locale.UK);

        //then
        assertThat(actualEnum).isEqualTo(LabelKey.Alphabetic_Ascending);
    }

    @Test(expected = Converter.ConversionException.class)
    public void invalidPresentation_constant() {
        //given
        String stringRep = "uk.q3c.krail.i18n.LabelKey.splot";
        //when
        I18NKey actualEnum = converter.convertToModel(stringRep, I18NKey.class, Locale.UK);
        //then
        //exception
    }

    @Test(expected = Converter.ConversionException.class)
    public void invalidPresentation_class() {
        //given
        String stringRep = "uk.q3c.krail.i18ni.LabelKey.Alphabetic_Ascending";
        //when
        I18NKey actualEnum = converter.convertToModel(stringRep, I18NKey.class, Locale.UK);
        //then
        //exception
    }

    @Test()
    public void invalidPresentation_not_implement_I18NKey() {
        //given
        String stringRep = "uk.q3c.krail.core.services.Service$Status.DEPENDENCY_FAILED";
        //when
        try {

            I18NKey actualEnum = converter.convertToModel(stringRep, I18NKey.class, Locale.UK);
        } catch (Converter.ConversionException ce) {
            assertThat(ce.getCause()).isInstanceOf(I18NException.class);
            assertThat(ce.getCause()
                         .getMessage()).isEqualTo("Does not implement I18NKey");
        }
        //then
        //exception
    }
}