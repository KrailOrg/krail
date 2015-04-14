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

package uk.q3c.krail.core.data;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.q3c.krail.core.data.Select.Compare.GREATER_THAN;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class SelectTest {

    @Test
    public void equals() {
        //given

        //when
        Select select = new Select().clazz(TestEntity.class)
                                    .where("name", "a")
                                    .and("age", 33);
        //then
        System.out.println(select);
        assertThat(select.toString()).isEqualTo("SELECT t FROM TestEntity t WHERE name='a' AND age=33");
    }

    @Test
    public void greaterThan() {
        //given

        //when
        Select select = new Select().clazz(TestEntity.class)
                                    .where("name", "a")
                                    .and("age", GREATER_THAN, 33);
        //then
        System.out.println(select);
        assertThat(select.toString()).isEqualTo("SELECT t FROM TestEntity t WHERE name='a' AND age>33");
    }

}