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

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.testutil.TestOptionModule;
import uk.q3c.krail.testutil.TestPersistenceModule;

import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestOptionModule.class, TestPersistenceModule.class})
public class NativeBundleReaderBaseTest {

    @Inject
    ClassBundleReader reader;

    @Test
    public void autoStub_null_value_autostub_off() {
        //given

        PatternCacheKey cacheKey = new PatternCacheKey(LabelKey.Yes, Locale.UK);
        //when
        Optional<String> value = reader.autoStub(cacheKey, null, false, false, "na");
        //then
        assertThat(value.isPresent()).isFalse();
    }

    @Test
    public void autoStub_null_value_autostub_on_use_name() {
        //given
        PatternCacheKey cacheKey = new PatternCacheKey(LabelKey.Yes, Locale.UK);
        //when
        Optional<String> value = reader.autoStub(cacheKey, null, true, true, "na");
        //then
        assertThat(value.isPresent()).isTrue();
        assertThat(value.get()).isEqualTo("Yes");
    }

    @Test
    public void autoStub_null_value_autostub_on_not_use_name() {
        //given
        PatternCacheKey cacheKey = new PatternCacheKey(LabelKey.Yes, Locale.UK);
        //when
        Optional<String> value = reader.autoStub(cacheKey, null, true, false, "na");
        //then
        assertThat(value.isPresent()).isTrue();
        assertThat(value.get()).isEqualTo("na");
    }

    @Test(expected = NullPointerException.class)
    public void autoStub_null_value_autostub_on_not_use_name_null() {
        //given
        PatternCacheKey cacheKey = new PatternCacheKey(LabelKey.Yes, Locale.UK);
        //when
        Optional<String> value = reader.autoStub(cacheKey, null, true, false, null);
        //then
        assertThat(value.isPresent()).isTrue();
        assertThat(value.get()).isEqualTo("na");
    }


}