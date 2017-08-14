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

package uk.q3c.krail.core.i18n;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.i18n.I18NKey;

import static org.assertj.core.api.Assertions.*;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class I18NKeyTest {

    @Test
    public void fullName() {
        //given
        String expected = "uk.q3c.krail.core.i18n.LabelKey.Yes";
        //when
        String actual = I18NKey.fullName(LabelKey.Yes);
        //then
        assertThat(actual).isEqualTo(expected);
    }
}