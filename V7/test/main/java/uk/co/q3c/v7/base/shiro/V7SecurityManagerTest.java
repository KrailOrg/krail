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
package uk.co.q3c.v7.base.shiro;

import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.*;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import uk.co.q3c.base.shiro.ShiroIntegrationTestBase;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class V7SecurityManagerTest extends ShiroIntegrationTestBase {

	@Mock
	LoginStatusMonitor monitor1;

	@Mock
	LoginStatusMonitor monitor2;

	@Override
	@Before
	public void setup() {
		super.setup();
	}

	@Test
	public void construction() {

		// given

		// when

		// then
		assertThat(false).isEqualTo(true);

	}

	@Test
	public void listeners() {

		// given
		V7SecurityManager securityManager = (V7SecurityManager) SecurityUtils.getSecurityManager();
		securityManager.addListener(monitor1);
		securityManager.addListener(monitor2);
		UsernamePasswordToken token = new UsernamePasswordToken("xxx", "password");
		// when
		subject.login(token);
		// then
		verify(monitor1).updateStatus(subject);
	}

}
