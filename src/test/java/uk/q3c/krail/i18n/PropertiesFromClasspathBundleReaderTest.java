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
import fixture.TestI18NModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.eventbus.EventBusModule;
import uk.q3c.krail.core.guice.uiscope.UIScopeModule;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.core.user.opt.Option;
import uk.q3c.krail.testutil.TestOptionModule;

import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestI18NModule.class, TestOptionModule.class, EventBusModule.class, UIScopeModule.class, VaadinSessionScopeModule.class})
public class PropertiesFromClasspathBundleReaderTest {

    PropertiesFromClasspathBundleReader reader;


    @Inject
    Option option;

    @Before
    public void setup() {
        ResourceBundle.clearCache();
    }


    @Test
    public void valueIsPresent() {
        //given

        PatternCacheKey cacheKey = new PatternCacheKey(TestLabelKey.Yes, Locale.ITALIAN);
        reader = new PropertiesFromClasspathBundleReader(option, new PropertiesFromClasspathBundleControl());
        //when
        Optional<String> value_it = reader.getValue(cacheKey, "properties", false, false, "na");

        //then
        assertThat(value_it.isPresent()).isTrue();
        assertThat(value_it.get()).isEqualTo("italian yes from properties");
    }

    @Test
    public void valueFromBaseBundle() {

        //given
        PatternCacheKey cacheKey = new PatternCacheKey(TestLabelKey.Yes, Locale.forLanguageTag(""));
        reader = new PropertiesFromClasspathBundleReader(option, new PropertiesFromClasspathBundleControl());
        //when
        Optional<String> value_en = reader.getValue(cacheKey, "properties", false, false, "na");

        //then
        assertThat(value_en.isPresent()).isTrue();
        assertThat(value_en.get()).isEqualTo("yes from properties");
    }

    @Test
    public void valueNotPresent() {
        //given
        PatternCacheKey cacheKey = new PatternCacheKey(TestLabelKey.Transfers, Locale.ITALIAN);
        reader = new PropertiesFromClasspathBundleReader(option, new PropertiesFromClasspathBundleControl());
        //when

        Optional<String> value = reader.getValue(cacheKey, "properties", false, false, "na");
        //then
        assertThat(value.isPresent()).isFalse();
    }

    /**
     * Class containing key-value pairs is not in the same package as the key declaration
     */
    @Test
    public void alternativePath() throws ClassNotFoundException {
        //given
        PatternCacheKey cacheKey = new PatternCacheKey(TestLabelKey2.Key1, Locale.forLanguageTag(""));
        reader = new PropertiesFromClasspathBundleReader(option, new PropertiesFromClasspathBundleControl());
        reader.getOption()
              .set(false, reader.getOptionKeyUseKeyPath()
                                .qualifiedWith("properties"));
        reader.getOption()
              .set("fixture1", reader.getOptionKeyPath()
                                     .qualifiedWith("properties"));

        //when
        Optional<String> value = reader.getValue(cacheKey, "properties", false, false, "na");
        //then
        assertThat(value.isPresent()).isTrue();
        assertThat(value.get()).isEqualTo("key1 from properties");
    }

    @Test
    public void bundle_does_not_exist() {
        //given
        PatternCacheKey cacheKey = new PatternCacheKey(TestLabelKey.Transfers, Locale.ITALY);
        reader = new PropertiesFromClasspathBundleReader(option, new PropertiesFromClasspathBundleControl());
        //when
        Optional<String> value = reader.getValue(cacheKey, "properties", false, false, "na");
        //then
        assertThat(value.isPresent()).isFalse();
    }
}