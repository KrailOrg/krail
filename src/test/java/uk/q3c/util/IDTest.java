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
package uk.q3c.util;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.ui.Button;
import com.vaadin.ui.Panel;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestIDModule.class})
public class IDTest {

    Button button = new Button();
    Panel panel = new Panel();

    @Inject
    IDTestInstance idt;

    @Test
    public void getIdComponentArray() {

        // given

        // when

        // then
        assertThat(ID.getId(Optional.empty(), panel, button)).isEqualTo("Panel-Button");

    }

    @Test
    public void getIdComponent() {

        // given

        // when

        // then
        assertThat(ID.getId(Optional.empty(), panel)).isEqualTo("Panel");

    }

    @Test
    public void getIdStringComponentArray() {

        // given

        // when

        // then
        assertThat(ID.getId(Optional.of("user"), panel, button)).isEqualTo("Panel-Button-user");
        assertThat(ID.getId(Optional.of(1), panel, button)).isEqualTo("Panel-Button-1");

    }

    //    https://github.com/davidsowerby/krail/issues/383
    @Test
    public void enhancedClass() {
        //given

        //when
        String rawClassName = idt.getClass()
                                 .getName();
        //then
        assertThat(rawClassName).contains("$$");
        assertThat(ID.getId(Optional.empty(), idt, new Button())).isEqualTo("IDTestInstance-Button");
    }
}
