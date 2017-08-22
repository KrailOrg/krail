/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */
package uk.q3c.krail.core.navigate;

import org.junit.Test;
import uk.q3c.krail.core.i18n.LabelKey;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class LabelKeyForNameTest {

    @Test
    public void keyForName_good() {

        // given
        LabelKeyForName lkfn = new LabelKeyForName(LabelKey.class);
        Set<String> missingEnums = new HashSet<>();
        // when
        Object result = lkfn.keyForName("First_Name", missingEnums);
        // then
        assertThat(result).isEqualTo(LabelKey.First_Name);
        assertThat(missingEnums).isEmpty();

    }

    /**
     * Null can be valid so just ignore it
     */
    @Test
    public void keyForName_null() {

        // given
        LabelKeyForName lkfn = new LabelKeyForName(LabelKey.class);
        Set<String> missingEnums = new HashSet<>();
        // when
        Object result = lkfn.keyForName(null, missingEnums);
        // then
        assertThat(result).isNull();
        assertThat(missingEnums).contains();

    }

    @Test
    public void keyForName_bad() {

        // given
        LabelKeyForName lkfn = new LabelKeyForName(LabelKey.class);
        Set<String> missingEnums = new HashSet<>();
        // when
        Object result = lkfn.keyForName("firt_name", missingEnums);
        // then
        assertThat(result).isNull();
        assertThat(missingEnums).contains("firt_name");

    }
}
