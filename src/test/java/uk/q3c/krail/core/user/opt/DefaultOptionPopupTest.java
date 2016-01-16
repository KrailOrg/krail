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
import com.vaadin.data.Property;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.ui.DataTypeToUI;
import uk.q3c.krail.i18n.TestLabelKey;
import uk.q3c.krail.i18n.Translate;

import javax.annotation.Nonnull;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class DefaultOptionPopupTest {

    DefaultOptionPopup popup;
    private MockContext context1;
    private MockContext2 context2;
    @Mock
    private DataTypeToUI dataTypetoUi;

    @Mock
    private Translate translate;

    @Before
    public void setup() {
        popup = new DefaultOptionPopup(dataTypetoUi, translate, new OptionKeyLocator());
        context1 = new MockContext();
        context2 = new MockContext2();
    }

    @Test
    public void optionKeys() {
        //given
        //when
        Map<OptionKey, Class<?>> actual = popup.contextKeys(context2);
        //then
        assertThat(actual).hasSize(4);
    }

    @Test
    public void optionKeys_empty() {
        //given
        //when
        Map<OptionKey, Class<?>> actual = popup.contextKeys(context1);
        //then
        assertThat(actual).hasSize(0);
    }


    static class MockContext implements OptionContext {

        @Nonnull
        @Override
        public Option getOption() {
            return null;
        }

        @Override
        public void optionValueChanged(Property.ValueChangeEvent event) {

        }
    }

    static class MockContext2 implements OptionContext {

        public static final OptionKey<Integer> key3 = new OptionKey<>(125, MockContext2.class, TestLabelKey.Static, TestLabelKey.Large);
        private static final OptionKey<Integer> key4 = new OptionKey<>(126, MockContext2.class, TestLabelKey.Private_Static, TestLabelKey.Large);
        public final OptionKey<Integer> key2 = new OptionKey<>(124, this, TestLabelKey.key2, TestLabelKey.Blank);
        private final OptionKey<Integer> key1 = new OptionKey<Integer>(123, this, TestLabelKey.key1);

        @Nonnull
        @Override
        public Option getOption() {
            return null;
        }

        @Override
        public void optionValueChanged(Property.ValueChangeEvent event) {

        }


    }

}