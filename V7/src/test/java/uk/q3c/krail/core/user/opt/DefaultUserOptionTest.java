/*
 * Copyright (C) 2013 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.q3c.krail.core.user.opt;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultUserOptionTest {

    DefaultUserOption dfo;

    @Before
    public void setup() {
        dfo = new DefaultUserOption(new DefaultUserOptionStore());
    }

    @Test
    public void integer() {

        // given

        // when
        dfo.setOption("a", "a", 1);
        // then
        assertThat(dfo.getOptionAsInt("a", "a", 2)).isEqualTo(1);
        // returns default
        assertThat(dfo.getOptionAsInt("a", "b", 3)).isEqualTo(3);
        // when
        dfo.setOption("a", UserOptionProperty.MAX_DEPTH, 7);
        // then
        assertThat(dfo.getOptionAsInt("a", UserOptionProperty.MAX_DEPTH, 2)).isEqualTo(7);

    }

    @Test
    public void dbl() {

        // given

        // when
        dfo.setOption("a", "a", 1.4);
        // then
        assertThat(dfo.getOptionAsDouble("a", "a", 2)).isEqualTo(1.4);
        // returns default
        assertThat(dfo.getOptionAsDouble("a", "b", 3.3)).isEqualTo(3.3);

    }

    @Test
    public void strng() {

        // given

        // when
        dfo.setOption("a", "a", "x");
        // then
        assertThat(dfo.getOptionAsString("a", "a", "ff")).isEqualTo("x");
        // returns default
        assertThat(dfo.getOptionAsString("a", "b", "y")).isEqualTo("y");

    }

    @Test
    public void dateTime() {

        // given
        DateTime dt1 = new DateTime();
        DateTime dt2 = dt1.minusDays(2);
        // when
        dfo.setOption("a", "a", dt1);
        // then
        assertThat(dfo.getOptionAsDateTime("a", "a", dt1)
                      .toString()).isEqualTo(dt1.toString());
        // returns default
        assertThat(dfo.getOptionAsDateTime("a", "b", dt2)).isEqualTo(dt2);

    }

    @Test
    public void map() {

        // given
        Map<String, String> map1 = new TreeMap<>();
        map1.put("a", "1");
        map1.put("b", "2");

        Map<String, String> map2 = new TreeMap<>();
        map2.put("c", "3");
        map2.put("d", "4");

        // when
        dfo.setOption("group", "map", map1);
        // then
        assertThat(dfo.getOptionAsMap("group", "map", map2)).isEqualTo(map1);
        assertThat(dfo.getOptionAsMap("group", "non-existent map", map2)).isEqualTo(map2);

    }

    @Test
    public void list() {

        // given
        List<String> list1 = new ArrayList<>();
        list1.add("a");
        list1.add("b");

        List<String> list2 = new ArrayList<>();
        list2.add("c");
        list2.add("d");

        // when
        dfo.setOption("group", "list", list1);
        // then
        assertThat(dfo.getOptionAsList("group", "list", list2)).isEqualTo(list1);
        assertThat(dfo.getOptionAsList("group", "non-existent lisdt", list2)).isEqualTo(list2);

    }

    @Test
    public void bool() {

        // given

        // when
        dfo.setOption("a", "a", true);
        // then
        assertThat(dfo.getOptionAsBoolean("a", "a", false)).isEqualTo(true);
        // returns default
        assertThat(dfo.getOptionAsBoolean("a", "b", false)).isEqualTo(false);

    }

}
