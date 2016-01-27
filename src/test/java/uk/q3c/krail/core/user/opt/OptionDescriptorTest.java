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

package uk.q3c.krail.core.user.opt;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.i18n.DescriptionKey;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class OptionDescriptorTest {

    OptionDescriptor descriptor;

    @Mock
    OptionKey optionKey1;

    @Before
    public void setup() {

    }

    @Test
    public void create() {
        //given
        descriptor = new OptionDescriptor(optionKey1, DescriptionKey.Confirm_Ok, true);
        //when

        //then
        assertThat(descriptor.getDescriptionKey()).isEqualTo(DescriptionKey.Confirm_Ok);
        assertThat(descriptor.getOptionKey()).isEqualTo(optionKey1);
        assertThat(descriptor.isAllQualifiers()).isTrue();
    }

    @Test
    public void create_default_false() {
        //given
        descriptor = new OptionDescriptor(optionKey1, DescriptionKey.Confirm_Ok);
        //when

        //then
        assertThat(descriptor.getDescriptionKey()).isEqualTo(DescriptionKey.Confirm_Ok);
        assertThat(descriptor.getOptionKey()).isEqualTo(optionKey1);
        assertThat(descriptor.isAllQualifiers()).isFalse();
    }


    @Test
    public void create_statically() {
        //given
        descriptor = OptionDescriptor.descriptor(optionKey1, DescriptionKey.Confirm_Ok, true);
        //when

        //then
        assertThat(descriptor.getDescriptionKey()).isEqualTo(DescriptionKey.Confirm_Ok);
        assertThat(descriptor.getOptionKey()).isEqualTo(optionKey1);
        assertThat(descriptor.isAllQualifiers()).isTrue();
    }


    @Test
    public void create_statically_default_false() {
        //given
        descriptor = OptionDescriptor.descriptor(optionKey1, DescriptionKey.Confirm_Ok);
        //when

        //then
        assertThat(descriptor.getDescriptionKey()).isEqualTo(DescriptionKey.Confirm_Ok);
        assertThat(descriptor.getOptionKey()).isEqualTo(optionKey1);
        assertThat(descriptor.isAllQualifiers()).isFalse();
    }

    @Test
    public void create_fluent() {
        //given
        descriptor = OptionDescriptor.descriptor(optionKey1, DescriptionKey.Confirm_Ok, true)
                                     .desc(optionKey1, DescriptionKey.Confirm_Ok, true);
        //when

        //then
        assertThat(descriptor.getDescriptionKey()).isEqualTo(DescriptionKey.Confirm_Ok);
        assertThat(descriptor.getOptionKey()).isEqualTo(optionKey1);
        assertThat(descriptor.isAllQualifiers()).isTrue();
    }

    @Test
    public void create_fluent_default_false() {
        //given
        descriptor = OptionDescriptor.descriptor(optionKey1, DescriptionKey.Confirm_Ok)
                                     .desc(optionKey1, DescriptionKey.Confirm_Ok);
        //when

        //then
        assertThat(descriptor.getDescriptionKey()).isEqualTo(DescriptionKey.Confirm_Ok);
        assertThat(descriptor.getOptionKey()).isEqualTo(optionKey1);
        assertThat(descriptor.isAllQualifiers()).isFalse();
    }

}