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

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.util.clazz.DefaultUnenhancedClassIdentifier;
import uk.q3c.util.test.AOPTestModule;

import static org.assertj.core.api.Assertions.*;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({AOPTestModule.class})
public class DefaultI18NHostClassIdentifierTest {

    DefaultUnenhancedClassIdentifier identifier;


    @Inject
    I18NTestClass t1;

    @Inject
    I18NTestClass4 t4;

    @Before
    public void setup() {
        identifier = new DefaultUnenhancedClassIdentifier();
    }

    @Test
    public void standardClass() {
        //given

        //when
        Class<?> actual = identifier.getOriginalClassFor(t1);
        //then
        assertThat(actual).isEqualTo(I18NTestClass.class);
    }

    @Test
    public void enhancedClass() {
        //given
        assertThat(t4.getClass()
                     .getName()).contains("$$"); // actually checks the test etup
        //when
        Class<?> actual = identifier.getOriginalClassFor(t4);
        //then
        assertThat(actual).isEqualTo(I18NTestClass4.class);
    }
}