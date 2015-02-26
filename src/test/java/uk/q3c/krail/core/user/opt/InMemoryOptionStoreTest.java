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

import java.util.ArrayList;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.when;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class InMemoryOptionStoreTest {
    private enum Key {key1, key2}

    InMemoryOptionStore store;
    private String hierarchyName1 = "h1";
    private String hierarchyRank1 = "r1";

    @Mock
    private OptionKey optionKey1;

    @Mock
    private OptionKey optionKey2;


    @Before
    public void setup() {
        store = new InMemoryOptionStore();
        when(optionKey1.compositeKey()).thenReturn("q-q-1");
        when(optionKey2.compositeKey()).thenReturn("q-q-2");
    }

    @Test
    public void setGetValue() {
        //given
        int value = 6;
        //when
        store.setValue(hierarchyName1, hierarchyRank1, optionKey1, value);
        Object actual = store.getValue(hierarchyName1, hierarchyRank1, optionKey1);
        //then
        assertThat(actual).isEqualTo(value);
    }

    @Test
    public void delete() {
        //given
        int value = 6;
        store.setValue(hierarchyName1, hierarchyRank1, optionKey1, value);
        //when
        Object returned = store.deleteValue(hierarchyName1, hierarchyRank1, optionKey1);
        Object actual = store.getValue(hierarchyName1, hierarchyRank1, optionKey1);
        //then
        assertThat(actual).isNull();
        assertThat(returned).isEqualTo(value);
    }

    @Test
    public void valueMapForOptionKey_some_values() {
        //given
        ArrayList<String> rankNames = Lists.newArrayList("a", "b", "c");
        store.setValue(hierarchyName1, "a", optionKey1, 1);
        store.setValue(hierarchyName1, "b", optionKey1, 2);
        store.setValue(hierarchyName1, "b", optionKey2, 2);
        //when
        final Map<String, Object> resultMap = store.valueMapForOptionKey(hierarchyName1, rankNames, optionKey1);
        //then
        assertThat(resultMap).isNotEmpty();
        assertThat(resultMap).contains(entry("a", 1), entry("b", 2));
    }

    @Test
    public void valueMapForOptionKey_no_values() {
        //given
        ArrayList<String> rankNames = Lists.newArrayList("a", "b", "c");
        //when
        final Map<String, Object> resultMap = store.valueMapForOptionKey(hierarchyName1, rankNames, optionKey1);
        //then
        assertThat(resultMap).isEmpty();
    }
}