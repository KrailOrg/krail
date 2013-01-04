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

import java.lang.reflect.InvocationTargetException;

import javax.inject.Inject;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;

import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.ErrorEvent;

/**
 * Extends the {@link DefaultErrorHandler} to intercept Shiro related exceptions - {@link AuthorizationException} and
 * {@link AuthenticationException}. Uses pluggable handlers for both.
 * 
 * @author David Sowerby 4 Jan 2013
 * 
 */
public class V7ErrorHandler extends DefaultErrorHandler {

	private final AuthenticationExceptionHandler authenticationHandler;
	private final AuthorizationExceptionHandler authorisationHandler;

	@Inject
	protected V7ErrorHandler(AuthenticationExceptionHandler authenticationHandler,
			AuthorizationExceptionHandler authorisationHandler) {
		super();
		this.authenticationHandler = authenticationHandler;
		this.authorisationHandler = authorisationHandler;
	}

	@Override
	public void error(ErrorEvent event) {
		Throwable originalError = event.getThrowable();
		if (!interceptShiroExceptions(originalError)) {
			doDefault(event);
		}
	}

	private boolean interceptShiroExceptions(Throwable throwable) {
		Throwable t = null;
		if (throwable instanceof InvocationTargetException) {
			t = ((InvocationTargetException) throwable).getTargetException();
		} else {
			t = throwable.getCause();
		}

		// handle the exception if possible
		if (t != null) {
			if (exceptionHandled(t)) {
				return true;
			} else {
				// drill down further
				return interceptShiroExceptions(t);
			}
		} else {
			return false;
		}
	}

	private void defaultExceptionHandler(Throwable throwable) {
		System.out.println("do something with this exception " + throwable.getMessage());
	}

	private boolean exceptionHandled(Throwable t) {
		if (t instanceof AuthorizationException) {
			return authorisationHandler.invoke((AuthorizationException) t);
		}
		if (t instanceof AuthenticationException) {
			return authenticationHandler.invoke((AuthenticationException) t);
		}
		return false;

	}

}
