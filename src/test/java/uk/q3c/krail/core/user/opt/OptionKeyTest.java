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

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.view.component.LocaleContainer;
import uk.q3c.krail.i18n.TestLabelKey;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class OptionKeyTest {


    private Class<? extends OptionContext> contextClass = LocaleContainer.class;


    @Test
    public void compositeKey() {
        //given

        //when
        OptionKey noQualifiers = new OptionKey(LocaleContainer.class, TestLabelKey.key1);
        OptionKey oneQualifiers = new OptionKey(LocaleContainer.class, TestLabelKey.key1, "q1");
        OptionKey twoQualifiers = new OptionKey(LocaleContainer.class, TestLabelKey.key1, "q1", "q2");
        //then
        assertThat(noQualifiers.compositeKey()).isEqualTo("LocaleContainer-key1");
        assertThat(oneQualifiers.compositeKey()).isEqualTo("LocaleContainer-key1-q1");
        assertThat(twoQualifiers.compositeKey()).isEqualTo("LocaleContainer-key1-q1-q2");
    }

    @Test(expected = NullPointerException.class)
    public void null_context() {
        //given

        //when
        OptionKey noQualifiers = new OptionKey(null, TestLabelKey.key1);
        //then
        assertThat(true).isFalse();
    }

    @Test(expected = NullPointerException.class)
    public void null_key() {
        //given

        //when
        OptionKey noQualifiers = new OptionKey(LocaleContainer.class, null);
        //then
        assertThat(true).isFalse();
    }
}