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
import mockit.Expectations;
import mockit.NonStrictExpectations;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.vaadin.server.Page;
import com.vaadin.ui.Notification;

@RunWith(JMockit.class)
public class DefaultUnauthorisedExceptionHandlerTest {

	@Tested
	DefaultUnauthorizedExceptionHandler handler;

	@Test
	public void invoke_logic() {

		// given
		new Expectations() {
			Page page;
			{
				Page.getCurrent();
				result = page;
				page.showNotification((Notification) any);
			}
		};
		// when
		handler.invoke();
		// then
		// expectations

	}

	@Test
	public void invoke_verifyParameters() {

		// given
		new NonStrictExpectations() {
			Page page;
			{
				Page.getCurrent();
				result = page;
			}
		};
		// when
		handler.invoke();
		// then
		new Verifications() {
			Page page;
			{
				Notification n;
				page.showNotification(n = withCapture());
				assertThat(n.getCaption()).isEqualTo("Authorization");
				assertThat(n.getDescription()).isEqualTo("Go away, you are not allowed to do that");
			}
		};

	}
}
