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

package uk.q3c.krail.persist.jpa;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.data.TestEntity;

import static org.assertj.core.api.Assertions.*;
import static uk.q3c.krail.persist.jpa.Select.Compare.*;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class SelectTest {

    @Test
    public void equals() {
        //given

        //when
        Select select = new Select().from(TestEntity.class)
                                    .where("name", "a")
                                    .and("age", 33);
        //then
        System.out.println(select);
        assertThat(select.toString()).isEqualTo("SELECT t FROM TestEntity t WHERE t.name=nameParam AND t.age=ageParam");
        assertThat(select.getParam("nameParam")).isEqualTo("a");
        assertThat(select.getParam("ageParam")).isEqualTo(33);
    }

    @Test
    public void greaterThan() {
        //given

        //when
        Select select = new Select().from(TestEntity.class)
                                    .where("name", "a")
                                    .and("age", GREATER_THAN, 33);
        //then
        System.out.println(select);
        assertThat(select.toString()).isEqualTo("SELECT t FROM TestEntity t WHERE t.name=nameParam AND t.age>ageParam");
        assertThat(select.getParam("nameParam")).isEqualTo("a");
        assertThat(select.getParam("ageParam")).isEqualTo(33);
    }

}