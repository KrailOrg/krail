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
package uk.co.q3c.v7.base.guice.services;

import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class ServiceChecksTest {
	@Mock
	Service service;

	@Test
	public void checkIsStarted() {

		// given
		when(service.isStarted()).thenReturn(true);
		// when
		ServiceChecks.checkIsStarted(service);
		// then
		// nothing to check but no exception should be thrown
	}

	@Test(expected = ServiceStatusException.class)
	public void checkIsStarted_fails() {

		// given
		when(service.isStarted()).thenReturn(false);
		// when
		ServiceChecks.checkIsStarted(service);
		// then
		// nothing to check but no exception should be thrown
	}
}
