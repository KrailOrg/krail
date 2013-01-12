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

import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.*;

import org.apache.shiro.subject.Subject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Very slightly modified version of the example from the Shiro documentation, http://shiro.apache.org/testing.html
 * 
 * @author David Sowerby 11 Jan 2013
 * 
 */
public class ShiroUnitTestBase extends AbstractShiroTest {

	Subject subject;

	@Before
	public void setup() {
		// Create a mock authenticated Subject instance for the test to run:
		subject = mock(Subject.class);
	}

	@Test
	public void testSimple() {

		// given
		when(subject.isAuthenticated()).thenReturn(true);
		// Bind the subject to the current thread:
		setSubject(subject);

		// when

		// perform test logic here. Any call to
		// SecurityUtils.getSubject() directly (or nested in the
		// call stack) will work properly.

		// then
		assertThat(subject.isAuthenticated()).isTrue();
	}

	@After
	public void tearDownSubject() {
		// Unbind the subject from the current thread:
		clearSubject();
	}
}
