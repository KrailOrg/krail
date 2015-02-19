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

package uk.q3c.krail.core.user.opt;

import com.google.common.collect.Lists;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.user.profile.UserHierarchy;
import uk.q3c.krail.core.view.component.LocaleContainer;
import uk.q3c.krail.util.KrailCodeException;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class DefaultOptionTest {

    private enum Key {key1, key2}
    DefaultOption option;
    DefaultOptionStore store;
    @Mock
            LocaleContainer localeContainer;
    List<String> allLayers;
    private Class<? extends OptionContext> contextClass=LocaleContainer.class;
    @Mock
    private UserHierarchy hierarchy;
    private ArrayList<String> singleLayer;

    @Test(expected = KrailCodeException.class)
    public void not_initialised() {
        //given
        DefaultOption option2 = new DefaultOption(store, hierarchy);
        TestContext_without_init context = new TestContext_without_init(option2);
        //when
        context.optionMaxDepth();

        //then
        //exception
    }

//
    @Before
    public void setup() {
        store = new DefaultOptionStore();
        option = new DefaultOption(store,hierarchy);
        option.init(LocaleContainer.class);
                when(hierarchy.layerForCurrentUser(0)).thenReturn("fbaton");
                when(hierarchy.layerForCurrentUser(1)).thenReturn("system");
                when(hierarchy.persistenceName()).thenReturn("MockHierarchy");
                allLayers = Lists.newArrayList("fbaton", "system");
                singleLayer = Lists.newArrayList("system");
    }

//
    @Test
    public void putAndGet() {
        //given
        when(hierarchy.layersForCurrentUser()).thenReturn(allLayers);

        //when
        option.set(3, Key.key1);
        //then
        assertThat(option.get(5,  Key.key1)).isEqualTo(3);
    }

    @Test
    public void put_and_get_override() {
        //given
        when(hierarchy.layersForCurrentUser()).thenReturn(allLayers);
        //when
        option.set(3,  Key.key1);
        option.set(7, hierarchy,1,  Key.key1);
        //then
        assertThat(option.get(5,  Key.key1)).isEqualTo(3);
        assertThat(option.get(5, LocaleContainer.class, Key.key1)).isEqualTo(3);
        assertThat(option.get(5, hierarchy, 1, Key.key1)).isEqualTo(7);
    }

    @Test
    public void delete() {
        //given
        when(hierarchy.layersForCurrentUser()).thenReturn(allLayers);
        //when
        option.set(3,  Key.key1);
        option.set(7, hierarchy,1,  Key.key1);
        option.delete(hierarchy,0,  Key.key1);
        //then
        assertThat(option.get(5,  Key.key1)).isEqualTo(7);
        assertThat(option.get(5, hierarchy, 1,  Key.key1)).isEqualTo(7);
        assertThat(option.get(5, hierarchy, 0,  Key.key1)).isEqualTo(7);
        assertThat(option.get(5, hierarchy,  Key.key1)).isEqualTo(7);
    }

    @Test
    public void delete2() {
        //given
        when(hierarchy.layersForCurrentUser()).thenReturn(allLayers);
        //when
        option.set(3,  Key.key1);
        option.set(7, hierarchy,1,  Key.key1);
        option.delete(hierarchy,  Key.key1);
        //then
        assertThat(option.get(5,  Key.key1)).isEqualTo(7);
        assertThat(option.get(5, hierarchy, 1,  Key.key1)).isEqualTo(7);
        assertThat(option.get(5, hierarchy, 0,  Key.key1)).isEqualTo(7);
        assertThat(option.get(5, hierarchy,  Key.key1)).isEqualTo(7);
    }

    @Test
    public void defaultValue() {
        //given
        when(hierarchy.layersForCurrentUser()).thenReturn(allLayers);
        //when

        //then
        assertThat(option.get(5,  Key.key1)).isEqualTo(5);
    }

    @Test
    public void defaultValue2() {
        //given
        when(hierarchy.layersForCurrentUser()).thenReturn(allLayers);
        //when
        option.set(3,  Key.key2);
        //then
        assertThat(option.get(5,  Key.key1)).isEqualTo(5);
    }

    @Test
    public void defaultValue3() {
        //given
        when(hierarchy.layersForCurrentUser()).thenReturn(allLayers);
        //when
        option.set(3, hierarchy,  Key.key2);
        option.delete(Key.key2);
        //then
        assertThat(option.get(5,  Key.key2)).isEqualTo(5);
    }

    @Test
    public void flushCache() {
        //given

        //when
option.flushCache();
        //then does nothing
        assertThat(true).isTrue();
    }

    @Test
    public void init_context_class() {
        //given
        DefaultOption option2 = new DefaultOption(store, hierarchy);
        //when
option2.init(LocaleContainer.class);
        //then
        assertThat(option.getContext()).isEqualTo(LocaleContainer.class);
    }

    @Test
    public void init_context_instance() {
        //given
        DefaultOption option2 = new DefaultOption(store, hierarchy);

        //when
option2.init(localeContainer);
        //then
        assertThat(option.getContext()).isEqualTo(LocaleContainer.class);
    }

    private static class TestContext_without_init implements OptionContext {

        public enum OptionProperty {
            MAX_DEPTH
        }

        private Option option;

        public TestContext_without_init(Option option) {
            this.option = option;
        }

        @Override
        public Option getOption() {
            return option;
        }

        public int optionMaxDepth() {
            return option.get(3, OptionProperty.MAX_DEPTH);
        }
    }
}