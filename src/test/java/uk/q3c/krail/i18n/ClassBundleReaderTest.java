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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.user.opt.Option;
import uk.q3c.krail.testutil.TestOptionModule;

import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestOptionModule.class})
public class ClassBundleReaderTest {
    private static Logger log = LoggerFactory.getLogger(ClassBundleReaderTest.class);
    @Inject
    Option option;

    ClassBundleReader reader;

    @Test
    public void valueIsPresent() {
        //given

        PatternCacheKey cacheKey = new PatternCacheKey(TestLabelKey.Yes, Locale.ITALIAN);
        reader = new ClassBundleReader(option, new ClassBundleControl());
        //when
        Optional<String> value_it = reader.getValue(cacheKey, "class", false, false, "na");

        //then
        assertThat(value_it.isPresent()).isTrue();
        assertThat(value_it.get()).isEqualTo("it_Yes");
    }

    @Test
    public void valueFromBaseBundle() {
        //given
        PatternCacheKey cacheKey = new PatternCacheKey(TestLabelKey.Home, Locale.forLanguageTag(""));
        reader = new ClassBundleReader(option, new ClassBundleControl());
        //when
        Optional<String> value_en = reader.getValue(cacheKey, "class", false, false, "na");

        //then
        assertThat(value_en.isPresent()).isTrue();
        assertThat(value_en.get()).isEqualTo("home");
    }

    @Test
    public void valueNotPresent() {
        //given
        PatternCacheKey cacheKey = new PatternCacheKey(TestLabelKey.Transfers, Locale.ITALIAN);
        reader = new ClassBundleReader(option, new ClassBundleControl());
        //when

        Optional<String> value = reader.getValue(cacheKey, "class", false, false, "na");
        //then
        assertThat(value.isPresent()).isFalse();
    }

    /**
     * Class containing key-value pairs is not in the same package as the key declaration
     */
    @Test
    public void alternativePath() throws ClassNotFoundException {
        log.info("alternativePath");
        //given
        PatternCacheKey cacheKey = new PatternCacheKey(TestLabelKey3.Key1, Locale.forLanguageTag(""));
        reader = new ClassBundleReader(option, new ClassBundleControl());
        reader.getOption()
              .set(false, BundleReaderBase.OptionProperty.USE_KEY_PATH, "class");
        reader.getOption()
              .set("fixture1", BundleReaderBase.OptionProperty.PATH, "class");

        //when
        Optional<String> value = reader.getValue(cacheKey, "class", false, false, "na");
        //then
        assertThat(value.isPresent()).isTrue();
    }

    @Test
    public void bundle_does_not_exist() {
        //given
        PatternCacheKey cacheKey = new PatternCacheKey(TestLabelKey.Transfers, Locale.ITALY);
        reader = new ClassBundleReader(option, new ClassBundleControl());
        //when
        Optional<String> value = reader.getValue(cacheKey, "class", false, false, "na");
        //then
        assertThat(value.isPresent()).isFalse();
    }
}