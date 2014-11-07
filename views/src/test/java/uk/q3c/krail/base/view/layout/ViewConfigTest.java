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
package uk.q3c.krail.base.view.layout;

import org.junit.Before;
import org.junit.Test;
import uk.q3c.krail.base.view.layout.DefaultViewConfig.Split;

import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;

public class ViewConfigTest {

    DefaultViewConfig vc;

    @Before
    public void setup() {
        vc = new DefaultViewConfig();
    }

    @Test
    public void splitCompare() {

        // given
        Split s1 = new DefaultViewConfig.Split();
        s1.section1 = 1;
        s1.section2 = 2;

        Split s2 = new DefaultViewConfig.Split();
        s2.section1 = 1;
        s2.section2 = 2;

        // when

        // then
        assertThat(s2.equals(s1)).isTrue();
        assertThat(s1).isEqualTo(s2);
    }

    @Test
    public void addSplit() {

        // given

        // when
        vc.addSplit(1, 2);
        // then
        assertThat(vc.hasSplit(1, 2)).isTrue();
        assertThat(vc.hasSplit(1, 3)).isFalse();
        assertThat(vc.hasSplit(2, 1)).isFalse();

    }

    @Test
    public void removeSplit() {

        // given
        vc.addSplit(1, 2);
        vc.addSplit(1, 3);
        vc.addSplit(2, 1);
        // when
        vc.removeSplit(1, 3);
        // then
        assertThat(vc.hasSplit(1, 2)).isTrue();
        assertThat(vc.hasSplit(1, 3)).isFalse();
        assertThat(vc.hasSplit(2, 1)).isTrue();

    }

    @Test
    public void order() {

        // given
        vc.addSplit(4, 2); // also checks reversal (lowest first)
        vc.addSplit(1, 3);
        vc.addSplit(1, 2);

        // when

        // then
        Iterator<Split> iter = vc.getSplits()
                                 .iterator();
        assertThat(iter.hasNext()).isTrue();
        Split split = iter.next();
        assertThat(split.section1).isEqualTo(1);
        assertThat(split.section2).isEqualTo(2);

        split = iter.next();
        assertThat(split.section1).isEqualTo(1);
        assertThat(split.section2).isEqualTo(3);

        split = iter.next();
        assertThat(split.section1).isEqualTo(2);
        assertThat(split.section2).isEqualTo(4);

    }
}
