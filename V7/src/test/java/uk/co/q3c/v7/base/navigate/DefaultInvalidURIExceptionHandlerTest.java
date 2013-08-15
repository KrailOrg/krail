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
package uk.co.q3c.v7.base.navigate;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import uk.co.q3c.v7.i18n.LabelKey;
import uk.co.q3c.v7.i18n.MessageKey;
import uk.co.q3c.v7.i18n.Notifier;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class DefaultInvalidURIExceptionHandlerTest {

	@Mock
	V7Navigator navigator;

	@Mock
	Notifier notifier;

	DefaultInvalidURIExceptionHandler handler;

	String navState = "public/wiggly/id=3";

	@Before
	public void setup() {
		when(navigator.getNavigationState()).thenReturn(navState);
		handler = new DefaultInvalidURIExceptionHandler(navigator, notifier);
	}

	@Test
	public void notify_() {

		// given

		// when
		handler.invoke();
		// then
		verify(notifier).notify(LabelKey.Invalid_Page, MessageKey.invalidURI, navState);

	}
}
