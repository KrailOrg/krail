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

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.data.util.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.user.opt.OptionKey;
import uk.q3c.krail.core.user.opt.cache.OptionCacheKey;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({DataModule.class})
public class DefaultStringPersistenceConverterTest {


    @Inject
    DefaultStringPersistenceConverter converter;


    @Mock
    OptionCacheKey cacheKey;

    @Mock
    OptionKey optionKey;

    @Before
    public void setup() {
        when(cacheKey.getOptionKey()).thenReturn(optionKey);
    }

    @Test(expected = ConverterException.class)
    public void noConverter() {
        //given

        //when
        converter.getConverter(Optional.of(new DataModule()));
        //then
    }

    @Test
    public void convertToPersistence_valid() {
        //given

        //when
        Optional<String> actual = converter.convertToPersistence(Optional.of(23));
        //then
        assertThat(actual.get()).isEqualTo("23");
    }

    @Test
    public void convertToPersistence_null() {
        //given

        //when
        final Optional<String> actual = converter.convertToPersistence(Optional.empty());
        //then
        assertThat(actual.isPresent()).isFalse();
    }

    @Test
    public void convertToPersistence_optionalPresent() {
        //given

        //when
        final Optional<String> actual = converter.convertToPersistence(Optional.of(35));
        //then
        assertThat(actual.get()).isEqualTo("35");
    }

    @Test
    public void convertToPersistence_optionalEmpty() {
        //given

        //when
        final Optional<String> actual = converter.convertToPersistence(Optional.empty());
        //then
        assertThat(actual.isPresent()).isFalse();
    }

    @Test
    public void convertFromPersistence() {
        //given
        when(optionKey.getDefaultValue()).thenReturn(99);
        //when
        Optional<?> actual = converter.convertFromPersistence(cacheKey, "23");
        //then
        assertThat(actual.get()).isEqualTo(23);
    }

    @Test(expected = Converter.ConversionException.class)
    public void convertFromPersistence_invalidConversion() {
        //given
        when(optionKey.getDefaultValue()).thenReturn(99);
        //when
        converter.convertFromPersistence(cacheKey, "23a");
        //then
    }

    @Test
    public void convertFromPersistence_null() {
        //given

        //when
        Optional<?> actual = converter.convertFromPersistence(cacheKey, null);
        //then
        assertThat(actual.isPresent()).isFalse();

    }
}