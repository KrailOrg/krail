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

package uk.q3c.krail.testApp.test;

import com.vaadin.testbench.util.VersionUtil;
import org.junit.Test;
import uk.q3c.krail.testbench.V7TestBenchTestCase;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by david on 04/10/14.
 */
public class VaadinVersionTest extends V7TestBenchTestCase {

    @Test
    public void confirmVersion() {
        //given
        driver.get(rootUrl());
        pause(500);
        //when

        //then
        assertThat(VersionUtil.getVaadinMajorVersion(getDriver())).isEqualTo(7);
        assertThat(VersionUtil.getVaadinMinorVersion(getDriver())).isEqualTo(3);
        assertThat(VersionUtil.getVaadinRevision(getDriver())).isEqualTo(4);
    }
}
