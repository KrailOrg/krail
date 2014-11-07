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

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ReplaceComponent {

    @Test
    public void replace() {

        // given
        VerticalLayout vl = new VerticalLayout();
        Label label1 = new Label("1");
        Label label2 = new Label("2");
        Label label3 = new Label("3");
        Label label4 = new Label("4");
        // when
        vl.addComponent(label1);
        vl.addComponent(label2);
        // then
        assertThat(vl.getComponentCount()).isEqualTo(2);
        // when
        vl.replaceComponent(label2, label3);
        // then
        assertThat(vl.getComponentCount()).isEqualTo(2);
        // when
        vl.replaceComponent(label1, label4);
        // then
        assertThat(vl.getComponentCount()).isEqualTo(2);
    }
}
