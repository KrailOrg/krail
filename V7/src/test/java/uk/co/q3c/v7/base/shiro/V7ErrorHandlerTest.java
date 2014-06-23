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

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.server.ErrorEvent;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.co.q3c.v7.base.navigate.InvalidURIException;
import uk.co.q3c.v7.base.navigate.InvalidURIExceptionHandler;
import uk.co.q3c.v7.base.navigate.V7Navigator;

import static org.mockito.Mockito.*;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class V7ErrorHandlerTest {

	V7ErrorHandler handler;
	@Mock
	UnauthenticatedExceptionHandler authenticationHandler;
	@Mock
	UnauthorizedExceptionHandler authorisationHandler;
	@Mock
	InvalidURIExceptionHandler invalidUriHandler;

	@Mock
	V7Navigator navigator;

	@Mock
	ErrorEvent event;

	@Before
	public void setup() {
		handler = new V7ErrorHandler(authenticationHandler, authorisationHandler, invalidUriHandler, navigator);
	}

	@Test
	public void invalidUri() {

		// given
		Throwable exception = new InvalidURIException();
		when(event.getThrowable()).thenReturn(exception);
		// when
		handler.error(event);
		// then
		verify(invalidUriHandler).invoke();

	}

	@Test
	public void authentication() {

		// given
		Throwable exception = new UnauthenticatedException();
		when(event.getThrowable()).thenReturn(exception);
		// when
		handler.error(event);
		// then
		verify(authenticationHandler).invoke();

	}

	@Test
	public void authorisation() {

		// given
		Throwable exception = new UnauthorizedException();
		when(event.getThrowable()).thenReturn(exception);
		// when
		handler.error(event);
		// then
		verify(authorisationHandler).invoke();
		verify(event, times(1)).getThrowable();

	}

	@Test
	public void other() {

		// given
		// given
		Throwable exception = new NullPointerException();
		when(event.getThrowable()).thenReturn(exception);
		// when
		handler.error(event);
		// then check default has been called
		// not a great check but the only I can use with static methods
		verify(navigator).error(exception);

	}

}
