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
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class LocaleConverterTest {


    @Test
    public void roundTrip() {
        //given
        LocaleConverter converter = new LocaleConverter();
        //when
        String localeString = converter.convertToPresentation(Locale.CANADA_FRENCH, String.class, Locale.UK);
        Locale locale = converter.convertToModel(localeString, Locale.class, Locale.UK);
        //then
        assertThat(locale).isEqualTo(Locale.CANADA_FRENCH);

    }
}