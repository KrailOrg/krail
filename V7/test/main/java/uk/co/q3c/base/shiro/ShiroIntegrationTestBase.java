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
package uk.co.q3c.base.shiro;

import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public abstract class ShiroIntegrationTestBase extends AbstractShiroTest {

	Subject subject;

	@Before
	public void setup() {
		// 1. Build the Subject instance for the test to run:
		subject = new Subject.Builder(getSecurityManager()).buildSubject();
		// 2. Bind the subject to the current thread:
		setSubject(subject);

	}

	@BeforeClass
	public static void beforeClass() {
		// 0. Build and set the SecurityManager used to build Subject instances used in your tests
		// This typically only needs to be done once per class if your shiro.ini doesn't change,
		// otherwise, you'll need to do this logic in each test that is different
		Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:test.shiro.ini");
		setSecurityManager(factory.getInstance());
	}

	@After
	public void tearDownSubject() {
		// 3. Unbind the subject from the current thread:
		clearSubject();
	}
}
