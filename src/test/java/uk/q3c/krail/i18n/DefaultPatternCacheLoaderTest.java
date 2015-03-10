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
import uk.q3c.krail.core.eventbus.EventBusModule;
import uk.q3c.krail.core.guice.uiscope.UIScopeModule;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.core.user.opt.Option;
import uk.q3c.krail.testutil.TestI18NModule;
import uk.q3c.krail.testutil.TestOptionModule;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestI18NModule.class, TestOptionModule.class, EventBusModule.class, UIScopeModule.class, VaadinSessionScopeModule.class})
public class DefaultPatternCacheLoaderTest {

    Map<String, BundleReader> bundleReaders = new LinkedHashMap<>();
    Set<String> bundleSourceOrderDefault = new LinkedHashSet<>();
    Map<String, Set<String>> bundleSourceOrder = new HashMap<>();


    @Inject
    Option option;


    DefaultPatternCacheLoader loader;


    @Test
    public void bundleSourceOrder() {
        //given we just have the readers
        bundleReaders.put("class", new ClassBundleReader(option, new ClassBundleControl()));
        bundleReaders.put("props", new PropertiesFromClasspathBundleReader(option, new
                PropertiesFromClasspathBundleControl()));
        //when
        createLoader();
        List<String> orderAny = loader.bundleSourceOrder(LabelKey.nullKey);
        //then
        assertThat(orderAny).containsExactly("class", "props");

        //given we specify a default order in I18NModule
        bundleSourceOrderDefault.add("props");
        bundleSourceOrderDefault.add("class");

        //when
        createLoader();
        orderAny = loader.bundleSourceOrder(LabelKey.nullKey);
        //then
        assertThat(orderAny).containsExactly("props", "class");

        //given we change the order for just one bundleName
        Set<String> tags = new LinkedHashSet<>();
        tags.add("class");
        tags.add("boots");
        bundleSourceOrder.put(TestLabelKey.Blank.bundleName(), tags);

        //when
        createLoader();
        orderAny = loader.bundleSourceOrder(LabelKey.nullKey);
        List<String> orderTestLabelKey = loader.bundleSourceOrder(TestLabelKey.Blank);
        //then
        assertThat(orderAny).containsExactly("props", "class");
        assertThat(orderTestLabelKey).containsExactly("class", "boots");

        //        given user option changes the default

        loader.setOptionReaderOrderDefault("eat", "hat");
        //when
        createLoader();
        orderAny = loader.bundleSourceOrder(LabelKey.nullKey);
        orderTestLabelKey = loader.bundleSourceOrder(TestLabelKey.Blank);
        //then
        assertThat(orderAny).containsExactly("eat", "hat");
        assertThat(orderTestLabelKey).containsExactly("eat", "hat");

        //given user option changes for a single bundleName
        loader.setOptionReaderOrder(TestLabelKey.Blank.bundleName(), "fat", "cat");
        //when
        orderAny = loader.bundleSourceOrder(LabelKey.nullKey);
        orderTestLabelKey = loader.bundleSourceOrder(TestLabelKey.Blank);
        //then
        assertThat(orderAny).containsExactly("eat", "hat");
        assertThat(orderTestLabelKey).containsExactly("fat", "cat");


    }

    private void createLoader() {
        loader = new DefaultPatternCacheLoader(bundleReaders, option, bundleSourceOrder, bundleSourceOrderDefault);
    }
}