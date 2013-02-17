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

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.vaadin.server.ErrorEvent;

@SuppressWarnings("unused")
@RunWith(JMockit.class)
public class V7ErrorHandlerTest {

	V7ErrorHandler handler;
	@Mocked
	UnauthenticatedExceptionHandler authenticationHandler;
	@Mocked
	UnauthorizedExceptionHandler authorisationHandler;
	@Mocked
	ErrorEvent event;

	@Before
	public void setup() {
		handler = new V7ErrorHandler(authenticationHandler, authorisationHandler);
	}

	@Test
	public void authorisationError() {

		// given
		new Expectations() {
			ExceptionUtils exceptionUtils;
			{
				Throwable exception = new UnauthorizedException();
				event.getThrowable();
				returns(exception);
				ExceptionUtils.indexOfThrowable(exception, UnauthorizedException.class);
				authorisationHandler.invoke();

			}
		};
		// when
		handler.error(event);
		// then
	}

	@Test
	public void authenticationError() {

		// given
		new Expectations() {

			ExceptionUtils exceptionUtils;
			{
				Throwable exception = new UnauthenticatedException();
				event.getThrowable();
				returns(exception);
				ExceptionUtils.indexOfThrowable(exception, UnauthorizedException.class);
				result = -1;

				ExceptionUtils.indexOfThrowable(exception, UnauthenticatedException.class);
				authenticationHandler.invoke();

			}
		};
		// when
		handler.error(event);
		// then
	}

	@Test
	public void otherError() {

		// given
		new Expectations() {
			ExceptionUtils exceptionUtils;
			{
				Throwable exception = new RuntimeException();
				event.getThrowable();
				returns(exception);
				ExceptionUtils.indexOfThrowable(exception, UnauthorizedException.class);
				result = -1;

				ExceptionUtils.indexOfThrowable(exception, UnauthenticatedException.class);
				result = -1;

				// default handler
				event.getThrowable();
			}
		};
		// when
		handler.error(event);
		// then

	}

}
