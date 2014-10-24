/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */

package uk.co.q3c.v7.i18n;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.co.q3c.v7.base.guice.vsscope.VaadinSessionScopeModule;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({I18NModule.class, VaadinSessionScopeModule.class})
public class MapTranslate2Test {


    @Inject
    MapTranslate2 t2;

    @Ignore
    @Test
    public void from() {
        //given

        //when
        String answer1 = t2.from(LabelKey2.Authorisation);
        String answer2 = t2.from(LabelKey2.Enable_Account);

        //then
        assertThat(answer1).isEqualTo("starting");
        assertThat(answer2).isEqualTo("Enable Account");
    }

    @Ignore
    @Test
    public void from1() {
        //given

        //when

        Type[] genericInterfaces = LabelKey2.class.getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                Type[] genericTypes = ((ParameterizedType) genericInterface).getActualTypeArguments();
                for (Type genericType : genericTypes) {
                    System.out.println("Generic type: " + genericType);
                }
            }
        }
        //then
    }

    @Ignore
    @Test
    public void collator() {
        //given

        //when

        //then
        assertThat(true).isFalse();
    }
}