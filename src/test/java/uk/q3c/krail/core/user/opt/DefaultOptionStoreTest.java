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
import uk.q3c.krail.core.user.profile.UserHierarchyException;
import uk.q3c.krail.core.view.component.LocaleContainer;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class DefaultOptionStoreTest {
    private enum Key {key1, key2}

    DefaultOptionStore store;
    List<String> allLayers;
    private Class<? extends OptionContext> contextClass = LocaleContainer.class;
    @Mock
    private UserHierarchy hierarchy;
    private ArrayList<String> singleLayer;


    @Before
    public void setup() {
        when(hierarchy.layerForCurrentUser(0)).thenReturn("fbaton");
        when(hierarchy.layerForCurrentUser(1)).thenReturn("system");
        when(hierarchy.persistenceName()).thenReturn("MockHierarchy");
        allLayers = Lists.newArrayList("fbaton", "system");
        singleLayer = Lists.newArrayList("system");
        store = new DefaultOptionStore();
    }

    @Test
    public void putAndGet() {
        //given
        when(hierarchy.layersForCurrentUser()).thenReturn(allLayers);

        //when
        store.setValue(3, hierarchy, 0, new OptionKey(contextClass, Key.key1));
        //then
        assertThat(store.getValue(5, hierarchy, 0, new OptionKey(contextClass, Key.key1))).isEqualTo(3);
    }

    @Test
    public void put_and_get_override() {
        //given
        when(hierarchy.layersForCurrentUser()).thenReturn(allLayers);
        //when
        store.setValue(3, hierarchy, 0, new OptionKey(contextClass, Key.key1));
        store.setValue(7, hierarchy, 1, new OptionKey(contextClass, Key.key1));
        //then
        assertThat(store.getValue(5, hierarchy, 0, new OptionKey(contextClass, Key.key1))).isEqualTo(3);
        assertThat(store.getValue(5, hierarchy, 1, new OptionKey(contextClass, Key.key1))).isEqualTo(7);
    }

    @Test
    public void delete() {
        //given
        when(hierarchy.layersForCurrentUser()).thenReturn(allLayers);
        //when
        store.setValue(3, hierarchy, 0, new OptionKey(contextClass, Key.key1));
        store.setValue(7, hierarchy, 1, new OptionKey(contextClass, Key.key1));
        store.deleteValue(hierarchy, 0, new OptionKey(contextClass, Key.key1));
        //then
        assertThat(store.getValue(5, hierarchy, 0, new OptionKey(contextClass, Key.key1))).isEqualTo(7);
        assertThat(store.getValue(5, hierarchy, 1, new OptionKey(contextClass, Key.key1))).isEqualTo(7);
    }

    @Test
    public void delete2() {
        //given
        when(hierarchy.layersForCurrentUser()).thenReturn(allLayers);
        //when
        store.setValue(3, hierarchy, 0, new OptionKey(contextClass, Key.key1));
        store.setValue(7, hierarchy, 1, new OptionKey(contextClass, Key.key1));
        store.deleteValue(hierarchy, 0, new OptionKey(contextClass, Key.key1));
        //then
        assertThat(store.getValue(5, hierarchy, 0, new OptionKey(contextClass, Key.key1))).isEqualTo(7);
        assertThat(store.getValue(5, hierarchy, 1, new OptionKey(contextClass, Key.key1))).isEqualTo(7);
    }

    @Test
    public void defaultValue() {
        //given
        when(hierarchy.layersForCurrentUser()).thenReturn(allLayers);
        //when

        //then
        assertThat(store.getValue(5, hierarchy, 0, new OptionKey(contextClass, Key.key1))).isEqualTo(5);
    }

    @Test
    public void defaultValue2() {
        //given
        when(hierarchy.layersForCurrentUser()).thenReturn(allLayers);
        //when
        store.setValue(3, hierarchy, 0, new OptionKey(contextClass, Key.key2));
        //then
        assertThat(store.getValue(5, hierarchy, 0, new OptionKey(contextClass, Key.key1))).isEqualTo(5);
    }

    @Test
    public void defaultValue3() {
        //given
        when(hierarchy.layersForCurrentUser()).thenReturn(allLayers);
        //when
        store.setValue(3, hierarchy, 0, new OptionKey(contextClass, Key.key2));
        store.deleteValue(hierarchy, 0, new OptionKey(contextClass, Key.key2));
        //then
        assertThat(store.getValue(5, hierarchy, 0, new OptionKey(contextClass, Key.key2))).isEqualTo(5);
    }

    @Test
    public void flushCache() {
        //given

        //when
        store.flushCache();
        //then does nothing
        assertThat(true).isTrue();
    }

    @Test(expected = UserHierarchyException.class)
    public void set_HierarchyIndex_out_of_range() {
        //given
        when(hierarchy.layersForCurrentUser()).thenReturn(allLayers);
        //when
        store.setValue(7, hierarchy, 2, new OptionKey(contextClass, Key.key1));
        //then
        //        exception
    }

    @Test(expected = UserHierarchyException.class)
    public void get_HierarchyIndex_out_of_range() {
        //given
        when(hierarchy.layersForCurrentUser()).thenReturn(allLayers);
        //when
        store.getValue(7, hierarchy, 2, new OptionKey(contextClass, Key.key1));
        //then
        //        exception
    }

    @Test(expected = UserHierarchyException.class)
    public void delete_HierarchyIndex_out_of_range() {
        //given
        when(hierarchy.layersForCurrentUser()).thenReturn(allLayers);
        //when
        store.deleteValue(hierarchy, 2, new OptionKey(contextClass, Key.key1));
        //then
        //        exception
    }

    @Test
    public void singleLayerHierarchy() {
        //given
        when(hierarchy.layersForCurrentUser()).thenReturn(singleLayer);
        //when
        store.setValue(3, hierarchy, 0, new OptionKey(contextClass, Key.key1));
        store.deleteValue(hierarchy, 0, new OptionKey(contextClass, Key.key1));
        //then
        assertThat(store.getValue(5, hierarchy, 0, new OptionKey(contextClass, Key.key1))).isEqualTo(5);
    }

    @Test
    public void compositeKey() {
        //given

        //when
        String noQualifiers = store.compositeKey(LocaleContainer.class, Key.key1);
        String oneQualifiers = store.compositeKey(LocaleContainer.class, Key.key1, "q1");
        String twoQualifiers = store.compositeKey(LocaleContainer.class, Key.key1, "q1", "q2");
        //then
        assertThat(noQualifiers).isEqualTo("LocaleContainer-key1");
        assertThat(oneQualifiers).isEqualTo("LocaleContainer-key1-q1");
        assertThat(twoQualifiers).isEqualTo("LocaleContainer-key1-q1-q2");
    }

}