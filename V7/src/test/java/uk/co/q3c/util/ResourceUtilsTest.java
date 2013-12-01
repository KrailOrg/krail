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
package uk.co.q3c.util;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.server.VaadinService;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class ResourceUtilsTest {

	@Mock
	VaadinService vaadinService;

	@Mock
	File file;

	@Test
	public void vaadinServicePresent() {

		// given
		when(vaadinService.getBaseDirectory()).thenReturn(file);
		VaadinService.setCurrent(vaadinService);
		// when
		File baseDir = ResourceUtils.applicationBaseDirectory();
		// then
		assertThat(baseDir).isEqualTo(file);
	}

	@Test(expected = IllegalStateException.class)
	public void vaadinServiceNotPresent() {

		// given

		VaadinService.setCurrent(null);
		// when
		ResourceUtils.applicationBaseDirectory();
		// then

	}
}
