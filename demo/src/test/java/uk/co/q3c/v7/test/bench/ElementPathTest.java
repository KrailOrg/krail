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
package uk.co.q3c.v7.test.bench;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

public class ElementPathTest {

	@Test
	public void treeNode0Expand() {

		// given
		String expected = "V7demo::PID_SDefaultUserNavigationTree#n[0]/expand";
		// when
		ElementPath path = new ElementPath("V7demo");
		String actual = path.id("DefaultUserNavigationTree").index(0).expand().get();
		// then
		assertThat(actual).isEqualTo(expected);

	}

	@Test
	public void treeNode0_1Expand() {

		// given
		String expected = "V7demo::PID_SDefaultUserNavigationTree#n[0]/n[1]/expand";
		// when
		ElementPath path = new ElementPath("V7demo");
		String actual = path.id("DefaultUserNavigationTree").index(0, 1).expand().get();
		// then
		assertThat(actual).isEqualTo(expected);

	}

	@Test
	public void treeNode0Expand_nocontext() {

		// given
		String expected = "ROOT::PID_SDefaultUserNavigationTree#n[0]/expand";
		// when
		ElementPath path = new ElementPath("");
		String actual = path.id("DefaultUserNavigationTree").index(0).expand().get();
		// then
		assertThat(actual).isEqualTo(expected);

	}

	@Test
	public void treeNode0_1Expand_nocontext() {

		// given
		String expected = "ROOT::PID_SDefaultUserNavigationTree#n[0]/n[1]/expand";
		// when
		ElementPath path = new ElementPath("");
		String actual = path.id("DefaultUserNavigationTree").index(0, 1).expand().get();
		// then
		assertThat(actual).isEqualTo(expected);

	}

}
