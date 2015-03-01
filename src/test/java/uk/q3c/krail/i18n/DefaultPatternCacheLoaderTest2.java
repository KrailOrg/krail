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

package uk.q3c.krail.i18n;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import fixture.TestI18NModule;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.testutil.TestOptionModule;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestI18NModule.class, TestOptionModule.class})
public class DefaultPatternCacheLoaderTest2 {


    @Inject
    DefaultPatternCacheLoader loader;

    @Test
    public void setOptionStubWithKeyName() {
        //given

        //when
        loader.setOptionStubWithKeyName(true, "class");
        //then
        Boolean optionValue = loader.getOption()
                                    .get(false, DefaultPatternCacheLoader.optionKeyStubWithKeyName.qualifiedWith("class"));
        assertThat(optionValue).isTrue();
    }

    @Test
    public void setOptionAutoStub() {
        //given

        //when
        loader.setOptionAutoStub(true, "class");
        //then
        Boolean optionValue = loader.getOption()
                                    .get(false, DefaultPatternCacheLoader.optionKeyAutoStub.qualifiedWith("class"));
        assertThat(optionValue).isTrue();
    }

    @Test
    public void setOptionStubValue() {
        //given

        //when
        loader.setOptionStubValue("Wiggly", "class");
        //then
        String optionValue = loader.getOption()
                                   .get("bottoms", DefaultPatternCacheLoader.optionKeyStubValue.qualifiedWith("class"));
        assertThat(optionValue).isEqualTo("Wiggly");
    }
}