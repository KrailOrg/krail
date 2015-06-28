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

package uk.q3c.krail.i18n;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.persist.CorePatternDaoProvider;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class DefaultDatabaseBundleReaderTest {


    @Mock
    CorePatternDaoProvider patternDaoProvider;

    @Mock
    PatternDao patternDao;

    @Mock
    PatternCacheKey cacheKey1;

    DefaultDatabaseBundleReader reader;

    @Before
    public void setup() {
        reader = new DefaultDatabaseBundleReader(patternDaoProvider);
        when(patternDaoProvider.get()).thenReturn(patternDao);
    }

    @Test
    public void getValue_value_present() {
        //given
        when(patternDao.getValue(cacheKey1)).thenReturn(Optional.of("wossat"));

        //when
        final Optional<String> result = reader.getValue(cacheKey1,"any", "stub");
        //then
        assertThat(result.get()).isEqualTo("wossat");
    }

    @Test
    public void getValue_value_not_present() {
        //given
        when(patternDao.getValue(cacheKey1)).thenReturn(Optional.<String>empty());
        //when
        final Optional<String> result = reader.getValue(cacheKey1, "any","stub");
        //then
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isEqualTo("stub");
        verify(patternDao).write(cacheKey1, "stub");
    }

    @Test
    public void writeStubValue() {
        //given
        String value = "wossat";
        //when
        reader.writeStubValue(cacheKey1, value);
        //then
        verify(patternDao).write(cacheKey1, value);
    }
}